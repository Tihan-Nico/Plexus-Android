package com.plexus.startup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.plexus.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {

    EditText username, name, surname, email, password;
    LinearLayout country_selection;
    Button register;
    TextView link_signin, country;
    private Dialog countries;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    TextView username_taken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_register);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        username = findViewById(R.id.username);
        country_selection = findViewById(R.id.country_selection);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        link_signin = findViewById(R.id.link_signin);
        country = findViewById(R.id.country);
        username_taken = findViewById(R.id.username_taken);

        auth = FirebaseAuth.getInstance();

        countries = new Dialog(RegisterActivity.this);
        countries.setContentView(R.layout.dialog_list_countries);
        countries.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        countries.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final ListView countries_list = countries.findViewById(R.id.list);
        countries_list.setOnItemClickListener(
                (adapterView, view, position, id) -> {
                    String selectedFromList = (String) countries_list.getItemAtPosition(position);
                    country.setText(selectedFromList);
                    countries.dismiss();
                });

        country_selection.setOnClickListener(v -> {
            countries.show();
        });

        link_signin.setOnClickListener(
                v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));


        register.setOnClickListener(
                view -> {
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Please wait...");
                    pd.show();

                    String str_username = username.getText().toString();
                    String str_name = name.getText().toString();
                    String str_surname = surname.getText().toString();
                    String str_email = email.getText().toString();
                    String str_password = password.getText().toString();
                    String str_country = country.getText().toString();

                    if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_surname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_country)) {
                        Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    } else if (str_password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
                    } else if (str_country.equals("Select a country")){
                        Toast.makeText(this, "Please select a country", Toast.LENGTH_SHORT).show();
                    } else {
                        register(str_username, str_name, str_surname, str_email, str_password, str_country);
                    }
                });

        Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(longTimed -> {
                    checkIfUsernameExists(username.getText().toString());
                });

    }

    public void register(final String username, final String name, final String surname, String email, String password, String country) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        RegisterActivity.this,
                        task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userID = firebaseUser.getUid();

                                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("id", userID);
                                map.put("username", username.toLowerCase());
                                map.put("name", name);
                                map.put("surname", surname);
                                map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/plexus-network.appspot.com/o/avatars%2Fmale_2.png?alt=media&token=ad6fb886-c021-4a80-9ec2-79034b9aaf42");
                                map.put("profile_cover", "");
                                map.put("country", country);
                                map.put("bio", "");
                                map.put("active", true);
                                map.put("account_type", "User");
                                map.put("presence", "Online");
                                map.put("typing", "nobody");
                                map.put("verifiedBefore", false);
                                map.put("verified", false);

                                reference
                                        .setValue(map)
                                        .addOnCompleteListener(
                                                task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        userPrivacy(userID);
                                                        chatPrivacy(userID);
                                                        postPrivacy(userID);
                                                        pd.dismiss();
                                                        Intent intent = new Intent(RegisterActivity.this, SetupProfileImageActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                });
                            } else {
                                pd.dismiss();
                                Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void checkIfUsernameExists(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Users").orderByChild("username").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if (!singleSnapshot.exists()){
                        username_taken.setVisibility(View.VISIBLE);
                        username_taken.setText("Username is available");
                        username_taken.setTextColor(Color.GREEN);
                    } else {
                        username_taken.setVisibility(View.VISIBLE);
                        username_taken.setText("Username is taken");
                        username_taken.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("HardwareIds")
    private void userPrivacy(String userID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy");

        HashMap<String, Object> deviceInfoMap = new HashMap<>();
        deviceInfoMap.put("account_private", false);

        reference.updateChildren(deviceInfoMap);
    }

    @SuppressLint("HardwareIds")
    private void chatPrivacy(String userID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Chat");

        HashMap<String, Object> deviceInfoMap = new HashMap<>();
        deviceInfoMap.put("screenshots_enabled", "Enabled");
        deviceInfoMap.put("last_seen_enabled", "Enabled");

        reference.updateChildren(deviceInfoMap);
    }

    @SuppressLint("HardwareIds")
    private void postPrivacy(String userID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Privacy").child("Posts");

        HashMap<String, Object> deviceInfoMap = new HashMap<>();
        deviceInfoMap.put("allow_screenshot", true);
        deviceInfoMap.put("allow_download", true);

        reference.updateChildren(deviceInfoMap);
    }

}
