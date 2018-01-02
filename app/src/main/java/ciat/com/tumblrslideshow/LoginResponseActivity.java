package ciat.com.tumblrslideshow;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginResponseActivity extends AppCompatActivity {

    public static final String LOG_ID = "LoginResponseActivity";

    // Used to store the temporary token secret given to us by the request_token call on the LoginActivity
    public static String oauthTokenTempSecret = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_response);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(LOG_ID, "Starting login response activity, getting URI");

        if (oauthTokenTempSecret == null) {
            // We don't have the secret to perform the final authorization process, so there's no reason to continue
            Log.e(LOG_ID, "Temp Token Secret missing, can't perform authorization.");
            return;
            // TODO: We'll hopefully never reach this point, but show dialog and go back to the login screen?
        }

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith("authorized://")) {
            if (uri.toString().equals("authorized://itworked.com#_=_")) {
                // The user denied the application access
                Log.e(LOG_ID, "The user denied the app access");
                return;
                // TODO: Show a dialog and go back to the login screen?
            }
            String oauthToken = uri.getQueryParameter("oauth_token");
            String oauthVerifier = uri.getQueryParameter("oauth_verifier");

            Log.v(LOG_ID, "Generating client");
            TumblrAuthClient client = ServiceGenerator.createService(TumblrAuthClient.class, oauthToken, oauthTokenTempSecret);

            final LoginResponseActivity context = this; // Required for using "this" in the callback

            Log.d(LOG_ID, "Starting call to get access token");
            Call<ResponseBody> call = client.getAccessToken(oauthVerifier);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    int code = response.code();

                    switch (code) {
                        case 200:
                            try {
                                Log.d(LOG_ID, "Received 200 response for access token, parsing and returning to main page");
                                String responseBody = response.body().string();
                                Uri toParse = Uri.parse("authorized://itworked.com?" + responseBody);
                                String oauthToken = toParse.getQueryParameter("oauth_token");
                                String oauthTokenSecret = toParse.getQueryParameter("oauth_token_secret");
                                OauthCredentials.setTokenKey(oauthToken);
                                OauthCredentials.setTokenSecret(oauthTokenSecret);
                                OauthCredentials.saveTokenKeySecret(getBaseContext());

                                // Navigate back to the main screen now that we're fully authorized
                                Log.d(LOG_ID, "Returning to main activity");
                                Intent i = new Intent(context, MainActivity.class);
                                startActivity(i);
                                finish();
                            } catch (IOException e) {
                                Log.e(LOG_ID, "Error parsing from access token 200 response", e);
                                return;
                            }
                            break;
                        case 400:
                        case 401:
                        case 404:
                            Log.e(LOG_ID, "Error " + code + " on token request");
                            return;
                        default:
                            Log.e(LOG_ID, "Unexpected response code " + code + " on token request");
                            return;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(LOG_ID, "Response failure", t);
                }
            });
        } else {
            Log.e(LOG_ID, "Received either an invalid URI or no URI");
        }
    }
}
