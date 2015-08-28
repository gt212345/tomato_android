package org.itri.tomato.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.itri.tomato.fragments.DialogFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DropboxAuthActivity extends AppCompatActivity implements View.OnClickListener, DataRetrieveListener {

    /**
     * OAuth 1.0 was officially deprecated on April 20, 2012, and is no longer supported.
     * Why the fuck do we still use this in 2015?
     */
    private final static String APP_KEY = "ta5mth4nhj2qckg";
    private final static String APP_SECRET = "f0j6ij3f4e4uxsu";
    private final static String REQUEST_TOKEN_URL = "https://api.dropbox.com/1/oauth/request_token";
    private final static String AUTH_URL = "https://www.dropbox.com/1/oauth/authorize?";
    private final static String ACCESS_TOKEN = "https://api.dropbox.com/1/oauth/access_token";

    private static final int DROP_BOX = 6;
    String oauth = "oauth_token=";
    String oauthCallback = "&oauth_callback=";
    String token;
    String secret;
    String accessSecrert;
    String accessToken;

    String uid;
    /**
     * For DropBox API, no longer in used.
     */
    DataRetrieveListener listener;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private static String db_access_token;
    SharedPreferences sharedPreferences;
    Button loginButtonDb;
    boolean isEnable;
    Toast toast;
    TextView status;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_auth);
        status = (TextView) findViewById(R.id.dbStatus);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        isEnable = getIntent().getBooleanExtra("enable", false);
        if (sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        /**
         * init DropBox API
         */
//        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
//        AndroidAuthSession session = new AndroidAuthSession(appKeys);
//        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        loginButtonDb = (Button) findViewById(R.id.login_button_db);
        if (isEnable) {
            status.setText("Status:                                 Enable");//temp use
            loginButtonDb.setText("Disable");
        } else {
            status.setText("Status:                                 Disable");//temp use
            loginButtonDb.setText("Enable");
        }
        loginButtonDb.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!isEnable) {
//            mDBApi.getSession().startOAuth2Authentication(DropboxAuthActivity.this);
            new Thread(requestToken).start();
        } else {
            new Thread(deleteToken).start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mDBApi.getSession().authenticationSuccessful()) {
//            try {
//                 Required to complete auth, sets the access token on the session
//                mDBApi.getSession().finishAuthentication();
//                db_access_token = mDBApi.getSession().getOAuth2AccessToken();
//                new Thread(sentToken).start();
//            } catch (IllegalStateException e) {
//                Log.i("DbAuthLog", "Error authenticating", e);
//            }
//        }
    }

    private void userAuth() {
        String[] parts = {token, secret};
        DialogFragment dialogFragment = DialogFragment.newInstance(parts, DROP_BOX, null, 0, 0);
        dialogFragment.show(getFragmentManager(), "");
    }

    Runnable requestToken = new Runnable() {
        @Override
        public void run() {
            try {
                URL request = new URL(REQUEST_TOKEN_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) request.openConnection();
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Authorization", "OAuth oauth_version=\"1.0\", oauth_signature_method=\"PLAINTEXT\",oauth_consumer_key=\"" + APP_KEY + "\", oauth_signature=\"" + APP_SECRET + "&\"");
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    Log.i("Response Code", httpURLConnection.getResponseCode() + "");
                } else {
                    Log.w("Response Code", httpURLConnection.getResponseCode() + "");
                }
                InputStream input = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                String[] parts = result.toString().split("&");
                secret = (parts[0].split("="))[1];
                token = (parts[1].split("="))[1];
                userAuth();
            } catch (MalformedURLException e) {
                Log.w("Mal", e.toString());
            } catch (IOException e) {
                Log.w("IO", e.toString());
            }
        }
    };

    Runnable authToken = new Runnable() {
        @Override
        public void run() {
            try {
                URL request = new URL(ACCESS_TOKEN);
                HttpURLConnection httpURLConnection = (HttpURLConnection) request.openConnection();
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Authorization", "OAuth oauth_version=\"1.0\", oauth_signature_method=\"PLAINTEXT\", oauth_consumer_key=\"" + APP_KEY + "\", oauth_token=\"" + token + "\",oauth_signature=\"" + APP_SECRET + "&" + secret + "\"");
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    Log.i("Response Code", httpURLConnection.getResponseCode() + "");
                } else {
                    Log.w("Response Code", httpURLConnection.getResponseCode() + "");
                }
                InputStream input = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                String[] parts = result.toString().split("&");
                accessSecrert = (parts[0].split("="))[1];
                accessToken = (parts[1].split("="))[1];
                uid = (parts[2].split("="))[1];
                Log.i("Auth token", result.toString());
                new Thread(sentToken).start();
            } catch (MalformedURLException e) {
                Log.w("Mal", e.toString());
            } catch (IOException e) {
                Log.w("IO", e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Auth failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    Runnable sentToken = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "PostUserConnectorTokenById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("serviceToken", accessToken);
                para.put("serviceKey", accessSecrert);
                para.put("serviceUserName", "");
                para.put("serviceUid", uid);
                para.put("connectorId", "2");
            } catch (JSONException e) {
            }
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, DropboxAuthActivity.this, true);
            if (Utilities.getResponseCode().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isEnable = true;
                        setResult(RESULT_OK);
                        DropboxAuthActivity.this.finish();
                        loginButtonDb.setText("Disable");
                    }
                });
            }
        }
    };

    Runnable deleteToken = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "DelUserConnectorById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("connectorId", "2");
            } catch (JSONException e) {
            }
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, DropboxAuthActivity.this, true);
            if (Utilities.getResponseCode().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginButtonDb.setText("Enable");
                        toast.setText("Disabled");
                        toast.show();
                        DropboxAuthActivity.this.finish();
                    }
                });
            }
        }
    };

    @Override
    public void onFinish() {
        new Thread(authToken).start();
    }

}
