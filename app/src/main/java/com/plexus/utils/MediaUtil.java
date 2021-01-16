package com.plexus.utils;

import android.os.Environment;

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

    public static String size2String(Long filesize) {
        Integer unit = 1024;
        if (filesize < unit) {
            return String.format("%d bytes", filesize);
        }
        int exp = (int) (Math.log(filesize) / Math.log(unit));

        return String.format("%.0f %sbytes", filesize / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }
}
