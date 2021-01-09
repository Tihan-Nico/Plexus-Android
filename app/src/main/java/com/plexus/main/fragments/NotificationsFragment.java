package com.plexus.main.fragments;

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
import com.plexus.core.components.PlexusRecyclerView;
import com.plexus.model.Notification;
import com.plexus.notifications.NotificationAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

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

public class NotificationsFragment extends Fragment {

  PlexusRecyclerView recyclerView;
  private NotificationAdapter notificationAdapter;
  private ArrayList<Notification> notificationList;
  FirebaseUser firebaseUser;
  FirebaseAuth firebaseAuth;
  ImageView clear_notifications;

  View empty_state;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_notification, container, false);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    firebaseAuth = FirebaseAuth.getInstance();

    clear_notifications = view.findViewById(R.id.clear_notifications);
    empty_state = view.findViewById(R.id.empty_state);

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(mLayoutManager);
    notificationList = new ArrayList<>();
    notificationAdapter = new NotificationAdapter(getContext(), notificationList);
    recyclerView.setAdapter(notificationAdapter);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
    recyclerView.addItemDecoration(dividerItemDecoration);
    recyclerView.setEmptyView(empty_state);

    clear_notifications.setOnClickListener(v -> deleteNotifications());

    return view;
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
            notificationList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              Notification notification = snapshot.getValue(Notification.class);
              notificationList.add(notification);
            }

            Collections.reverse(notificationList);
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
