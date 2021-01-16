package com.plexus.startup;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.plexus.R;
import com.plexus.components.components.compressor.Compressor;
import com.plexus.main.activity.MainActivity;
import com.plexus.utils.DialogViews;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class SetupProfileActivity extends AppCompatActivity {

    TextView select_image, gender, birthday;
    EditText about;
    SimpleDraweeView profile_cover;
    LinearLayout gender_selection, birthday_selection;
    MaterialButton save;

    FirebaseUser firebaseUser;
    StorageReference storageReference;

    Uri image_uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        select_image = findViewById(R.id.select_image);
        gender = findViewById(R.id.gender);
        birthday = findViewById(R.id.birthday);
        about = findViewById(R.id.about);
        profile_cover = findViewById(R.id.profile_cover);
        gender_selection = findViewById(R.id.gender_selection);
        birthday_selection = findViewById(R.id.birthday_selection);
        save = findViewById(R.id.save);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("avatars");

        birthday_selection.setOnClickListener(
                view -> {
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    DatePickerDialog picker = new DatePickerDialog(SetupProfileActivity.this, (view1, year1, monthOfYear, dayOfMonth) ->
                            birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
                    picker.show();
                });

        gender_selection.setOnClickListener(view -> DialogViews.genderDialog(SetupProfileActivity.this, gender));

        select_image.setOnClickListener(view -> CropImage.activity().start(SetupProfileActivity.this));

        save.setOnClickListener(view -> {
            saveProfileData();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

    }

    private void saveProfileData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("bio", about.getText().toString());
        profileMap.put("birthday", birthday.getText().toString());
        profileMap.put("gender", gender.getText().toString());

        reference.updateChildren(profileMap);
        uploadCover();
    }

    private void uploadCover(){
        final ProgressDialog progressDialog = new ProgressDialog(SetupProfileActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        if (image_uri != null) {
            final StorageReference fileReference = storageReference.child(String.valueOf(System.currentTimeMillis()));

            File postImage = new File(image_uri.getPath());
            try {
                Bitmap compressImage = new Compressor(SetupProfileActivity.this)
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
                                                                .child(firebaseUser.getUid());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                image_uri = result.getUri();

                profile_cover.setImageURI(image_uri);
            } else {
                Toast.makeText(this, "Something has gone wrong!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SetupProfileActivity.this, MainActivity.class));
                finish();
            }
        }
    }

}
