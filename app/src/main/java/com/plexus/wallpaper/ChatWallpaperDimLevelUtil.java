package com.plexus.wallpaper;

import android.view.View;

import androidx.annotation.NonNull;

import com.plexus.utils.ThemeUtil;

public final class ChatWallpaperDimLevelUtil {

  private ChatWallpaperDimLevelUtil() {
  }

  public static void applyDimLevelForNightMode(@NonNull View dimmer, @NonNull ChatWallpaper chatWallpaper) {
    if (ThemeUtil.isDarkTheme(dimmer.getContext())) {
      dimmer.setAlpha(chatWallpaper.getDimLevelForDarkTheme());
      dimmer.setVisibility(View.VISIBLE);
    } else {
      dimmer.setVisibility(View.GONE);
    }
  }
}
