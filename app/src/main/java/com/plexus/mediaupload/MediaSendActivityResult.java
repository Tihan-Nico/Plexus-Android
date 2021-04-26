package com.plexus.mediaupload;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.plexus.core.utils.guava.Preconditions;
import com.plexus.model.Mention;
import com.plexus.model.account.User;
import com.plexus.utils.ParcelUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class that lets us nicely format data that we'll send back to ConversationActivity.
 */
public class MediaSendActivityResult implements Parcelable {
    private final User                 recipientId;
    /*private final Collection<PreUploadResult> uploadResults;*/
    private final Collection<Media>           nonUploadedMedia;
    private final String                      body;
    private final boolean                     viewOnce;
    private final Collection<Mention>         mentions;

    static @NonNull MediaSendActivityResult forPreUpload(@NonNull User recipientId,
                                                        /* @NonNull Collection<PreUploadResult> uploadResults,*/
                                                         @NonNull String body,
                                                         boolean viewOnce,
                                                         @NonNull List<Mention> mentions)
    {
        /*Preconditions.checkArgument(uploadResults.size() > 0, "Must supply uploadResults!");*/
        /*return new MediaSendActivityResult(recipientId, null, Collections.emptyList(), body, viewOnce, mentions);*/
        return null;
    }

    static @NonNull MediaSendActivityResult forTraditionalSend(@NonNull User recipientId,
                                                               @NonNull List<Media> nonUploadedMedia,
                                                               @NonNull String body,
                                                               boolean viewOnce,
                                                               @NonNull List<Mention> mentions)
    {
        Preconditions.checkArgument(nonUploadedMedia.size() > 0, "Must supply media!");
        /*return new MediaSendActivityResult(recipientId, Collections.emptyList(), nonUploadedMedia, body, viewOnce, mentions);*/
        return null;
    }

    private MediaSendActivityResult(@NonNull User recipientId,
                                    /*@NonNull Collection<PreUploadResult> uploadResults,*/
                                    @NonNull List<Media> nonUploadedMedia,
                                    @NonNull String body,
                                    boolean viewOnce,
                                    @NonNull List<Mention> mentions)
    {
        this.recipientId      = recipientId;
        /*this.uploadResults    = uploadResults;*/
        this.nonUploadedMedia = nonUploadedMedia;
        this.body             = body;
        this.viewOnce         = viewOnce;
        this.mentions         = mentions;
    }

    private MediaSendActivityResult(Parcel in) {
        this.recipientId      = in.readParcelable(User.class.getClassLoader());
        /*this.uploadResults    = ParcelUtil.readParcelableCollection(in, PreUploadResult.class);*/
        this.nonUploadedMedia = ParcelUtil.readParcelableCollection(in, Media.class);
        this.body             = in.readString();
        this.viewOnce         = ParcelUtil.readBoolean(in);
        this.mentions         = ParcelUtil.readParcelableCollection(in, Mention.class);
    }

    public @NonNull User getRecipientId() {
        return recipientId;
    }

    /*public boolean isPushPreUpload() {
        return uploadResults.size() > 0;
    }

    public @NonNull Collection<PreUploadResult> getPreUploadResults() {
        return uploadResults;
    }*/

    public @NonNull Collection<Media> getNonUploadedMedia() {
        return nonUploadedMedia;
    }

    public @NonNull String getBody() {
        return body;
    }

    public boolean isViewOnce() {
        return viewOnce;
    }

    public @NonNull Collection<Mention> getMentions() {
        return mentions;
    }

    public static final Creator<MediaSendActivityResult> CREATOR = new Creator<MediaSendActivityResult>() {
        @Override
        public MediaSendActivityResult createFromParcel(Parcel in) {
            return new MediaSendActivityResult(in);
        }

        @Override
        public MediaSendActivityResult[] newArray(int size) {
            return new MediaSendActivityResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(recipientId, 0);
        /*ParcelUtil.writeParcelableCollection(dest, uploadResults);*/
        ParcelUtil.writeParcelableCollection(dest, nonUploadedMedia);
        dest.writeString(body);
        ParcelUtil.writeBoolean(dest, viewOnce);
        ParcelUtil.writeParcelableCollection(dest, mentions);
    }
}
