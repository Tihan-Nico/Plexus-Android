package com.plexus.components.background;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.account.activity.ProfileActivity;
import com.plexus.model.account.User;
import com.plexus.model.notifications.PlexusNotification;
import com.plexus.model.posts.Post;
import com.plexus.model.posts.SavedPostsCollection;
import com.plexus.posts.activity.CreatePostActivity;
import com.plexus.posts.activity.saved_posts.CreateCollectionsSavesActivity;
import com.plexus.posts.adapter.saves.CollectionSheetAdapter;
import com.plexus.utils.MasterCipher;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DialogInformation {

    public static final String[] titles = new String[]{"Report User", "Block"};
    public static FirebaseUser firebaseUser;
    //Save Sheet
    private static CollectionSheetAdapter collectionSheetAdapter;
    private static List<SavedPostsCollection> savedPostsCollectionList;

    //Notifications

    public static void getProfileImage(String id, ImageView profile_image, Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).asBitmap().load(MasterCipher.decrypt(user.getImageurl())).into(profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getNotificationDescription(PlexusNotification plexusNotification, TextView notification_description, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (plexusNotification.isFollower()) {
                            String sourceString = "<b>" + MasterCipher.decrypt(user.getName()) + " " + MasterCipher.decrypt(user.getSurname()) + "</b> " + "started following you.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        } else if (plexusNotification.isComment()) {
                            String sourceString = "<b>" + MasterCipher.decrypt(user.getName()) + " " + MasterCipher.decrypt(user.getSurname()) + "</b> " + "commented on your post.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        } else if (plexusNotification.isReaction()) {
                            String sourceString = "<b>" + MasterCipher.decrypt(user.getName()) + " " + MasterCipher.decrypt(user.getSurname()) + "</b> " + "liked your post.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        } else if (plexusNotification.isShared()) {
                            String sourceString = "<b>" + MasterCipher.decrypt(user.getName()) + " " + MasterCipher.decrypt(user.getSurname()) + "</b> " + "shared your post.";
                            notification_description.setText(Html.fromHtml(sourceString));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public static void showNotificationBottomSheet(Context context, PlexusNotification plexusNotification, String profileid) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_notification);

        ImageView profile_image = bottomSheetDialog.findViewById(R.id.profile_image);
        TextView notification_description = bottomSheetDialog.findViewById(R.id.notification_description);
        LinearLayout delete_notification = bottomSheetDialog.findViewById(R.id.delete_notification);
        LinearLayout view_profile = bottomSheetDialog.findViewById(R.id.view_profile);
        LinearLayout bug_report = bottomSheetDialog.findViewById(R.id.bug_report);

        getNotificationDescription(plexusNotification, notification_description, profileid);
        getProfileImage(profileid, profile_image, context);

        delete_notification.setOnClickListener(v -> {
            PlexusDelete.deleteNotification(plexusNotification.getId());
            bottomSheetDialog.dismiss();
        });

        view_profile.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("userid", profileid);
            context.startActivity(intent);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    //This only shows when user has been verified for the first time on Plexus

    public static void showVerified(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_verified);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button btn_continue = dialog.findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //Edit Profile

    public static void editName(Context context, String profileid) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_edit_name);

        TextView title = bottomSheetDialog.findViewById(R.id.title_edit);
        EditText edit_text = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btn_save = bottomSheetDialog.findViewById(R.id.btn_save);

        title.setText("Edit Name");
        getName(edit_text, profileid);
        btn_save.setOnClickListener(v -> {
            updateProfile("name", edit_text.getText().toString(), profileid);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    public static void editSurname(Context context, String profileid) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_edit_name);

        TextView title = bottomSheetDialog.findViewById(R.id.title_edit);
        EditText edit_text = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btn_save = bottomSheetDialog.findViewById(R.id.btn_save);

        title.setText("Edit Surname");
        getSurname(edit_text, profileid);
        btn_save.setOnClickListener(v -> {
            updateProfile("surname", edit_text.getText().toString(), profileid);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    public static void editWebsite(Context context, String profileid) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_edit_name);

        TextView title = bottomSheetDialog.findViewById(R.id.title_edit);
        EditText edit_text = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btn_save = bottomSheetDialog.findViewById(R.id.btn_save);

        title.setText("Edit Website Link");
        getWebsite(edit_text, profileid);
        btn_save.setOnClickListener(v -> {
            updateProfile("website", edit_text.getText().toString(), profileid);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    public static void editBio(Context context, String profileid) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_edit_name);

        TextView title = bottomSheetDialog.findViewById(R.id.title_edit);
        EditText edit_text = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btn_save = bottomSheetDialog.findViewById(R.id.btn_save);

        title.setText("Edit Bio");
        getBio(edit_text, profileid);

        btn_save.setOnClickListener(v -> {
            updateProfile("bio", edit_text.getText().toString(), profileid);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    public static void getName(EditText name, String profileid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setHint(MasterCipher.decrypt(user.getName()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getSurname(EditText surname, String profileid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                surname.setHint(MasterCipher.decrypt(user.getSurname()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getBio(EditText bio, String profileid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setHint(MasterCipher.decrypt(user.getBio()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getWebsite(EditText website, String profileid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                website.setHint(MasterCipher.decrypt(user.getWebsite()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void updateProfile(String child, String value, String profileid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(child, MasterCipher.encrypt(value));

        reference.updateChildren(hashMap);
    }

    //Groups

    public static void selectPrivacy(Context context, TextView privacy) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_group_privacy);

        RelativeLayout group_public = bottomSheetDialog.findViewById(R.id.group_public);
        RelativeLayout group_private = bottomSheetDialog.findViewById(R.id.group_private);
        RadioButton privacy_public = bottomSheetDialog.findViewById(R.id.privacy_public);
        RadioButton privacy_private = bottomSheetDialog.findViewById(R.id.privacy_private);

        group_public.setOnClickListener(view -> privacy_public.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                privacy_private.setChecked(false);
                privacy_public.setChecked(true);

                privacy.setText("Public");
                bottomSheetDialog.dismiss();
            }
        }));

        group_private.setOnClickListener(view -> privacy_private.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                privacy_private.setChecked(true);
                privacy_public.setChecked(false);

                privacy.setText("Private");
                bottomSheetDialog.dismiss();
            }
        }));


        bottomSheetDialog.show();

    }

    //Posts for groups and users

    public static void moreSheet(Context mContext, Post post, String postid, String userID, String email, String input, String postDescription) {
        BottomSheetDialog sheetPost = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        sheetPost.setContentView(R.layout.sheet_post);
        LinearLayout edit_post = sheetPost.findViewById(R.id.edit_post);
        LinearLayout delete_posts = sheetPost.findViewById(R.id.delete_posts);
        LinearLayout save_post = sheetPost.findViewById(R.id.save_post);
        ImageView save = sheetPost.findViewById(R.id.save);
        LinearLayout report_post = sheetPost.findViewById(R.id.report_post);
        LinearLayout copy_link = sheetPost.findViewById(R.id.copy_link);
        View line2 = sheetPost.findViewById(R.id.line2);
        View line3 = sheetPost.findViewById(R.id.line3);

        delete_posts.setOnClickListener(v -> {
            PlexusDelete.deletePost(sheetPost, postid, firebaseUser.getUid(), mContext);
        });

        report_post.setOnClickListener(v -> reportPost(mContext, userID, userID, email, postid, input));

        edit_post.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CreatePostActivity.class);
            intent.putExtra("postid", postid);
            intent.putExtra("description", postDescription);
            intent.putExtra("post_edit", true);
            mContext.startActivity(intent);

        });

        isSaved(post.getPostid(), save);

        delete_posts.setOnClickListener(v -> PlexusDelete.deletePost(sheetPost, post.getPostid(), userID, mContext));

        save_post.setOnClickListener(v -> {
            checkCollections(mContext, sheetPost, save, post);
        });

        if (!post.getPublisher().equals(firebaseUser.getUid())) {
            edit_post.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            delete_posts.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        }

        sheetPost.show();
    }

    private static void isSaved(final String postid, final ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.bookmark_multiple);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.bookmark_multiple_outline);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public static void reportPost(Context context, String profileid, String myID, String myEmail, String postID, String inputText) {
        BottomSheetDialog report_post = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        report_post.setContentView(R.layout.report_sheet);
        ImageView back = report_post.findViewById(R.id.back);
        Button send_report = report_post.findViewById(R.id.report_post);
        LinearLayout unfollow = report_post.findViewById(R.id.unfollow_user);
        LinearLayout block_user = report_post.findViewById(R.id.block_user);
        EditText editText = report_post.findViewById(R.id.report);
        TextView block_name = report_post.findViewById(R.id.block_name);
        TextView unfollow_name = report_post.findViewById(R.id.unfollow_name);

        block_user.setOnClickListener(v -> blockUser(profileid, context));

        back.setOnClickListener(v -> report_post.dismiss());

        send_report.setOnClickListener(v -> {
            try {
                PlexusReporting.sendMail(context, myID, myEmail, profileid, postID, inputText);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(profileid);
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        block_name.setText(MessageFormat.format("Block {0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                        unfollow_name.setText(MessageFormat.format("Unfollow {0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        unfollow.setOnClickListener(v -> {
            unfollowUser(profileid);
        });

        report_post.show();
    }

    public static void saveCollectionSheet(Context mContext, Post post) {
        BottomSheetDialog save_collection_sheet = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        save_collection_sheet.setContentView(R.layout.sheet_save_post);

        LinearLayout create_collection = save_collection_sheet.findViewById(R.id.create_collection);
        RecyclerView recyclerView = save_collection_sheet.findViewById(R.id.recycler_view);

        create_collection.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, CreateCollectionsSavesActivity.class)));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        savedPostsCollectionList = new ArrayList<>();
        collectionSheetAdapter = new CollectionSheetAdapter(mContext, savedPostsCollectionList, post.getPostid());
        recyclerView.setAdapter(collectionSheetAdapter);

        getCollections();
        isSavedInCollection(mContext, post);

        save_collection_sheet.show();

    }

    private static void checkCollections(Context context, BottomSheetDialog sheetPost, ImageView imageView, Post post) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    saveCollectionSheet(context, post);
                    savePost(imageView, post);
                    sheetPost.dismiss();
                } else {
                    savePost(imageView, post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void getCollections() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                savedPostsCollectionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SavedPostsCollection savedPostsCollection = snapshot.getValue(SavedPostsCollection.class);
                    savedPostsCollectionList.add(savedPostsCollection);
                }
                Collections.reverse(savedPostsCollectionList);
                collectionSheetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private static void isSavedInCollection(Context mContext, Post post) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Collections");
        ref.child("Collections").orderByChild(post.getPostid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(mContext, "Saved In Memes", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Not Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private static void savePost(ImageView imageView, Post post) {
        if (imageView.getTag().equals("save")) {
            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent")
                    .child(post.getPostid()).setValue(true);
        } else {
            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child("Recent")
                    .child(post.getPostid()).removeValue();
        }
    }

    private static void blockUser(String profileid, Context mContext) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", profileid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid()).child("Blocked Users").child(profileid).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mContext, "User was blocked successfully.", Toast.LENGTH_SHORT).show();
                    unfollowUser(profileid);
                });
    }

    private static void unfollowUser(String profileid) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Follow")
                .child(firebaseUser.getUid())
                .child("following")
                .child(profileid)
                .removeValue();
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Follow")
                .child(profileid)
                .child("followers")
                .child(firebaseUser.getUid())
                .removeValue();
    }

}
