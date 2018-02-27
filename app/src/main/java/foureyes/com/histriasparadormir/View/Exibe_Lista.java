package foureyes.com.histriasparadormir.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

        mAdView = (AdView) findViewById(R.id.adViewLista);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // inicializa();
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

    //Metodo que verifica a inicializacao do app, se o banco nao estiver populado ele realiza o download dos dados
    public void inicializa() {
        if (b.getQuantDados() == 0) {
            baixaJson();
        }
    }

    //Metodo que inicia a tarefa de baixar o json com os dados do webservice
    public void baixaJson() {
        if (networkConnectivity(this)) {
            //link do webservice feito com wordpress
            String wpUrlLivro = "http://tkdhkd.96.lt/api/get_livros/";
            new JsonReceiverHistorias(this).execute(wpUrlLivro);
            populaLista();
        } else {
            Toast.makeText(getApplicationContext(), "Sem conexão com a internet!", Toast.LENGTH_LONG).show();
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
