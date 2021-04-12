package com.plexus.shakereport;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.plexus.R;
import com.plexus.core.utils.logging.Log;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.utils.FeatureFlags;
import com.plexus.utils.ServiceUtil;

import java.lang.ref.WeakReference;

/**
 * A class that will detect a shake and then prompts the user to submit a debuglog. Basically a
 * shortcut to submit a debuglog from anywhere.
 */
public final class ShakeToReport implements ShakeDetector.Listener {

    private static final String TAG = Log.tag(ShakeToReport.class);

    private final Application application;
    private final ShakeDetector detector;

    private WeakReference<Activity> weakActivity;

    public ShakeToReport(@NonNull Application application) {
        this.application = application;
        this.detector = new ShakeDetector(this);
        this.weakActivity = new WeakReference<>(null);
    }

    public void enable() {
        if (!FeatureFlags.internalUser()) return;
        detector.start(ServiceUtil.getSensorManager(application));
    }

    public void disable() {
        if (!FeatureFlags.internalUser()) return;
        detector.stop();
    }

    public void registerActivity(@NonNull Activity activity) {
        if (!FeatureFlags.internalUser()) return;
        this.weakActivity = new WeakReference<>(activity);
    }

    @Override
    public void onShakeDetected() {
        Activity activity = weakActivity.get();
        if (activity == null) {
            Log.w(TAG, "No registered activity!");
            return;
        }

        disable();

        Dialog shakeDetection = new Dialog(application);
        shakeDetection.setContentView(R.layout.dialog_shake_detection);
        shakeDetection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dismiss = shakeDetection.findViewById(R.id.dismiss);
        TextView feedback = shakeDetection.findViewById(R.id.feedback);

        dismiss.setOnClickListener(v -> {
            shakeDetection.dismiss();
            enableIfVisible();
        });
        /*feedback.setOnClickListener(v -> {
            application.startActivity(new Intent(application, SendFeedbackActivity.class));
            shakeDetection.dismiss();
        });*/
        shakeDetection.show();
    }

    private void enableIfVisible() {
        if (PlexusDependencies.getAppForegroundObserver().isForegrounded()) {
            enable();
        }
    }
}