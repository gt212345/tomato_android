package org.itri.tomato.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.itri.tomato.R;

public class FacebookAuthActivity extends AppCompatActivity {

    /**
     * For Facebook API
     */
    private CallbackManager callbackManager;
    private static String fb_access_token;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        /**
         * init Facebook API
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_facebook_auth);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_fb);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                toast.setText("Success");
                toast.show();
                fb_access_token = loginResult.getAccessToken().getToken();
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

}
