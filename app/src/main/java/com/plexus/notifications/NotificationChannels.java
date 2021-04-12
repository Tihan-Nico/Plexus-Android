package com.plexus.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.BuildConfig;
import com.plexus.R;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.ServiceUtil;
import com.plexus.utils.logging.Log;

import java.util.Arrays;

public class NotificationChannels {

    private static final String TAG = Log.tag(NotificationChannels.class);

    private static class Version {
        static final int MESSAGES_CATEGORY   = 2;
        static final int CALLS_PRIORITY_BUMP = 3;
    }

    private static final int VERSION = 3;

    private static final String CATEGORY_PLEXUS = "plexus";

    public static final String LOCKED_STATUS = "locked_status_v2";
    public static final String PLEXUS_MESSAGE_REQUEST = "message_request";
    public static final String PLEXUS_MESSAGES = "messages";
    public static final String LOOKOUT_MESSAGES = "lookout_messages";
    public static final String LOOKOUT_LAST_LOCATION = "lookout_last_location";
    public static final String UPLOADS = "uploads";
    public static final String UPLOAD_FAILURE = "upload_failure";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String COMMENTS_LIKES = "comments_likes";
    public static final String NEW_FOLLOWERS = "new_followers";
    public static final String OTHER = "other_v2";
    public static final String APP_UPDATES = "app_updates";

    public static boolean supported() {
        return Build.VERSION.SDK_INT >= 26;
    }

    /**
     * Ensures all of the notification channels are created. No harm in repeat calls. Call is safely
     * ignored for API < 26.
     */
    public static synchronized void create(@NonNull Context context) {
        if (!supported()) {
            return;
        }

        NotificationManager notificationManager = ServiceUtil.getNotificationManager(context);

        int oldVersion = PlexusPreferences.getNotificationChannelVersion(context);
        if (oldVersion != VERSION) {
            onUpgrade(notificationManager, oldVersion, VERSION);
            PlexusPreferences.setNotificationChannelVersion(context, VERSION);
        }

        onCreate(context, notificationManager);
    }

    /**
     * Navigates the user to the system settings for the desired notification channel.
     */
    public static void openChannelSettings(@NonNull Context context, @NonNull String channelId) {
        if (!supported()) {
            return;
        }

        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
    }

    @TargetApi(26)
    private static void onCreate(@NonNull Context context, @NonNull NotificationManager notificationManager) {
        NotificationChannelGroup messagesGroup = new NotificationChannelGroup(CATEGORY_PLEXUS, context.getResources().getString(R.string.NotificationChannel_Plexus));
        notificationManager.createNotificationChannelGroup(messagesGroup);

        NotificationChannel message_request = new NotificationChannel(PLEXUS_MESSAGE_REQUEST,context.getString(R.string.NotificationChannel_PlexusMessageRequest), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel lookout_messages = new NotificationChannel(LOOKOUT_MESSAGES,context.getString(R.string.NotificationChannel_PlexusMessages), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel messages = new NotificationChannel(PLEXUS_MESSAGES,context.getString(R.string.NotificationChannel_PlexusMessages), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel uploads = new NotificationChannel(UPLOADS,context.getString(R.string.NotificationChannel_Uploads), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel upload_failure = new NotificationChannel(UPLOAD_FAILURE,context.getString(R.string.NotificationChannel_UploadFailure), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel likes = new NotificationChannel(LIKES,context.getString(R.string.NotificationChannel_Likes), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel comments = new NotificationChannel(COMMENTS,context.getString(R.string.NotificationChannel_Comments), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel comments_likes = new NotificationChannel(COMMENTS_LIKES,context.getString(R.string.NotificationChannel_CommentsLikes), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel new_followers = new NotificationChannel(NEW_FOLLOWERS,context.getString(R.string.NotificationChannel_NewFollowers), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel other = new NotificationChannel(OTHER, context.getString(R.string.NotificationChannel_other), NotificationManager.IMPORTANCE_LOW);
        NotificationChannel lockedStatus = new NotificationChannel(LOCKED_STATUS, context.getString(R.string.NotificationChannel_locked_status), NotificationManager.IMPORTANCE_LOW);

        messages.setGroup(CATEGORY_PLEXUS);
        messages.enableVibration(PlexusPreferences.isNotificationVibrateEnabled(context));
        messages.setSound(PlexusPreferences.getNotificationRingtone(context), getRingtoneAudioAttributes());
        setLedPreference(messages, PlexusPreferences.getNotificationLedColor(context));

        lookout_messages.setGroup(CATEGORY_PLEXUS);
        lookout_messages.enableVibration(PlexusPreferences.isNotificationVibrateEnabled(context));
        lookout_messages.setSound(PlexusPreferences.getNotificationRingtone(context), getRingtoneAudioAttributes());
        setLedPreference(lookout_messages, PlexusPreferences.getNotificationLedColor(context));

        likes.setGroup(CATEGORY_PLEXUS);
        likes.enableVibration(PlexusPreferences.isNotificationVibrateEnabled(context));
        likes.setSound(PlexusPreferences.getNotificationRingtone(context), getRingtoneAudioAttributes());
        setLedPreference(likes, PlexusPreferences.getNotificationLedColor(context));

        comments_likes.setGroup(CATEGORY_PLEXUS);
        comments_likes.enableVibration(PlexusPreferences.isNotificationVibrateEnabled(context));
        comments_likes.setSound(PlexusPreferences.getNotificationRingtone(context), getRingtoneAudioAttributes());
        setLedPreference(comments_likes, PlexusPreferences.getNotificationLedColor(context));

        comments.setGroup(CATEGORY_PLEXUS);
        comments.enableVibration(PlexusPreferences.isNotificationVibrateEnabled(context));
        comments.setSound(PlexusPreferences.getNotificationRingtone(context), getRingtoneAudioAttributes());
        setLedPreference(comments, PlexusPreferences.getNotificationLedColor(context));

        message_request.setShowBadge(false);
        uploads.setShowBadge(false);
        upload_failure.setShowBadge(false);
        new_followers.setShowBadge(false);

        notificationManager.createNotificationChannels(Arrays.asList(message_request, messages, lookout_messages, uploads, upload_failure, likes, comments, comments_likes, new_followers));

        if (BuildConfig.PLAY_STORE_DISABLED) {
            NotificationChannel appUpdates = new NotificationChannel(APP_UPDATES, context.getString(R.string.NotificationChannel_app_updates), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(appUpdates);
        } else {
            notificationManager.deleteNotificationChannel(APP_UPDATES);
        }

    }

    @TargetApi(26)
    private static void onUpgrade(@NonNull NotificationManager notificationManager, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading channels from " + oldVersion + " to " + newVersion);

        if (oldVersion < Version.MESSAGES_CATEGORY) {
            notificationManager.deleteNotificationChannel("messages");
            notificationManager.deleteNotificationChannel("calls");
            notificationManager.deleteNotificationChannel("locked_status");
            notificationManager.deleteNotificationChannel("backups");
            notificationManager.deleteNotificationChannel("other");
        }

        if (oldVersion < Version.CALLS_PRIORITY_BUMP) {
            notificationManager.deleteNotificationChannel("calls_v2");
        }
    }

    @TargetApi(26)
    private static void setLedPreference(@NonNull NotificationChannel channel, @NonNull String ledColor) {
        if ("none".equals(ledColor)) {
            channel.enableLights(false);
        } else {
            channel.enableLights(true);
            channel.setLightColor(Color.parseColor(ledColor));
        }
    }

    @TargetApi(21)
    private static AudioAttributes getRingtoneAudioAttributes() {
        return new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                .build();
    }


    @TargetApi(26)
    private static @NonNull NotificationChannel copyChannel(@NonNull NotificationChannel original, @NonNull String id) {
        NotificationChannel copy = new NotificationChannel(id, original.getName(), original.getImportance());

        copy.setGroup(original.getGroup());
        copy.setSound(original.getSound(), original.getAudioAttributes());
        copy.setBypassDnd(original.canBypassDnd());
        copy.setVibrationPattern(original.getVibrationPattern());
        copy.enableVibration(original.shouldVibrate());
        copy.setLockscreenVisibility(original.getLockscreenVisibility());
        copy.setShowBadge(original.canShowBadge());
        copy.setLightColor(original.getLightColor());
        copy.enableLights(original.shouldShowLights());

        return copy;
    }

    @TargetApi(26)
    private static boolean updateExistingChannel(@NonNull NotificationManager notificationManager,
                                                 @NonNull String channelId,
                                                 @NonNull String newChannelId,
                                                 @NonNull ChannelUpdater updater)
    {
        NotificationChannel existingChannel = notificationManager.getNotificationChannel(channelId);
        if (existingChannel == null) {
            Log.w(TAG, "Tried to update a channel, but it didn't exist.");
            return false;
        }

        notificationManager.deleteNotificationChannel(existingChannel.getId());

        NotificationChannel newChannel = copyChannel(existingChannel, newChannelId);
        updater.update(newChannel);
        notificationManager.createNotificationChannel(newChannel);
        return true;
    }

    @TargetApi(26)
    private static boolean channelExists(@Nullable NotificationChannel channel) {
        return channel != null && !NotificationChannel.DEFAULT_CHANNEL_ID.equals(channel.getId());
    }

    private interface ChannelUpdater {
        @TargetApi(26)
        void update(@NonNull NotificationChannel channel);
    }

}
