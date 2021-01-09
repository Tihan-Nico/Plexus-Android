package com.plexus.account.management.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.model.account.User;
import com.plexus.utils.MasterCipher;

public class RequestVerificationActivity extends AppCompatActivity {

    TextView verification_description;
    View toolbar;
    EditText username, fullname, user_id, known_as;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_request_verification);

        verification_description = findViewById(R.id.verification_description);
        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        user_id = findViewById(R.id.user_id);
        known_as = findViewById(R.id.known_as);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init();

    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("Request Verification");

        fetchData();
        setVerificationDescriptionStyle();

    }

    private void fetchData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                username.setText(user.getUsername());
                fullname.setText(MasterCipher.decrypt(user.getName()) + " " + MasterCipher.decrypt(user.getSurname()));
                user_id.setText(firebaseUser.getUid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setVerificationDescriptionStyle(){
        String text = "A verified badge is a check mark that appears next to your name on Plexus to indicate that your account is the authentic presence of you.\n\nSubmitting a request for verification does not guarantee that you will get verified. Check our requirements for receiving a verification. Learn More";

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        ForegroundColorSpan learn_more = new ForegroundColorSpan(Color.parseColor("#f78361"));

        ssb.setSpan(learn_more, 276, 287, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        verification_description.setText(ssb);
    }

}
