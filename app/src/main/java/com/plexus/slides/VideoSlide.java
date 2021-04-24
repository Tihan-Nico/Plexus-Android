package com.plexus.slides;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.R;
import com.plexus.database.AttachmentDatabase;
import com.plexus.model.attachments.Attachment;
import com.plexus.utils.MediaUtil;

public class VideoSlide extends Slide {

    public VideoSlide(Context context, Uri uri, long dataSize) {
        this(context, uri, dataSize, null, null);
    }

    public VideoSlide(Context context, Uri uri, long dataSize, @Nullable String caption, @Nullable AttachmentDatabase.TransformProperties transformProperties) {
        super(context, constructAttachmentFromUri(context, uri, MediaUtil.VIDEO_UNSPECIFIED, dataSize, 0, 0, MediaUtil.hasVideoThumbnail(context, uri), null, caption, null, null, null, false, false, false, transformProperties));
    }

    public VideoSlide(Context context, Uri uri, long dataSize, int width, int height, @Nullable String caption, @Nullable AttachmentDatabase.TransformProperties transformProperties) {
        super(context, constructAttachmentFromUri(context, uri, MediaUtil.VIDEO_UNSPECIFIED, dataSize, width, height, MediaUtil.hasVideoThumbnail(context, uri), null, caption, null, null, null, false, false, false, transformProperties));
    }

    public VideoSlide(Context context, Attachment attachment) {
        super(context, attachment);
    }

    @Override
    public boolean hasPlaceholder() {
        return true;
    }

    @Override
    public boolean hasPlayOverlay() {
        return true;
    }

    @Override
    public @DrawableRes int getPlaceholderRes(Theme theme) {
        return R.drawable.video_outline;
    }

    @Override
    public boolean hasImage() {
        return true;
    }

    @Override
    public boolean hasVideo() {
        return true;
    }

    @NonNull @Override
    public String getContentDescription() {
        return "Video";
    }
}
