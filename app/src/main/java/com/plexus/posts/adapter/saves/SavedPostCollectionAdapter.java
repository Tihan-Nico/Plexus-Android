package com.plexus.posts.adapter.saves;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.posts.SavedPostsCollection;
import com.plexus.posts.activity.saved_posts.CollectionActivity;
import com.plexus.utils.MasterCipher;

import java.text.MessageFormat;
import java.util.List;

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

public class SavedPostCollectionAdapter extends RecyclerView.Adapter<SavedPostCollectionAdapter.ViewHolder> {

    private Context mContext;
    private List<SavedPostsCollection> savedPostsCollectionList;
    FirebaseUser firebaseUser;

    public SavedPostCollectionAdapter(Context context, List<SavedPostsCollection> savedPostsCollections){
        mContext = context;
        savedPostsCollectionList = savedPostsCollections;
    }

    @NonNull
    @Override
    public SavedPostCollectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_saves_collections_item, parent, false);
        return new SavedPostCollectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SavedPostsCollection postsCollection = savedPostsCollectionList.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String description = MasterCipher.decrypt(postsCollection.getCollection_name());
        holder.collection_name.setText(description);

        holder.collection_image.setImageURI(MasterCipher.decrypt(postsCollection.getCollection_image_url()));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections").child(postsCollection.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if (dataSnapshot1.exists()){
                        holder.collection_total.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, CollectionActivity.class);
            intent.putExtra("id", postsCollection.getId());
            intent.putExtra("collection_name", postsCollection.getCollection_name());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return savedPostsCollectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView collection_image;
        public TextView collection_name, collection_total;

        public ViewHolder(View itemView) {
            super(itemView);

            collection_image = itemView.findViewById(R.id.collection_image);
            collection_name = itemView.findViewById(R.id.collection_name);
            collection_total = itemView.findViewById(R.id.collection_total);

        }
    }
}
