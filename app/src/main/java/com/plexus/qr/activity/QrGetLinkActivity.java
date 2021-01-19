package com.plexus.qr.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.plexus.R;
import com.plexus.components.components.bottomsheet.adapter.SheetOptionsAdapter;
import com.plexus.components.components.bottomsheet.model.SheetOptions;
import com.plexus.posts.activity.CreatePostActivity;

import java.util.ArrayList;

public class QrGetLinkActivity extends AppCompatActivity {

    View toolbar;
    LinearLayout share_link;
    SwitchMaterial shareable_link;
    TextView link, title;

    String type;
    String ID;

    public static final String[] titles = new String[]{"Share via Plexus", "Copy", "QR Code", "Share"};
    public static final Integer[] images = {R.drawable.plexus_logo, R.drawable.message_copy, R.drawable.qrcode, R.drawable.share_external};

    private BottomSheetDialog qr_sheet;
    private ArrayList<SheetOptions> rowItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_get_link);

        toolbar = findViewById(R.id.toolbar);
        share_link = findViewById(R.id.share_link);
        shareable_link = findViewById(R.id.shareable_link);
        link = findViewById(R.id.link);
        title = findViewById(R.id.title);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        ID = intent.getStringExtra("id");

        init();

    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("Shareable Link");

        if (type.equals("group")){
            title.setText("Your Group Link");
        } else {
            title.setText("Your Profile Link");
        }

        link.setText(generateDeepLinkUrl());

        share_link.setOnClickListener(v -> {

            qr_sheet = new BottomSheetDialog(QrGetLinkActivity.this, R.style.BottomSheetDialogTheme);
            qr_sheet.setContentView(R.layout.sheet_layout);
            ListView listView = qr_sheet.findViewById(R.id.listview);

            rowItems = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                SheetOptions item = new SheetOptions(titles[i], images[i]);
                rowItems.add(item);
            }

            SheetOptionsAdapter optionsAdapter = new SheetOptionsAdapter(QrGetLinkActivity.this, rowItems);
            listView.setAdapter(optionsAdapter);

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                    intent.putExtra("link", generateDeepLinkUrl());
                    startActivity(intent);
                    qr_sheet.dismiss();
                }

                if (position == 1) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("link", generateDeepLinkUrl());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Link Copied", Toast.LENGTH_SHORT).show();
                    qr_sheet.dismiss();
                }

                if (position == 2) {
                    Intent intent = new Intent(getApplicationContext(), GenerateQrCodeActivity.class);
                    intent.putExtra("id", ID);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    qr_sheet.dismiss();
                }

                if (position == 3) {
                    shareDeepLink(generateDeepLinkUrl());
                    qr_sheet.dismiss();
                }

            });

            qr_sheet.show();

        });

    }

    private String generateDeepLinkUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("plexus.dev")
                .appendPath(type)
                .appendQueryParameter("id", ID);
        return builder.build().toString();
    }

    private void shareDeepLink(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share" + type + " via"));
    }

}
