package foureyes.com.histriasparadormir.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import foureyes.com.histriasparadormir.Controll.JsonReceiverHistorias;
import foureyes.com.histriasparadormir.DAO.Banco;
import foureyes.com.histriasparadormir.R;


public class Exibe_Lista extends AppCompatActivity {

    private Banco b;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe__lista);
        b = new Banco(this, null, null, 1);


        RequestConfiguration conf = new RequestConfiguration.Builder()
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE).build();

        MobileAds.setRequestConfiguration(conf);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = (AdView) findViewById(R.id.adViewList);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        populaLista();
        exibeHistoria();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem edit_item = menu.add(0, 1, 0, "");
        edit_item.setIcon(R.drawable.info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            edit_item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                exibeAlert();
                return true;
            default:
                return false;
        }
    }

    public void populaLista() {
        Cursor cursor = b.gethistoriasCursor();

        startManagingCursor(cursor);

        String[] titulos = new String[]{"thumb", "titulo", "tipo"};
        int[] viwIds = new int[]{R.id.thumb, R.id.titulo_sobre, R.id.categoria};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_layout, cursor, titulos, viwIds);
        ListView lista = (ListView) findViewById(R.id.listaHistorias);
        lista.setAdapter(adapter);
    }

    public void exibeHistoria() {
        ListView lista = (ListView) findViewById(R.id.listaHistorias);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = b.getHistoriaId(id);

                if (cursor.moveToFirst()) {
                    //String idBD = cursor.getString(cursor.getColumnIndex("_id"));
                    String titulo = cursor.getString(cursor.getColumnIndex("titulo"));
                    String conteudo = cursor.getString(cursor.getColumnIndex("conteudo"));
                    String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
                    String thumb = cursor.getString(cursor.getColumnIndex("thumb"));

                    Intent intent = new Intent(getApplicationContext(), Exibe_Historia.class).putExtra("titulo", titulo).putExtra("tipo", tipo).putExtra("conteudo", conteudo).putExtra("thumb", thumb);
                    startActivity(intent);
                }
                cursor.close();
            }
        });
    }

    //Metodo que verifica a conexao com a internet
    private boolean networkConnectivity(Context c) {
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            status = true;
        }
        return status;
    }

    public void exibeAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("O Aplicativo Histórias para dormir foi elaborado para estimular a imaginação das crianças antes de dormir com uma boa dose de histórias clássicas. \n Todas as histórias do app foram retiradas de sites que já as disponibilizam de forma gratuíta. Ícone: Designed by Freepik").setTitle("Sobre este aplicativo");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
