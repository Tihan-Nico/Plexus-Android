package com.plexus.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.startup.LoginActivity;
import com.plexus.model.account.Banned;

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

public class UserBannedActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    TextView time_banned, read_more, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_banned);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        time_banned = findViewById(R.id.time_banned);
        read_more = findViewById(R.id.read_more);
        description = findViewById(R.id.description);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banned").child(firebaseUser.getUid()).child("Times");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long times = dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        if(times == 3){
                            runOnUiThread(() -> time_banned.setText("You have been banned permanently"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getType();

    }

    private void getType(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banned").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Banned banned = dataSnapshot.getValue(Banned.class);
                String type = banned.getType();

                if (type.equals("community")){
                    runOnUiThread(() -> description.setText("You have been banned for breaking our community rules."));
                } else if(type.equals("fake_account")){
                    runOnUiThread(() -> description.setText("Your account was banned due to a suspicion of being a fake account."));
                } else if(type.equals("automation")){
                    runOnUiThread(() -> description.setText("Your account got banned due to the use of automation tools which are not allowed on Plexus. This ban will result in termination of your account effectively."));
                } else if(type.equals("proxy")){
                    runOnUiThread(() -> description.setText("Your account is banned and up for termination due to using proxies."));
                } else if(type.equals("buying_followers")){
                    runOnUiThread(() -> description.setText("Buying followers is against Plexus rules and therefor your account is temporarely banned"));
                } else if(type.equals("buying_account")){
                    runOnUiThread(() -> description.setText("We do not tolerate people buying Plexus accounts. This account is up for termination."));
                } else if(type.equals("spamming")){
                    runOnUiThread(() -> description.setText("Spamming in Plexus is against our community standards and therefor resulted in a ban."));
                } else if(type.equals("copyright_content")){
                    runOnUiThread(() -> description.setText("You were reported for multiple counts of using copyrighted content on Plexus."));
                } else if(type.equals("illegal")){
                    runOnUiThread(() -> description.setText("The selling of illegal products on Plexus will not be tolerated and your account is up for termination."));
                } else if(type.equals("abusive")){
                    runOnUiThread(() -> description.setText("Vulgar and abusive languages or content will not be tolerated on Plexus."));
                } else {
                    runOnUiThread(() -> description.setText("Bullying a user on Plexus is against community standards. This ban will result in termination of all commenting post or messaging users."));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void terminateAccount(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banned").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Banned banned = dataSnapshot.getValue(Banned.class);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if (dataSnapshot1.exists()){
                        if(banned.isTermination()){

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                            String id = databaseReference1.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("email", firebaseUser.getEmail());

                            databaseReference.child("Whitelist Emails").child(id).setValue(hashMap);

                            firebaseUser.delete().addOnCompleteListener(task -> {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
