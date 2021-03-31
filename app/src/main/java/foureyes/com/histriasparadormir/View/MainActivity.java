package foureyes.com.histriasparadormir.View;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import foureyes.com.histriasparadormir.Controll.JsonReceiverHistorias;
import foureyes.com.histriasparadormir.DAO.Banco;
import foureyes.com.histriasparadormir.R;

public class MainActivity extends AppCompatActivity {

    private static Banco banco;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banco = new Banco(this, null, null, 1);
        inicializa();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

        /*
    Método que realiza a inicializacao do app, se o banco nao estiver populado ele realiza o download dos dados

    */

    public void inicializa() {

        if (networkConnectivity(this)) {

            if (banco.getQuantDados() == 0) {
                baixaJson();
            } else {
                if (checkForUpdates()) {
                    baixaJson();
                } else {
                    startActivity(new Intent(this, Exibe_Lista.class));
                }
            }

        } else {
            if (banco.getQuantDados() > 0) {
                startActivity(new Intent(this, Exibe_Lista.class));
            } else {
                Toast.makeText(getApplicationContext(), "Você precisa se conectar à internet pelo menos uma vez!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
 @Método que controla as atualizações a cada 7 dias.
 @Autor: Tiago Góes.
 */
    public boolean checkForUpdates() {

        String Datahoje = dateFormat.format(new Date());
        long diff = 0;
        boolean status = false;

        if (banco.getLastUpdate() != null) {

            try {
                Date dataAtual = dateFormat.parse(Datahoje);
                Date dataGravada = dateFormat.parse(banco.getLastUpdate());
                diff = dataAtual.getTime() - dataGravada.getTime();

                Log.e("T-dataAtual", String.valueOf(dataAtual));
                Log.e("T-dataGravada", String.valueOf(dataGravada));
                Log.e("T-diff", String.valueOf(diff));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if ((diff / 1000 / 60 / 60 / 24) >= 7) {
                status = true;
            } else {
                status = false;
            }
        }
        return status;
    }

    //Metodo que inicia a tarefa de baixar o json com os dados do webservice
    public void baixaJson() {
        if (networkConnectivity(this)) {
            String wpUrlLivro = "http://tkdhkd.96.lt/api/get_livros/";
            new JsonReceiverHistorias(this).execute(wpUrlLivro);
        } else {
            Toast.makeText(getApplicationContext(), "Você precisa se conectar à internet pelo menos uma vez!", Toast.LENGTH_LONG).show();
        }
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
}
