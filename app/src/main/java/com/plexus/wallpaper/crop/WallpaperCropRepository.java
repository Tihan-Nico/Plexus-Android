package com.plexus.wallpaper.crop;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.plexus.core.utils.logging.Log;
import com.plexus.database.DatabaseFactory;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.keyvalue.PlexusStore;
import com.plexus.model.account.User;
import com.plexus.wallpaper.ChatWallpaper;
import com.plexus.wallpaper.WallpaperStorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

final class WallpaperCropRepository {

  private static final String TAG = Log.tag(WallpaperCropRepository.class);

  @Nullable private final User recipientId;
  private final           Context     context;

  public WallpaperCropRepository(@Nullable User recipientId) {
    this.context     = PlexusDependencies.getApplication();
    this.recipientId = recipientId;
  }

  @WorkerThread
  @NonNull
  ChatWallpaper setWallPaper(byte[] bytes) throws IOException {
    try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
      ChatWallpaper wallpaper = WallpaperStorage.save(context, inputStream, "webp");

      if (recipientId != null) {
        Log.i(TAG, "Setting image wallpaper for " + recipientId);
        /*DatabaseFactory.getRecipientDatabase(context).setWallpaper(recipientId, wallpaper);*/
      } else {
        Log.i(TAG, "Setting image wallpaper for default");
        PlexusStore.wallpaper().setWallpaper(context, wallpaper);
      }

      return wallpaper;
    }
  }
}
