package ciat.com.tumblrslideshow;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_ID = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If the client key and secret aren't loaded, load them
        if (!OauthCredentials.clientKeyAndSecretExist()) {
            try {
                OauthCredentials.loadClientKeySecret(getBaseContext());
            } catch (IOException e) {
                Log.e(LOG_ID, "Error loading client key/secret", e);
                return; // If these don't work we literally can't do anything of use, just stop here
                // TODO: Show a dialog here or something
            }
        }
        // If the token key and secret aren't loaded, try to load them
        if (!OauthCredentials.tokenKeyAndSecretExist()) {
            boolean tokenExists = OauthCredentials.loadTokenKeySecret(getBaseContext());
            if (!tokenExists) { // If these don't exist, we need to log in.
                Log.i(LOG_ID, "Tokens don't exist, navigating to login screen");

                // Go to the login screen
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);

            } else { // If these tokens exist, just go ahead and continue as normal
                Log.i(LOG_ID, "Tokens exist, navigating to main screen");
            }
        }
    }
}
