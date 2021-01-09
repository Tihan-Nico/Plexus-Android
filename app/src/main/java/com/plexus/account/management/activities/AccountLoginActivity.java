package com.plexus.account.management.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.account.management.adapters.DevicesAdapter;
import com.plexus.model.account.Devices;

import java.util.ArrayList;
import java.util.List;

public class AccountLoginActivity extends AppCompatActivity {

    View toolbar;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;

    List<Devices> devicesList;

    DevicesAdapter devicesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_logins);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        devicesList = new ArrayList<>();

        init();
    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());
        toolbar_name.setText("Login Activity");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        devicesAdapter = new DevicesAdapter(AccountLoginActivity.this, devicesList, AccountLoginActivity.this);
        recyclerView.setAdapter(devicesAdapter);

        readDevices();

    }

    private void readDevices(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Security").child("Login Activity");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                devicesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Devices devices = snapshot.getValue(Devices.class);
                    devicesList.add(devices);
                }
                devicesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
