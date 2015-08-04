package org.itri.tomato.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.itri.tomato.Activities.GCMTestActivity;
import org.itri.tomato.Activities.LoginActivity;
import org.itri.tomato.Activities.MarketActivity;
import org.itri.tomato.R;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "GcmListenerService";
    static final int NOTIFY_ID = 0;
    public static int numMessage = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, from);
    }

    private void sendNotification(String message, String from) {
        Intent intent = new Intent(this, GCMTestActivity.class);
        numMessage = 0;
        intent.putExtra("from", from);
        intent.putExtra("message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_school_white_48dp)
                .setContentTitle("Tomato Message")
                .setContentText(message)
                .setNumber(++numMessage)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFY_ID /* ID of notification */, notificationBuilder.build());
    }
}
