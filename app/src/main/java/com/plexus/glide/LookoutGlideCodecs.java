package com.plexus.glide;

import androidx.annotation.NonNull;

public final class LookoutGlideCodecs {

  private static Log.Provider logProvider = Log.Provider.EMPTY;

  private LookoutGlideCodecs() {}

  public static void setLogProvider(@NonNull Log.Provider provider) {
    logProvider = provider;
  }

  public static @NonNull Log.Provider getLogProvider() {
    return logProvider;
  }
}
