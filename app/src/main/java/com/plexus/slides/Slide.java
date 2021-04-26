package com.plexus.slides;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.audio.AudioHash;
import com.plexus.blurhash.BlurHash;
import com.plexus.core.utils.guava.Optional;
import com.plexus.database.AttachmentDatabase;
import com.plexus.model.attachments.Attachment;
import com.plexus.model.attachments.UriAttachment;
import com.plexus.stickers.StickerLocator;
import com.plexus.utils.MediaUtil;
import com.plexus.utils.Util;

import java.security.SecureRandom;

public abstract class Slide {

    protected final Attachment attachment;
    protected final Context    context;

    public Slide(@NonNull Context context, @NonNull Attachment attachment) {
        this.context    = context;
        this.attachment = attachment;
    }

    public String getContentType() {
        return attachment.getContentType();
    }

    @Nullable
    public Uri getUri() {
        return attachment.getUri();
    }

    /*public @Nullable Uri getPublicUri() {
        if (Build.VERSION.SDK_INT >= 28) {
            return attachment.getPublicUri();
        } else {
            return attachment.getUri();
        }
    }*/

    @NonNull
    public Optional<String> getBody() {
        return Optional.absent();
    }

    @NonNull
    public Optional<String> getCaption() {
        return Optional.fromNullable(attachment.getCaption());
    }

    @NonNull
    public Optional<String> getFileName() {
        return Optional.fromNullable(attachment.getFileName());
    }

    @Nullable
    public String getFastPreflightId() {
        return attachment.getFastPreflightId();
    }

    public long getFileSize() {
        return attachment.getSize();
    }

    public boolean hasImage() {
        return false;
    }

    public boolean hasSticker() { return false; }

    public boolean hasVideo() {
        return false;
    }

    public boolean hasAudio() {
        return false;
    }

    public boolean hasDocument() {
        return false;
    }

    public boolean hasLocation() {
        return false;
    }

    public boolean hasViewOnce() {
        return false;
    }

    public boolean isBorderless() {
        return false;
    }

    /*public boolean isVideoGif() {
        return hasVideo() && attachment.isVideoGif();
    }*/

    public @NonNull String getContentDescription() { return ""; }

    public @NonNull Attachment asAttachment() {
        return attachment;
    }

    public boolean isInProgress() {
        return attachment.isInProgress();
    }

    public boolean isPendingDownload() {
        return getTransferState() == AttachmentDatabase.TRANSFER_PROGRESS_FAILED ||
                getTransferState() == AttachmentDatabase.TRANSFER_PROGRESS_PENDING;
    }

    public int getTransferState() {
        return attachment.getTransferState();
    }

    public @DrawableRes int getPlaceholderRes(Theme theme) {
        throw new AssertionError("getPlaceholderRes() called for non-drawable slide");
    }

    public @Nullable
    BlurHash getPlaceholderBlur() {
        return attachment.getBlurHash();
    }

    public boolean hasPlaceholder() {
        return false;
    }

    public boolean hasPlayOverlay() {
        return false;
    }

    protected static Attachment constructAttachmentFromUri(@NonNull  Context        context,
                                                           @NonNull  Uri            uri,
                                                           @NonNull  String         defaultMime,
                                                           long           size,
                                                           int            width,
                                                           int            height,
                                                           boolean        hasThumbnail,
                                                           @Nullable String         fileName,
                                                           @Nullable String         caption,
                                                           @Nullable StickerLocator stickerLocator,
                                                           @Nullable BlurHash       blurHash,
                                                           @Nullable AudioHash      audioHash,
                                                           boolean        voiceNote,
                                                           boolean        borderless,
                                                           boolean        gif,
                                                           boolean        quote)
    {
        return constructAttachmentFromUri(context, uri, defaultMime, size, width, height, hasThumbnail, fileName, caption, stickerLocator, blurHash, audioHash, voiceNote, borderless, gif, quote, null);
    }

    protected static Attachment constructAttachmentFromUri(@NonNull  Context        context,
                                                           @NonNull  Uri            uri,
                                                           @NonNull  String         defaultMime,
                                                           long           size,
                                                           int            width,
                                                           int            height,
                                                           boolean        hasThumbnail,
                                                           @Nullable String         fileName,
                                                           @Nullable String         caption,
                                                           @Nullable StickerLocator stickerLocator,
                                                           @Nullable BlurHash       blurHash,
                                                           @Nullable AudioHash audioHash,
                                                           boolean        voiceNote,
                                                           boolean        borderless,
                                                           boolean        gif,
                                                           boolean        quote,
                                                           @Nullable AttachmentDatabase.TransformProperties transformProperties)
    {
        String                 resolvedType    = Optional.fromNullable(MediaUtil.getMimeType(context, uri)).or(defaultMime);
        String                 fastPreflightId = String.valueOf(new SecureRandom().nextLong());
        return new UriAttachment(uri,
                resolvedType,
                AttachmentDatabase.TRANSFER_PROGRESS_STARTED,
                size,
                width,
                height,
                fileName,
                fastPreflightId,
                voiceNote,
                borderless,
                gif,
                quote,
                caption,
                stickerLocator,
                blurHash,
                audioHash,
                transformProperties);
    }

    public @NonNull Optional<String> getFileType(@NonNull Context context) {
        Optional<String> fileName = getFileName();

        if (fileName.isPresent()) {
            String fileType = getFileType(fileName);
            if (!fileType.isEmpty()) {
                return Optional.of(fileType);
            }
        }

        return Optional.fromNullable(MediaUtil.getExtension(context, getUri()));
    }

    private static @NonNull String getFileType(Optional<String> fileName) {
        if (!fileName.isPresent()) return "";

        String[] parts = fileName.get().split("\\.");

        if (parts.length < 2) {
            return "";
        }

        String suffix = parts[parts.length - 1];

        if (suffix.length() <= 3) {
            return suffix;
        }

        return "";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)             return false;
        if (!(other instanceof Slide)) return false;

        Slide that = (Slide)other;

        return Util.equals(this.getContentType(), that.getContentType()) &&
                this.hasAudio() == that.hasAudio()                        &&
                this.hasImage() == that.hasImage()                        &&
                this.hasVideo() == that.hasVideo()                        &&
                this.getTransferState() == that.getTransferState()        &&
                Util.equals(this.getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Util.hashCode(getContentType(), hasAudio(), hasImage(),
                hasVideo(), getUri(), getTransferState());
    }
}
