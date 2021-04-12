package com.plexus.jobmanagers;

import androidx.annotation.NonNull;

import com.plexus.jobmanagers.persistence.JobSpec;


public interface JobPredicate {
    JobPredicate NONE = jobSpec -> true;

    boolean shouldRun(@NonNull JobSpec jobSpec);
}
