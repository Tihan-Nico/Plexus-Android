package com.plexus.settings.helper.license.internal;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.plexus.settings.helper.license.Library;
import com.plexus.settings.helper.license.License;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public final class LibrariesHolder extends AndroidViewModel {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final String CACHE_DIR_NAME = "license-adapter-cache";
    private final LruCache<Library, License> cachedLicenses = new LruCache<>(25);

    public LibrariesHolder(@NonNull Application application) {
        super(application);
    }

    public void load(final Library library, Listener rawListener) {
        if (library.isLoaded()) rawListener.onComplete(library.getLicense(), null);

        final WeakReference<Listener> listener = new WeakReference<>(rawListener);
        License cache = cachedLicenses.get(library);
        if (cache == null) {
            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        library.load(new File(getApplication().getCacheDir(), CACHE_DIR_NAME + File.separator +
                                library.getAuthor() + File.separator + library.getName()));

                        License license = library.getLicense();
                        cachedLicenses.put(library, license);
                        notify(license, null);
                    } catch (Exception e) {
                        notify(library.getLicense(), e);
                    }
                }

                private void notify(@NonNull final License license, @Nullable final Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Listener rawListener = listener.get();
                            if (rawListener != null) {
                                rawListener.onComplete(license, e);
                            }
                        }
                    });
                }
            });
        } else {
            rawListener.onComplete(cache, null);
        }
    }

    public interface Listener {
        void onComplete(@NonNull License license, @Nullable Exception e);
    }
}
