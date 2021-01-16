package com.plexus.story.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.plexus.R;
import com.plexus.components.components.compressor.Compressor;
import com.plexus.main.activity.MainActivity;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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

public class AddStoryActivity extends AppCompatActivity {

  String miUrlOk = "";
  StorageReference storageRef;
  private Uri mImageUri;
  private StorageTask uploadTask;
  private FirebaseUser fuser;

  private ImageView profile_image, story_photo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_story);

    storageRef = FirebaseStorage.getInstance().getReference("story");
    fuser = FirebaseAuth.getInstance().getCurrentUser();

    profile_image = findViewById(R.id.profile_image);
    story_photo = findViewById(R.id.story_photo);

    CropImage.activity().setAspectRatio(9, 16).start(AddStoryActivity.this);

    DatabaseReference databaseReference =
        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
    databaseReference.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            User user = dataSnapshot.getValue(User.class);
            Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
  }

  private void uploadImage_10() {
    if (mImageUri != null) {
      final StorageReference fileReference =
          storageRef.child(String.valueOf(System.currentTimeMillis()));

      File postImage = new File(mImageUri.getPath());
      try {
        Bitmap compressImage = new Compressor(this).setQuality(100).compressToBitmap(postImage);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        compressImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] compressedImage = byteArrayOutputStream.toByteArray();

        final UploadTask uploadTask = fileReference.putBytes(compressedImage);
        uploadTask
            .continueWithTask(
                task -> {
                  if (!task.isSuccessful()) {
                    throw task.getException();
                  }
                  return fileReference.getDownloadUrl();
                })
            .addOnCompleteListener(
                task -> {
                  if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    miUrlOk = downloadUri.toString();

                    String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference reference =
                        FirebaseDatabase.getInstance().getReference("Story").child(myid);

                    String storyid = reference.push().getKey();
                    long timeend = System.currentTimeMillis() + 86400000; // 1 day later

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageurl", MasterCipher.encrypt(miUrlOk));
                    hashMap.put("timestart", ServerValue.TIMESTAMP);
                    hashMap.put("timeend", timeend);
                    hashMap.put("storyid", storyid);
                    hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    hashMap.put("userid", myid);

                    reference.child(storyid).setValue(hashMap);

                    finish();

                  } else {
                    Toast.makeText(AddStoryActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                  }
                })
            .addOnFailureListener(
                e ->
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                        .show());

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      mImageUri = result.getUri();

      story_photo.setImageURI(mImageUri);
      uploadImage_10();
      profileActivity();
    } else {
      Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
      finish();
    }
  }

  private void profileActivity(){
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Activity Log");
    String id = reference.push().getKey();

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("id", id);
    hashMap.put("title", "You added a new story");
    hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
    hashMap.put("isStory", true);

    reference.child(id).setValue(hashMap);
  }
}
