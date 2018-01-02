package ciat.com.tumblrslideshow;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TumblrAuthClient {
    @POST("https://www.tumblr.com/oauth/request_token")
    Call<ResponseBody> getTokenAndSecret();

    @POST("https://www.tumblr.com/oauth/access_token")
    Call<ResponseBody> getAccessToken(@Query("oauth_verifier") String oauthVerifier);
}
