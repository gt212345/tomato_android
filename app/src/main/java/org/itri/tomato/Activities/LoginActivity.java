package org.itri.tomato.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static String UserAccount;
    private static String UserPassword;
    JSONObject jsonObject;
    EditText editAccount;
    EditText editPass;
    SharedPreferences sharedPreferences;
    Thread loginThread;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editAccount = (EditText) findViewById(R.id.editAccount);
        editPass = (EditText) findViewById(R.id.editPass);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginThread = new Thread(loginRunnable);
                progressDialog = ProgressDialog.show(LoginActivity.this,"Logging in", "please wait......", true);
                loginThread.start();
            }

        });
//        Intent intent = new Intent();
//        intent.setClass(LoginActivity.this, MarketActivity.class);
//        startActivity(intent);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            if(isEmailValid(editAccount.getText().toString())) {
                UserAccount = editAccount.getText().toString();
                UserPassword = editPass.getText().toString();
                String Action = Utilities.ACTION + "Login";
                String Params = Utilities.PARAMS + "{\"email\":\"" + UserAccount + "\",\"pass\":\"" + UserPassword + "\"}";
                jsonObject = Utilities.API_CONNECT(Action, Params, true);
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                        Log.i("uid", jsonObjectTmp.get("uid").toString());
                        Log.i("token", jsonObjectTmp.get("token").toString());
                    sharedPreferences.edit().putString(Utilities.USER_ID,jsonObjectTmp.get("uid").toString());
                    sharedPreferences.edit().putString(Utilities.USER_TOKEN,jsonObjectTmp.get("token").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(LoginActivity.this, "Account format invalid", Toast.LENGTH_SHORT).show();
            }
            progressDialog.cancel();
        }
    };
}
