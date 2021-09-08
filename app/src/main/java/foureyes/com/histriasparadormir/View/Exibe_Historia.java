package foureyes.com.histriasparadormir.View;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


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
    private String background_color;
    private Button branco, preto, sepia;
    private View backgroundLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_historia);

        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.custom_dialog, null);
        seekBar = layout.findViewById(R.id.seekBar);
        branco = layout.findViewById(R.id.btn_branco);
        preto = layout.findViewById(R.id.btn_preto);
        sepia = layout.findViewById(R.id.btn_sepia);
        backgroundLayout = findViewById(R.id.backgroundLayout);


        RequestConfiguration conf = new RequestConfiguration.Builder()
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE).build();

        MobileAds.setRequestConfiguration(conf);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adViewHistory);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        txtTitulo = findViewById(R.id.titulo_sobre);
        txtCategoria = findViewById(R.id.categoria);
        txtConteudo = findViewById(R.id.txtconteudo);
        thumbnail = findViewById(R.id.thumb);
        tv = layout.findViewById(R.id.size);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        tituloHistoria = getIntent().getStringExtra("title");
        categoriaHistoria = getIntent().getStringExtra("type");
        caminhoImagemHistoria = getIntent().getStringExtra("thumbnail");
        conteudoHistoria = getIntent().getStringExtra("content");
        imagemHistoria = BitmapFactory.decodeFile(caminhoImagemHistoria, options);

        thumbnail.setImageBitmap(imagemHistoria);
        txtTitulo.append(tituloHistoria);
        txtCategoria.append(categoriaHistoria);
        txtConteudo.append(Html.fromHtml(conteudoHistoria));
        seekBar.setProgress(20);
        sizeDialog = new Dialog(this);

        preferencias = getPreferences(Context.MODE_PRIVATE);
        text_size = preferencias.getInt("text_size", 20);
        background_color = preferencias.getString("background_color", "branco");
        setColors(background_color);
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
        } else if (id == R.id.acction_size) {
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
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Leia: " + tituloHistoria + "para seu filho em: https://play.google.com/store/apps/details?id=com.digitalbooks");
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
        if (sizeDialog != null) {
            sizeDialog.setContentView(layout);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(preferencias.getInt("text_size", 20));
        }
        tv.setText(String.valueOf(preferencias.getInt("text_size", 20) + "px"));
        txtConteudo.setTextSize(preferencias.getInt("text_size", 20));


        Exibe_Historia.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                branco.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setColors("branco");
                    }
                });

                preto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setColors("preto");
                    }
                });

                sepia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setColors("sepia");
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tv.setText(progress + "px");
                        txtConteudo.setTextSize(Float.parseFloat(String.valueOf(progress)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        editor = preferencias.edit();
                        editor.putInt("text_size", seekBar.getProgress());
                        editor.commit();
                    }
                });
            }
        });
        sizeDialog.show();
    }

    public void setColors(String color) {
        editor = preferencias.edit();

        switch (color) {
            case "branco":
                backgroundLayout.setBackground(getResources().getDrawable(R.color.branco));
                txtTitulo.setTextColor(Color.parseColor("#808080"));
                txtCategoria.setTextColor(Color.parseColor("#808080"));
                txtConteudo.setTextColor(Color.parseColor("#808080"));
                editor.putString("background_color", "branco");
                break;
            case "preto":
                backgroundLayout.setBackground(getResources().getDrawable(R.color.preto));
                txtTitulo.setTextColor(Color.WHITE);
                txtCategoria.setTextColor(Color.WHITE);
                txtConteudo.setTextColor(Color.WHITE);
                editor.putString("background_color", "preto");
                break;
            case "sepia":
                backgroundLayout.setBackground(getResources().getDrawable(R.color.sepia));
                txtTitulo.setTextColor(Color.parseColor("#616161"));
                txtCategoria.setTextColor(Color.parseColor("#616161"));
                txtConteudo.setTextColor(Color.parseColor("#616161"));
                editor.putString("background_color", "sepia");
                break;
        }
        editor.commit();
    }
}
