package com.plexus.messaging.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.plexus.R;

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

public class ViewVideo extends AppCompatActivity {

    VideoView videoView;
    ImageView close;
    String messageid;
    Uri videoUrl;
    FirebaseUser firebaseUser;
    private Toolbar toolbar;

    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_video_player);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        messageid = intent.getStringExtra("messageid");
        videoUrl = Uri.parse(intent.getStringExtra("videoUrl"));

        videoView = findViewById(R.id.video_view);
        close = findViewById(R.id.close);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        close.setOnClickListener(v -> finish());

        videoView.setVideoURI(videoUrl);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

    }

    private void deleteMessage(){

    }

    private void forwardMessage(){

    }

    private void shareVideo(){

    }

    void DownloadVideo(String VideoUrl) {

        if (ContextCompat.checkSelfPermission(ViewVideo.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ViewVideo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewVideo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            ActivityCompat.requestPermissions(ViewVideo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            Toast.makeText(this, "Need permission to download", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show();
            //Asynctask to create a thread to downlaod image in the background
            /*new DownloadService(ViewVideo.this).execute(VideoUrl);*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_message_imageview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                return true;
            case R.id.forward:
                forwardMessage();
                return true;
            case R.id.share:
                shareVideo();
                return true;
            case R.id.download:
                DownloadVideo(String.valueOf(videoUrl));
                return true;
            case R.id.delete:
                deleteMessage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
