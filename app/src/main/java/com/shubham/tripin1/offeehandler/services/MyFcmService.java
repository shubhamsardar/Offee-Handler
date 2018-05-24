package com.shubham.tripin1.offeehandler.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shubham.tripin1.offeehandler.MainActivity;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;
import com.shubham.tripin1.offeehandler.R;

/**
 * Created by Tripin1 on 7/4/2017.
 */

public class MyFcmService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private DatabaseReference ref;
    private SharedPrefManager mSharedpPref;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedpPref = new SharedPrefManager(getApplicationContext());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

          if (remoteMessage.getData().size() > 0) {
            android.util.Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String type = remoteMessage.getData().get("type");
            android.util.Log.d(TAG, "type" + type);
            if(type != null) {
                if (type.equals("1")) {
                    String username = remoteMessage.getData().get("mUserName");
                    android.util.Log.d(TAG, "mUserName: " + username);
                    displayNewOrderNotification(username);}}}


        super.onMessageReceived(remoteMessage);
    }

      private void displayNewOrderNotification(String username) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_hot)
                        .setContentTitle("New Order!")
                        .setContentText("From : "+ username)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

                    android.util.Log.d(TAG, "new order");


    }



}
