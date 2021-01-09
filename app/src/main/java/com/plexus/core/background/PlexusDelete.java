package com.plexus.core.background;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plexus.R;
import com.plexus.model.posts.Post;
import com.plexus.utils.MasterCipher;

public class PlexusDelete {

    public static void deletePost(BottomSheetDialog sheetPost, String postid, String userID, Context context){
        sheetPost.dismiss();

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_post_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button delete = dialog.findViewById(R.id.delete);
        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            final String id = postid;
            FirebaseDatabase.getInstance().getReference("Posts")
                    .child(postid).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            deletePostNotification(id, userID, context);
                            deleteCommentsAndLikes(id);
                            dialog.dismiss();
                        }
                    });
        });

        dialog.show();
    }

    public static void deletePostNotification(final String postid, String userid, Context context){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Notification");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue().addOnCompleteListener(task -> Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteNotification(String id){
        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Notification").child(id).removeValue();
    }

    private static void deleteCommentsAndLikes(String postID){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postID).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postID).removeValue();
    }

    public static void deleteAllUserData(){

    }


}
