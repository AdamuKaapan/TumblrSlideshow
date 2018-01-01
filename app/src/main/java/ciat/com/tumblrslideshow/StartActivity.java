package ciat.com.tumblrslideshow;

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

        try {
            OauthCredentials.loadClientKeySecret(getBaseContext());
            Log.i(LOG_ID, "Client key: " + OauthCredentials.getClientKey());
            Log.i(LOG_ID, "Client secret: " + OauthCredentials.getClientSecret());
        } catch (IOException e) {
            Log.e(LOG_ID, "Error loading client key/secret", e);
        }

        boolean tokenExists = OauthCredentials.loadTokenKeySecret(getBaseContext());
        if (!tokenExists) {
            Log.i(LOG_ID, "Tokens don't exist.");

            // Go to the login screen
        } else {
            Log.i(LOG_ID, "Tokens exist:");
            Log.i(LOG_ID, "Token key: " + OauthCredentials.getTokenKey());
            Log.i(LOG_ID, "Token secret: " + OauthCredentials.getTokenSecret());

            // Go to the main app screen
        }
    }
}
