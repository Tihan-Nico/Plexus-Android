package com.plexus.posts.adapter.saves;

import android.content.Context;
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
import com.plexus.utils.MasterCipher;

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

public class CollectionSheetAdapter extends RecyclerView.Adapter<CollectionSheetAdapter.ViewHolder> {

    private Context mContext;
    private List<SavedPostsCollection> savedPostsCollectionList;
    private String postID;
    FirebaseUser firebaseUser;

    public CollectionSheetAdapter(Context context, List<SavedPostsCollection> savedPostsCollections, String postid){
        mContext = context;
        savedPostsCollectionList = savedPostsCollections;
        postID = postid;
    }

    @NonNull
    @Override
    public CollectionSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sheet_save_post_item, parent, false);
        return new CollectionSheetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionSheetAdapter.ViewHolder holder, int position) {
        final SavedPostsCollection postsCollection = savedPostsCollectionList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String description = MasterCipher.decrypt(postsCollection.getCollection_name());
        holder.collection_name.setText(description);

        holder.collection_image.setImageURI(MasterCipher.decrypt(postsCollection.getCollection_image_url()));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections").child(postsCollection.getId()).child("Collection Saves");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if (!dataSnapshot1.exists()){
                        holder.collection_total.setText("No saved post in this collections");
                    } else if (dataSnapshot.getChildrenCount() == 1){
                        holder.collection_total.setText("" + dataSnapshot.getChildrenCount() + " Saved Post");
                    } else {
                        holder.collection_total.setText("" + dataSnapshot.getChildrenCount() + " Saved Posts");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections").child(postsCollection.getId()).child("Collection Saves")
                .child(postID).setValue(true));
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
