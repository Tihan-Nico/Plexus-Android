package com.plexus.settings.helper.license;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.plexus.R;
import com.plexus.settings.helper.license.internal.ExpandableLibrary;
import com.plexus.settings.helper.license.internal.LibrariesHolder;
import com.plexus.settings.helper.license.internal.LibraryViewHolder;
import com.plexus.settings.helper.license.internal.LicenseViewHolder;
import com.plexus.settings.helper.license.internal.ViewHolderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} which displays expandable items with a library and its license.
 * <p>
 * For optimal performance, the {@link RecyclerView} using this adapter should be attached a support
 * library context such as the {@link AppCompatActivity}.
 *
 * @see Library
 * @see Licenses
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

public final class LicenseAdapter extends RecyclerView.Adapter<ViewHolderBase>
    implements ExpandableLibrary.ExpandListener {
  private static final int TYPE_LIBRARY = 0;
  private static final int TYPE_LICENSE = 1;

  private final List<ExpandableLibrary> libraries;
  private LibrariesHolder holder;

  /**
   * Construct a new adapter to display a list of libraries and their licenses.
   *
   * @param libraries the libraries to display
   */
  public LicenseAdapter(@NonNull List<Library> libraries) {
    List<ExpandableLibrary> wrappedLibraries = new ArrayList<>();
    for (Library library : libraries) {
      wrappedLibraries.add(new ExpandableLibrary(library, this));
    }
    this.libraries = Collections.unmodifiableList(wrappedLibraries);
  }

  @Override
  public int getItemViewType(int position) {
    return position % 2 == 0 ? TYPE_LIBRARY : TYPE_LICENSE;
  }

  @Override
  public int getItemCount() {
    return libraries.size() * 2;
  }

  @Override
  public ViewHolderBase onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();

    if (holder == null) {
      if (context instanceof FragmentActivity) {
        holder = ViewModelProviders.of((FragmentActivity) context).get(LibrariesHolder.class);
      } else if (context instanceof Activity) {
        holder = new LibrariesHolder(((Activity) context).getApplication());
      } else {
        holder = new LibrariesHolder((Application) context.getApplicationContext());
      }
    }

    if (viewType == TYPE_LIBRARY) {
      return new LibraryViewHolder(LayoutInflater.from(context)
          .inflate(R.layout.settings_library, parent, false));
    } else if (viewType == TYPE_LICENSE) {
      return new LicenseViewHolder(LayoutInflater.from(context)
          .inflate(R.layout.settings_license, parent, false), holder);
    } else {
      throw new IllegalStateException("Unknown view type: " + viewType);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolderBase holder, int position) {
    int type = getItemViewType(position);
    if (type == TYPE_LIBRARY) {
      holder.bind(libraries.get(position / 2));
    } else if (type == TYPE_LICENSE) {
      holder.bind(libraries.get((position - 1) / 2));
    } else {
      throw new IllegalStateException("Unknown view type: " + type);
    }
  }

  @Override
  public void onExpand(@NonNull ExpandableLibrary library, boolean expanded) {
    int index = libraries.indexOf(library);

    if (index == -1) {
      throw new IllegalStateException("Could not find library: " + library);
    }

    notifyItemRangeChanged(index * 2, 2);
  }
}
