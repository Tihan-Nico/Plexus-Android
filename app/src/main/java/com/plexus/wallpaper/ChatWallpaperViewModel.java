package com.plexus.wallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.annimon.stream.Stream;
import com.plexus.core.utils.guava.Optional;
import com.plexus.keyvalue.PlexusStore;
import com.plexus.model.account.User;
import com.plexus.utils.MappingModel;
import com.plexus.utils.livedata.LiveDataUtil;

import java.util.List;
import java.util.Objects;

public class ChatWallpaperViewModel extends ViewModel {

  private final ChatWallpaperRepository                  repository              = new ChatWallpaperRepository();
  private final MutableLiveData<Optional<ChatWallpaper>> wallpaper               = new MutableLiveData<>();
  private final MutableLiveData<List<ChatWallpaper>>     builtins                = new MutableLiveData<>();
  private final MutableLiveData<Boolean>                 dimInDarkTheme          = new MutableLiveData<>();
  private final MutableLiveData<Boolean>                 enableWallpaperControls = new MutableLiveData<>();
  private final User recipientId;

  private ChatWallpaperViewModel(@Nullable User recipientId) {
    this.recipientId = recipientId;

    ChatWallpaper currentWallpaper = repository.getCurrentWallpaper(recipientId);
    dimInDarkTheme.setValue(currentWallpaper == null || currentWallpaper.getDimLevelForDarkTheme() > 0f);
    enableWallpaperControls.setValue(hasClearableWallpaper());
    wallpaper.setValue(Optional.fromNullable(currentWallpaper));
  }

  void refreshWallpaper() {
    repository.getAllWallpaper(builtins::postValue);
  }

  void setDimInDarkTheme(boolean shouldDimInDarkTheme) {
    dimInDarkTheme.setValue(shouldDimInDarkTheme);

    Optional<ChatWallpaper> wallpaper = this.wallpaper.getValue();
    if (wallpaper.isPresent()) {
      repository.setDimInDarkTheme(recipientId, shouldDimInDarkTheme);
    }
  }

  void setWallpaper(@Nullable ChatWallpaper chatWallpaper) {
    wallpaper.setValue(Optional.fromNullable(chatWallpaper));
  }

  void saveWallpaperSelection() {
    Optional<ChatWallpaper> wallpaper      = this.wallpaper.getValue();
    boolean                 dimInDarkTheme = this.dimInDarkTheme.getValue();

    if (!wallpaper.isPresent()) {
      repository.saveWallpaper(recipientId, null);

      if (recipientId != null) {
        ChatWallpaper globalWallpaper = PlexusStore.wallpaper().getWallpaper();

        this.wallpaper.setValue(Optional.fromNullable(globalWallpaper));
        this.dimInDarkTheme.setValue(globalWallpaper == null || globalWallpaper.getDimLevelForDarkTheme() > 0);
      }

      enableWallpaperControls.setValue(false);
      return;
    } else {
      enableWallpaperControls.setValue(true);
    }

    Optional<ChatWallpaper> updated = wallpaper.transform(paper -> ChatWallpaperFactory.updateWithDimming(paper, dimInDarkTheme ? ChatWallpaper.FIXED_DIM_LEVEL_FOR_DARK_THEME : 0f));

    if (updated.isPresent()) {
      repository.saveWallpaper(recipientId, updated.get());
    }
  }

  void resetAllWallpaper() {
    repository.resetAllWallpaper();
  }

  @Nullable User getRecipientId() {
    return recipientId;
  }

  @NonNull LiveData<Optional<ChatWallpaper>> getCurrentWallpaper() {
    return wallpaper;
  }

  @NonNull LiveData<List<MappingModel<?>>> getWallpapers() {
    return LiveDataUtil.combineLatest(builtins, dimInDarkTheme, (wallpapers, dimInDarkMode) ->
      Stream.of(wallpapers)
            .map(paper -> ChatWallpaperFactory.updateWithDimming(paper, dimInDarkMode ? ChatWallpaper.FIXED_DIM_LEVEL_FOR_DARK_THEME : 0f))
            .<MappingModel<?>>map(ChatWallpaperSelectionMappingModel::new).toList()
    );
  }

  @NonNull LiveData<Boolean> getDimInDarkTheme() {
    return dimInDarkTheme;
  }

  @NonNull LiveData<Boolean> getEnableWallpaperControls() {
    return enableWallpaperControls;
  }

  boolean isGlobal() {
    return recipientId == null;
  }

  private boolean hasClearableWallpaper() {
    return (isGlobal() && PlexusStore.wallpaper().hasWallpaperSet()) ||
           (recipientId != null && User.hasOwnWallpaper());
  }

  public static class Factory implements ViewModelProvider.Factory {

    private final User recipientId;

    public Factory(@Nullable User recipientId) {
      this.recipientId = recipientId;
    }

    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      return Objects.requireNonNull(modelClass.cast(new ChatWallpaperViewModel(recipientId)));
    }
  }
}
