package foureyes.com.histriasparadormir.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import foureyes.com.histriasparadormir.DAO.Database;
import foureyes.com.histriasparadormir.R;


public class StoryList extends AppCompatActivity {

    private Database database;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe__lista);
        database = new Database(this, null, null, 1);
        populateList();
        showStory();
        MobileAds.initialize(this, initializationStatus -> {
        });
        mAdView = findViewById(R.id.adViewList);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
                appInfo();
                return true;
            default:
                return false;
        }
    }

    public void populateList() {
        Cursor cursor = database.getStoryCursor();

        startManagingCursor(cursor);

        String[] titles = new String[]{"thumbnail", "title", "type"};
        int[] viwIds = new int[]{R.id.thumb, R.id.titulo_sobre, R.id.categoria};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_layout, cursor, titles, viwIds);
        ListView listView = findViewById(R.id.listaHistorias);
        listView.setAdapter(adapter);
    }

    public void showStory() {
        ListView listView = findViewById(R.id.listaHistorias);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = database.getStoryId(id);

                if (cursor.moveToFirst()) {
                    //String idBD = cursor.getString(cursor.getColumnIndex("_id"));
                    String titulo = cursor.getString(cursor.getColumnIndex("title"));
                    String conteudo = cursor.getString(cursor.getColumnIndex("content"));
                    String tipo = cursor.getString(cursor.getColumnIndex("type"));
                    String thumb = cursor.getString(cursor.getColumnIndex("thumbnail"));

                    Intent intent = new Intent(getApplicationContext(), Exibe_Historia.class).putExtra("title", titulo).putExtra("type", tipo).putExtra("content", conteudo).putExtra("thumbnail", thumb);
                    startActivity(intent);
                }
                cursor.close();
            }
        });
    }

    public void appInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml("<b>Sobre</b> <br><br>- <a href=\"https://historiasparadormir.foureyesapps.tech/politica-de-privacidade/\">Política de privacidade</a><br><br>- <a href=\"https://historiasparadormir.foureyesapps.tech/termos-de-uso/\">Termos de uso</a>"));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
