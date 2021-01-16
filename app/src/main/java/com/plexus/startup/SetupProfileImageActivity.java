package com.plexus.startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
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
import com.plexus.components.components.compressor.Compressor;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

public class SetupProfileImageActivity extends AppCompatActivity {

    SimpleDraweeView profile_image;
    TextView fullname;
    MaterialButton finish;

    FirebaseUser firebaseUser;
    StorageReference storageReference;

    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile_image);

        profile_image = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.fullname);
        finish = findViewById(R.id.save);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("avatars");

        profile_image.setOnClickListener(view -> CropImage.activity().start(SetupProfileImageActivity.this));

        finish.setOnClickListener(view -> {
            uploadImage();
            startActivity(new Intent(getApplicationContext(), SetupProfileActivity.class));
        });

        getUserName();
    }

    private void getUserName() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(SetupProfileImageActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(String.valueOf(System.currentTimeMillis()));

            File postImage = new File(imageUri.getPath());
            try {
                Bitmap compressImage = new Compressor(this)
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

                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("imageurl", "" + myUrl);

                                                reference.updateChildren(hashMap);
                                                progressDialog.dismiss();
                                            } else {
                                                Toast.makeText(SetupProfileImageActivity.this, "Failed!", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                        .addOnFailureListener(
                                e -> Toast.makeText(SetupProfileImageActivity.this, "", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                profile_image.setImageURI(imageUri);
                uploadImage();
            }
        }
    }
}
