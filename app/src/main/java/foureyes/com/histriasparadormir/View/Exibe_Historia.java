package foureyes.com.histriasparadormir.View;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import foureyes.com.histriasparadormir.R;

public class Exibe_Historia extends AppCompatActivity {

    private TextView txtTitulo, txtCategoria, txtConteudo;
    private ImageView thumbnail;
    private Menu menu;
    private String tituloHistoria;
    private String categoriaHistoria;
    private String caminhoImagemHistoria;
    private String conteudoHistoria;
    private Bitmap imagemHistoria;
    private AdView mAdView;
    private View layout;
    private Dialog sizeDialog;
    private SeekBar seekBar;
    private TextView tv;
    private LayoutInflater inflater;
    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;
    private int text_size = 0;
    private Button branco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_historia);

        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.custom_dialog, null);
        seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
        branco = (Button) layout.findViewById(R.id.btn_branco);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        txtTitulo = (TextView) findViewById(R.id.titulo_sobre);
        txtCategoria = (TextView) findViewById(R.id.categoria);
        txtConteudo = (TextView) findViewById(R.id.txtconteudo);
        thumbnail = (ImageView) findViewById(R.id.thumb);
        tv = (TextView) layout.findViewById(R.id.size);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        tituloHistoria = getIntent().getStringExtra("titulo");
        categoriaHistoria = getIntent().getStringExtra("tipo");
        caminhoImagemHistoria = getIntent().getStringExtra("thumb");
        conteudoHistoria = getIntent().getStringExtra("conteudo");
        imagemHistoria = BitmapFactory.decodeFile(caminhoImagemHistoria, options);

        thumbnail.setImageBitmap(imagemHistoria);
        txtTitulo.append(tituloHistoria);
        txtCategoria.append(categoriaHistoria);
        txtConteudo.append(Html.fromHtml(conteudoHistoria));
        seekBar.setProgress(20);
        sizeDialog = new Dialog(this);

        preferencias = getPreferences(Context.MODE_PRIVATE);
        text_size = preferencias.getInt("text_size",20);
        txtConteudo.setTextSize(text_size);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_secundario, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_play) {
            handleMenuOption(id);
            return true;

        } else if (id == R.id.action_share) {
            share();
            return true;
        } else if (id == R.id.action_pause) {
            handleMenuOption(id);
        }else if(id == R.id.acction_size){
            showDialogSize();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleMenuOption(int id) {
        if (id == R.id.action_play) {
            fullScreen();
            menu.findItem(R.id.action_play).setVisible(false);
            menu.findItem(R.id.action_pause).setVisible(true);
        } else if (id == R.id.action_pause) {
            fullScreen();
            menu.findItem(R.id.action_pause).setVisible(false);
            menu.findItem(R.id.action_play).setVisible(true);
        }
    }

    public void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Leia: " + tituloHistoria + " em: https://play.google.com/store/apps/details?id=com.digitalbooks");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void fullScreen() {
        int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("MSG", "Turning immersive mode mode off. ");
        } else {
            Log.i("MSG", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }


    private void showDialogSize() {
        if(sizeDialog!=null){
            sizeDialog.setContentView(layout);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(text_size);
        }
        tv.setText(String.valueOf(text_size + "px"));
        txtConteudo.setTextSize(text_size,20);


        Exibe_Historia.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                branco.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("Erro","Clicado");
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tv.setText(String.valueOf(progress) + "px");
                        txtConteudo.setTextSize(Float.parseFloat(String.valueOf(progress)));
                        editor = preferencias.edit();
                        editor.putInt("text_size",progress);
                        editor.commit();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });

        sizeDialog.show();
    }

}
