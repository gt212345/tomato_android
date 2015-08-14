package org.itri.tomato.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.itri.tomato.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegistIntentService";
    private static final String[] TOPICS = {"global"};
    private static final String TYPE = "android";


    SharedPreferences sharedPreferences;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(Utilities.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                sendRegistrationToServer(token);

                // Subscribe to topic channels
                subscribeTopics(token);

                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Utilities.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Utilities.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        String UID = sharedPreferences.getString(Utilities.USER_ID, null);
        String TOKEN = sharedPreferences.getString(Utilities.USER_TOKEN, null);
        if (!sharedPreferences.getBoolean(Utilities.SENT_TOKEN_TO_SERVER, false)) {
            String Action = Utilities.ACTION + "PostGCMDataByDevice";
            String Params = Utilities.PARAMS + "{\"uid\":\"" + UID + "\",\"token\":\"" + TOKEN + "\",\"regId\":\"" + token + "\",\"type\":\"" + Utilities.TYPE + "\"}";
            JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, getApplicationContext(), true);
            try {
                JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                if (jsonObjectTmp.getString("status").equals("true")) {
                    sharedPreferences.edit().putBoolean(Utilities.SENT_TOKEN_TO_SERVER, true).apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setUseCaches(false);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("uid", "wuheiru.5203@gmail.com");
//                httpURLConnection.setRequestProperty("token", "123");
//                httpURLConnection.setRequestProperty("reg_id", token);
//                httpURLConnection.setRequestProperty("type", "android");
//                httpURLConnection.connect();
        }
    }

    private void unRegistrationToServer(String token) {
        sharedPreferences.edit().putBoolean(Utilities.SENT_TOKEN_TO_SERVER, false).apply();
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
