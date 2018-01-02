package ciat.com.tumblrslideshow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class StartActivity extends AppCompatActivity {

    public static String LOG_ID = "StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            OauthCredentials.loadClientKeySecret(getBaseContext());
        } catch (IOException e) {
            Log.e(LOG_ID, "Error loading client key/secret", e);
        }

        boolean tokenExists = OauthCredentials.loadTokenKeySecret(getBaseContext());
        if (!tokenExists) {
            Log.i(LOG_ID, "Tokens don't exist, navigating to login screen");

            // Go to the login screen
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);

        } else {
            Log.i(LOG_ID, "Tokens exist, navigating to main screen");

            // Go to the main app screen
        }
    }
}
