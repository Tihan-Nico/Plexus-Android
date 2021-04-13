package com.plexus.components.voice;

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.plexus.utils.logging.Log;

/**
 * Factory responsible for building out MediaDescriptionCompat objects for voice notes.
 */
class VoiceNoteMediaDescriptionCompatFactory {

    public static final String EXTRA_MESSAGE_POSITION = "voice.note.extra.MESSAGE_POSITION";
    public static final String EXTRA_THREAD_RECIPIENT_ID = "voice.note.extra.RECIPIENT_ID";
    public static final String EXTRA_AVATAR_RECIPIENT_ID = "voice.note.extra.SENDER_ID";
    public static final String EXTRA_THREAD_ID = "voice.note.extra.THREAD_ID";
    public static final String EXTRA_COLOR = "voice.note.extra.COLOR";
    public static final String EXTRA_MESSAGE_ID = "voice.note.extra.MESSAGE_ID";

    private static final String TAG = Log.tag(VoiceNoteMediaDescriptionCompatFactory.class);

    private VoiceNoteMediaDescriptionCompatFactory() {
    }

    /**
     * Build out a MediaDescriptionCompat for a given voice note. Expects to be run
     * on a background thread.
     *
     * @param context       Context.
     * @param messageRecord The MessageRecord of the given voice note.
     * @return A MediaDescriptionCompat with all the details the service expects.
     */
    /*@WorkerThread
    static MediaDescriptionCompat buildMediaDescription(@NonNull Context context,
                                                        @NonNull ConversationMessages messageRecord, String profileID) {
        *//*int startingPosition = DatabaseFactory.getMmsSmsDatabase(context)
                .getMessagePositionInConversation(messageRecord.getThreadId(),
                        messageRecord.getDateReceived());

        Recipient threadRecipient = Objects.requireNonNull(DatabaseFactory.getThreadDatabase(context)
                .getRecipientForThreadId(messageRecord.getThreadId()));
        Recipient sender          = messageRecord.isOutgoing() ? Recipient.self() : messageRecord.getIndividualRecipient();
        Recipient avatarRecipient = threadRecipient.isGroup() ? threadRecipient : sender;

        Bundle extras = new Bundle();
        extras.putString(EXTRA_THREAD_RECIPIENT_ID, profileID);
        extras.putString(EXTRA_AVATAR_RECIPIENT_ID, profileID);
        extras.putLong(EXTRA_MESSAGE_POSITION, startingPosition);
        extras.putLong(EXTRA_THREAD_ID, messageRecord.getThreadId());
        extras.putString(EXTRA_COLOR, threadRecipient.getColor().serialize());
        extras.putLong(EXTRA_MESSAGE_ID, messageRecord.getId());

        NotificationPrivacyPreference preference = TextSecurePreferences.getNotificationPrivacy(context);

        String title = context.getString(R.string.MessageNotifier_signal_message);

        String subtitle = null;
        if (preference.isDisplayContact()) {
            subtitle = context.getString(R.string.VoiceNoteMediaDescriptionCompatFactory__voice_message,
                    DateUtils.formatDateWithoutDayOfWeek(Locale.getDefault(),
                            messageRecord.getDateReceived()));
        }

        Uri uri = ((MmsMessageRecord) messageRecord).getSlideDeck().getAudioSlide().getUri();*//*

        *//*return new MediaDescriptionCompat.Builder()
         *//**//*.setMediaUri(uri)*//**//*
                .setTitle(title)
                .setSubtitle(subtitle)
                .setExtras(extras)
                .build();*//*

        return null;
    }*/

}
