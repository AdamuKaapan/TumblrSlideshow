package ciat.com.tumblrslideshow;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * This class handles the storage and loading of various OAuth keys.
 *
 * This class functions off of one file, res/raw/keys.txt, which stores the OAuth client key and
 * secret. This class also uses the SharedPreferences to store the token key/secret once authorized.
 */
public final class OauthCredentials {
    public static final String LOG_ID = "TS.OauthCredentials";
    public static final String PREFS_NAME = "TumblrSlideshow_Prefs";

    private static String clientKey, clientSecret;
    private static String tokenKey, tokenSecret;

    /**
     * Loads the client key and secret from res/raw/keys.txt
     * @param c The Android Context
     * @throws IOException Error loading res/raw/keys.txt
     */
    public static final void loadClientKeySecret(Context c) throws IOException {
        // Load the client key and secret, which should always be here.
        InputStream file = c.getResources().openRawResource(R.raw.keys);
        if (file == null) {
            throw new IOException("Keys file not found: could not load keys.");
        }
        Scanner scan = new Scanner(file);
        Log.d(LOG_ID, "Keys file opened for reading.");

        // Load client key
        if (!scan.hasNextLine()) {
            // No lines in the file
            throw new IOException("Keys file invalidly formatted: no lines to read.");
        }
        clientKey = scan.nextLine();
        Log.v(LOG_ID, "Client key loaded");

        // Load client secret
        if (!scan.hasNextLine()) {
            // No lines in the file
            throw new IOException("Keys file invalidly formatted: only 1 line to read.");
        }

        clientSecret = scan.nextLine();
        Log.i(LOG_ID, "OAuth client key/secret loaded.");
    }

    /**
     * Loads the token key and secret from Android Shared Preferences
     * @param c The Android Context
     * @return Whether the preferences were loaded successfully (do both tokens exist)
     */
    public static final boolean loadTokenKeySecret(Context c) {
        tokenKey = c.getSharedPreferences(PREFS_NAME, 0).getString("tokenKey", null);
        tokenSecret = c.getSharedPreferences(PREFS_NAME, 0).getString("tokenSecret", null);

        // One of the tokens wasn't loaded successfully: set them both to null to prevent any weird
        // accidental behavior.
        if (tokenKey == null || tokenSecret == null) {
            tokenKey = null;
            tokenSecret = null;
            return false;
        }

        // Both tokens loaded successfully
        return true;
    }

    /**
     * Saves the token key and secret to Android Shared Preferences
     * @param c The Android Context
     */
    public static void saveTokenKeySecret(Context c) {
        c.getSharedPreferences(PREFS_NAME, 0).edit()
                .putString("tokenKey", tokenKey)
                .putString("tokenSecret", tokenSecret)
                .commit();
    }

    public static String getClientKey() {
        return clientKey;
    }

    public static String getClientSecret() {
        return clientSecret;
    }

    public static String getTokenKey() {
        return tokenKey;
    }

    public static void setTokenKey(String tokenKey) {
        OauthCredentials.tokenKey = tokenKey;
    }

    public static String getTokenSecret() {
        return tokenSecret;
    }

    public static void setTokenSecret(String tokenSecret) {
        OauthCredentials.tokenSecret = tokenSecret;
    }
}
