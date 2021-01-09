package com.plexus.settings.activity.advance_features;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;

import java.io.File;
import java.text.DecimalFormat;

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

public class StorageActivity extends AppCompatActivity {

    TextView available_storage, cached_data, available_storage_ext;
    LinearLayout delete_cached_data;
    private Dialog delete_cache;
    ImageView back;

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_storage);

        available_storage = findViewById(R.id.available_storage);
        cached_data = findViewById(R.id.cached_data);
        delete_cached_data = findViewById(R.id.delete_cached_data);
        available_storage_ext = findViewById(R.id.available_storage_ext);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        available_storage.setText(bytesToHuman(getFreeInternalMemory()));
        available_storage_ext.setText(bytesToHuman(getFreeExternalMemory()));

        delete_cache = new Dialog(StorageActivity.this);
        delete_cache.setContentView(R.layout.dialog);
        delete_cache.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView cancel = delete_cache.findViewById(R.id.cancel);
        TextView delete_all = delete_cache.findViewById(R.id.delete_all);

        delete_cached_data.setOnClickListener(v -> {
            delete_cache.show();
            cancel.setOnClickListener(v1 -> delete_cache.dismiss());
            delete_all.setOnClickListener(v12 -> {
                deleteCache(getApplicationContext());
                delete_cache.dismiss();
            });
        });

        initializeCache();

    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        long finalSize = size;
        runOnUiThread(() -> cached_data.setText(bytesToHuman(finalSize)));
    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

    public long getFreeInternalMemory() {
        return getFreeMemory(Environment.getDataDirectory());
    }

    public long getFreeExternalMemory() {
        return getFreeMemory(Environment.getExternalStorageDirectory());
    }

    public long getFreeMemory(File path) {
        if ((null != path) && (path.exists()) && (path.isDirectory())) {
            StatFs stats = new StatFs(path.getAbsolutePath());
            return stats.getAvailableBlocksLong() * stats.getBlockSizeLong();
        }
        return -1;
    }

    /**
     * Convert bytes to human format.
     * @param totalBytes {@code long} - Total of bytes.
     * @return {@link String} - Converted size.
     */
    public String bytesToHuman(long totalBytes) {
        String[] simbols = new String[] {"B", "KB", "MB", "GB", "TB", "PB", "EB"};
        long scale = 1L;
        for (String simbol : simbols) {
            if (totalBytes < (scale * 1024L)) {
                return String.format("%s %s", new DecimalFormat("#.##").format((double)totalBytes / scale), simbol);
            }
            scale *= 1024L;
        }
        return "-1 B";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
