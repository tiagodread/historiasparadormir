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
import java.util.concurrent.TimeUnit;

import foureyes.com.histriasparadormir.Controll.JsonReceiverHistorias;
import foureyes.com.histriasparadormir.DAO.Banco;
import foureyes.com.histriasparadormir.R;

public class MainActivity extends AppCompatActivity {

    private static Banco banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banco = new Banco(this, null, null, 1);
        initApp();
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

    /**
     * Initial logic for the app
     */
    public void initApp() {
        if (isDeviceConnected(this)) {
            if (banco.getQuantDados() == 0) {
                downloadContent();
            } else {
                if (isContentOutOfDate()) {
                    downloadContent();
                } else {
                    startActivity(new Intent(this, Exibe_Lista.class));
                }
            }

        } else {
            if (banco.getQuantDados() > 0) {
                startActivity(new Intent(this, Exibe_Lista.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet_message,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Get the last update day in database and check if the content date is greater than 15 days
     *
     * @return boolean
     */
    public boolean isContentOutOfDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long diffInDays = 0;
        try {
            String todayString = dateFormat.format(new Date());
            Log.i("checkForUpdates", "Today: " + todayString);

            Date today = dateFormat.parse(todayString);
            Date lastUpdate = dateFormat.parse(banco.getLastUpdate());
            Log.i("checkForUpdates", "Last Update: " + banco.getLastUpdate());


            long diffInMillies = Math.abs(lastUpdate.getTime() - today.getTime());

            diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            Log.i("checkForUpdates", "Diff In Days: " + String.valueOf(diffInDays));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (diffInDays > 15) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Download all content
     */
    public void downloadContent() {
        if (isDeviceConnected(this)) {
            String wpUrlLivro = "http://tkdhkd.96.lt/api/get_livros/";
            new JsonReceiverHistorias(this).execute(wpUrlLivro);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_message,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check internet connection
     *
     * @param c
     * @return boolean
     */
    private boolean isDeviceConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
