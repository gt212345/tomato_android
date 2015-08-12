package org.itri.tomato.services;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.itri.tomato.Utilities;

public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().remove(Utilities.SENT_TOKEN_TO_SERVER);
        sharedPreferences.edit().putBoolean(Utilities.SENT_TOKEN_TO_SERVER, false).apply();
        startService(intent);
    }


}
