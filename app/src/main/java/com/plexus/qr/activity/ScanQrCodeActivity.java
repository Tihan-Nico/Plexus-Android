package com.plexus.qr.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.camera.CameraView;
import com.plexus.qr.LinkClickedListener;
import com.plexus.qr.ScanListener;
import com.plexus.qr.ScanningThread;
import com.plexus.utils.Util;
import com.plexus.utils.logging.Log;
import com.plexus.utils.task.ProgressDialogAsyncTask;

public class ScanQrCodeActivity extends AppCompatActivity  implements ScanListener, LinkClickedListener {

    private static final String TAG = ScanQrCodeActivity.class.getSimpleName();

    LinearLayout overlay;
    CameraView scannerView;
    TextView scan_type;

    private LinkClickedListener linkClickedListener;
    private Uri uri;
    private ScanningThread scanningThread;
    private ScanListener scanListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scanner);

        overlay = findViewById(R.id.overlay);
        scannerView = findViewById(R.id.scanner);
        scan_type = findViewById(R.id.scan_type);

        init();

    }

    private void init(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.overlay.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            this.overlay.setOrientation(LinearLayout.VERTICAL);
        }

        setScanListener(scanListener);

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

    @Override
    public void onQrDataFound(final String data) {
        Util.runOnMain(() -> {
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
            Uri uri = Uri.parse(data);
            setLinkClickedListener(uri, (LinkClickedListener) ScanQrCodeActivity.this);
        });
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onLink(Uri uri) {
        new ProgressDialogAsyncTask<Void, Void, Integer>(this, "Fetching ID", "Getting information")
        {
            private static final int SUCCESS        = 0;
            private static final int NO_EXISTENCE = 1;
            private static final int NETWORK_ERROR  = 2;
            private static final int KEY_ERROR      = 3;
            private static final int BAD_CODE       = 4;

            @Override
            protected Integer doInBackground(Void... params) {
                Context context = ScanQrCodeActivity.this;
                String id = uri.getQueryParameter("id");
                String type = uri.getQueryParameter("type");

                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type)) {
                    Log.w(TAG, "UUID or Key is empty!");
                    return BAD_CODE;
                }

                return SUCCESS;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);

                Context context = ScanQrCodeActivity.this;

                switch (result) {
                    case SUCCESS:
                        Toast.makeText(context, "Found ID!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    case NO_EXISTENCE:
                        Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_ERROR:
                        Toast.makeText(context, "Check network connection", Toast.LENGTH_LONG).show();
                        break;
                    case KEY_ERROR:
                        Toast.makeText(context, "Somethings wrong with the QR Code", Toast.LENGTH_LONG).show();
                        break;
                    case BAD_CODE:
                        Toast.makeText(context, "Faulty QR Code", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setLinkClickedListener(Uri uri, LinkClickedListener linkClickedListener) {
        this.uri = uri;
        this.linkClickedListener = linkClickedListener;
    }

}
