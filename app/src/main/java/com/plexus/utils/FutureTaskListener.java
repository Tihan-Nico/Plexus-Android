package com.plexus.utils;

import java.util.concurrent.ExecutionException;

public interface FutureTaskListener<V> {
    public void onSuccess(V result);

    public void onFailure(ExecutionException exception);
}
