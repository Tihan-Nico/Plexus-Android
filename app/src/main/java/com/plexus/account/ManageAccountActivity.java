package com.plexus.account;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.MessageFormat;

public class ManageAccountActivity extends AppCompatActivity {

    ImageView back;
    SimpleDraweeView profile_image;
    TextView fullname, birthday, gender, email, number;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        back = findViewById(R.id.back);
        profile_image = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.fullname);
        birthday = findViewById(R.id.birthday);
        gender = findViewById(R.id.gender);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        email.setText(firebaseUser.getEmail());

        back.setOnClickListener(v -> finish());

        getProfileData();

        init();
    }

    private void init(){
        profile_image.setOnClickListener(v -> CropImage.activity().getIntent(ManageAccountActivity.this));
    }

    private void getProfileData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                profile_image.setImageURI(MasterCipher.decrypt(user.getImageurl()));
                fullname.setText(MessageFormat.format("{0} {1}", MasterCipher.decrypt(user.getName()), MasterCipher.decrypt(user.getSurname())));
                birthday.setText(MasterCipher.decrypt(user.getBirthday()));
                gender.setText(MasterCipher.decrypt(user.getGender()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
