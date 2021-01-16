package com.plexus.story.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.tntkhang.gmailsenderlibrary.GMailSender;
import com.github.tntkhang.gmailsenderlibrary.GmailListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.account.activity.FollowersActivity;
import com.plexus.model.account.User;
import com.plexus.model.posts.Story;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

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

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;

    StoriesProgressView storiesProgressView;
    SimpleDraweeView story_photo, image;
    TextView story_username;
    LinearLayout r_seen, my_storie;
    TextView seen_number;
    View rootview;
    ImageView story_delete, menu;
    BottomSheetDialog bottomSheetDialog, report_bug, report_storie;
    List<String> images;
    List<String> storyids;
    String userid;
    VideoView video;
    private RelativeTimeTextView timestamp;
    private LinearLayout add_storie, delete_storie;
    private FirebaseUser firebaseUser;

    private final View.OnTouchListener onTouchListener =
            new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pressTime = System.currentTimeMillis();
                            storiesProgressView.pause();
                            return false;
                        case MotionEvent.ACTION_UP:
                            long now = System.currentTimeMillis();
                            storiesProgressView.resume();
                            return limit < now - pressTime;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity);

        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        story_photo = findViewById(R.id.story_photo);
        story_username = findViewById(R.id.story_username);
        menu = findViewById(R.id.menu);
        video = findViewById(R.id.video);
        my_storie = findViewById(R.id.my_storie);
        timestamp = findViewById(R.id.story_time);
        rootview = getWindow().getDecorView().findViewById(android.R.id.content);
        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        delete_storie = findViewById(R.id.delete_storie);
        add_storie = findViewById(R.id.add_storie);

        userid = getIntent().getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            my_storie.setVisibility(View.VISIBLE);
        } else {
            my_storie.setVisibility(View.GONE);
        }

        getStories(userid);
        userInfo(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_story);
        bottomSheetDialog.setOnDismissListener(dialog -> storiesProgressView.resume());
        LinearLayout bug_report = bottomSheetDialog.findViewById(R.id.bug_report);
        LinearLayout report_story = bottomSheetDialog.findViewById(R.id.report_story);

        report_bug = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        report_bug.setContentView(R.layout.sheet_bug_reporting);
        ImageView screenshot = report_bug.findViewById(R.id.screenshot);
        ChipGroup bug_chip_group = report_bug.findViewById(R.id.bug_chip_group);
        MaterialButton report = report_bug.findViewById(R.id.send_report);
        report_bug.setOnShowListener(dialog -> {
            storiesProgressView.pause();
            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);
            screenshot.setImageBitmap(bitmap);
        });
        report_bug.setOnDismissListener(dialog -> storiesProgressView.resume());
        report.setOnClickListener(v -> {
            try {
                sendMail();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });
        bug_chip_group.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = bug_chip_group.findViewById(checkedId);

            if (chip != null) {
                Toast.makeText(this, chip.getText().toString(), Toast.LENGTH_LONG).show();
            }

        });

        report_storie = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        report_storie.setContentView(R.layout.report_sheet);
        report_storie.setOnDismissListener(dialog -> storiesProgressView.resume());
        report_story.setOnClickListener(v -> {
            report_storie.show();
            storiesProgressView.pause();
            bottomSheetDialog.dismiss();
        });


        menu.setOnClickListener(v -> {
            bottomSheetDialog.show();
            storiesProgressView.pause();
        });

        bug_report.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            report_bug.show();
        });

        r_seen.setOnClickListener(
                view -> {
                    Intent intent = new Intent(StoryActivity.this, FollowersActivity.class);
                    intent.putExtra("id", userid);
                    intent.putExtra("storyid", storyids.get(counter));
                    intent.putExtra("title", "views");
                    startActivity(intent);
                });

        add_storie.setOnClickListener(v -> {
            Intent intent = new Intent(StoryActivity.this, AddStoryActivity.class);
            startActivity(intent);
        });

        delete_storie.setOnClickListener(
                view -> {
                    DatabaseReference reference =
                            FirebaseDatabase.getInstance()
                                    .getReference("Story")
                                    .child(userid)
                                    .child(storyids.get(counter));
                    reference
                            .removeValue()
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(StoryActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                });

    }

    @Override
    public void onNext() {
        image.setImageURI(images.get(++counter));
        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        image.setImageURI(images.get(--counter));
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid) {
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        Story story = dataSnapshot.getValue(Story.class);
                        images.clear();
                        storyids.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            story = snapshot.getValue(Story.class);
                            long timecurrent = System.currentTimeMillis();
                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                                images.add(MasterCipher.decrypt(story.getImageurl()));
                                storyids.add(story.getStoryid());
                            }
                        }

                        storiesProgressView.setStoriesCount(images.size());
                        storiesProgressView.setStoryDuration(5000L);
                        storiesProgressView.setStoriesListener(StoryActivity.this);
                        storiesProgressView.startStories(counter);

                        image.setImageURI(images.get(counter));
                        timestamp.setReferenceTime(Long.parseLong(story.getTimestamp()));
                        addView(storyids.get(counter));
                        seenNumber(storyids.get(counter));
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
    }

    private void userInfo(String userid) {
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        story_photo.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                        story_username.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
    }

    private void sendMail() throws PackageManager.NameNotFoundException {

        String serviceType = Context.TELEPHONY_SERVICE;
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            TelephonyManager m_telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(serviceType);
                            if (ActivityCompat.checkSelfPermission(StoryActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            GMailSender.withAccount("tihannicopaxton2@gmail.com", "Chocolates123")
                                    .withTitle("Bug Report - Plexus Android")
                                    .withBody("A bug has happened in the StoryActivity.class"
                                            + "\n"
                                            + "\n Plexus User Information"
                                            + "\n"
                                            + "\n Users User ID: " + firebaseUser.getUid()
                                            + "\n Users Email: " + firebaseUser.getEmail()
                                            + "\n Users Full Name: " + user.getName() + " " + user.getSurname()
                                            + "\n Plexus Story ID: " + child.getKey()
                                            + "\n Plexus Story Link: " + child.getRef().toString()
                                            + "\n"
                                            + "\n Plexus Application Information"
                                            + "\n"
                                            + "\n APP Package Name: " + getPackageName()
                                            + "\n APP Version Name: " + pInfo.versionName
                                            + "\n APP Version Code: " + pInfo.versionCode
                                            + "\n"
                                            + "\n Mobile Operator Information"
                                            + "\n"
                                            + "\n Device ID: " + m_telephonyManager.getDeviceId()
                                            + "\n Subscriber ID: " + m_telephonyManager.getSubscriberId()
                                            + "\n Network Operator: " + m_telephonyManager.getNetworkOperator()
                                            + "\n Sim Operator Name: " + m_telephonyManager.getSimOperatorName()
                                            + "\n"
                                            + "\n Device Information"
                                            + "\n"
                                            + "\n OS Version: " + System.getProperty("os.version") + " (" + android.os.Build.VERSION.INCREMENTAL + ")"
                                            + "\n OS API Level: " + android.os.Build.VERSION.SDK
                                            + "\n Device: " + android.os.Build.DEVICE
                                            + "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")"
                                            + "\n Manufacturer: " + android.os.Build.MANUFACTURER
                                            + "\n Other TAGS: " + android.os.Build.TAGS
                                            + "\n screenWidth: " + getWindow().getWindowManager().getDefaultDisplay().getWidth()
                                            + "\n screenHeight: " + getWindow().getWindowManager().getDefaultDisplay().getHeight()
                                    )
                                    .withSender(getString(R.string.app_name))
                                    .toEmailAddress("plexusincsa@gmail.com") // one or multiple addresses separated by a comma
                                    .withListenner(new GmailListener() {
                                        @Override
                                        public void sendSuccess() {
                                            Toast.makeText(StoryActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void sendFail(String err) {
                                            Toast.makeText(StoryActivity.this, "Fail: " + err, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .send();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //
    private void addView(String storyid) {
        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

        } else {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Story")
                    .child(userid)
                    .child(storyid)
                    .child("views")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(true);
        }
    }

    private void seenNumber(String storyid) {
        DatabaseReference reference =
                FirebaseDatabase.getInstance()
                        .getReference("Story")
                        .child(userid)
                        .child(storyid)
                        .child("views");
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        seen_number.setText(dataSnapshot.getChildrenCount() + " Viewers");
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
}
