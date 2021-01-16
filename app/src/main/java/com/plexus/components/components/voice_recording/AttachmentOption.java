package com.plexus.components.components.voice_recording;

import com.plexus.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Varun John on 12 Feb, 2020
 * Github : https://github.com/varunjohn
 */
public class AttachmentOption implements Serializable {

    public static final byte DOCUMENT_ID = 101;
    public static final byte CAMERA_ID = 102;
    public static final byte GALLERY_ID = 103;
    public static final byte AUDIO_ID = 104;
    public static final byte LOCATION_ID = 105;
    public static final byte CONTACT_ID = 106;
    private final int id;
    private final String title;
    private final int resourceImage;

    public AttachmentOption(int id, String title, int resourceImage) {
        this.id = id;
        this.title = title;
        this.resourceImage = resourceImage;
    }

    public static List<AttachmentOption> getDefaultList() {
        List<AttachmentOption> attachmentOptions = new ArrayList<>();
        attachmentOptions.add(new AttachmentOption(DOCUMENT_ID, "Document", R.drawable.file_outline));
        attachmentOptions.add(new AttachmentOption(CAMERA_ID, "Camera", R.drawable.camera_outline));
        attachmentOptions.add(new AttachmentOption(GALLERY_ID, "Gallery", R.drawable.image_outline));
        attachmentOptions.add(new AttachmentOption(AUDIO_ID, "Audio", R.drawable.microphone));
        attachmentOptions.add(new AttachmentOption(LOCATION_ID, "Location", R.drawable.location));
        attachmentOptions.add(new AttachmentOption(CONTACT_ID, "Contact", R.drawable.account_outline));

        return attachmentOptions;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getResourceImage() {
        return resourceImage;
    }
}
