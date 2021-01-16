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
    ImageView clear_notifications;

    View empty_state;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_lookout, container, false);

        clear_notifications = view.findViewById(R.id.clear_notifications);
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

        clear_notifications.setOnClickListener(v -> deleteNotifications());
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

    private void deleteNotifications() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView description = dialog.findViewById(R.id.description);
        TextView delete = dialog.findViewById(R.id.delete_all);
        TextView cancel = dialog.findViewById(R.id.cancel);

        description.setText("Are you sure you want to clear all notifications? \n \n You won't be able to recover it after clearing.");

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseUser.getUid()).child("Notification").removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "All notifications cleared", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        });

        dialog.show();
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
