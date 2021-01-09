package com.plexus.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.MultiFactor;
import com.plexus.R;
import com.plexus.account.management.activities.AccountLoginActivity;
import com.plexus.account.management.activities.AccountStatusActivity;
import com.plexus.account.management.activities.RequestVerificationActivity;
import com.plexus.account.management.activities.SearchHistoryActivity;
import com.plexus.account.management.activities.SecurityQuestionsActivity;
import com.plexus.account.management.activities.SharingActivity;

public class AccountSettingsActivity extends AppCompatActivity {

    View toolbar;
    RelativeLayout manage_account, multi_factor, security_questions, request_verification, account_login, search_history, account_status, sharing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        toolbar = findViewById(R.id.toolbar);
        manage_account = findViewById(R.id.manage_account);
        multi_factor = findViewById(R.id.multi_factor);
        security_questions = findViewById(R.id.security_questions);
        request_verification = findViewById(R.id.request_verification);
        account_login = findViewById(R.id.account_login);
        search_history = findViewById(R.id.search_history);
        account_status = findViewById(R.id.account_status);
        sharing = findViewById(R.id.sharing);

        init();

    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("Account Settings");

        manage_account.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ManageAccountActivity.class)));

        multi_factor.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MultiFactor.class)));

        sharing.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SharingActivity.class)));

        security_questions.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SecurityQuestionsActivity.class)));

        request_verification.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RequestVerificationActivity.class)));

        account_status.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AccountStatusActivity.class)));

        search_history.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SearchHistoryActivity.class)));

        account_login.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AccountLoginActivity.class)));

    }
}
