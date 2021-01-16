package com.plexus.account.management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.startup.ForgotPasswordActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    View toolbar;
    EditText current_password, new_password, re_type_new_password;
    MaterialButton update_password;
    TextView forgot_password, current_password_notice, re_type_new_password_notice;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_password);

        toolbar = findViewById(R.id.toolbar);
        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        re_type_new_password = findViewById(R.id.re_type_new_password);
        update_password = findViewById(R.id.update_password);
        forgot_password = findViewById(R.id.forgot_password);
        current_password_notice = findViewById(R.id.current_password_notice);
        re_type_new_password_notice = findViewById(R.id.re_type_new_password_notice);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

    }

    private void init() {

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());
        toolbar_name.setText("Change Password");

        update_password.setOnClickListener(v -> {
            if (new_password.getText().toString().equals(re_type_new_password.getText().toString())) {
                authenticateUser(current_password.getText().toString());
            } else if (TextUtils.isEmpty(current_password.getText().toString())) {
                current_password_notice.setVisibility(View.VISIBLE);
                re_type_new_password_notice.setVisibility(View.GONE);
                current_password_notice.setText("Enter your password to continue");
            } else if (TextUtils.isEmpty(new_password.getText().toString())) {
                current_password_notice.setVisibility(View.GONE);
                re_type_new_password_notice.setVisibility(View.VISIBLE);
            } else {
                current_password_notice.setVisibility(View.GONE);
                re_type_new_password_notice.setVisibility(View.VISIBLE);
            }
        });

        forgot_password.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            intent.putExtra("fromSignIn", false);
            startActivity(intent);
        });

    }

    private void authenticateUser(String password) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUser.updatePassword(new_password.getText().toString()).addOnCompleteListener(task1 -> finish());
            } else {
                current_password_notice.setVisibility(View.VISIBLE);
            }
        });
    }
}
