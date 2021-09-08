package foureyes.com.histriasparadormir.View;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import foureyes.com.histriasparadormir.Controll.JsonReceiverHistorias;
import foureyes.com.histriasparadormir.DAO.Database;
import foureyes.com.histriasparadormir.R;

public class MainActivity extends AppCompatActivity {

    private static Database database;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        database = new Database(this, null, null, 1);
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
            if (database.isEmpty() || isContentOutOfDate()) {
                downloadContent();
            } else {
                startActivity(new Intent(this, Exibe_Lista.class));
            }

        } else {
            if (!database.isEmpty()) {
                startActivity(new Intent(this, Exibe_Lista.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet_message, Toast.LENGTH_LONG).show();
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
        int OUT_OF_DATE_DAYS_LIMIT = 15;
        int DIFF_IN_DAYS = 0;
        String TODAY_STRING = dateFormat.format(new Date());
        try {
            Date today = dateFormat.parse(TODAY_STRING);
            Date lastUpdate = dateFormat.parse(database.getLastUpdate());
            Log.i("checkForUpdates", "Today: " + TODAY_STRING);
            Log.i("checkForUpdates", "Last Update: " + database.getLastUpdate());
            long diffInMillis = Math.abs(lastUpdate.getTime() - today.getTime());
            DIFF_IN_DAYS = (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            Log.i("checkForUpdates", "Diff In Days: " + String.valueOf(DIFF_IN_DAYS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DIFF_IN_DAYS > OUT_OF_DATE_DAYS_LIMIT;
    }

    /**
     * Download all content
     */
    public void downloadContent() {
        if (isDeviceConnected(this)) {
            new JsonReceiverHistorias(this).execute(String.valueOf(R.string.api_url));
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_message,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check internet connection
     *
     * @param context
     * @return boolean
     */
    private boolean isDeviceConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
