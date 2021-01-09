package com.plexus.notifications.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.plexus.R;
import com.plexus.main.activity.MainActivity;
import com.plexus.messaging.activity.MessageUserActivity;
import com.plexus.model.Token;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationService extends FirebaseMessagingService {

    Context context = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("type").equals("follower")){
            if (remoteMessage.getData().size() > 0) {
                sendFollowerNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("click_action"), remoteMessage.getData().get("from_user_id"), getBitmapFromURL(remoteMessage.getData().get("imageurl")));
            }
        } else if (remoteMessage.getData().get("type").equals("like")) {
            if (remoteMessage.getData().size() > 0) {
                sendLikeNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("postid"), remoteMessage.getData().get("click_action"), getBitmapFromURL(remoteMessage.getData().get("imageurl")));
            }
        } else if (remoteMessage.getData().get("type").equals("comment")) {
            if (remoteMessage.getData().size() > 0) {
                sendCommentNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("postid"), remoteMessage.getData().get("click_action"), getBitmapFromURL(remoteMessage.getData().get("imageurl")));
            }
        } else if (remoteMessage.getData().get("type").equals("shared")) {
            if (remoteMessage.getData().size() > 0) {
                sendShareNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("postid"), remoteMessage.getData().get("click_action"), getBitmapFromURL(remoteMessage.getData().get("imageurl")));
            }
        }
    }

    private void sendLikeNotification(String body, String postid, String click_action, Bitmap imageurl) {
        int notifyID = 2;

        Intent intent = new Intent(click_action);
        intent.putExtra("postid", postid);

        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName() + notifyID;// The id of the channel.

        CharSequence name = "Like Notification";// The user-visible name of the channel.
        String description = "";

        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        long[] mVibratePattern = new long[]{0, 400, 200, 400};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Plexus")
                .setContentText(body)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(imageurl)
                .setVibrate(mVibratePattern)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.YELLOW, 500, 500)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            notificationManager.createNotificationChannel(mChannel);
        }
        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }
    }

    private void sendFollowerNotification(String body, String click_action, String from_user_id, Bitmap imageurl) {

        int notifyID = 1;

        Intent intent = new Intent(click_action);
        intent.putExtra("userid", from_user_id);

        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName() + notifyID;// The id of the channel.

        CharSequence name = "Follower Notification";// The user-visible name of the channel.

        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        long[] mVibratePattern = new long[]{0, 400, 200, 400};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Plexus")
                .setContentText(body)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(imageurl)
                .setVibrate(mVibratePattern)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.YELLOW, 500, 500)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }
    }

    private void sendShareNotification(String body, String postid, String click_action, Bitmap imageurl) {
        int notifyID = 2;

        Intent intent = new Intent(click_action);
        intent.putExtra("postid", postid);

        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName() + notifyID;// The id of the channel.

        CharSequence name = "Like Notification";// The user-visible name of the channel.
        String description = "";

        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        long[] mVibratePattern = new long[]{0, 400, 200, 400};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Plexus")
                .setContentText(body)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(imageurl)
                .setVibrate(mVibratePattern)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.YELLOW, 500, 500)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            notificationManager.createNotificationChannel(mChannel);
        }
        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }
    }

    private void sendCommentNotification(String body, String postid, String click_action, Bitmap imageurl) {
        int notifyID = 2;

        Intent intent = new Intent(click_action);
        intent.putExtra("postid", postid);

        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName() + notifyID;// The id of the channel.

        CharSequence name = "Like Notification";// The user-visible name of the channel.
        String description = "";

        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        long[] mVibratePattern = new long[]{0, 400, 200, 400};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Plexus")
                .setContentText(body)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(imageurl)
                .setVibrate(mVibratePattern)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.YELLOW, 500, 500)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            notificationManager.createNotificationChannel(mChannel);
        }
        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap loadRoundUserAvatar(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.BLACK);
        canvas.drawOval(new RectF(rect), paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            updateToken(s);
        }
    }

    private void updateToken(String s){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(s);
        databaseReference.child(firebaseUser.getUid()).setValue(token);

    }
}