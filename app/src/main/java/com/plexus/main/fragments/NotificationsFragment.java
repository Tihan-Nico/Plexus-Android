package com.plexus.main.fragments;

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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.plexus.R;
import com.plexus.notifications.LookOutNotificationFragment;
import com.plexus.notifications.PlexusNotificationFragment;
import com.plexus.utils.PlexusPreferences;

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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }

    private void initPlexusOnly(){

        tabLayout.setVisibility(View.GONE);
        plexusNotificationFragment = new PlexusNotificationFragment();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(plexusNotificationFragment, "Plexus");
        viewPager.setAdapter(viewPagerAdapter);

    }

    private void initAll() {

        lookOutNotificationFragment = new LookOutNotificationFragment();
        plexusNotificationFragment = new PlexusNotificationFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(plexusNotificationFragment, "Plexus");
        viewPagerAdapter.addFragment(lookOutNotificationFragment, "LookOut");
        viewPager.setAdapter(viewPagerAdapter);

        BadgeDrawable plexusNotificationCount = tabLayout.getTabAt(0).getOrCreateBadge();
        plexusNotificationCount.setVisible(false);
        plexusNotificationCount.setNumber(1);

        BadgeDrawable lookoutNotificationCount = tabLayout.getTabAt(0).getOrCreateBadge();
        lookoutNotificationCount.setVisible(false);
        lookoutNotificationCount.setNumber(1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        clear_notifications.setOnClickListener(v -> deletePlexusNotifications());
                        break;
                    case 1:
                        clear_notifications.setOnClickListener(v -> deleteLookoutNotifications());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Nothing here needs to be done
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Nothing here needs to be done
            }
        });
    }


    private void deletePlexusNotifications() {
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

    private void deleteLookoutNotifications() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView description = dialog.findViewById(R.id.description);
        TextView delete = dialog.findViewById(R.id.delete_all);
        TextView cancel = dialog.findViewById(R.id.cancel);

        description.setText("Are you sure you want to clear all notifications? \n \n You won't be able to recover it after clearing.");

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance("https://saveourchildren.firebaseio.com/")
                    .getReference("Users")
                    .child(firebaseUser.getUid()).child("Notification")
                    .removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "All notifications cleared", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        });

        dialog.show();
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager, int behaviour) {
            super(supportFragmentManager, behaviour);

        }

        public void addFragment(Fragment fragment, String title) {
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
