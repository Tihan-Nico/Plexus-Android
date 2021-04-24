package com.plexus.mediaupload;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.IOException;

/**
 * Represents a piece of media that the user has on their device.
 */
public class Media implements Parcelable {

    public static final String ALL_MEDIA_BUCKET_ID = "com.plexus.ALL_MEDIA";

    private final Uri     uri;
    private final String  mimeType;
    private final long    date;
    private final int     width;
    private final int     height;
    private final long    size;
    private final long    duration;
    private final boolean borderless;
    private final boolean videoGif;

    public Media(@NonNull Uri uri,
                 @NonNull String mimeType,
                 long date,
                 int width,
                 int height,
                 long size,
                 long duration,
                 boolean borderless,
                 boolean videoGif)
    {
        this.uri                 = uri;
        this.mimeType            = mimeType;
        this.date                = date;
        this.width               = width;
        this.height              = height;
        this.size                = size;
        this.duration            = duration;
        this.borderless          = borderless;
        this.videoGif            = videoGif;
    }

    protected Media(Parcel in) {
        uri        = in.readParcelable(Uri.class.getClassLoader());
        mimeType   = in.readString();
        date       = in.readLong();
        width      = in.readInt();
        height     = in.readInt();
        size       = in.readLong();
        duration   = in.readLong();
        borderless = in.readInt() == 1;
        videoGif   = in.readInt() == 1;
    }

    public Uri getUri() {
        return uri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getDate() {
        return date;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isBorderless() {
        return borderless;
    }

    public boolean isVideoGif() {
        return videoGif;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(mimeType);
        dest.writeLong(date);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeInt(borderless ? 1 : 0);
        dest.writeInt(videoGif ? 1 : 0);
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        return uri.equals(media.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
