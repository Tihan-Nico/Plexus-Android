package com.plexus.settings.helper.license.internal;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

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

public abstract class ViewHolderBase extends RecyclerView.ViewHolder {
    private final int colorPrimary;

    public ViewHolderBase(View itemView) {
        super(itemView);

        colorPrimary = Utils.getIntValueFromAttribute(itemView.getContext(), androidx.appcompat.R.attr.colorPrimary);
    }

    protected final void launchUri(Uri uri) {
        new CustomTabsIntent.Builder()
                .setToolbarColor(colorPrimary)
                .setShowTitle(true)
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .build()
                .launchUrl(itemView.getContext(), uri);
    }

    public abstract void bind(@NonNull ExpandableLibrary library);
}
