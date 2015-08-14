package org.itri.tomato.activities;

import android.annotation.TargetApi;
import android.content.Intent;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookAuthActivity extends AppCompatActivity {

    /**
     * For Facebook API
     */
    private CallbackManager callbackManager;
    private static String fb_access_token = "";
    Toast toast;
    SharedPreferences sharedPreferences;
    ProfileTracker profileTracker;
    String id;
    String name;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if (sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        /**
         * init Facebook API
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_auth);
        callbackManager = CallbackManager.Factory.create();
        Button loginButton = (Button) findViewById(R.id.login_button_fb);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(FacebookAuthActivity.this, Arrays.asList("public_profile", "email"));
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fb_access_token = loginResult.getAccessToken().getToken();
                profileTracker.startTracking();
            }

            @Override
            public void onCancel() {
                toast.setText("Canceled");
                toast.show();
            }

            @Override
            public void onError(FacebookException e) {
                toast.setText("Error");
                toast.show();
            }
        });
        profileTracker = new ProfileTracker() {
            @Override
            public void startTracking() {
                super.startTracking();
                if (!fb_access_token.equals("")) {
                    name = Profile.getCurrentProfile().getName();
                    id = Profile.getCurrentProfile().getId();
                    Log.w("facebook account", name + "," + id);
                    new Thread(sentToken).start();
                }
            }

            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                name = currentProfile.getName();
                id = currentProfile.getId();
                new Thread(sentToken).start();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    Runnable sentToken = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "PostConnectorTokenById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("serviceToken", fb_access_token);
                para.put("serviceKey", "");
                para.put("serviceUserName", name);
                para.put("serviceId", id);
                para.put("connectorId", "1");
            } catch (JSONException e) {
                Log.w("FACEBOOK", e.toString());
            }
            Log.w("FACEBOOK", para.toString());
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, true);
            if (Utilities.getResponseCode().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FacebookAuthActivity.this, "OAuth finished", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

}
