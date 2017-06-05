package digitaltrinity.bikechainapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class SetUpToken extends IntentService {

    private final static String SenderID = "540759147722";
    private Handler control;
    private Runnable sendToast;
    private Runnable sendDelete;

    public SetUpToken() {
        super("SetUpToken");
        control = new Handler();
        sendToast = new Runnable() {
            @Override
            public void run() {
                SharedPreferences valid = PreferenceManager.getDefaultSharedPreferences(SetUpToken.this);
                Toast.makeText(SetUpToken.this,
                        (valid.getBoolean("tokenEntry", false) ? "Registration Successful" : "Registration Failed")
                        , Toast.LENGTH_SHORT).show();
            }
        };
        sendDelete = new Runnable() {
            @Override
            public void run() {
                SharedPreferences valid = PreferenceManager.getDefaultSharedPreferences(SetUpToken.this);
                Toast.makeText(SetUpToken.this,
                        (valid.getBoolean("deleteEntry", false) ? "Data Cleared" : "Data clearance failed")
                        , Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);

        SharedPreferences database = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean delete = intent.getBooleanExtra("ShikkenDinner", true);
        if(delete) {
            String token;
            try {
                token = instanceID.getToken(SenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Connection website = Jsoup.connect(
                        "https://script.google.com/macros/s/AKfycbwigFZ5KeTFJEdyWwtHKr-lCUNsZLpcFfIQ-G3TPUej8LDXROk/exec?id=" + token);
                Connection.Response doc = website.userAgent(
                        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .timeout(10000)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .referrer("http://www.google.com")
                        .method(Connection.Method.POST)
                        .execute();
                String error = doc.body();

                if (!error.equals("Success")) throw new IOException();

                database.edit().putBoolean("tokenEntry", true).apply();
            } catch (IOException e) {
                database.edit().putBoolean("tokenEntry", false).apply();
            }

            control.post(sendToast);
        }
        else {
            try {
                InstanceID.getInstance(this).deleteInstanceID();
                database.edit().putBoolean("deleteEntry", true).apply();
            } catch (IOException e) {
                database.edit().putBoolean("deleteEntry", false).apply();
            }

            control.post(sendDelete);
        }
    }

}
