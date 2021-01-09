package com.plexus.settings.helper.license.internal;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.plexus.settings.helper.license.BaseLibrary;
import com.plexus.settings.helper.license.License;
import com.plexus.settings.helper.license.Licenses;
import com.plexus.settings.helper.license.OpenSourceLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

public final class GitHubLibrary extends BaseLibrary implements OpenSourceLibrary {
  private static final String TAG = "GitHubLibrary";

  private static final String URL_BASE_PUBLIC = "https://github.com/";
  private static final String URL_REPO_SPLIT = "/";

  private final List<String> possibleLicenseUrls;

  private GitHubLibrary(@NonNull String name, @NonNull String author, @NonNull License license,
                        @NonNull List<String> possibleLicenseUrls) {
    super(name, author, license);
    this.possibleLicenseUrls = possibleLicenseUrls;
  }

  @NonNull
  @WorkerThread
  private static String loadContents(@NonNull String url) throws IOException {
    BufferedReader in = null;
    try {
      return read(in = new BufferedReader(new InputStreamReader(new URL(url).openStream())));
    } catch (IOException e) {
      Log.d(TAG, "Failed to load license.", e);
      throw e;
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  @Override
  public boolean isLoaded() {
    return !TextUtils.isEmpty(getLicense().getText());
  }

  @NonNull
  @WorkerThread
  @Override
  protected License doLoad() {
    if (possibleLicenseUrls.isEmpty()) return getLicense();

    for (String url : possibleLicenseUrls) {
      try {
        return new License.Builder(getLicense()).setText(loadContents(url)).setUrl(url).build();
      } catch (IOException ignored) {
        // Handled below
      }
    }

    Log.e(TAG, "Developer error: no license file found. "
        + "Searched for the following license files:\n" + possibleLicenseUrls);
    throw new IllegalStateException("Unable to load license");
  }

  @Override
  public boolean hasContent() {
    return true;
  }

  @NonNull
  @Override
  public String getSourceUrl() {
    return URL_BASE_PUBLIC + getAuthor() + URL_REPO_SPLIT + getName();
  }

  public static final class Builder {
    private static final String URL_BASE_RAW = "https://raw.githubusercontent.com/";

    private final String author;
    private final String name;
    private final String licenseName;

    private List<String> possibleLicenseUrls;

    public Builder(@NonNull String shortLink, @NonNull String licenseName) {
      author = parseRepoAuthor(shortLink);
      name = parseRepoName(shortLink);
      this.licenseName = licenseName;
    }

    @NonNull
    private static String parseRepoAuthor(@NonNull String fullRepo) {
      checkValidRepoUrl(fullRepo);
      return fullRepo.substring(0, fullRepo.indexOf(URL_REPO_SPLIT));
    }

    @NonNull
    private static String parseRepoName(@NonNull String fullRepo) {
      checkValidRepoUrl(fullRepo);
      return fullRepo.substring(fullRepo.indexOf(URL_REPO_SPLIT) + 1);
    }

    private static void checkValidRepoUrl(@NonNull String fullRepo) {
      int repoSplitIndex = fullRepo.indexOf(URL_REPO_SPLIT);
      if (repoSplitIndex == -1 || repoSplitIndex != fullRepo.lastIndexOf(URL_REPO_SPLIT)) {
        throw new IllegalArgumentException(
            "The GitHub repository url must be of the form `author/repo`.");
      }
    }

    @NonNull
    public Builder setRawLicenseUrl(@Nullable String url) {
      possibleLicenseUrls =
          TextUtils.isEmpty(url) ? Collections.emptyList() : Collections.singletonList(url);
      return this;
    }

    @NonNull
    public Builder setRelativeLicensePath(@NonNull String path) {
      possibleLicenseUrls = new ArrayList<>();
      String fullBase = URL_BASE_RAW + author + "/" + name + "/";

      if (path.contains(Licenses.FILE_AUTO)) {
        List<String> possibleFiles = Arrays.asList(
            Licenses.FILE_NO_EXTENSION, Licenses.FILE_TXT, Licenses.FILE_MD);
        for (String possibleFile : possibleFiles) {
          possibleLicenseUrls.add(fullBase + path.replace(Licenses.FILE_AUTO, possibleFile));
        }
      } else {
        possibleLicenseUrls.add(fullBase + path);
      }

      return this;
    }

    @NonNull
    public GitHubLibrary build() {
      return new GitHubLibrary(name, author, new License.Builder(licenseName).build(),
          Collections.unmodifiableList(possibleLicenseUrls));
    }
  }
}
