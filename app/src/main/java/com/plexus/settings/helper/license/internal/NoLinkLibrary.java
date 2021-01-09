package com.plexus.settings.helper.license.internal;

import androidx.annotation.NonNull;

import com.plexus.settings.helper.license.BaseLibrary;
import com.plexus.settings.helper.license.License;

/**
 * Library without a license url.
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

public final class NoLinkLibrary extends BaseLibrary {
  public NoLinkLibrary(@NonNull String name, @NonNull String author,
                       @NonNull License license) {
    super(name, author, license);
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  @NonNull
  @Override
  public License doLoad() {
    // There's no link to load
    return getLicense();
  }

  @Override
  public boolean hasContent() {
    return true;
  }
}
