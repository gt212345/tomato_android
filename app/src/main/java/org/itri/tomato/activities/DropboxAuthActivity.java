package org.itri.tomato.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

public class DropboxAuthActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * For DropBox API
     */
    private final static String APP_KEY = "v6muq2c27l4zfsi";
    private final static String APP_SECRET = "dgs7aafbchna97t";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private static String db_access_token;
    SharedPreferences sharedPreferences;
    Button loginButtonDb;
    boolean isEnable;
    Toast toast;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_auth);
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
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        loginButtonDb = (Button) findViewById(R.id.login_button_db);
        if (isEnable) {
            loginButtonDb.setText("Disable");
        } else {
            loginButtonDb.setText("Enable");
        }
        loginButtonDb.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(!isEnable) {
            mDBApi.getSession().startOAuth2Authentication(DropboxAuthActivity.this);
        } else {
            toast.setText("Not Support Yet");
            toast.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();
                db_access_token = mDBApi.getSession().getOAuth2AccessToken();
                new Thread(sentToken).start();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    Runnable sentToken = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "PostConnectorTokenById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("serviceToken", db_access_token);
                para.put("serviceKey", "");
                para.put("serviceUserName", "");
                para.put("serviceId", "");
                para.put("connectorId", "2");
            } catch (JSONException e) {
            }
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, true);
            if (Utilities.getResponseCode().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginButtonDb.setText("Disable");
                    }
                });
            }
        }
    };

}
