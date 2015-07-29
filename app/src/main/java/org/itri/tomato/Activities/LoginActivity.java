package org.itri.tomato.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

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
        Button create = (Button) findViewById(R.id.create);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount = editAccount.getText().toString();
                UserPassword = editPass.getText().toString();
                progressDialog = ProgressDialog.show(LoginActivity.this, "Logging in", "please wait......", true);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                    }
                });
                loginThread = new Thread(loginRunnable);
                loginThread.start();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount = editAccount.getText().toString();
                UserPassword = editPass.getText().toString();
                progressDialog = ProgressDialog.show(LoginActivity.this, "Creating Account", "please wait......", true);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                    }
                });
                loginThread = new Thread(createRunnable);
                loginThread.start();
            }
        });
        if(sharedPreferences.getBoolean(Utilities.HAS_ACCOUNT, false)) {
            UserAccount = sharedPreferences.getString(Utilities.USER_ACCOUNT,null);
            UserPassword = sharedPreferences.getString(Utilities.USER_PASSWORD, null);
            progressDialog = ProgressDialog.show(LoginActivity.this, "Logging in", "please wait......", true);
            loginThread = new Thread(loginRunnable);
            loginThread.start();
        }
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email input
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
            if(isEmailValid(UserAccount)) {
                String Action = Utilities.ACTION + "Login";
                String Params = Utilities.PARAMS + "{\"email\":\"" + UserAccount + "\",\"pass\":\"" + UserPassword + "\"}";
                jsonObject = Utilities.API_CONNECT(Action, Params, true);
                if (Utilities.getResponseCode() == 200) {
                    if (jsonObject != null) {
                        try {
                            JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                            if (jsonObjectTmp.getString("status").equals("200")) {
                                if (!sharedPreferences.getBoolean(Utilities.HAS_ACCOUNT, false)) {
                                    sharedPreferences.edit().putString(Utilities.USER_ACCOUNT, UserAccount).apply();
                                    sharedPreferences.edit().putString(Utilities.USER_PASSWORD,UserPassword).apply();
                                    sharedPreferences.edit().putBoolean(Utilities.HAS_ACCOUNT, true).apply();
                                }
                                Log.i("uid", jsonObjectTmp.get("uid").toString());
//                                Log.i("token", jsonObjectTmp.get("token").toString());
                                sharedPreferences.edit().putString(Utilities.USER_ID, jsonObjectTmp.get("uid").toString()).apply();
                                sharedPreferences.edit().putString(Utilities.USER_TOKEN, jsonObjectTmp.get("token").toString()).apply();
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MarketActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } else {
                                progressDialog.cancel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                progressDialog.cancel();
            }
        }
    };

    Runnable createRunnable = new Runnable() {
        @Override
        public void run() {
            if(isEmailValid(UserAccount)) {
                String Action = Utilities.ACTION + "CreateAccount";
                String Params = Utilities.PARAMS + "{\"email\":\"" + UserAccount + "\",\"pass\":\"" + UserPassword + "\"}";
                jsonObject = Utilities.API_CONNECT(Action, Params, true);
                if (Utilities.getResponseCode() == 200) {
                    if (jsonObject != null) {
                        try {
                            JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                            if (jsonObjectTmp.getString("status").equals("200")) {
                                if (!sharedPreferences.getBoolean(Utilities.HAS_ACCOUNT, false)) {
                                    sharedPreferences.edit().putString(Utilities.USER_ACCOUNT, UserAccount).apply();
                                    sharedPreferences.edit().putString(Utilities.USER_PASSWORD,UserPassword).apply();
                                    sharedPreferences.edit().putBoolean(Utilities.HAS_ACCOUNT, true).apply();
                                }
                                Log.i("uid", jsonObjectTmp.get("uid").toString());
//                                Log.i("token", jsonObjectTmp.get("token").toString());
                                sharedPreferences.edit().putString(Utilities.USER_ID, jsonObjectTmp.get("uid").toString()).apply();
                                sharedPreferences.edit().putString(Utilities.USER_TOKEN, jsonObjectTmp.get("token").toString()).apply();
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MarketActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } else {
                                progressDialog.cancel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                progressDialog.cancel();
            }
        }
    };
}
