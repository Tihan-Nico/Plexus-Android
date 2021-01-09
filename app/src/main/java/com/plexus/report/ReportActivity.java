package com.plexus.report;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.User;

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

public class ReportActivity extends AppCompatActivity {

    private Button emergency_call;
    private LinearLayout unfollow_user, block_user;
    private TextView unfollow_name, block_name;
    private FirebaseUser firebaseUser;
    private String profileid;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_sheet);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences("plexus", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        unfollow_user = findViewById(R.id.unfollow_user);
        block_user = findViewById(R.id.block_user);
        unfollow_name = findViewById(R.id.unfollow_name);
        block_name = findViewById(R.id.block_name);

        bottomSheetDialog = new BottomSheetDialog(ReportActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_block);
        TextView title = bottomSheetDialog.findViewById(R.id.title_block);
        TextView your_summary = bottomSheetDialog.findViewById(R.id.your_summary);
        TextView their_title = bottomSheetDialog.findViewById(R.id.their_title);
        TextView their_summary = bottomSheetDialog.findViewById(R.id.their_summary);
        ImageView image_profile_mine = bottomSheetDialog.findViewById(R.id.image_profile_mine);
        ImageView image_profile_theirs = bottomSheetDialog.findViewById(R.id.image_profile_theirs);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                unfollow_name.setText("Unfollow " + user.getName() + " " + user.getSurname());
                block_name.setText("Block " + user.getName() + " " + user.getSurname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        block_user.setOnClickListener(v -> bottomSheetDialog.show());

        emergency_call.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:0614181903"));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                title.setText("Block " + user.getName() + " " + user.getSurname()+ "?");
                your_summary.setText("You'll no longer be able to see" + user.getName() + " " + user.getSurname()+ "'s" + " profile or send them messages");
                their_title.setText("What happens for " + user.getName() + " " + user.getSurname());
                their_summary.setText(user.getName() + " " + user.getSurname() + " won't get notified that you've blocked them, but they won't be able to see your profile, posts or message you.");
                Glide.with(getApplicationContext()).asBitmap().load(user.getImageurl()).into(image_profile_mine);
                Glide.with(getApplicationContext()).asBitmap().load(user.getImageurl()).into(image_profile_theirs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
