package com.plexus.messaging.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.plexus.R;
import com.plexus.main.activity.MainActivity;
import com.plexus.messaging.adapter.ChatlistAdapter;
import com.plexus.model.account.User;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

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

public class NewMessageFragment extends AppCompatActivity {

    ImageView back;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ChatlistAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_new_chat_fragment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back = findViewById(R.id.back);

        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Disposable a = RxView.clicks(back).subscribe(unit ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        compositeDisposable.add(a);

        progressBar = findViewById(R.id.progress);
        recyclerView = findViewById(R.id.recycler_view);

        mUsers = new ArrayList<>();
        userAdapter = new ChatlistAdapter(this, mUsers, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        readUsers();

    }

    private void readUsers() {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("name").startAt("A").endAt("Z");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new ChatlistAdapter(NewMessageFragment.this, mUsers, false);
                recyclerView.setAdapter(userAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
