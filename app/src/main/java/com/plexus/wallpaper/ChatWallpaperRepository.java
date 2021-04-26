package com.plexus.wallpaper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.plexus.core.utils.concurrent.PlexusExecutors;
import com.plexus.database.DatabaseFactory;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.keyvalue.PlexusStore;
import com.plexus.model.account.User;
import com.plexus.utils.concurrent.SerialExecutor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

class ChatWallpaperRepository {

  private static final Executor EXECUTOR = new SerialExecutor(PlexusExecutors.BOUNDED);

  @MainThread
  @Nullable ChatWallpaper getCurrentWallpaper(@Nullable User recipientId) {
    if (recipientId != null) {
      return User.getWallpaper();
    } else {
      return PlexusStore.wallpaper().getWallpaper();
    }
  }

  void getAllWallpaper(@NonNull Consumer<List<ChatWallpaper>> consumer) {
    EXECUTOR.execute(() -> {
      List<ChatWallpaper> wallpapers = new ArrayList<>(ChatWallpaper.BUILTINS);

      wallpapers.addAll(WallpaperStorage.getAll(PlexusDependencies.getApplication()));
      consumer.accept(wallpapers);
    });
  }

  void saveWallpaper(@Nullable User recipientId, @Nullable ChatWallpaper chatWallpaper) {
    if (recipientId != null) {
      //noinspection CodeBlock2Expr
      EXECUTOR.execute(() -> {
        /*DatabaseFactory.getRecipientDatabase(PlexusDependencies.getApplication()).setWallpaper(recipientId, chatWallpaper);*/
      });
    } else {
      PlexusStore.wallpaper().setWallpaper(PlexusDependencies.getApplication(), chatWallpaper);
    }
  }

  void resetAllWallpaper() {
    PlexusStore.wallpaper().setWallpaper(PlexusDependencies.getApplication(), null);
    EXECUTOR.execute(() -> {
      /*DatabaseFactory.getRecipientDatabase(PlexusDependencies.getApplication()).resetAllWallpaper();*/
    });
  }

  void setDimInDarkTheme(@Nullable User recipientId, boolean dimInDarkTheme) {
    if (recipientId != null) {
      EXECUTOR.execute(() -> {
        User recipient = null;
        if (User.hasOwnWallpaper()) {
          /*DatabaseFactory.getRecipientDatabase(PlexusDependencies.getApplication()).setDimWallpaperInDarkTheme(recipientId, dimInDarkTheme);*/
        } else if (recipient.hasWallpaper()) {
          /*DatabaseFactory.getRecipientDatabase(PlexusDependencies.getApplication())
                         .setWallpaper(recipientId,
                                       ChatWallpaperFactory.updateWithDimming(recipient.getWallpaper(),
                                                                              dimInDarkTheme ? ChatWallpaper.FIXED_DIM_LEVEL_FOR_DARK_THEME
                                                                                             : 0f));*/
        } else {
          throw new IllegalStateException("Unexpected call to setDimInDarkTheme, no wallpaper has been set on the given recipient or globally.");
        }
      });
    } else {
      PlexusStore.wallpaper().setDimInDarkTheme(dimInDarkTheme);
    }
  }
}
