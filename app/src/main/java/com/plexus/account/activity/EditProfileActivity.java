package com.plexus.account.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.plexus.R;
import com.plexus.components.background.DialogInformation;
import com.plexus.components.components.compressor.Compressor;
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

public class EditProfileActivity extends AppCompatActivity {

  StorageReference storageReference;
  String miUrlOk = "";
  private TextView name, surname, about_view, username, email, feeling, website;
  ImageView save;
  private SimpleDraweeView image_profile, profile_cover;
  LinearLayout status, lin_name, lin_surname, lin_about, lin_website;
  private FirebaseUser fuser;
  private Uri imageUri, coverURI;
  private Dialog feelings;

  final int PROFILE_IMAGE = 1;
  final int PROFILE_COVER = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.profile_edit);

    name = findViewById(R.id.name);
    surname = findViewById(R.id.surname);
    lin_about = findViewById(R.id.lin_about);
    lin_name = findViewById(R.id.lin_name);
    lin_surname = findViewById(R.id.lin_surname);
    lin_website = findViewById(R.id.lin_website);
    status = findViewById(R.id.status);
    feeling = findViewById(R.id.feeling);
    about_view = findViewById(R.id.about_view);
    email = findViewById(R.id.email);
    username = findViewById(R.id.username);
    website = findViewById(R.id.website);
    save = findViewById(R.id.save);
    image_profile = findViewById(R.id.profile_image);
    profile_cover = findViewById(R.id.profile_cover);

    storageReference = FirebaseStorage.getInstance().getReference("avatars");
    fuser = FirebaseAuth.getInstance().getCurrentUser();

    feelings = new Dialog(EditProfileActivity.this);
    feelings.setContentView(R.layout.dialog_list);
    feelings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    feelings.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    final ListView feelings_list = feelings.findViewById(R.id.list);
    feelings_list.setOnItemClickListener(
        (adapterView, view, position, id) -> {
          String selectedFromList = (String) feelings_list.getItemAtPosition(position);
          feeling.setText(selectedFromList);
          feelings.dismiss();
        });

    status.setOnClickListener(view -> feelings.show());

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
    databaseReference.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            name.setText(MasterCipher.decrypt(user.getName()));
            surname.setText(MasterCipher.decrypt(user.getSurname()));
            about_view.setText(MasterCipher.decrypt(user.getBio()));
            username.setText(MasterCipher.decrypt(user.getUsername()));
            email.setText(fuser.getEmail());
            feeling.setText(user.getFeeling());
            website.setText(MasterCipher.decrypt(user.getWebsite()));
            image_profile.setImageURI(MasterCipher.decrypt(user.getImageurl()));
            profile_cover.setImageURI(MasterCipher.decrypt(user.getProfile_cover()));

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    save.setOnClickListener(
        v -> {
          updateProfile();
          finish();
        });

    lin_name.setOnClickListener(v -> DialogInformation.editName(EditProfileActivity.this, fuser.getUid()));

    lin_surname.setOnClickListener(v -> DialogInformation.editSurname(EditProfileActivity.this, fuser.getUid()));

    lin_about.setOnClickListener(v -> DialogInformation.editBio(EditProfileActivity.this, fuser.getUid()));

    lin_website.setOnClickListener(v -> DialogInformation.editWebsite(EditProfileActivity.this, fuser.getUid()));

    /*expansionLayout.addListener((expansionLayout, expanded) -> {

    });*/

    image_profile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CropImage.activity().start(EditProfileActivity.this);
      }
    });

    profile_cover.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PROFILE_COVER);
      }
    });

  }

  private void uploadImage() {
    final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
    progressDialog.setMessage("Please wait...");
    progressDialog.show();

    if (imageUri != null) {
      final StorageReference fileReference =
          storageReference.child(String.valueOf(System.currentTimeMillis()));

      File postImage = new File(imageUri.getPath());
      try {
        Bitmap compressImage =
            new Compressor(this)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(35)
                .compressToBitmap(postImage);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        compressImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] compressedImage = byteArrayOutputStream.toByteArray();

        final UploadTask uploadTask = fileReference.putBytes(compressedImage);
        uploadTask
            .continueWithTask(
                (Continuation)
                    task -> {
                      if (!task.isSuccessful()) {
                        throw task.getException();
                      }

                      return fileReference.getDownloadUrl();
                    })
            .addOnCompleteListener(
                (OnCompleteListener<Uri>)
                    task -> {
                      if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference =
                            FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(fuser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", "" + myUrl);

                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();
                      } else {
                        Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT)
                            .show();
                      }
                    })
            .addOnFailureListener(
                e -> Toast.makeText(EditProfileActivity.this, "", Toast.LENGTH_SHORT).show());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void addToTimeline() {
    final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
    progressDialog.setMessage("Please wait...");
    progressDialog.show();

    final StorageReference fileReference = storageReference.child(String.valueOf(System.currentTimeMillis()));

    File postImage = new File(imageUri.getPath());
    try {
      Bitmap compressImage =
          new Compressor(this)
              .setMaxWidth(640)
              .setMaxHeight(480)
              .setQuality(35)
              .compressToBitmap(postImage);

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      compressImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
      byte[] compressedImage = byteArrayOutputStream.toByteArray();

      final UploadTask uploadTask = fileReference.putBytes(compressedImage);
      uploadTask
          .continueWithTask(
              (Continuation)
                  task -> {
                    if (!task.isSuccessful()) {
                      throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                  })
          .addOnCompleteListener(
              (OnCompleteListener<Uri>)
                  task -> {
                    if (task.isSuccessful()) {
                      Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();

                        DatabaseReference reference =
                                FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", "");
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("publisher", fuser.getUid());
                        hashMap.put("type", "profile_image");
                        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                        reference.child(postid).setValue(hashMap);
                        progressDialog.dismiss();
                    } else {
                      Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT)
                          .show();
                    }
                  })
          .addOnFailureListener(
              e -> Toast.makeText(EditProfileActivity.this, "", Toast.LENGTH_SHORT).show());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void uploadCover(){
    final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
    progressDialog.setMessage("Please wait...");
    progressDialog.show();

    if (coverURI != null) {
      final StorageReference fileReference =
              storageReference.child(String.valueOf(System.currentTimeMillis()));

      File postImage = new File(coverURI.getPath());
      try {
        Bitmap compressImage =
                new Compressor(EditProfileActivity.this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(35)
                        .compressToBitmap(postImage);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        compressImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] compressedImage = byteArrayOutputStream.toByteArray();

        final UploadTask uploadTask = fileReference.putBytes(compressedImage);
        uploadTask
                .continueWithTask(
                        (Continuation)
                                task -> {
                                  if (!task.isSuccessful()) {
                                    throw task.getException();
                                  }

                                  return fileReference.getDownloadUrl();
                                })
                .addOnCompleteListener(
                        (OnCompleteListener<Uri>)
                                task -> {
                                  if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String myUrl = downloadUri.toString();

                                    DatabaseReference reference =
                                            FirebaseDatabase.getInstance()
                                                    .getReference("Users")
                                                    .child(fuser.getUid());

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("profile_cover", "" + myUrl);

                                    reference.updateChildren(hashMap);
                                    progressDialog.dismiss();
                                  } else {
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT)
                                            .show();
                                  }
                                })
                .addOnFailureListener(
                        e -> Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK){
        imageUri = result.getUri();

        uploadImage();
        addToTimeline();
      }
    }
    else if (requestCode == PROFILE_COVER) {
      if (data != null) {
        coverURI = data.getData();
        CropImage.activity(coverURI).start(this);

        uploadCover();
      }
    }
  }

  private void updateProfile() {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

    HashMap<String, Object> profileMap = new HashMap<>();
    profileMap.put("feeling", feeling.getText().toString());

    reference.updateChildren(profileMap);
  }
}
