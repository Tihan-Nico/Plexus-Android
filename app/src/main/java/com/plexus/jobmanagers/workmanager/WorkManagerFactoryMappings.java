package com.plexus.jobmanagers.workmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.jobs.UpdateLookoutJob;

import java.util.HashMap;
import java.util.Map;

public class WorkManagerFactoryMappings {

    private static final Map<String, String> FACTORY_MAP = new HashMap<String, String>() {{
        put("UpdateLookoutJob", UpdateLookoutJob.KEY);
    }};

    public static @Nullable
    String getFactoryKey(@NonNull String workManagerClass) {
        return FACTORY_MAP.get(workManagerClass);
    }
}
