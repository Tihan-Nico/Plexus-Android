package com.plexus.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.account.adapters.UserAdapter;
import com.plexus.components.components.socials.commons.SocialAutoCompleteTextView;
import com.plexus.components.providers.SearchDatabase;
import com.plexus.model.account.User;
import com.plexus.search.adapter.HashtagAdapter;
import com.plexus.search.adapter.RecentSearchAdapter;
import com.plexus.utils.MasterCipher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

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

public class SearchFragment extends Fragment {

    SocialAutoCompleteTextView search_bar;
    RecyclerView recyclerViewPlexus, trending, recyclerView;
    ImageView search;
    SimpleDraweeView profile_image;
    LinearLayout search_views;
    private UserAdapter userAdapter;
    private HashtagAdapter hashtagAdapter;
    private RecentSearchAdapter recentSearchAdapter;
    private List<User> userList;
    private FirebaseUser firebaseUser;
    private List<String> mHashTags;
    private List<String> mSearches;
    private List<String> mTagCounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        search_views = view.findViewById(R.id.search_views);

        recyclerViewPlexus = view.findViewById(R.id.recycler_view_plexus);
        recyclerViewPlexus.setHasFixedSize(true);
        recyclerViewPlexus.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearches = new ArrayList<>();
        recentSearchAdapter = new RecentSearchAdapter(getContext(), mSearches);
        recyclerViewPlexus.setAdapter(recentSearchAdapter);

        trending = view.findViewById(R.id.trending);
        trending.setHasFixedSize(true);
        trending.setLayoutManager(new LinearLayoutManager(getContext()));
        mHashTags = new ArrayList<>();
        mTagCounts = new ArrayList<>();
        hashtagAdapter = new HashtagAdapter(getContext(), mHashTags, mTagCounts);
        trending.setAdapter(hashtagAdapter);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, true);
        recyclerView.setAdapter(userAdapter);

        search_bar = view.findViewById(R.id.search_bar);
        search = view.findViewById(R.id.search);
        profile_image = view.findViewById(R.id.profile_image);

        search.setOnClickListener(v -> {
            searchUsers(search_bar.getText().toString());
            saveSearches(search_bar.getText().toString());
            saveToAppDatabase(search_bar.getText().toString());
            filterSearch(search_bar.getText().toString());
        });

        readUsers();
        readTags();
        readSearches();

        hideSoftKeyboard();
        getUserInfo(profile_image);

        return view;
    }

    private void getUserInfo(SimpleDraweeView imageView) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                imageView.setImageURI(MasterCipher.decrypt(user.getImageurl()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUsers(String s) {
        userList.clear();
        if (s.length() == 0) {
            recyclerView.setVisibility(View.GONE);
            search_views.setVisibility(View.VISIBLE);
        } else {
            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s + "\uf8ff");
            query.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                userList.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
            recyclerView.setVisibility(View.VISIBLE);
            search_views.setVisibility(View.GONE);
        }
    }

    private void readTags() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Hashtags");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHashTags.clear();
                mTagCounts.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mHashTags.add(snapshot.getKey());
                    mTagCounts.add(snapshot.getChildrenCount() + "");
                }
                hashtagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (search_bar.getText().toString().equals("")) {
                            userList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (!user.getId().equals(firebaseUser.getUid())) {
                                    userList.add(user);
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void filterSearch(String text) {
        List<String> mSearchHashtags = new ArrayList<>();
        List<String> mSearchHashtagCounts = new ArrayList<>();

        for (String s : mHashTags) {
            if (s.toLowerCase().contains(text.toLowerCase())) {
                mSearchHashtags.add(s);
                mSearchHashtagCounts.add(mTagCounts.get(mHashTags.indexOf(s)));
            }
        }
        hashtagAdapter.hashtagFilter(mSearchHashtags, mSearchHashtagCounts);

    }

    private void saveToAppDatabase(String search_string) {
        SearchDatabase.getInstance(getActivity()).addSearchString(search_string);
    }

    private void saveSearches(String search_string) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Searches").child(firebaseUser.getUid());
        String searchID = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", searchID);
        hashMap.put("search_term", search_string);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

        reference.child(search_bar.getText().toString().toLowerCase()).child(searchID).setValue(hashMap);
    }

    private void readSearches() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Searches").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mSearches.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mSearches.add(dataSnapshot.getKey());
                }
                recentSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
