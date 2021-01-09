package com.plexus.posts.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;

import java.text.MessageFormat;

public class PostVideoViewActivity extends AppCompatActivity {

    VideoView videoView;
    TextView fullname, description;
    ProgressBar progressBar;

    String downloadLink, descriptionData, id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_video_fullscreen);

        Intent intent = getIntent();
        id = intent.getStringExtra("publisher");
        downloadLink = intent.getStringExtra("downloadLink");
        descriptionData = intent.getStringExtra("description");

        videoView = findViewById(R.id.videoView);
        fullname = findViewById(R.id.fullname);
        description = findViewById(R.id.description);
        progressBar = findViewById(R.id.progressBar);

        description.setText(descriptionData);

        setVideoData(downloadLink);
        getUserData();
    }

    private void getUserData (){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setVideoData(String videoUrl){

        videoView.setVideoPath(videoUrl);
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            mp.start();
        });
        videoView.setOnCompletionListener(MediaPlayer::start);
    }

}
