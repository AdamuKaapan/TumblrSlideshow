package ciat.com.tumblrslideshow;

import android.util.Log;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Class based on example from here:
 * https://futurestud.io/tutorials/retrofit-2-creating-a-sustainable-android-client
 *
 * Modified to function for my specific needs.
 */
public class ServiceGenerator {

    private static final String BASE_URL = "https://api.tumblr.com/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static Interceptor logging = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.i("ServiceGenerator", "Request URI: " + request.url().toString());
            Log.i("ServiceGenerator", "Request Auth: " + request.header("Authorization"));
            Response response = chain.proceed(request);
            return response;
        }
    };

    private static OkHttpOAuthConsumer consumer = null;
    private static SigningInterceptor signer = null;

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return createService(serviceClass, OauthCredentials.getTokenKey(), OauthCredentials.getTokenSecret());
    }

    public static <S> S createService(
            Class<S> serviceClass, String oauthToken, String oauthTokenSecret) {

        consumer = new OkHttpOAuthConsumer(OauthCredentials.getClientKey(), OauthCredentials.getClientSecret());
        signer = new SigningInterceptor(consumer);
        if (oauthToken != null && oauthTokenSecret != null)
            consumer.setTokenWithSecret(oauthToken, oauthTokenSecret);

        if (!httpClient.interceptors().contains(signer)) {
            httpClient.addInterceptor(signer);
            builder.client(httpClient.build());
            //retrofit = builder.build();
        }

        // Add the logging interceptor if it hasn't been added yet
        // (rebuild retrofit once we've added it)
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            //retrofit = builder.build();
        }

        retrofit = builder.build(); // TODO: This can be optimized to only run when necessary
        return retrofit.create(serviceClass);
    }
}
