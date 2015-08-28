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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
    String id;
    String name;
    Button loginButton;
    TextView status;
    boolean isEnable;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
         * init Facebook API
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_auth);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (Button) findViewById(R.id.login_button_fb);
        LoginManager.getInstance().logOut();
        status = (TextView) findViewById(R.id.fbStatus);
        if (isEnable) {
            status.setText("Status:                                 Enable");//temp use
            loginButton.setText("Disable");
        } else {
            status.setText("Status:                                 Disable");//temp use
            loginButton.setText("Enable");
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEnable) {
                    LoginManager.getInstance().logInWithPublishPermissions(FacebookAuthActivity.this, Arrays.asList("publish_actions"));
                } else {
                    new Thread(deleteToken).start();
                }
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fb_access_token = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    //當RESPONSE回來的時候
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //讀出姓名 ID FB個人頁面連結
                        name = object.optString("name");
                        id = object.optString("id");
                        new Thread(sentToken).start();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAsync();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    Runnable sentToken = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "PostUserConnectorTokenById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("serviceToken", fb_access_token);
                para.put("serviceKey", "");
                para.put("serviceUserName", name);
                para.put("serviceUid", id);
                para.put("connectorId", "1");
            } catch (JSONException e) {
                Log.w("FACEBOOK", e.toString());
            }
            Log.w("FACEBOOK", para.toString());
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, FacebookAuthActivity.this, true);
            if (Utilities.getResponseCode().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isEnable = true;
                        loginButton.setText("Disable");
                        setResult(RESULT_OK);
                        FacebookAuthActivity.this.finish();
                    }
                });
            } else {
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
                para.put("connectorId", "1");
            } catch (JSONException e) {
            }
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, FacebookAuthActivity.this, true);
            if (Utilities.getResponseCode().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginButton.setText("Enable");
                        toast.setText("Disabled");
                        FacebookAuthActivity.this.finish();
                        toast.show();
                    }
                });
            } else {
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
