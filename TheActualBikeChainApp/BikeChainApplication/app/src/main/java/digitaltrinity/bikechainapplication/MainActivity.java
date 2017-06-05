package digitaltrinity.bikechainapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends Activity{
    private EditText userName;
    private TextView errorDialog;
    private Button clickable;

    private final static String errorNotThere = "Error: Google Play Services needs to be download or updated from the Google Play Store";
    private final static String errorEnable = "Error: Google Play Services needs to be enabled";

    private GoogleApiAvailability available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText)findViewById(R.id.userName);
        userName.setHint("Enter full name used in waitlist");

        errorDialog = (TextView) findViewById(R.id.error);
        clickable = (Button) findViewById(R.id.clickable);

        available = GoogleApiAvailability.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int status = available.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS){
            clickable.setEnabled(false);
            if(status != ConnectionResult.SERVICE_DISABLED){
                errorDialog.setText(errorNotThere);
            } else errorDialog.setText(errorEnable);
        } else clickable.setEnabled(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userName.setText(null);
        userName.setHint("Enter full name used in waitlist");
    }

    public void emptyStuff(View view){
        SharedPreferences.Editor name = getSharedPreferences("name", MODE_PRIVATE).edit();
        name.putString("userName", userName.getText().toString().replace(" ", "")).apply();
        Intent intent = new Intent(this, SuccessScreen.class);
        startActivity(intent);
    }
}
