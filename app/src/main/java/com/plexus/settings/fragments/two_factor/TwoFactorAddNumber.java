package com.plexus.settings.fragments.two_factor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.settings.helper.CountryData;

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

public class TwoFactorAddNumber extends AppCompatActivity {

    private Button confirm;
    private EditText phone_number;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private String verificationId;
    private Spinner spinnerCountries;
    private Context context;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_two_factor_enter_number);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        confirm = findViewById(R.id.confirm);
        phone_number = findViewById(R.id.phone_number);
        spinnerCountries = findViewById(R.id.spinnerCountries);

        spinnerCountries.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        confirm.setOnClickListener(v -> {
            String code = CountryData.countryAreaCodes[spinnerCountries.getSelectedItemPosition()];

            String number = phone_number.getText().toString().trim();

            if (number.isEmpty() || number.length() < 10) {
                phone_number.setError("Valid number is required");
                phone_number.requestFocus();
                return;
            }

            /*String phoneNumber = "+" + code + number;*/

            Intent intent = new Intent(TwoFactorAddNumber.this, TwoFactorCode.class);
            intent.putExtra("phonenumber", number);
            startActivity(intent);


        });

    }

}
