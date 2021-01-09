package com.plexus.groups.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plexus.R;
import com.plexus.core.background.PlexusUpload;
import com.plexus.model.group.Group;
import com.plexus.utils.MasterCipher;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GroupEditActivity extends AppCompatActivity {

    ImageView back;
    SimpleDraweeView group_cover;
    LinearLayout lin_name, lin_about, lin_location, lin_group_type, lin_group_colour;
    TextView group_color, group_type, location, about_view, name;

    String groupID, groupName, groupAbout;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        back = findViewById(R.id.back);
        group_cover = findViewById(R.id.group_cover);
        lin_name = findViewById(R.id.lin_name);
        lin_about = findViewById(R.id.lin_about);
        lin_location = findViewById(R.id.lin_location);
        lin_group_type = findViewById(R.id.lin_group_type);
        lin_group_colour = findViewById(R.id.lin_group_colour);
        group_color = findViewById(R.id.group_color);
        group_type = findViewById(R.id.group_type);
        location = findViewById(R.id.location);
        about_view = findViewById(R.id.about_view);
        name = findViewById(R.id.name);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupName = intent.getStringExtra("groupName");
        groupAbout = intent.getStringExtra("groupAbout");

        back.setOnClickListener(view -> finish());
        group_color.setOnClickListener(view -> CropImage.activity().start(GroupEditActivity.this));
        lin_name.setOnClickListener(view -> editName());
        lin_about.setOnClickListener(view -> editAbout());

        init();

    }

    private void init(){
        getGroupData();
        selectLocation();
    }

    private void getGroupData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                group_cover.setImageURI(MasterCipher.decrypt(group.getCoverImageUrl()));
                name.setText(MasterCipher.decrypt(group.getName()));
                about_view.setText(MasterCipher.decrypt(group.getAbout()));
                group_type.setText(MasterCipher.decrypt(group.getType()));
                group_color.setText(group.getColour());

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void selectLocation(){
        Dialog countries = new Dialog(GroupEditActivity.this);
        countries.setContentView(R.layout.dialog_list_countries);
        countries.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        countries.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final ListView countries_list = countries.findViewById(R.id.list);
        countries_list.setOnItemClickListener(
                (adapterView, view, position, id) -> {
                    String selectedFromList = (String) countries_list.getItemAtPosition(position);
                    location.setText(selectedFromList);
                    updateLocation(selectedFromList);
                    countries.dismiss();
                });

        lin_location.setOnClickListener(v -> {
            countries.show();
        });

    }

    private void updateLocation(String value){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("location", MasterCipher.encrypt(value));

        reference.updateChildren(hashMap);
    }

    private void editName(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(GroupEditActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_edit_name);

        TextView title = bottomSheetDialog.findViewById(R.id.title_edit);
        EditText edit_text = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btn_save = bottomSheetDialog.findViewById(R.id.btn_save);

        title.setText("Edit Group Name");
        edit_text.setHint(groupName);

        btn_save.setOnClickListener(view -> {
            updateName(edit_text.getText().toString());
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void updateName(String value) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", MasterCipher.encrypt(value));

        reference.updateChildren(hashMap);
    }

    private void editAbout(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(GroupEditActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.sheet_edit_about);

        TextView title = bottomSheetDialog.findViewById(R.id.title_edit);
        EditText edit_text = bottomSheetDialog.findViewById(R.id.edit_text);
        Button btn_save = bottomSheetDialog.findViewById(R.id.btn_save);

        title.setText("Edit Group Description");
        edit_text.setHint(groupAbout);

        btn_save.setOnClickListener(view -> {
            updateAbout(edit_text.getText().toString());
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void updateAbout(String value) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("about", MasterCipher.encrypt(value));

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                imageUri = result.getUri();

                PlexusUpload.uploadGroupCover(getApplicationContext(), imageUri, groupID);
            }
        }
    }

}
