package com.plexus.posts.activity.saved_posts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.plexus.R;
import com.plexus.components.components.compressor.Compressor;
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

public class CreateCollectionsSavesActivity extends AppCompatActivity {

    Button create_collection;
    TextView upload_collection_image;
    SimpleDraweeView collection_image;
    EditText collection_name;
    ImageView back;
    StorageReference storageRef;
    private Uri mImageUri;
    private String miUrlOk = "";
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_saves_create_collections_activity);

        create_collection = findViewById(R.id.create_collection);
        upload_collection_image = findViewById(R.id.upload_collection_image);
        collection_image = findViewById(R.id.collection_image);
        collection_name = findViewById(R.id.collection_name);
        back = findViewById(R.id.back);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("Saves/");

        create_collection.setOnClickListener(v -> createCollection());

        collection_image.setOnClickListener(v -> CropImage.activity().start(CreateCollectionsSavesActivity.this));

        upload_collection_image.setOnClickListener(v -> CropImage.activity().start(CreateCollectionsSavesActivity.this));

        back.setOnClickListener(v -> finish());

    }

    private void createCollection() {
        if (mImageUri != null) {
            final StorageReference fileReference = storageRef.child(firebaseUser.getUid()).child("collection_cover_" + System.currentTimeMillis());

            File postImage = new File(mImageUri.getPath());
            try {
                Bitmap compressImage = new Compressor(getApplicationContext())
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(45)
                        .compressToBitmap(postImage);

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

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
                                        String id = reference.push().getKey();

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id", id);
                                        hashMap.put("collection_image_url", miUrlOk);
                                        hashMap.put("collection_name", collection_name.getText().toString());
                                        hashMap.put("collection_owner", firebaseUser.getUid());
                                        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                                        reference.child(id).setValue(hashMap);

                                        startActivity(new Intent(CreateCollectionsSavesActivity.this, SavedPostsActivity.class));
                                        finish();

                                    } else {
                                        Toast.makeText(CreateCollectionsSavesActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                e -> Toast.makeText(CreateCollectionsSavesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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

            collection_image.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
