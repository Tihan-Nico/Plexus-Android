package com.plexus.dependecies;

import android.app.Application;

import androidx.annotation.NonNull;

import com.plexus.database.DatabaseObserver;
import com.plexus.database.JobDatabase;
import com.plexus.jobmanagers.FastJobStorage;
import com.plexus.jobmanagers.JobManager;
import com.plexus.jobmanagers.impl.JsonDataSerializer;
import com.plexus.jobs.JobManagerFactories;
import com.plexus.megaphone.MegaphoneRepository;
import com.plexus.notifications.DefaultMessageNotifier;
import com.plexus.notifications.MessageNotifier;
import com.plexus.notifications.OptimizedMessageNotifier;
import com.plexus.shakereport.ShakeToReport;
import com.plexus.utils.AppForegroundObserver;
import com.plexus.utils.FrameRateTracker;
import com.plexus.utils.logging.Log;

/**
 * Implementation of {@link PlexusDependencies.Provider} that provides real app dependencies.
 */
public class PlexusDependencyProvider implements PlexusDependencies.Provider {

    private static final String TAG = Log.tag(PlexusDependencyProvider.class);

    private final Application context;

    public PlexusDependencyProvider(@NonNull Application context) {
        this.context = context;
    }

    @Override
    public @NonNull
    JobManager provideJobManager() {
        JobManager.Configuration config = new JobManager.Configuration.Builder()
                .setDataSerializer(new JsonDataSerializer())
                .setJobFactories(JobManagerFactories.getJobFactories(context))
                .setConstraintFactories(JobManagerFactories.getConstraintFactories(context))
                .setJobStorage(new FastJobStorage(JobDatabase.getInstance(context)))
                .build();

        return new JobManager(context, config);
    }

    @Override
    public @NonNull
    MessageNotifier provideMessageNotifier() {
        return new OptimizedMessageNotifier(new DefaultMessageNotifier());
    }

    @Override
    public @NonNull
    FrameRateTracker provideFrameRateTracker() {
        return new FrameRateTracker(context);
    }

    public @NonNull
    MegaphoneRepository provideMegaphoneRepository() {
        return new MegaphoneRepository(context);
    }

    @Override
    public @NonNull
    DatabaseObserver provideDatabaseObserver() {
        return new DatabaseObserver(context);
    }

    @Override
    public @NonNull
    ShakeToReport provideShakeToReport() {
        return new ShakeToReport(context);
    }

    @Override
    public @NonNull
    AppForegroundObserver provideAppForegroundObserver() {
        return new AppForegroundObserver();
    }

}
