package com.plexus.qr.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plexus.R;
import com.plexus.providers.BlobProvider;
import com.plexus.qr.QrView;
import com.plexus.utils.QrCode;

import com.plexus.utils.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GenerateQrCodeActivity extends AppCompatActivity {

    private static final String TAG = Log.tag(GenerateQrCodeActivity.class);

    View toolbar;
    MaterialButton share_qr;
    QrView qrImageView;

    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_qr_code);

        toolbar = findViewById(R.id.toolbar);
        share_qr = findViewById(R.id.share_qr);
        qrImageView = findViewById(R.id.qr_image);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        init();
    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("QR Code");

        presentUrl(generateDeepLinkUrl());

    }

    private String generateDeepLinkUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("plexus.dev")
                .appendPath("profile")
                .appendQueryParameter("id", id);
        return builder.build().toString();
    }

    private void presentUrl(@Nullable String url) {
        qrImageView.setQrText(url);

        // Restricted to API26 because of MemoryFileUtil not supporting lower API levels well
        if (Build.VERSION.SDK_INT >= 26) {
            share_qr.setOnClickListener(v -> {
                Uri shareUri;

                try {
                    shareUri = createTemporaryPng(url);
                } catch (IOException e) {
                    Log.w(TAG, e);
                    return;
                }

                Intent intent = ShareCompat.IntentBuilder.from(GenerateQrCodeActivity.this)
                        .setType("image/png")
                        .setStream(shareUri)
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);
            });
        } else {
            share_qr.setVisibility(View.GONE);
        }
    }

    private static Uri createTemporaryPng(@Nullable String url) throws IOException {
        Bitmap qrBitmap = QrCode.create(url, Color.BLACK, Color.WHITE);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();

            byte[] bytes = byteArrayOutputStream.toByteArray();

            return BlobProvider.getInstance()
                    .forData(bytes)
                    .withMimeType("image/png")
                    .withFileName("SignalGroupQr.png")
                    .createForSingleSessionInMemory();
        } finally {
            qrBitmap.recycle();
        }
    }

}
