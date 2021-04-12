package com.plexus.jobs;

import com.plexus.BuildConfig;
import com.plexus.PlexusExpiredException;
import com.plexus.jobmanagers.BaseJob;
import com.plexus.jobmanagers.Job;
import com.plexus.keyvalue.PlexusStore;

public abstract class SendJob extends BaseJob {

    @SuppressWarnings("unused")
    private final static String TAG = SendJob.class.getSimpleName();

    public SendJob(Job.Parameters parameters) {
        super(parameters);
    }

    @Override
    public final void onRun() throws Exception {
        if (PlexusStore.misc().isClientDeprecated()) {
            throw new PlexusExpiredException(String.format("Plexus expired (build %d, now %d)",
                    BuildConfig.BUILD_TIMESTAMP,
                    System.currentTimeMillis()));
        }
    }

    protected abstract void onSend() throws Exception;
}
