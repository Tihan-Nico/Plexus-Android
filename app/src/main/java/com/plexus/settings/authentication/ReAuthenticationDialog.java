package com.plexus.settings.authentication;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.settings.activity.SettingsActivity;

import java.util.Objects;

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

public class ReAuthenticationDialog extends DialogFragment {

    public static String TAG = "ReAuthentication";
    private EditText password, email;
    private TextView cancel, login;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reauthenticate, container);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        password = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        cancel = view.findViewById(R.id.cancel);
        login = view.findViewById(R.id.login);

        login.setOnClickListener(v -> {
            reAuthenticateUser(email.getText().toString(), password.getText().toString());
            login.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        });

        cancel.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        return view;
    }

    private void reAuthenticateUser(String email, String password) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
        firebaseUser.reauthenticate(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void show(FragmentTransaction ft, String tag) {
    }
}
