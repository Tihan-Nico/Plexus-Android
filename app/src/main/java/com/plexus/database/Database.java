package com.plexus.database;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.plexus.database.helpers.SQLCipherOpenHelper;
import com.plexus.dependecies.PlexusDependencies;

import java.util.Set;

public abstract class Database {

    protected static final String ID_WHERE = "_id = ?";
    protected final Context context;
    protected SQLCipherOpenHelper databaseHelper;

    public Database(Context context, SQLCipherOpenHelper databaseHelper) {
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    protected void notifyConversationListeners(Set<Long> threadIds) {
        PlexusDependencies.getDatabaseObserver().notifyConversationListeners(threadIds);

        for (long threadId : threadIds) {
            notifyConversationListeners(threadId);
        }
    }

    protected void notifyConversationListeners(long threadId) {
        PlexusDependencies.getDatabaseObserver().notifyConversationListeners(threadId);

        context.getContentResolver().notifyChange(DatabaseContentProviders.Conversation.getUriForThread(threadId), null);
        notifyVerboseConversationListeners(threadId);
    }

    protected void notifyVerboseConversationListeners(long threadId) {
        PlexusDependencies.getDatabaseObserver().notifyVerboseConversationListeners(threadId);
        context.getContentResolver().notifyChange(DatabaseContentProviders.Conversation.getVerboseUriForThread(threadId), null);
    }

    protected void notifyConversationListListeners() {
        PlexusDependencies.getDatabaseObserver().notifyConversationListListeners();
        context.getContentResolver().notifyChange(DatabaseContentProviders.ConversationList.CONTENT_URI, null);
    }

    protected void notifyStickerListeners() {
        context.getContentResolver().notifyChange(DatabaseContentProviders.Sticker.CONTENT_URI, null);
    }

    protected void notifyStickerPackListeners() {
        context.getContentResolver().notifyChange(DatabaseContentProviders.StickerPack.CONTENT_URI, null);
    }

    @Deprecated
    protected void setNotifyConversationListeners(Cursor cursor, long threadId) {
        cursor.setNotificationUri(context.getContentResolver(), DatabaseContentProviders.Conversation.getUriForThread(threadId));
    }

    @Deprecated
    protected void setNotifyConversationListeners(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), DatabaseContentProviders.Conversation.getUriForAllThreads());
    }

    @Deprecated
    protected void setNotifyVerboseConversationListeners(Cursor cursor, long threadId) {
        cursor.setNotificationUri(context.getContentResolver(), DatabaseContentProviders.Conversation.getVerboseUriForThread(threadId));
    }

    @Deprecated
    protected void setNotifyConversationListListeners(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), DatabaseContentProviders.ConversationList.CONTENT_URI);
    }

    @Deprecated
    protected void setNotifyStickerListeners(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), DatabaseContentProviders.Sticker.CONTENT_URI);
    }

    @Deprecated
    protected void setNotifyStickerPackListeners(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), DatabaseContentProviders.StickerPack.CONTENT_URI);
    }

    protected void registerAttachmentListeners(@NonNull ContentObserver observer) {
        context.getContentResolver().registerContentObserver(DatabaseContentProviders.Attachment.CONTENT_URI,
                true,
                observer);
    }

    protected void notifyAttachmentListeners() {
        context.getContentResolver().notifyChange(DatabaseContentProviders.Attachment.CONTENT_URI, null);
    }

    public void reset(SQLCipherOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
}
