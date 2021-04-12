package com.plexus.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.plexus.core.utils.concurrent.PlexusExecutors;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.utils.BubbleUtil;

public interface MessageNotifier {
    long getVisibleThread();

    void setVisibleThread(long threadId);

    void clearVisibleThread();

    void cancelDelayedNotifications();

    void updateNotification(@NonNull Context context);

    void updateNotification(@NonNull Context context, long threadId);

    void updateNotification(@NonNull Context context, long threadId, @NonNull BubbleUtil.BubbleState defaultBubbleState);

    void updateNotification(@NonNull Context context, long threadId, boolean signal);

    void updateNotification(@NonNull Context context, long threadId, boolean signal, int reminderCount, @NonNull BubbleUtil.BubbleState defaultBubbleState);

    void clearReminder(@NonNull Context context);


    class ReminderReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            PlexusExecutors.BOUNDED.execute(() -> {
                int reminderCount = intent.getIntExtra("reminder_count", 0);
                PlexusDependencies.getMessageNotifier().updateNotification(context, -1, true, reminderCount + 1, BubbleUtil.BubbleState.HIDDEN);
            });
        }
    }
}
