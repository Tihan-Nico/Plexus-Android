package com.plexus.startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.main.activity.MainActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btn_login;
    TextView link_signup, forgot_password;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.login);
        link_signup = findViewById(R.id.link_signup);
        /*forgot_password = findViewById(R.id.forgot_password);*/

        firebaseAuth = FirebaseAuth.getInstance();

        init();

    }

    private void init() {
        /*forgot_password.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));*/

        link_signup.setOnClickListener(
                v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        btn_login.setOnClickListener(
                v -> {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    String txt_email = email.getText().toString();
                    String txt_password = password.getText().toString();

                    if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(txt_email, txt_password)
                                .addOnCompleteListener(
                                        task -> {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("loggedIn", true);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
