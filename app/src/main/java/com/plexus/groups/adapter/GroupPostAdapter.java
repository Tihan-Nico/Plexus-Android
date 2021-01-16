package com.plexus.groups.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.components.background.DialogInformation;
import com.plexus.components.components.socials.PlexusSocialTextView;
import com.plexus.model.Token;
import com.plexus.model.group.GroupPosts;
import com.plexus.model.account.User;
import com.plexus.notifications.fcm.FirebaseNotificationHelper;
import com.plexus.posts.activity.HashTagViewActivity;
import com.plexus.posts.activity.comment.CommentActivity;
import com.plexus.account.activity.FollowersActivity;
import com.plexus.account.activity.ProfileActivity;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

public class GroupPostAdapter extends RecyclerView.Adapter{

    public static Context context;
    List<GroupPosts> groupPosts;

    private static final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    //Layouts
    private static final int VIEW_TYPE_TEXT = 0;
    private static final int VIEW_TYPE_IMAGE= 1;
    private static final int VIEW_TYPE_VIDEO = 2;
    private static final int VIEW_TYPE_AUDIO = 3;

    public GroupPostAdapter(Context context, List<GroupPosts> groupPosts){
        this.context = context;
        this.groupPosts = groupPosts;
    }

    @Override
    public int getItemViewType(int position) {
        GroupPosts posts = groupPosts.get(position);
        switch (posts.getType()) {
            case "text":
                return VIEW_TYPE_TEXT;
            case "image":
                return VIEW_TYPE_IMAGE;
            case "video":
                return VIEW_TYPE_VIDEO;
            case "audio":
                return VIEW_TYPE_AUDIO;
            default:
                return -1;
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_TEXT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_text, parent, false);
            return new TextHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_image, parent, false);
            return new ImageHolder(view);
        } else if (viewType == VIEW_TYPE_VIDEO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_video, parent, false);
            return new VideoHolder(view);
        } else if (viewType == VIEW_TYPE_AUDIO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_audio, parent, false);
            return new AudioHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupPosts posts = groupPosts.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT:
                ((TextHolder) holder).bind(posts);
                break;
            case VIEW_TYPE_IMAGE:
                ((ImageHolder) holder).bind(posts);
                break;
            case VIEW_TYPE_VIDEO:
                ((VideoHolder) holder).bind(posts);
                break;
            case VIEW_TYPE_AUDIO:
                ((AudioHolder) holder).bind(posts);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return groupPosts.size();
    }

    private static void likeCount(String postId, final TextView like_count) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        like_count.setText(dataSnapshot.getChildrenCount() + " Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private static void commentCount(String postId, final TextView comment_count) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        comment_count.setText(dataSnapshot.getChildrenCount() + " Comments");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private static void getProfileData(String profileID, TextView fullname, SimpleDraweeView profile_image){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()));

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private static void sendNotification(String postid, String profileid){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(profileid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);

                            FirebaseNotificationHelper.initialize(com.plexus.components.Constants.FCM_KEY)
                                    .defaultJson(false, getJsonBody(MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getImageurl()), postid))
                                    .receiverFirebaseToken(token.getToken())
                                    .send();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid).child("Notification");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("notificationRead", false);
        hashMap.put("notificationViewed", false);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("reaction", true);

        reference.child(id).setValue(hashMap);

    }

    private static String getJsonBody(String name, String imageUrl, String postid) {
        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put("title", "Plexus");
            jsonObjectData.put("body", name + " " + " liked your post");
            jsonObjectData.put("click_action", "com.plexus.plexus_POST_TARGET_NOTIFICATION");
            jsonObjectData.put("from_user_id", firebaseUser.getUid());
            jsonObjectData.put("imageurl", imageUrl);
            jsonObjectData.put("postid", postid);
            jsonObjectData.put("type", "like");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectData.toString();
    }

    private static void profileActivity(String postID, String groupID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Activity Log");
        String id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("title", "You liked a post in a group");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hashMap.put("postid", postID);
        hashMap.put("groupid", groupID);
        hashMap.put("isLike", true);

        reference.child(id).setValue(hashMap);
    }

    public static class TextHolder extends RecyclerView.ViewHolder {

        TextView fullname, comment_count, like_count;
        PlexusSocialTextView description;
        ImageView save, comment, like, more;
        SimpleDraweeView profile_image;
        RelativeTimeTextView timestamp;

        public TextHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            save = itemView.findViewById(R.id.save);
            timestamp = itemView.findViewById(R.id.timestamp);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.menu);
            fullname = itemView.findViewById(R.id.publisher);
            profile_image = itemView.findViewById(R.id.image_profile);

        }

        void bind(GroupPosts posts){
            description.setText(MasterCipher.decrypt(posts.getDescription()));

            description.setOnHashtagClickListener((view, text) -> {
                Intent intent = new Intent(context, HashTagViewActivity.class);
                intent.putExtra("hashtag", text.toString().toLowerCase());
                context.startActivity(intent);
            });

            getProfileData(posts.getPublisher(), fullname, profile_image);

            fullname.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            profile_image.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            comment_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisherid", posts.getPublisher());
                context.startActivity(intent);
            });

            commentCount(posts.getPostid(), comment_count);

            like_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id", posts.getPostid());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            });

            likeCount(posts.getPostid(), like_count);

            like.setOnClickListener(view -> {
                if (like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .setValue(true);

                    profileActivity(posts.getPostid(), posts.getGroupID());

                    if (!posts.getPublisher().equals(firebaseUser.getUid())) {
                        sendNotification(posts.getPostid(), posts.getPublisher());
                    }
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference("Users").child(posts.getPublisher()).child("Notifications").child(posts.getPostid()).removeValue();
                }
            });

            more.setOnClickListener(view -> DialogInformation.reportPost(context, posts.getPublisher(), firebaseUser.getUid(), firebaseUser.getEmail(), posts.getPostid(), posts.getDescription()));

        }
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {

        TextView fullname, comment_count, like_count;
        PlexusSocialTextView description;
        ImageView save, comment, like, more;
        SimpleDraweeView profile_image;
        RelativeTimeTextView timestamp;

        public ImageHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            save = itemView.findViewById(R.id.save);
            timestamp = itemView.findViewById(R.id.timestamp);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.menu);
            fullname = itemView.findViewById(R.id.publisher);
            profile_image = itemView.findViewById(R.id.image_profile);

        }

        void bind(GroupPosts posts){
            description.setText(MasterCipher.decrypt(posts.getDescription()));

            description.setOnHashtagClickListener((view, text) -> {
                Intent intent = new Intent(context, HashTagViewActivity.class);
                intent.putExtra("hashtag", text.toString().toLowerCase());
                context.startActivity(intent);
            });

            getProfileData(posts.getPublisher(), fullname, profile_image);

            fullname.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            profile_image.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            comment_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisherid", posts.getPublisher());
                context.startActivity(intent);
            });

            commentCount(posts.getPostid(), comment_count);

            like_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id", posts.getPostid());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            });

            likeCount(posts.getPostid(), like_count);

            like.setOnClickListener(view -> {
                if (like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .setValue(true);

                    profileActivity(posts.getPostid(), posts.getGroupID());

                    if (!posts.getPublisher().equals(firebaseUser.getUid())) {
                        sendNotification(posts.getPostid(), posts.getPublisher());
                    }
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference("Users").child(posts.getPublisher()).child("Notifications").child(posts.getPostid()).removeValue();
                }
            });

            more.setOnClickListener(view -> DialogInformation.reportPost(context, posts.getPublisher(), firebaseUser.getUid(), firebaseUser.getEmail(), posts.getPostid(), posts.getDescription()));

        }

    }

    public static class VideoHolder extends RecyclerView.ViewHolder {

        TextView fullname, comment_count, like_count;
        PlexusSocialTextView description;
        ImageView save, comment, like, more;
        SimpleDraweeView profile_image;
        RelativeTimeTextView timestamp;

        public VideoHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            save = itemView.findViewById(R.id.save);
            timestamp = itemView.findViewById(R.id.timestamp);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.menu);
            fullname = itemView.findViewById(R.id.publisher);
            profile_image = itemView.findViewById(R.id.image_profile);

        }

        void bind(GroupPosts posts){
            description.setText(MasterCipher.decrypt(posts.getDescription()));

            description.setOnHashtagClickListener((view, text) -> {
                Intent intent = new Intent(context, HashTagViewActivity.class);
                intent.putExtra("hashtag", text.toString().toLowerCase());
                context.startActivity(intent);
            });

            getProfileData(posts.getPublisher(), fullname, profile_image);

            fullname.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            profile_image.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            comment_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisherid", posts.getPublisher());
                context.startActivity(intent);
            });

            commentCount(posts.getPostid(), comment_count);

            like_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id", posts.getPostid());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            });

            likeCount(posts.getPostid(), like_count);

            like.setOnClickListener(view -> {
                if (like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .setValue(true);

                    profileActivity(posts.getPostid(), posts.getGroupID());

                    if (!posts.getPublisher().equals(firebaseUser.getUid())) {
                        sendNotification(posts.getPostid(), posts.getPublisher());
                    }
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference("Users").child(posts.getPublisher()).child("Notifications").child(posts.getPostid()).removeValue();
                }
            });

            more.setOnClickListener(view -> DialogInformation.reportPost(context, posts.getPublisher(), firebaseUser.getUid(), firebaseUser.getEmail(), posts.getPostid(), posts.getDescription()));

        }

    }

    public static class AudioHolder extends RecyclerView.ViewHolder {

        TextView fullname, comment_count, like_count;
        PlexusSocialTextView description;
        ImageView save, comment, like, more;
        SimpleDraweeView profile_image;
        RelativeTimeTextView timestamp;

        public AudioHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            save = itemView.findViewById(R.id.save);
            timestamp = itemView.findViewById(R.id.timestamp);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.menu);
            fullname = itemView.findViewById(R.id.publisher);
            profile_image = itemView.findViewById(R.id.image_profile);

        }

        void bind(GroupPosts posts){
            description.setText(MasterCipher.decrypt(posts.getDescription()));

            description.setOnHashtagClickListener((view, text) -> {
                Intent intent = new Intent(context, HashTagViewActivity.class);
                intent.putExtra("hashtag", text.toString().toLowerCase());
                context.startActivity(intent);
            });

            getProfileData(posts.getPublisher(), fullname, profile_image);

            fullname.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            profile_image.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", posts.getPublisher());
                context.startActivity(intent);
            });

            comment_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisherid", posts.getPublisher());
                context.startActivity(intent);
            });

            commentCount(posts.getPostid(), comment_count);

            like_count.setOnClickListener(view -> {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id", posts.getPostid());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            });

            likeCount(posts.getPostid(), like_count);

            like.setOnClickListener(view -> {
                if (like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .setValue(true);

                    profileActivity(posts.getPostid(), posts.getGroupID());

                    if (!posts.getPublisher().equals(firebaseUser.getUid())) {
                        sendNotification(posts.getPostid(), posts.getPublisher());
                    }
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Likes")
                            .child(posts.getPostid())
                            .child(firebaseUser.getUid())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference("Users").child(posts.getPublisher()).child("Notifications").child(posts.getPostid()).removeValue();
                }
            });

            more.setOnClickListener(view -> DialogInformation.reportPost(context, posts.getPublisher(), firebaseUser.getUid(), firebaseUser.getEmail(), posts.getPostid(), posts.getDescription()));

        }
    }
}
