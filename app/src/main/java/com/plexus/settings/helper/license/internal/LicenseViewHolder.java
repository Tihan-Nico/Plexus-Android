package com.plexus.settings.helper.license.internal;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.R;
import com.plexus.settings.helper.license.License;

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

public final class LicenseViewHolder extends ViewHolderBase implements View.OnClickListener, LibrariesHolder.Listener {
    private final LibrariesHolder holder;

    private final TextView licenseName;
    private final TextView license;

    private ExpandableLibrary expandableLibrary;

    public LicenseViewHolder(View itemView, @NonNull LibrariesHolder holder) {
        super(itemView);
        this.holder = holder;

        licenseName = itemView.findViewById(R.id.license_name);
        license = itemView.findViewById(R.id.license);

        licenseName.setOnClickListener(this);
    }

    @Override
    public void bind(@NonNull ExpandableLibrary library) {
        expandableLibrary = library;
        boolean expanded = library.isExpanded();

        licenseName.setText(expandableLibrary.getLibrary().getLicense().getName());
        license.setText(R.string.license_loading);

        int visibility = expanded ? View.VISIBLE : View.GONE;
        // Hack to circumvent padding bug. See XML.
        ((FrameLayout) itemView).getChildAt(0).setVisibility(visibility);
        licenseName.setVisibility(visibility);
        license.setVisibility(visibility);

        if (expanded) holder.load(expandableLibrary.getLibrary(), this);
    }

    @Override
    public void onClick(View v) {
        String url = expandableLibrary.getLibrary().getLicense().getUrl();
        if (!TextUtils.isEmpty(url)) {
            launchUri(Uri.parse(url));
        }
    }

    @Override
    public void onComplete(@NonNull License license, @Nullable Exception e) {
        // Since this view holder could be reused for different libraries, ensure it wasn't
        // rebound while we were waiting for the license to load.
        if (expandableLibrary.getLibrary().getLicense().equals(license)) {
            if (e == null) {
                //noinspection ConstantConditions
                this.license.setText(license.getText());
            } else {
                this.license.setText(R.string.license_load_error);
            }
        }
    }
}
