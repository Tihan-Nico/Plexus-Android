package com.plexus.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.plexus.lookout.R;
import com.plexus.lookout.account.adapters.SheetOptionsAdapter;
import com.plexus.lookout.emergency.MyKidsActivity;
import com.plexus.lookout.model.Post;
import com.plexus.lookout.model.account.SheetOptions;
import com.plexus.lookout.model.account.User;
import com.plexus.lookout.utils.MasterCipher;
import com.plexus.lookout.widgets.PlexusRecyclerView;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    SimpleDraweeView image_cover, profile_image;
    ImageView menu;
    TextView post_count, follower_count, following_count, fullname, about, username;
    PlexusRecyclerView recycler_view;
    FirebaseUser firebaseUser;

    private BottomSheetDialog profile_sheet;
    public static final String[] titles = new String[]{"My Kids", "Account Settings", "Profile Link"};
    public static final Integer[] images = {R.drawable.account_child_outline, R.drawable.account_lock_outline, R.drawable.ic_link};
    ArrayList<SheetOptions> rowItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile_image = view.findViewById(R.id.profile_image);
        image_cover = view.findViewById(R.id.image_cover);
        menu = view.findViewById(R.id.menu);
        username = view.findViewById(R.id.username);
        post_count = view.findViewById(R.id.posts);
        follower_count = view.findViewById(R.id.followers);
        following_count = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        about = view.findViewById(R.id.about);
        recycler_view = view.findViewById(R.id.recycler_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

        return view;
    }

    private void init(){

        profile_sheet = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        profile_sheet.setContentView(R.layout.sheet_profile_layout);
        ListView listView = profile_sheet.findViewById(R.id.listview);

        rowItems = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            SheetOptions item = new SheetOptions(titles[i], images[i]);
            rowItems.add(item);
        }

        SheetOptionsAdapter optionsAdapter = new SheetOptionsAdapter(getContext(), rowItems);
        listView.setAdapter(optionsAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0){
                startActivity(new Intent(getContext(), MyKidsActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 1) {
                startActivity(new Intent(getContext(), AccountSettingsActivity.class));
                profile_sheet.dismiss();
            }

            if (position == 2) {
                shareDeepLink(generateDeepLinkUrl());
                profile_sheet.dismiss();
            }

        });

        menu.setOnClickListener(v -> profile_sheet.show());

        getCounts();
        userInformation();

    }

    private void getCounts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid())){
                        i++;
                    }
                }
                post_count.setText(MessageFormat.format("{0}", i));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("followers");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                follower_count.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                following_count.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
    }

    private void userInformation(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                about.setText(MasterCipher.decrypt(user.getBio()));
                username.setText(MasterCipher.decrypt(user.getUsername()));
                profile_image.setImageURI(Uri.parse(MasterCipher.decrypt(user.getImageurl())));
                image_cover.setImageURI(Uri.parse(MasterCipher.decrypt(user.getProfile_cover())));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private String generateDeepLinkUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("plexus.dev")
                .appendPath("profile")
                .appendQueryParameter("id", firebaseUser.getUid());
        return builder.build().toString();
    }

    private void shareDeepLink(String url){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share Kidnapper via"));
    }

}
