package com.plexus.utils;

public final class MessageRecordUtil {

    private MessageRecordUtil() {
    }

    public static boolean hasAudio(ConversationMessages messageRecord) {
        return messageRecord.getSlideDeck().getAudioSlide() != null;
    }
}
