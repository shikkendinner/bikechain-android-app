package digitaltrinity.bikechainapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class SuccessScreen extends Activity {

    private TextView userCounter;
    private TextView errorDescription;

    private Handler repeatControl = new Handler();
    private Runnable repeatChecking = new Runnable() {
        @Override
        public void run() {
            new checkName().execute();
            repeatControl.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_screen);

        userCounter = (TextView) findViewById(R.id.userCounter);
        errorDescription = (TextView) findViewById(R.id.descriptionForError);

        userCounter.setTextSize(80);
        userCounter.setText("Please Wait");
        errorDescription.setText("while we search the waitlist...");

        startService(new Intent(this, SetUpToken.class).putExtra("ShikkenDinner", true));
    }

    @Override
    protected void onStart() {
        super.onStart();
        repeatControl.post(repeatChecking);
    }

    @Override
    protected void onStop() {
        super.onStop();
        repeatControl.removeCallbacks(repeatChecking);

        if (userCounter.getText().equals("It's your turn!") || userCounter.getText().equals("Oops")) {
            startService(new Intent(this, SetUpToken.class).putExtra("ShikkenDinner", false));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void changeName(View view) {
        finish();
    }

    private class checkName extends AsyncTask<Void, Void, String> {
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
                error = "-2";
            }
            return error;
        }

        @Override
        protected void onPostExecute(String e) {
            if (e.equals("-1")) {
                userCounter.setTextSize(120);
                userCounter.setText("Oops");
                errorDescription.setText("user not on the list");
            } else if (e.equals("-2")) {
                userCounter.setTextSize(80);
                userCounter.setText("Big Error");
                errorDescription.setText("Check your internet connection");
            } else if (!(e.equals("0"))) {
                userCounter.setTextSize(150);
                userCounter.setText(e);
                errorDescription.setText("user" + (e.equals("1") ? " " : "s ") + "ahead of you on the waitlist");
            } else if (e.equals("0")) {
                userCounter.setTextSize(80);
                userCounter.setText("It's your turn!");
                errorDescription.setText("Arrive at the workshop immediately or your turn may be cancelled");
            }
        }
    }
}
