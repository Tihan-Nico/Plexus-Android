package com.plexus.lintchecks;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class Registry extends IssueRegistry {

  @Override
  public List<Issue> getIssues() {
    return Arrays.asList(PlexusLogDetector.LOG_NOT_PLEXUS,
                         PlexusLogDetector.LOG_NOT_APP,
                         PlexusLogDetector.INLINE_TAG,
                         VersionCodeDetector.VERSION_CODE_USAGE);
  }

  @Override
  public int getApi() {
    return ApiKt.CURRENT_API;
  }
}
