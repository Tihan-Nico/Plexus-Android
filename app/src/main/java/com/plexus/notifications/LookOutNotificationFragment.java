package com.plexus.notifications;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.components.components.PlexusRecyclerView;
import com.plexus.model.notifications.LookoutNotifications;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class LookOutNotificationFragment extends Fragment {

    PlexusRecyclerView recyclerView;
    LookoutNotificationAdapter notificationAdapter;
    ArrayList<LookoutNotifications> lookoutNotificationsList;
    FirebaseUser firebaseUser;

    View empty_state;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_lookout, container, false);

        empty_state = view.findViewById(R.id.empty_state);
        recyclerView = view.findViewById(R.id.recycler_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

        return view;
    }

    private void init() {

        lookoutNotificationsList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        notificationAdapter = new LookoutNotificationAdapter(getContext(), lookoutNotificationsList);
        recyclerView.setAdapter(notificationAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setEmptyView(empty_state);

    }

    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://saveourchildren.firebaseio.com/")
                        .getReference("Users")
                        .child(firebaseUser.getUid())
                        .child("Notification");

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        lookoutNotificationsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            LookoutNotifications lookoutNotifications = snapshot.getValue(LookoutNotifications.class);
                            lookoutNotificationsList.add(lookoutNotifications);
                        }

                        Collections.reverse(lookoutNotificationsList);
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        readNotifications();
    }

    @Override
    public void onResume() {
        super.onResume();
        readNotifications();
    }

}
