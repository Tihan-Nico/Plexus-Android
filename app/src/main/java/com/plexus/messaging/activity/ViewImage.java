package com.plexus.messaging.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.components.components.ImageView.Constants;
import com.plexus.components.components.ImageView.PhotoView;
import com.plexus.model.messaging.Message;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;
import com.plexus.utils.MediaUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

public class ViewImage extends AppCompatActivity {

    RelativeLayout parent;
    PhotoView photoView;
    Bitmap bitmap;
    FirebaseUser firebaseUser;
    String messageid;
    String userid;
    boolean showPostDetails = true;
    ImageView back;
    TextView fullname;
    private Toolbar toolbar;
    String imagePath = MediaUtil.getRootPath()+"/Plexus/Images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_image_view_fullscreen);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        messageid = intent.getStringExtra("messageid");
        userid = intent.getStringExtra("userid");

        photoView = findViewById(R.id.image_view);
        parent = findViewById(R.id.background);
        back = findViewById(R.id.back);
        toolbar = findViewById(R.id.toolbar);
        fullname = findViewById(R.id.fullname);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DatabaseReference reference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com/").getReference().child("Chats").child(messageid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                File image = new File(imagePath + message.getFilename());
                if (bitmap != null) {
                    parent.setBackground(new BitmapDrawable(ViewImage.this.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true)))); // ));
                    photoView.setImageBitmap(bitmap);
                } else {
                    if(image.exists()){
                        Glide.with(ViewImage.this)
                                .asBitmap()
                                .load(Uri.fromFile(image))
                                .error(R.mipmap.ic_launcher)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        onPalette(Palette.from(resource).generate());
                                        photoView.setImageBitmap(resource);
                                        return false;
                                    }
                                })
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(photoView);
                        Toast.makeText(getApplicationContext(), "loaded from storage", Toast.LENGTH_SHORT).show();
                    } else {
                        Glide.with(ViewImage.this)
                                .asBitmap()
                                .load(message.getDownloadURL())
                                .error(R.mipmap.ic_launcher)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        onPalette(Palette.from(resource).generate());
                                        photoView.setImageBitmap(resource);
                                        return false;
                                    }
                                })
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(photoView);
                        Toast.makeText(getApplicationContext(), "loaded from network", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {}
        });

        back.setOnClickListener(view1 -> finish());

        photoView.setOnClickListener(
                view13 -> {
                    if (showPostDetails) {
                        toolbar.setVisibility(View.GONE);
                        showPostDetails = false;
                    } else {
                        toolbar.setVisibility(View.VISIBLE);
                        showPostDetails = true;
                    }
                });

        getUserDetails(fullname);

    }

    private void getUserDetails(TextView fullname){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.equals(firebaseUser.getUid())){
                    fullname.setText("You");
                } else {
                    fullname.setText(MasterCipher.decrypt(String.format("%s %s", user.getName(), user.getSurname())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteMessage(){

    }

    private void forwardMessage(){

    }

    private Uri getBitmapUri(Bitmap bitmap) {

        try {

            OutputStream outputStream=null;
            File dir=new File(Environment.DIRECTORY_PICTURES + "/Plexus");

            if(!dir.exists()){
                dir.mkdirs();
            }

            File file=new File(dir,"Plexus"+System.currentTimeMillis()+".jpeg");
            if(file.exists()){
                file.delete();
            }else{
                file.createNewFile();
            }

            outputStream = new FileOutputStream(file);
            BufferedOutputStream outputStream1=new BufferedOutputStream(outputStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream1);
            outputStream1.flush();
            outputStream1.close();
            return Uri.parse(file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void shareImage(){
        Uri imageInternalUri = getBitmapUri(getBitmap(photoView));
        Intent intent = new Intent(Intent.ACTION_SEND).setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageInternalUri);
        startActivity(Intent.createChooser(intent, "Share using..."));
    }

    private Bitmap getBitmap(ImageView view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.parseColor("#212121"));
        }
        view.draw(canvas);
        return bitmap;
    }

    private void getImageUrl(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://plexus-network-chat.firebaseio.com/").getReference("Chats").child(messageid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                DownloadImage(MasterCipher.decrypt(message.getDownloadURL()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void DownloadImage(String ImageUrl) {

        if (ContextCompat.checkSelfPermission(ViewImage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ViewImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            ActivityCompat.requestPermissions(ViewImage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            Toast.makeText(this, "Need permission to download", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show();
            //Asynctask to create a thread to downlaod image in the background
            /*new DownloadService(ViewImage.this).execute(ImageUrl);*/
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
                shareImage();
                return true;
            case R.id.download:
                getImageUrl();
                return true;
            case R.id.delete:
                deleteMessage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPalette(Palette palette) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }

}
