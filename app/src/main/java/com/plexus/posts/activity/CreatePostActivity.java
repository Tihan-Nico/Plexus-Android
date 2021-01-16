package com.plexus.posts.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jakewharton.rxbinding4.view.RxView;
import com.plexus.R;
import com.plexus.components.background.PlexusUpload;
import com.plexus.components.components.ImageView.Constants;
import com.plexus.components.components.socials.commons.Hashtag;
import com.plexus.components.components.socials.commons.HashtagArrayAdapter;
import com.plexus.components.components.socials.commons.SocialAutoCompleteTextView;
import com.plexus.main.activity.MainActivity;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;
import com.plexus.utils.TimeUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

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

public class CreatePostActivity extends AppCompatActivity {

    /**
     * Voice recording
     */

    Button upload;
    SocialAutoCompleteTextView post_description;
    TextView character_count, fullname, post_date, group_name;
    MaterialCheckBox add_to_moments;

    String current_date = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date());
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int VIDEO_REQUEST_CODE = 2;

    SimpleDraweeView profile_image;

    FirebaseUser firebaseUser;
    StorageReference storageRef;
    Intent intent;

    Uri image_uri, video_uri, camera_uri;
    ImageView close, upload_image, upload_camera, upload_voice, upload_video;
    boolean isRecordingUpload, isUploadImage, isUploadCamera, isText, group;
    String groupName;
    String groupID;

    /**
     * Recording Variables
     */

    int myProgress = 0;
    int progress;
    CountDownTimer countDownTimer;
    int timerEnd = 300;
    int timeInterval = 300;
    private boolean recording = false;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private File file;
    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static String TEMP_FILTER_AUDIO_NAME = "tempAudio.pcm";
    private static String TEMP_FILE_AUDIO_NAME = "/plexus_vn_audio.3gp";
    private static String mFileName = null;

    /**
     * View add Ons located below
     */
    View add_on_image, add_on_feeling, add_on_voice, add_on_video;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_create);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("posts");

        intent = getIntent();
        isText = intent.getExtras().getBoolean("isText");
        isRecordingUpload = intent.getExtras().getBoolean("isRecordingUpload");
        isUploadImage = intent.getExtras().getBoolean("isUploadImage");
        isUploadCamera = intent.getExtras().getBoolean("isUploadCamera");
        group = intent.getExtras().getBoolean("group");
        groupName = intent.getStringExtra("groupName");
        groupID = intent.getStringExtra("group_id");

        close = findViewById(R.id.close);
        upload = findViewById(R.id.upload);
        post_description = findViewById(R.id.post_description);
        character_count = findViewById(R.id.character_count);
        fullname = findViewById(R.id.fullname);
        profile_image = findViewById(R.id.profile_image);
        post_date = findViewById(R.id.post_date);
        upload_image = findViewById(R.id.upload_image);
        upload_camera = findViewById(R.id.upload_camera);
        upload_voice = findViewById(R.id.upload_voice);
        upload_video = findViewById(R.id.upload_video);
        add_on_image = findViewById(R.id.add_on_image);
        add_on_feeling = findViewById(R.id.add_on_feeling);
        add_on_voice = findViewById(R.id.add_on_voice);
        add_to_moments = findViewById(R.id.add_to_moments);
        add_on_video = findViewById(R.id.add_on_video);
        group_name = findViewById(R.id.group_name);

        post_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                character_count.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        post_date.setText(current_date);

        if (isRecordingUpload){
            voiceRecording();
        } else if (isUploadImage){
            CropImage.activity().start(CreatePostActivity.this);
        } else if (isUploadCamera){
            //Open Camera to take a picture
        } else if (isText){
            //Do Nothing
        }

        if (group){
            group_name.setText(groupName);
            group_name.setVisibility(View.VISIBLE);
        }

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();;
        mFileName += TEMP_FILE_AUDIO_NAME;

        init();
    }

    private void init() {
        getProfileData(profile_image, fullname);
        handleClicks();
        checkIfImageExist();
        addOnVoiceAttachment();

    }

    private void handleClicks() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Disposable a = RxView.clicks(close).subscribe(unit -> finish());
        compositeDisposable.add(a);

        Disposable b = RxView.clicks(upload_image).subscribe(unit -> CropImage.activity().start(CreatePostActivity.this));
        compositeDisposable.add(b);

        Disposable c = RxView.clicks(upload_camera).subscribe(unit -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        compositeDisposable.add(c);

        Disposable voice = RxView.clicks(upload_voice).subscribe(unit -> {
            voiceRecording();
        });
        compositeDisposable.add(voice);

        Disposable video = RxView.clicks(upload_video).subscribe(unit -> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_REQUEST_CODE);
        });
        compositeDisposable.add(video);

        if (group){
            upload.setOnClickListener(v -> {
                if (add_on_image.getVisibility() == View.VISIBLE) {
                    PlexusUpload.uploadGroupImage(post_description, firebaseUser.getUid(), image_uri, CreatePostActivity.this, groupID);
                } else if (add_on_video.getVisibility() == View.VISIBLE){
                    PlexusUpload.uploadGroupVideo(post_description, firebaseUser.getUid(), video_uri, CreatePostActivity.this, groupID);
                } else if (add_on_voice.getVisibility() == View.VISIBLE){
                    PlexusUpload.uploadGroupVoice(post_description, firebaseUser.getUid(),CreatePostActivity.this, mFileName, groupID);
                } else {
                    PlexusUpload.uploadGroupText(post_description, firebaseUser.getUid(), CreatePostActivity.this, groupID);
                }
            });
        } else {
            upload.setOnClickListener(v -> {
                if (add_on_image.getVisibility() == View.VISIBLE) {
                    PlexusUpload.uploadImage(post_description, firebaseUser.getUid(), add_to_moments, image_uri, CreatePostActivity.this);
                } else if (add_on_video.getVisibility() == View.VISIBLE){
                    PlexusUpload.uploadVideo(post_description, firebaseUser.getUid(), add_to_moments, video_uri, CreatePostActivity.this);
                } else if (add_on_voice.getVisibility() == View.VISIBLE){
                    PlexusUpload.uploadVoice(post_description, firebaseUser.getUid(),CreatePostActivity.this, mFileName);
                } else {
                    PlexusUpload.uploadText(post_description, firebaseUser.getUid(), CreatePostActivity.this);
                }
            });
        }
    }

    private void checkIfImageExist() {
        ImageView post_image = add_on_image.findViewById(R.id.post_image);
        ImageView post_video = add_on_video.findViewById(R.id.post_image);

        if (!(post_image.getDrawable() == null)) {
            clearImageView(post_image, add_on_image);
        } else if (!(post_video.getDrawable() == null)) {
            clearVideoView(post_video, add_on_video);
        }
    }

    private void clearImageView(ImageView post_image, View view) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        ImageView post_remove_image = add_on_image.findViewById(R.id.post_remove_image);
        Disposable remove = RxView.clicks(post_remove_image).subscribe(unit -> {
            post_image.setImageDrawable(null);
            view.setVisibility(View.GONE);
        });
        compositeDisposable.add(remove);
    }

    private void clearVideoView(ImageView post_image, View view) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        ImageView post_remove_video = add_on_video.findViewById(R.id.post_remove_image);
        Disposable remove = RxView.clicks(post_remove_video).subscribe(unit -> {
            post_image.setImageDrawable(null);
            view.setVisibility(View.GONE);
        });
        compositeDisposable.add(remove);
    }


    /**
     * Server uploading and fetching from Firebase happens here
     */

    private void getProfileData(SimpleDraweeView profile_image, TextView full_name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * Voice Posts
     */

    private void voiceRecording() {
        BottomSheetDialog voice_recording = new BottomSheetDialog(CreatePostActivity.this, R.style.BottomSheetDialogTheme);
        voice_recording.setContentView(R.layout.sheet_recording);

        Button record = voice_recording.findViewById(R.id.record_voice_note);
        TextView timer = voice_recording.findViewById(R.id.voice_time);
        ProgressBar progressBar = voice_recording.findViewById(R.id.voice_progress);
        LinearLayout linearLayout = voice_recording.findViewById(R.id.voice_recording);

        //Create Folder
        String folder_main = "Plexus/Posts/Recordings";
        file = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!file.exists()) {
            //folder doesn't exist
            file.mkdirs();
        }

        record.setOnClickListener(v -> {
            if (!recording) {
                // if recording is not already running
                //record audio
                prepareMediaRecorder();
                linearLayout.setVisibility(View.VISIBLE);
                countdownTimer(progressBar, timer);

                //View changes
                recording = true;
                record.setText("Stop Recording");
            } else {
                // if recording is already running
                //record audio by
                recorder.stop();
                recorder.release();
                recorder = null;
                linearLayout.setVisibility(View.GONE);
                add_on_voice.setVisibility(View.VISIBLE);

                //View changing
                recording = false;
                record.setText("Start Recording");
                voice_recording.dismiss();
            }

        });

        voice_recording.show();

    }

    private void prepareMediaRecorder() {
        //prepare recorder for record new file
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(mFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    private void countdownTimer(ProgressBar progressBar, TextView timer){
        myProgress = 0;

        try {
            countDownTimer.cancel();

        } catch (Exception e) {

        }

        progress = 1;
        timerEnd = timeInterval; // up to finish time

        countDownTimer = new CountDownTimer(timerEnd * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setProgress(progress, timerEnd, progressBar);
                progress = progress + 1;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String newtime = hours + ":" + minutes + ":" + seconds;

                if (newtime.equals("0:0:0")) {
                    timer.setText("00:00:00");
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    timer.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                    timer.setText("0" + hours + ":0" + minutes + ":" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    timer.setText("0" + hours + ":" + minutes + ":0" + seconds);
                } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    timer.setText(hours + ":0" + minutes + ":0" + seconds);
                } else if (String.valueOf(hours).length() == 1) {
                    timer.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else if (String.valueOf(minutes).length() == 1) {
                    timer.setText(hours + ":0" + minutes + ":" + seconds);
                } else if (String.valueOf(seconds).length() == 1) {
                    timer.setText(hours + ":" + minutes + ":0" + seconds);
                } else {
                    timer.setText(hours + ":" + minutes + ":" + seconds);
                }

            }

            @Override
            public void onFinish() {
                setProgress(progress, timerEnd, progressBar);
            }
        };
        countDownTimer.start();
    }

    private void setProgress(int startTime, int endTime, ProgressBar progressBar) {
        progressBar.setMax(endTime);
        progressBar.setSecondaryProgress(endTime);
        progressBar.setProgress(startTime);
    }

    private void addOnVoiceAttachment() {

        TextView time = add_on_voice.findViewById(R.id.voice_time);
        ImageView voice_profile_image = add_on_voice.findViewById(R.id.voice_profile_image);
        ImageView voice_background = add_on_voice.findViewById(R.id.voice_background);
        ImageView voice_note_play = add_on_voice.findViewById(R.id.voice_note_play);

        voiceBackground(voice_background, voice_profile_image);

        voice_note_play.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()){
                if(mediaPlayer!=null){
                   stopVoiceNote();
                    // Changing button image to play button
                    voice_note_play.setImageResource(R.drawable.play);
                }
            }else{
                // Resume audio
                if(mediaPlayer!=null){
                    try {
                        playVoiceNote();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Changing button image to pause button
                    voice_note_play.setImageResource(R.drawable.pause);
                }
            }
        });

        if (fileExist()){
            String mediaPath = Uri.parse(mFileName).getPath();
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mediaPath);
            long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mmr.release();

            time.setText(TimeUtils.msToString(duration));
        } else {
            //Do Nothing
        }
    }

    public boolean fileExist(){
        File file = new File(mFileName);
        return file.exists();
    }

    private void playVoiceNote() throws IOException {
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(mFileName));
        mediaPlayer.prepare();
        mediaPlayer.start();

    }

    public void stopVoiceNote() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void voiceBackground(ImageView parent, ImageView imageView) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (bitmap != null) {
                            parent.setBackground(new BitmapDrawable(CreatePostActivity.this.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Glide.with(CreatePostActivity.this)
                                    .asBitmap()
                                    .load(user.getImageurl())
                                    .error(R.mipmap.ic_launcher)
                                    .listener(new RequestListener<Bitmap>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                            if (Build.VERSION.SDK_INT >= 16) {
                                                parent.setBackground(new BitmapDrawable(
                                                        CreatePostActivity.this.getResources(),
                                                        Constants.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true)))); // ));
                                            } else {
                                                onPalette(Palette.from(resource).generate(), imageView);
                                            }
                                            imageView.setImageBitmap(resource);
                                            return false;
                                        }
                                    })
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void onPalette(Palette palette, ImageView photoView) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }

    /**
     * Background processes
     *
     * @return
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView post_image = add_on_image.findViewById(R.id.post_image);
        ImageView post_video = add_on_video.findViewById(R.id.post_image);

        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                image_uri = result.getUri();

                post_image.setImageURI(image_uri);
                add_on_image.setVisibility(View.VISIBLE);
            } else if (requestCode == VIDEO_REQUEST_CODE && data != null && data.getData() != null) {
                video_uri = data.getData();
                Glide.with(getApplicationContext()).asBitmap().load(video_uri).into(post_video);
                add_on_video.setVisibility(View.VISIBLE);

                add_on_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PostVideoViewActivity.class);
                        intent.putExtra("downloadLink", video_uri.toString());
                        startActivity(intent);
                    }
                });

            } else if (requestCode == CAMERA_REQUEST_CODE) {
                camera_uri = data.getData();
                post_image.setImageURI(camera_uri);
            } else {
                Toast.makeText(this, "Something has gone wrong!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference("Hashtags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    hashtagAdapter.add(new Hashtag(snapshot.getKey(), (int) snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        post_description.setHashtagAdapter(hashtagAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
