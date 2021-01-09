package com.plexus.settings.helper.license;

import androidx.annotation.NonNull;

/**
 * Library who's source code is available online.
 *
 * @see Library
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

public interface OpenSourceLibrary extends Library {
  /**
   * @return a link to the source code or home page of the library. Example:
   * "https://github.com/yshrsmz/LicenseAdapter"
   */
  @NonNull
  String getSourceUrl();
}
