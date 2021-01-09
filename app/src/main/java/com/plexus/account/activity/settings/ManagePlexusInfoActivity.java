package com.plexus.account.activity.settings;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;

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

public class ManagePlexusInfoActivity extends AppCompatActivity {

    RelativeLayout required_details, download_account_info, delete_account_info, linked_services;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_manage_plexus_info);

        required_details = findViewById(R.id.required_details);
        download_account_info = findViewById(R.id.download_account_info);
        delete_account_info = findViewById(R.id.delete_account_info);
        linked_services = findViewById(R.id.linked_services);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

    }

}
