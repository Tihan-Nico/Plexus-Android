package com.plexus.settings.fragments.two_factor;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorAssertion;
import com.google.firebase.auth.MultiFactorSession;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneMultiFactorGenerator;
import com.plexus.R;

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

public class TwoFactorCode extends AppCompatActivity {

    private String verificationId, code;
    private FirebaseAuth mAuth;
    private EditText sms_code;
    private FirebaseUser firebaseUser;
    private MultiFactorAssertion multiFactorAssertion;
    private PhoneAuthCredential credential;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        private PhoneAuthCredential credential;
        private String verificationId;
        private PhoneAuthProvider.ForceResendingToken forceResendingToken;

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1) Instant verification. In some cases, the phone number can be
            //    instantly verified without needing to send or enter a verification
            //    code. You can disable this feature by calling
            //    PhoneAuthOptions.builder#requireSmsValidation(true) when building
            //    the options to pass to PhoneAuthProvider#verifyPhoneNumber().
            // 2) Auto-retrieval. On some devices, Google Play services can
            //    automatically detect the incoming verification SMS and perform
            //    verification without user action.
            this.credential = credential;
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in response to invalid requests for
            // verification, like an incorrect phone number.
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }
            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number.
            // We now need to ask the user to enter the code and then construct a
            // credential by combining the code with a verification ID.
            // Save the verification ID and resending token for later use.
            this.verificationId = verificationId;
            this.forceResendingToken = token;
            // ...
        }
    };

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_two_factor_enter_code);

        sms_code = findViewById(R.id.sms_code);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String phonenumber = getIntent().getStringExtra("phonenumber");

        firebaseUser.getMultiFactor().getSession()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                MultiFactorSession multiFactorSession = task.getResult();
                                PhoneAuthOptions phoneAuthOptions =
                                        PhoneAuthOptions.newBuilder()
                                                .setPhoneNumber(phonenumber)
                                                .setTimeout(30L, TimeUnit.SECONDS)
                                                .setMultiFactorSession(multiFactorSession)
                                                .setCallbacks(callbacks)
                                                .build();
                                // Send SMS verification code.
                                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
                            }
                        });

        // Ask user for the verification code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential);
        // Complete enrollment.
        FirebaseAuth.getInstance()
                .getCurrentUser()
                .getMultiFactor()
                .enroll(multiFactorAssertion, "My personal phone number")
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });

    }
}
