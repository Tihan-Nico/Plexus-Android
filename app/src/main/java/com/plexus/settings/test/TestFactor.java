package com.plexus.settings.test;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.plexus.R;
import com.plexus.settings.activity.SettingsActivity;
import com.plexus.settings.helper.CountryData;

import java.util.concurrent.TimeUnit;

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

public class TestFactor extends AppCompatActivity {

    Button confirm, send_code;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    EditText sms_code;
    String mVerificationId, code;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private EditText phone_number;
    private TextView resend_code;
    private Spinner spinnerCountries;
    private Context context;
    private Dialog reAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_two_factor_enter_number);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        confirm = findViewById(R.id.confirm);
        phone_number = findViewById(R.id.phone_number);
        spinnerCountries = findViewById(R.id.spinnerCountries);
        send_code = findViewById(R.id.send_code);
        resend_code = findViewById(R.id.resend_code);
        sms_code = findViewById(R.id.sms_code);

        spinnerCountries.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        confirm.setOnClickListener(v -> {
            String code = CountryData.countryAreaCodes[spinnerCountries.getSelectedItemPosition()];

            String number = phone_number.getText().toString().trim();

            if (number.isEmpty() || number.length() < 10) {
                phone_number.setError("Valid number is required");
                phone_number.requestFocus();
                return;
            }

            String phoneNumber = "+" + code + number;

            send_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPhoneNumberVerification(phoneNumber);
                }
            });

            resend_code.setOnClickListener(v1 -> resendVerificationCode(phoneNumber, forceResendingToken));

        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                linkCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                /*if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                forceResendingToken = token;
            }
        };

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumberWithCode(mVerificationId, sms_code.getText().toString());
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = task.getResult().getUser();
                            reAuthenticateUser();
                        } else {
                            /*Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }*/
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void reAuthenticateUser() {

        final Dialog dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.dialog_reauthenticate);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        EditText password_et = dialog.findViewById(R.id.password);
        EditText email_et = dialog.findViewById(R.id.email);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView login = dialog.findViewById(R.id.login);

        AuthCredential authCredential = EmailAuthProvider.getCredential(email_et.getText().toString(), password_et.getText().toString());

        login.setOnClickListener(v -> {
            login.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        });

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(TestFactor.this, SettingsActivity.class));
        });

        firebaseUser.reauthenticate(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        linkCredential(authCredential);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(TestFactor.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                callbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                callbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void linkCredential(AuthCredential credential) {
        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        firebaseUser = task.getResult().getUser();
                        Toast.makeText(TestFactor.this, "Merged", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(TestFactor.this, "Failed to merge" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
