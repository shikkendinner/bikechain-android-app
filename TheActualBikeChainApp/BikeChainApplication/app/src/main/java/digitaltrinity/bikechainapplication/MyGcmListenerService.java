package digitaltrinity.bikechainapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class MyGcmListenerService extends GcmListenerService {

    public static final int ID = 2015;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // if its your turn delete instanceID instance, and tokens
        new checkName().execute();
    }

    private void displayNotification() {
        Intent transfer = new Intent(this, SuccessScreen.class);
        PendingIntent pendingTransfer = PendingIntent.getActivity(this, 0, transfer, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_bikechainlogotransparent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setTicker("You're next on the waitlist!")
                .setContentTitle("It is finally your turn!")
                .setContentText("Arrive at the workshop immediately")
                .setWhen(System.currentTimeMillis())
                .setUsesChronometer(true)
                .setContentIntent(pendingTransfer)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(ID, notification);
    }

    private class checkName extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            String error;
            try {
                String userName = getSharedPreferences("name", MODE_PRIVATE).getString("userName", "ERROR");
                Connection website = Jsoup.connect(
                        "https://script.google.com/macros/s/AKfycbwigFZ5KeTFJEdyWwtHKr-lCUNsZLpcFfIQ-G3TPUej8LDXROk/exec?userName=" + userName);
                Connection.Response doc = website.userAgent(
                        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .timeout(10000)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .referrer("http://www.google.com")
                        .execute();
                error = doc.body();
            } catch (IOException e) {
                error = e.getMessage();
            }
            return error;
        }

        @Override
        protected void onPostExecute(String e) {
            if(e.equals("0")){
                displayNotification();
                startService(new Intent(MyGcmListenerService.this, SetUpToken.class).putExtra("ShikkenDinner", false));
            }
        }
    }
}
