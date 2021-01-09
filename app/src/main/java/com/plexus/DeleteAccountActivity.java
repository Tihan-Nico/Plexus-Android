package com.plexus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class DeleteAccountActivity extends AppCompatActivity {

    View toolbar;
    TextView delete_information;
    MaterialButton account_delete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);

        toolbar = findViewById(R.id.toolbar);
        delete_information = findViewById(R.id.delete_information);
        account_delete = findViewById(R.id.account_delete);

        init();

    }

    private void init(){

        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_name);
        ImageView back = toolbar.findViewById(R.id.back);

        toolbar_title.setText("Delete Account");
        back.setOnClickListener(v -> finish());

        account_delete.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), DeleteAccountFeedback.class)));

        setInformationText();
    }

    private void setInformationText(){
        String text = "Deleting your account is permanent. When you delete your Plexus account, you won't be able to retrieve any information you have shared on Plexus.\\n\\nThe following information will be deleted:\\n\\n· LookOut Data\\n· LookOut Messages\\n· LookOut Images\\n· Plexus Data\\n· Plexus Messages\\n· Plexus Images\\n\\nBefore you delete your account you will be asked to provide some feedback, as we be may be able to help you with common issues. You can also continue to delete your account.";

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        ForegroundColorSpan lookout_data = new ForegroundColorSpan(Color.parseColor(String.valueOf(R.color.textDescriptionColor)));
        ForegroundColorSpan lookout_messages = new ForegroundColorSpan(Color.parseColor(String.valueOf(R.color.textDescriptionColor)));
        ForegroundColorSpan lookout_images = new ForegroundColorSpan(Color.parseColor(String.valueOf(R.color.textDescriptionColor)));
        ForegroundColorSpan plexus_data = new ForegroundColorSpan(Color.parseColor(String.valueOf(R.color.textDescriptionColor)));
        ForegroundColorSpan plexus_messages = new ForegroundColorSpan(Color.parseColor(String.valueOf(R.color.textDescriptionColor)));
        ForegroundColorSpan plexus_images = new ForegroundColorSpan(Color.parseColor(String.valueOf(R.color.textDescriptionColor)));

        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(lookout_data, 198, 210, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(lookout_messages, 214, 230, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(lookout_images, 233, 247, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(plexus_data, 252, 263, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(plexus_messages, 267, 282, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(plexus_images, 286, 299, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        delete_information.setText(ssb);
    }

}
