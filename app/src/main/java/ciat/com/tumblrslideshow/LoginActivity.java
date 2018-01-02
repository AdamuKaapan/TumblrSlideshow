package ciat.com.tumblrslideshow;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.URI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String LOG_ID = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonPress(View view) {
        TumblrAuthClient authClient = ServiceGenerator.createService(TumblrAuthClient.class);
        Call<ResponseBody> call = authClient.getTokenAndSecret();

        Log.d(LOG_ID, "Starting call to get token and secret");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();

                switch (code) {
                    case 200:
                        try {
                            Log.d(LOG_ID, "Received 200 response for token and secret, parsing and navigating to authorization webpage");
                            String responseBody = response.body().string();
                            Uri toParse = Uri.parse("https://www.tumblr.com?" + responseBody);
                            String tokenKey = toParse.getQueryParameter("oauth_token");
                            String tokenSecret = toParse.getQueryParameter("oauth_token_secret");
                            OauthCredentials.setTokenKey(tokenKey);
                            LoginResponseActivity.oauthTokenTempSecret = tokenSecret;

                            Log.v(LOG_ID, "URI parsing successful, navigating to webpage");
                            Intent i = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://www.tumblr.com/oauth/authorize?oauth_token=" + tokenKey));
                            startActivity(i);
                        } catch (IOException e) {
                            Log.e(LOG_ID, "Error parsing from token and secret 200 response", e);
                            return;
                        }
                        break;
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
    }
}
