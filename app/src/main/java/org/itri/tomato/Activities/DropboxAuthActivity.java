package org.itri.tomato.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import org.itri.tomato.R;

public class DropboxAuthActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * For DropBox API
     */
    private final static String APP_KEY = "v6muq2c27l4zfsi";
    private final static String APP_SECRET = "dgs7aafbchna97t";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private static String db_access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_auth);
        /**
         * init DropBox API
         */
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        Button loginButtonDb = (Button) findViewById(R.id.login_button_db);
        loginButtonDb.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mDBApi.getSession().startOAuth2Authentication(DropboxAuthActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                db_access_token = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

}
