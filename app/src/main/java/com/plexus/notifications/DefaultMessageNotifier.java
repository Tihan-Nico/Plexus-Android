package com.plexus.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.plexus.utils.BubbleUtil;


/**
 * Handles posting system notifications for new messages.
 *
 * @author Moxie Marlinspike
 */
public class DefaultMessageNotifier implements MessageNotifier {

    private static final String TAG = DefaultMessageNotifier.class.getSimpleName();

    private volatile long visibleThread = -1;

    @Override
    public long getVisibleThread() {
        return visibleThread;
    }

    @Override
    public void setVisibleThread(long threadId) {
        visibleThread = threadId;
    }

    @Override
    public void clearVisibleThread() {
        setVisibleThread(-1);
    }

    @Override
    public void cancelDelayedNotifications() {

    }

    @Override
    public void updateNotification(@NonNull Context context) {

    }

    @Override
    public void updateNotification(@NonNull Context context, long threadId) {

    }

    @Override
    public void updateNotification(@NonNull Context context, long threadId, @NonNull BubbleUtil.BubbleState defaultBubbleState) {

    }

    @Override
    public void updateNotification(@NonNull Context context, long threadId, boolean signal) {

    }

    @Override
    public void updateNotification(@NonNull Context context, long threadId, boolean signal, int reminderCount, @NonNull BubbleUtil.BubbleState defaultBubbleState) {

    }

    @Override
    public void clearReminder(@NonNull Context context) {
        Intent alarmIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }
}
