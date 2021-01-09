package com.plexus.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.plexus.Plexus;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import javax.activation.MimetypesFileTypeMap;

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

public class Files {

    public static boolean save(File file, InputStream inputStream) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            return true;
        } catch (IOException e) {
            Log.e(Plexus.TAG, e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                Log.e(Plexus.TAG, e.getMessage(), e);
            }
        }
        return false;
    }

    public static String getFileType(String filePath) {
        File file = new File(filePath);
        return new MimetypesFileTypeMap().getContentType(file).split("/")[0];
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String getFileExtension(String filename) {
        int mid = filename.lastIndexOf(".");
        if (mid == -1) {
            return "";
        }

        return filename.substring(mid + 1);
    }

    public static String getFileName(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        int mid = path.lastIndexOf("/");
        if (mid == -1) {
            return path;
        }
        return path.substring(mid + 1);
    }

    // UPDATED!
    public static String getPath(Uri uri, Context context) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

}
