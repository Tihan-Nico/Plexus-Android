package com.plexus.slides;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.plexus.R;
import com.plexus.database.AttachmentDatabase;
import com.plexus.model.attachments.Attachment;
import com.plexus.model.attachments.UriAttachment;
import com.plexus.utils.MediaUtil;


public class AudioSlide extends Slide {

    public AudioSlide(Context context, Uri uri, long dataSize, boolean voiceNote) {
        super(context, constructAttachmentFromUri(context, uri, MediaUtil.AUDIO_UNSPECIFIED, dataSize, 0, 0, false, null, null, null, null, null, voiceNote, false, false, false));
    }

    public AudioSlide(Context context, Uri uri, long dataSize, String contentType, boolean voiceNote) {
        super(context,  new UriAttachment(uri, contentType, AttachmentDatabase.TRANSFER_PROGRESS_STARTED, dataSize, 0, 0, null, null, voiceNote, false, false, false, null, null, null, null, null));
    }

    public AudioSlide(Context context, Attachment attachment) {
        super(context, attachment);
    }

    @Override
    public boolean hasPlaceholder() {
        return true;
    }

    @Override
    public boolean hasImage() {
        return false;
    }

    @Override
    public boolean hasAudio() {
        return true;
    }

    @NonNull
    @Override
    public String getContentDescription() {
        return "Audio";
    }

    @Override
    public @DrawableRes int getPlaceholderRes(Theme theme) {
        return R.drawable.audio;
    }
}
