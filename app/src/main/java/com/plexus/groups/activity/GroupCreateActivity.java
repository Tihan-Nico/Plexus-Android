package com.plexus.groups.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plexus.R;
import com.plexus.core.background.DialogInformation;
import com.plexus.utils.MasterCipher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GroupCreateActivity extends AppCompatActivity {

    ImageView back;
    EditText group_name;
    TextView group_privacy;
    LinearLayout choose_privacy;
    Button create_group;

    String current_date = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date());
    String ID;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        back = findViewById(R.id.back);
        group_name = findViewById(R.id.group_name);
        group_privacy = findViewById(R.id.group_privacy);
        choose_privacy = findViewById(R.id.choose_privacy);
        create_group = findViewById(R.id.create_group);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        back.setOnClickListener(view -> finish());
        create_group.setOnClickListener(view -> createGroup());
        choose_privacy.setOnClickListener(view -> DialogInformation.selectPrivacy(GroupCreateActivity.this, group_privacy));
    }

    private void createGroup(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        ID = databaseReference.push().getKey();

        HashMap<String, Object> groupData = new HashMap<>();
        groupData.put("id", ID);
        groupData.put("name", MasterCipher.encrypt(group_name.getText().toString()));
        groupData.put("privacy", group_privacy.getText().toString());
        groupData.put("createdAt", current_date);

        addAdmin(ID);
        addGroupToProfile(ID);
        addMemberToGroup(ID);

        databaseReference.child(ID).setValue(groupData).addOnSuccessListener(unused -> {
            startActivity(new Intent(getApplicationContext(), GroupActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(GroupCreateActivity.this, "Couldn't create group at this moment!", Toast.LENGTH_SHORT).show());
    }

    private void addAdmin(String ID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(ID).child("Admins");

        HashMap<String, Object> groupData = new HashMap<>();
        groupData.put(firebaseUser.getUid(), true);

        databaseReference.setValue(groupData);
    }

    private void addGroupToProfile(String ID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Groups");

        HashMap<String, Object> groupData = new HashMap<>();
        groupData.put(ID, true);

        databaseReference.setValue(groupData);
    }

    private void addMemberToGroup(String ID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(ID).child("Members");

        HashMap<String, Object> groupData = new HashMap<>();
        groupData.put(firebaseUser.getUid(), true);

        databaseReference.setValue(groupData);
    }

}
