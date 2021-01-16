package com.plexus.settings.helper.license.internal;

import androidx.annotation.NonNull;

import com.plexus.settings.helper.license.Library;

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

public final class ExpandableLibrary {
    private final Library library;
    private final ExpandListener listener;
    private boolean expanded;

    public ExpandableLibrary(@NonNull Library library, @NonNull ExpandListener listener) {
        this.library = library;
        this.listener = listener;
    }

    @NonNull
    public Library getLibrary() {
        return library;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        if (this.expanded == expanded) return;

        this.expanded = expanded;
        listener.onExpand(this, expanded);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpandableLibrary that = (ExpandableLibrary) o;

        return expanded == that.expanded && library.equals(that.library);
    }

    @Override
    public int hashCode() {
        int result = library.hashCode();
        result = 31 * result + (expanded ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExpandableLibrary{" +
                "library=" + library +
                ", expanded=" + expanded +
                '}';
    }

    public interface ExpandListener {
        void onExpand(@NonNull ExpandableLibrary library, boolean expanded);
    }
}
