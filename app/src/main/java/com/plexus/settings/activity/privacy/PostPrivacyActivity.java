package com.plexus.settings.activity.privacy;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.plexus.R;
import com.plexus.model.settings.PostPrivacy;

import java.util.HashMap;

public class PostPrivacyActivity extends AppCompatActivity {

    SwitchMaterial allow_download, allow_screenshot;
    ImageView back;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_privacy_post);

        back = findViewById(R.id.back);
        allow_download = findViewById(R.id.allow_download);
        allow_screenshot = findViewById(R.id.allow_screenshot);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        allow_download.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateDownload(isChecked);
        });

        allow_screenshot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateScreenshot(isChecked);
        });

        RxView.clicks(back).subscribe(unit -> finish());

        fetchData();

    }

    private void fetchData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostPrivacy postPrivacy = dataSnapshot.getValue(PostPrivacy.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        runOnUiThread(() -> {
                            allow_download.setChecked(postPrivacy.isAllow_download());
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Posts");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostPrivacy postPrivacy = dataSnapshot.getValue(PostPrivacy.class);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        runOnUiThread(() -> {
                            allow_screenshot.setChecked(postPrivacy.isAllow_screenshot());
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateScreenshot(boolean checked) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Posts");

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("allow_screenshot", checked);

        reference.updateChildren(profileMap);
    }

    private void updateDownload(boolean checked) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Privacy").child("Posts");

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("allow_download", checked);

        reference.updateChildren(profileMap);
    }

}
