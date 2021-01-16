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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
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
import com.plexus.notifications.LookOutNotificationFragment;
import com.plexus.notifications.NotificationAdapter;
import com.plexus.notifications.PlexusNotificationFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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

public class NotificationsFragment extends Fragment {

  FirebaseUser firebaseUser;
  ImageView clear_notifications;

  TabLayout tabLayout;
  ViewPager viewPager;

  PlexusNotificationFragment plexusNotificationFragment;
  LookOutNotificationFragment lookOutNotificationFragment;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_notification, container, false);

    clear_notifications = view.findViewById(R.id.clear_notifications);
    tabLayout = view.findViewById(R.id.tabLayout);
    viewPager = view.findViewById(R.id.viewPager);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    init();

    return view;
  }

  private void init(){

    lookOutNotificationFragment = new LookOutNotificationFragment();
    plexusNotificationFragment = new PlexusNotificationFragment();

    tabLayout.setupWithViewPager(viewPager);

    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);
    viewPagerAdapter.addFragment(plexusNotificationFragment, "Plexus");
    viewPagerAdapter.addFragment(lookOutNotificationFragment, "LookOut");
    viewPager.setAdapter(viewPagerAdapter);

    BadgeDrawable badgeDrawable = tabLayout.getTabAt(0).getOrCreateBadge();
    badgeDrawable.setVisible(false);
    badgeDrawable.setNumber(1);

    clear_notifications.setOnClickListener(v -> deleteNotifications());
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

  private class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager supportFragmentManager, int behaviour) {
      super(supportFragmentManager, behaviour);

    }

    public void addFragment(Fragment fragment, String title){
      fragments.add(fragment);
      fragmentTitles.add(title);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return fragmentTitles.get(position);
    }
  }
}
