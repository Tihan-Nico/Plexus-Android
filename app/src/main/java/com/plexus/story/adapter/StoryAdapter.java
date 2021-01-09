package com.plexus.story.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.posts.Story;
import com.plexus.model.account.User;
import com.plexus.story.activity.AddStoryActivity;
import com.plexus.story.activity.StoryActivity;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

  List<String> images;
  List<String> storyids;
  private Context mContext;
  private List<Story> mStory;
  private Handler mHandler = new Handler(Looper.getMainLooper());

  public StoryAdapter(Context mContext, List<Story> mStory) {
    this.mContext = mContext;
    this.mStory = mStory;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    if (i == 0) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.story_add_item, viewGroup, false);
      return new ViewHolder(view);
    } else {
      View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, viewGroup, false);
      return new ViewHolder(view);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
    final Story story = mStory.get(i);

    userInfo(viewHolder, story.getUserid(), i);

    if (viewHolder.getAdapterPosition() != 0) {
      seenStory(viewHolder, story.getUserid());
    }

    if (viewHolder.getAdapterPosition() == 0) {
      myStory(viewHolder.addstory_text, viewHolder.story_plus, false, viewHolder);
    }

    viewHolder.itemView.setOnClickListener(
        view -> {
          if (viewHolder.getAdapterPosition() == 0) {
            myStory(viewHolder.addstory_text, viewHolder.story_image, true, viewHolder);
          } else {
            // TODO: go to story
            Intent intent = new Intent(mContext, StoryActivity.class);
            intent.putExtra("userid", story.getUserid());
            mContext.startActivity(intent);
          }
        });
  }

  @Override
  public int getItemCount() {
    return mStory.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return 0;
    }
    return 1;
  }

  private void userInfo(final ViewHolder viewHolder, String userid, final int pos) {
    DatabaseReference reference =
        FirebaseDatabase.getInstance().getReference("Users").child(userid);
    reference.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            mHandler.post(() -> {
              if (pos != 0) {
                viewHolder.profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                Glide.with(mContext).asBitmap().load(MasterCipher.decrypt(user.getImageurl())).into(viewHolder.profile_image_unseen);
                viewHolder.fullname.setText(MasterCipher.decrypt(user.getName()));
              }
            });
          }

          @Override
          public void onCancelled(@NotNull DatabaseError databaseError) {}
        });
  }

  private void myStory(
      final TextView textView,
      final ImageView imageView,
      final boolean click,
      final ViewHolder viewHolder) {
    images = new ArrayList<>();
    storyids = new ArrayList<>();
    final DatabaseReference reference =
        FirebaseDatabase.getInstance()
            .getReference("Story")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    reference.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
            int count = 0;
            Story story = dataSnapshot.getValue(Story.class);
            long timecurrent = System.currentTimeMillis();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              story = snapshot.getValue(Story.class);
              if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                count++;
              }
            }

            images.clear();
            storyids.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              story = snapshot.getValue(Story.class);
              timecurrent = System.currentTimeMillis();
              if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                images.add(MasterCipher.decrypt(story.getImageurl()));
                storyids.add(story.getStoryid());
              }
            }

            DatabaseReference reference1 =
                FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            int finalCount = count;
            Story finalStory = story;
            reference1.addListenerForSingleValueEvent(
                new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (click) {
                      if (finalCount > 0) {
                        Intent intent = new Intent(mContext, StoryActivity.class);
                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mContext.startActivity(intent);
                      } else {
                          Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                      }
                    } else {
                      if (finalCount > 0) {
                        mHandler.post(new Runnable() {
                          @Override
                          public void run() {
                            textView.setText("My story");
                            viewHolder.story_image.setVisibility(View.VISIBLE);
                            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(MasterCipher.decrypt(finalStory.getImageurl())))
                                    .setProgressiveRenderingEnabled(true)
                                    .build();
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(viewHolder.story_image.getController())
                                    .build();
                            viewHolder.story_image.setController(controller);
                            imageView.setVisibility(View.INVISIBLE);
                          }
                        });
                      } else {
                        mHandler.post(() -> {
                          textView.setText("Add to story");
                          String image = dataSnapshot.child("imageurl").getValue(String.class);
                          viewHolder.story_image.setImageURI(MasterCipher.decrypt(image));
                          imageView.setVisibility(View.VISIBLE);
                        });
                      }
                    }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
          }

          @Override
          public void onCancelled(@NotNull DatabaseError databaseError) {}
        });
  }

  private void seenStory(final ViewHolder viewHolder, String userid) {
    images = new ArrayList<>();
    storyids = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
    reference.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
            Story story = dataSnapshot.getValue(Story.class);
            int i = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              if (!snapshot
                      .child("views")
                      .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                      .exists()
                  && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeend()) {
                i++;
              }
            }

            long timecurrent = System.currentTimeMillis();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              story = snapshot.getValue(Story.class);
              if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                i++;
              }
            }

            images.clear();
            storyids.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              story = snapshot.getValue(Story.class);
              timecurrent = System.currentTimeMillis();
              if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                images.add(MasterCipher.decrypt(story.getImageurl()));
                storyids.add(story.getStoryid());
              }
            }

            Story finalStory = story;
            viewHolder.story_image.setImageURI(story.getImageurl());

            if (i > 0) {
              viewHolder.story_image.setVisibility(View.VISIBLE);
              viewHolder.fullname.setVisibility(View.VISIBLE);
              viewHolder.profile_image_unseen.setVisibility(View.GONE);
              viewHolder.profile_image.setVisibility(View.VISIBLE);
            } else {
              viewHolder.story_image.setVisibility(View.VISIBLE);
              viewHolder.fullname.setVisibility(View.VISIBLE);
              viewHolder.profile_image_unseen.setVisibility(View.VISIBLE);
              viewHolder.profile_image.setVisibility(View.GONE);
            }
          }

          @Override
          public void onCancelled(@NotNull DatabaseError databaseError) {}
        });
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView story_plus, profile_image_unseen;
    public TextView fullname, addstory_text;
    SimpleDraweeView story_image, profile_image;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      story_image = itemView.findViewById(R.id.story_image);
      fullname = itemView.findViewById(R.id.story_username);
      story_plus = itemView.findViewById(R.id.story_plus);
      addstory_text = itemView.findViewById(R.id.addstory_text);
      profile_image_unseen = itemView.findViewById(R.id.profile_image_unseen);
      profile_image = itemView.findViewById(R.id.profile_image);
    }
  }
}
