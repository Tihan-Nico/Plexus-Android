package com.plexus.qr.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.camera.CameraView;
import com.plexus.qr.ScanListener;
import com.plexus.qr.ScanningThread;

public class ScanQrCodeActivity extends AppCompatActivity {

    LinearLayout overlay;
    CameraView scannerView;
    TextView scan_type;

    private ScanningThread scanningThread;
    private ScanListener scanListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scanner);

        overlay = findViewById(R.id.overlay);
        scannerView = findViewById(R.id.scanner);
        scan_type = findViewById(R.id.scan_type);


    }

    private void init(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.overlay.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            this.overlay.setOrientation(LinearLayout.VERTICAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.scanningThread = new ScanningThread();
        this.scanningThread.setScanListener(scanListener);
        this.scannerView.onResume();
        this.scannerView.setPreviewCallback(scanningThread);
        this.scanningThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.scannerView.onPause();
        this.scanningThread.stopScanning();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);

        this.scannerView.onPause();

        if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            overlay.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            overlay.setOrientation(LinearLayout.VERTICAL);
        }

        this.scannerView.onResume();
        this.scannerView.setPreviewCallback(scanningThread);
    }

    public void setScanListener(ScanListener scanListener) {
        this.scanListener = scanListener;

        if (this.scanningThread != null) {
            this.scanningThread.setScanListener(scanListener);
        }
    }

}
