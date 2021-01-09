package com.plexus.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.plexus.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    View toolbar;
    EditText email;
    MaterialButton send_email;
    TextView link_signin;
    LinearLayout link_signin_ln;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = findViewById(R.id.toolbar);
        email = findViewById(R.id.email);
        send_email = findViewById(R.id.send_email);
        link_signin = findViewById(R.id.link_signin);
        link_signin_ln = findViewById(R.id.link_signin_ln);

        firebaseAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean fromSignIn = extras.getBoolean("fromSignIn", false);
            if (fromSignIn) {
                link_signin_ln.setVisibility(View.VISIBLE);
            } else {
                link_signin_ln.setVisibility(View.GONE);
            }

        }

        init();

    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());
        toolbar_name.setText("Forgot Password");

        send_email.setOnClickListener(v -> firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(task -> startActivity(new Intent(getApplicationContext(), LoginActivity.class))));

        link_signin.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

    }

}
