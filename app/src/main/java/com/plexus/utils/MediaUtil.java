package com.plexus.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.plexus.core.utils.logging.Log;
import com.plexus.providers.PartAuthority;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class MediaUtil {

    public static final String IMAGE_PNG         = "image/png";
    public static final String IMAGE_JPEG        = "image/jpeg";
    public static final String IMAGE_HEIC        = "image/heic";
    public static final String IMAGE_HEIF        = "image/heif";
    public static final String IMAGE_WEBP        = "image/webp";
    public static final String IMAGE_GIF         = "image/gif";
    public static final String AUDIO_AAC         = "audio/aac";
    public static final String AUDIO_UNSPECIFIED = "audio/*";
    public static final String VIDEO_MP4         = "video/mp4";
    public static final String VIDEO_UNSPECIFIED = "video/*";
    public static final String VCARD             = "text/x-vcard";
    public static final String LONG_TEXT         = "text/x-plexus-plain";
    public static final String VIEW_ONCE         = "application/x-plexus-view-once";
    public static final String UNKNOWN           = "*/*";

    public static String getRootPath() {
        String sdPath;
        String ext1 = Environment.getExternalStorageState();
        if (ext1.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdPath = Environment.MEDIA_UNMOUNTED;
        }
        return sdPath;
    }

    @SuppressLint("DefaultLocale")
    public static String size2String(Long filesize) {
        Integer unit = 1024;
        if (filesize < unit) {
            return String.format("%d bytes", filesize);
        }
        int exp = (int) (Math.log(filesize) / Math.log(unit));

        return String.format("%.0f %sbytes", filesize / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }

    public static @Nullable String getMimeType(@NonNull Context context, @Nullable Uri uri) {
        if (uri == null) return null;

        if (PartAuthority.isLocalUri(uri)) {
            return PartAuthority.getAttachmentContentType(context, uri);
        }

        String type = context.getContentResolver().getType(uri);
        if (type == null) {
            final String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }

        return getCorrectedMimeType(type);
    }

    public static @Nullable String getExtension(@NonNull Context context, @Nullable Uri uri) {
        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(getMimeType(context, uri));
    }

    public static @Nullable String getCorrectedMimeType(@Nullable String mimeType) {
        if (mimeType == null) return null;

        switch(mimeType) {
            case "image/jpg":
                return MimeTypeMap.getSingleton().hasMimeType(IMAGE_JPEG)
                        ? IMAGE_JPEG
                        : mimeType;
            default:
                return mimeType;
        }
    }

    public static long getMediaSize(Context context, Uri uri) throws IOException {
        InputStream in = PartAuthority.getAttachmentStream(context, uri);
        if (in == null) throw new IOException("Couldn't obtain input stream.");

        long   size   = 0;
        byte[] buffer = new byte[4096];
        int    read;

        while ((read = in.read(buffer)) != -1) {
            size += read;
        }
        in.close();

        return size;
    }

    public static boolean isVideo(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.trim().startsWith("video/");
    }

    public static boolean isVcard(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.trim().equals(VCARD);
    }

    public static boolean isGif(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.trim().equals("image/gif");
    }

    public static boolean isJpegType(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.trim().equals(IMAGE_JPEG);
    }

    public static boolean isHeicType(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.trim().equals(IMAGE_HEIC);
    }

    public static boolean isHeifType(String contentType) {
        return !TextUtils.isEmpty(contentType) && contentType.trim().equals(IMAGE_HEIF);
    }

    public static boolean isTextType(String contentType) {
        return (null != contentType) && contentType.startsWith("text/");
    }

    public static boolean isImageType(String contentType) {
        return (null != contentType) && contentType.startsWith("image/");
    }

    public static boolean isAudioType(String contentType) {
        return (null != contentType) && contentType.startsWith("audio/");
    }

    public static boolean isVideoType(String contentType) {
        return (null != contentType) && contentType.startsWith("video/");
    }

    public static boolean isImageOrVideoType(String contentType) {
        return isImageType(contentType) || isVideoType(contentType);
    }

    public static boolean isLongTextType(String contentType) {
        return (null != contentType) && contentType.equals(LONG_TEXT);
    }

    public static boolean isViewOnceType(String contentType) {
        return (null != contentType) && contentType.equals(VIEW_ONCE);
    }

}
