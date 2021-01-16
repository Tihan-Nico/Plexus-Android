package com.plexus.components.background;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.plexus.components.components.compressor.Compressor;
import com.plexus.components.components.socials.commons.SocialAutoCompleteTextView;
import com.plexus.groups.activity.GroupActivity;
import com.plexus.main.activity.MainActivity;
import com.plexus.utils.Files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class PlexusUpload {

    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static DatabaseReference databaseReference;
    public static StorageReference storageReference;
    public static String url = "";
    public static ProgressDialog pd;

    //User Upload

    public static void uploadText(SocialAutoCompleteTextView description, String publisher, Context context) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        String ID = databaseReference.getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", ID);
        hashMap.put("description", description.getText().toString());
        hashMap.put("publisher", publisher);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("type", "text");

        databaseReference.child(ID).setValue(hashMap);

        List<String> hashtags = description.getHashtags();
        if (!hashtags.isEmpty()) {
            for (String tag : hashtags) {
                DocumentReference documentReference = db.collection("Hashtages").document(tag.toLowerCase());
                documentReference.addSnapshotListener((value, error) -> {
                    if (value.exists()) {
                        db.collection("Hashtags").document(tag.toLowerCase()).collection("Posts").document(ID).set(true);
                    } else {
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("createdBy", publisher);
                        hashMap1.put("createdAt", String.valueOf(System.currentTimeMillis()));

                        db.collection("Hashtags").document(tag.toLowerCase()).collection("Posts").document(ID).set(true);

                        documentReference.set(hashMap1);
                    }
                });
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void uploadImage(SocialAutoCompleteTextView description, String publisher, MaterialCheckBox add_to_moments, Uri image_uri, Context context) {

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        storageReference = FirebaseStorage.getInstance().getReference("posts/" + "Images");
        if (image_uri != null) {

            String fileName = String.valueOf(System.currentTimeMillis());

            final StorageReference fileReference = storageReference.child(fileName);

            File postImage = new File(image_uri.getPath());
            try {
                Bitmap compressImage = new Compressor(context)
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
                                        url = downloadUri.toString();

                                        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                                        String ID = databaseReference.getKey();

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("postid", ID);
                                        hashMap.put("postimage", url);
                                        hashMap.put("fileName", fileName);
                                        hashMap.put("description", description.getText().toString());
                                        hashMap.put("publisher", publisher);
                                        hashMap.put("image", true);
                                        hashMap.put("type", Files.getFileType(image_uri.toString()));
                                        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                                        databaseReference.child(ID).setValue(hashMap);
                                        pd.dismiss();

                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);

                                    } else {
                                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadVideo(SocialAutoCompleteTextView description, String publisher, MaterialCheckBox add_to_moments, Uri image_uri, Context context) {

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        storageReference = FirebaseStorage.getInstance().getReference("posts/" + "Videos");

        if (image_uri != null) {
            final StorageReference fileReference = storageReference.child(String.valueOf(System.currentTimeMillis()));

            final UploadTask uploadTask = fileReference.putFile(image_uri);
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
                                    url = downloadUri.toString();

                                    databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                                    String ID = databaseReference.getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("postid", ID);
                                    hashMap.put("videoURL", url);
                                    hashMap.put("fileName", String.valueOf(System.currentTimeMillis()));
                                    hashMap.put("description", description.getText().toString());
                                    hashMap.put("publisher", publisher);
                                    hashMap.put("video", true);
                                    hashMap.put("type", "video");
                                    hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                                    databaseReference.child(ID).setValue(hashMap);

                                    pd.dismiss();

                                    Intent intent = new Intent(context, MainActivity.class);
                                    context.startActivity(intent);

                                } else {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public static void uploadVoice(SocialAutoCompleteTextView description, String publisher, Context context, String fileName) {

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        String filePath = "posts/" + "Voice Notes/" + "post_" + System.currentTimeMillis() + "_voice";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePath);

        final Uri uri = Uri.fromFile(new File(fileName).getAbsoluteFile());

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            String downloadLinkAudio = uri1.toString();
            databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
            String ID = databaseReference.getKey();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postid", ID);
            hashMap.put("audioURL", downloadLinkAudio);
            hashMap.put("fileName", String.valueOf(System.currentTimeMillis()));
            hashMap.put("description", description.getText().toString());
            hashMap.put("publisher", publisher);
            hashMap.put("audio", true);
            hashMap.put("type", "audio");
            hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

            databaseReference.child(ID).setValue(hashMap);

            pd.dismiss();

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }));
    }

    //Group Upload

    public static void uploadGroupCover(Context context, Uri coverURI, String groupID) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference("groups/" + groupID + "/Images");
        if (coverURI != null) {
            final StorageReference fileReference = storageReference.child(String.valueOf(System.currentTimeMillis()));

            File postImage = new File(coverURI.getPath());
            try {
                Bitmap compressImage =
                        new Compressor(context)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(50)
                                .compressToBitmap(postImage);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                compressImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] compressedImage = byteArrayOutputStream.toByteArray();

                final UploadTask uploadTask = fileReference.putBytes(compressedImage);
                uploadTask.continueWithTask((Continuation) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener(
                        (OnCompleteListener<Uri>)
                                task -> {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        String myUrl = downloadUri.toString();
                                        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("coverImageUrl", myUrl);
                                        databaseReference.updateChildren(hashMap);

                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(context, "", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadGroupText(SocialAutoCompleteTextView description, String publisher, Context context, String groupID) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts Groups");
        String ID = databaseReference.getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", ID);
        hashMap.put("description", description.getText().toString());
        hashMap.put("publisher", publisher);
        hashMap.put("groupID", groupID);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("type", "text");

        databaseReference.child(ID).setValue(hashMap);

        List<String> hashtags = description.getHashtags();
        if (!hashtags.isEmpty()) {
            for (String tag : hashtags) {
                DocumentReference documentReference = db.collection("Hashtages").document(tag.toLowerCase());
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            db.collection("Hashtags").document(tag.toLowerCase()).collection("Posts").document(ID).set(true);
                        } else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("createdBy", publisher);
                            hashMap.put("createdAt", String.valueOf(System.currentTimeMillis()));

                            db.collection("Hashtags").document(tag.toLowerCase()).collection("Posts").document(ID).set(true);

                            documentReference.set(hashMap);
                        }
                    }
                });
            }
        }

        Intent intent = new Intent(context, GroupActivity.class);
        intent.putExtra("group_id", groupID);
        intent.putExtra("user_id", publisher);
        context.startActivity(intent);
    }

    public static void uploadGroupImage(SocialAutoCompleteTextView description, String publisher, Uri image_uri, Context context, String groupID) {

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        storageReference = FirebaseStorage.getInstance().getReference("Groups/" + "Posts/" + "Images");
        if (image_uri != null) {

            String fileName = String.valueOf(System.currentTimeMillis());

            final StorageReference fileReference = storageReference.child(fileName);

            File postImage = new File(image_uri.getPath());
            try {
                Bitmap compressImage = new Compressor(context)
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
                                        url = downloadUri.toString();

                                        databaseReference = FirebaseDatabase.getInstance().getReference("Posts Groups");
                                        String ID = databaseReference.getKey();

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("postid", ID);
                                        hashMap.put("postimage", url);
                                        hashMap.put("description", description.getText().toString());
                                        hashMap.put("publisher", publisher);
                                        hashMap.put("type", Files.getFileType(image_uri.toString()));
                                        hashMap.put("groupID", groupID);
                                        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                                        databaseReference.child(ID).setValue(hashMap);
                                        pd.dismiss();

                                        Intent intent = new Intent(context, GroupActivity.class);
                                        intent.putExtra("group_id", groupID);
                                        intent.putExtra("user_id", publisher);
                                        context.startActivity(intent);

                                    } else {
                                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadGroupVideo(SocialAutoCompleteTextView description, String publisher, Uri image_uri, Context context, String groupID) {

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        storageReference = FirebaseStorage.getInstance().getReference("Groups/" + "Posts/" + "Videos");

        if (image_uri != null) {
            final StorageReference fileReference = storageReference.child(String.valueOf(System.currentTimeMillis()));

            final UploadTask uploadTask = fileReference.putFile(image_uri);
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
                                    url = downloadUri.toString();

                                    databaseReference = FirebaseDatabase.getInstance().getReference("Posts Groups");
                                    String ID = databaseReference.getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("postid", ID);
                                    hashMap.put("videoURL", url);
                                    hashMap.put("description", description.getText().toString());
                                    hashMap.put("publisher", publisher);
                                    hashMap.put("type", "video");
                                    hashMap.put("groupID", groupID);
                                    hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

                                    databaseReference.child(ID).setValue(hashMap);

                                    pd.dismiss();

                                    Intent intent = new Intent(context, GroupActivity.class);
                                    intent.putExtra("group_id", groupID);
                                    intent.putExtra("user_id", publisher);
                                    context.startActivity(intent);

                                } else {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public static void uploadGroupVoice(SocialAutoCompleteTextView description, String publisher, Context context, String fileName, String groupID) {

        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();

        String filePath = "Groups/" + "Posts" + "Voice Notes/" + "post_" + System.currentTimeMillis() + "_voice";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePath);

        final Uri uri = Uri.fromFile(new File(fileName).getAbsoluteFile());

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            String downloadLinkAudio = uri1.toString();
            databaseReference = FirebaseDatabase.getInstance().getReference("Posts Groups");
            String ID = databaseReference.getKey();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postid", ID);
            hashMap.put("audioURL", downloadLinkAudio);
            hashMap.put("description", description.getText().toString());
            hashMap.put("publisher", publisher);
            hashMap.put("type", "audio");
            hashMap.put("groupID", groupID);
            hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

            databaseReference.child(ID).setValue(hashMap);

            pd.dismiss();

            Intent intent = new Intent(context, GroupActivity.class);
            intent = intent.putExtra("group_id", groupID);
            context.startActivity(intent);
        }));
    }
}
