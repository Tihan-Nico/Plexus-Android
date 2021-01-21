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
import com.plexus.model.notifications.PlexusNotification;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class PlexusNotificationFragment extends Fragment {

    PlexusRecyclerView recyclerView;
    FirebaseUser firebaseUser;
    View empty_state;
    private NotificationAdapter notificationAdapter;
    private ArrayList<PlexusNotification> plexusNotificationList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_plexus, container, false);

        empty_state = view.findViewById(R.id.empty_state);
        recyclerView = view.findViewById(R.id.recycler_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

        return view;
    }

    private void init() {

        plexusNotificationList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        notificationAdapter = new NotificationAdapter(getContext(), plexusNotificationList);
        recyclerView.setAdapter(notificationAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setEmptyView(empty_state);
    }

    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(firebaseUser.getUid())
                        .child("Notification");

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        plexusNotificationList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PlexusNotification plexusNotification = snapshot.getValue(PlexusNotification.class);
                            plexusNotificationList.add(plexusNotification);
                        }

                        Collections.reverse(plexusNotificationList);
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
