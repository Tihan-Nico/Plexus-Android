package com.plexus.jobs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.plexus.jobmanagers.BaseJob;
import com.plexus.jobmanagers.Data;
import com.plexus.jobmanagers.Job;
import com.plexus.jobmanagers.impl.NetworkConstraint;
import com.plexus.transport.PushNetworkException;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.logging.Log;

import java.io.IOException;

public class CreateSignedPreKeyJob extends BaseJob {

    public static final String KEY = "CreateSignedPreKeyJob";

    private static final String TAG = CreateSignedPreKeyJob.class.getSimpleName();

    public CreateSignedPreKeyJob(Context context) {
        this(new Job.Parameters.Builder()
                .addConstraint(NetworkConstraint.KEY)
                .setQueue("CreateSignedPreKeyJob")
                .setMaxAttempts(25)
                .build());
    }

    private CreateSignedPreKeyJob(@NonNull Job.Parameters parameters) {
        super(parameters);
    }

    @Override
    public @NonNull
    Data serialize() {
        return Data.EMPTY;
    }

    @Override
    public @NonNull
    String getFactoryKey() {
        return KEY;
    }

    @Override
    public void onRun() throws IOException {
        if (PlexusPreferences.isSignedPreKeyRegistered(context)) {
            Log.w(TAG, "Signed prekey already registered...");
            return;
        }

        if (!PlexusPreferences.isPushRegistered(context)) {
            Log.w(TAG, "Not yet registered...");
            return;
        }

        PlexusPreferences.setSignedPreKeyRegistered(context, true);
    }

    @Override
    public void onFailure() {
    }

    @Override
    public boolean onShouldRetry(@NonNull Exception exception) {
        if (exception instanceof PushNetworkException) return true;
        return false;
    }

    public static final class Factory implements Job.Factory<CreateSignedPreKeyJob> {
        @Override
        public @NonNull
        CreateSignedPreKeyJob create(@NonNull Parameters parameters, @NonNull Data data) {
            return new CreateSignedPreKeyJob(parameters);
        }
    }
}
