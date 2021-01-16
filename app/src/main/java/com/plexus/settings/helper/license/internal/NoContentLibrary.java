package com.plexus.settings.helper.license.internal;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.plexus.settings.helper.license.BaseLibrary;
import com.plexus.settings.helper.license.License;
import com.plexus.settings.helper.license.OpenSourceLibrary;

/**
 * Library without license text.
 */

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

public final class NoContentLibrary extends BaseLibrary implements OpenSourceLibrary {
    public NoContentLibrary(String name, String author, License license) {
        super(name, author, license);
        if (TextUtils.isEmpty(license.getUrl())) {
            throw new IllegalArgumentException("License url must not be null.");
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @NonNull
    @Override
    public License doLoad() {
        // There's no content
        return getLicense();
    }

    @Override
    public boolean hasContent() {
        return false;
    }

    @NonNull
    @Override
    public String getSourceUrl() {
        //noinspection ConstantConditions checked in constuctor
        return getLicense().getUrl();
    }
}
