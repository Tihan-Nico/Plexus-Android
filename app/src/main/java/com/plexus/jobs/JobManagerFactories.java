package com.plexus.jobs;

import android.app.Application;

import androidx.annotation.NonNull;

import com.plexus.jobmanagers.Constraint;
import com.plexus.jobmanagers.ConstraintObserver;
import com.plexus.jobmanagers.Job;
import com.plexus.jobmanagers.JobMigration;
import com.plexus.jobmanagers.impl.NetworkConstraint;
import com.plexus.migrations.LegacyMigrationJob;
import com.plexus.migrations.MigrationCompleteJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JobManagerFactories {

    public static Map<String, Job.Factory> getJobFactories(@NonNull Application application) {
        return new HashMap<String, Job.Factory>() {{
            put(CreateSignedPreKeyJob.KEY, new CreateSignedPreKeyJob.Factory());
            put(UpdateLookoutJob.KEY, new UpdateLookoutJob.Factory());

            // Migrations
            put(LegacyMigrationJob.KEY, new LegacyMigrationJob.Factory());
            put(MigrationCompleteJob.KEY, new MigrationCompleteJob.Factory());

        }};
    }

    public static Map<String, Constraint.Factory> getConstraintFactories(@NonNull Application application) {
        return new HashMap<String, Constraint.Factory>() {{
            put(NetworkConstraint.KEY, new NetworkConstraint.Factory(application));
        }};
    }

    public static List<ConstraintObserver> getConstraintObservers(@NonNull Application application) {
        return null;
    }

    public static List<JobMigration> getJobMigrations(@NonNull Application application) {
        return null;
    }
}
