package com.plexus.wallpaper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.annimon.stream.Stream;
import com.plexus.BaseActivity;
import com.plexus.R;
import com.plexus.TracerActivity;
import com.plexus.model.account.User;
import com.plexus.utils.ActivityTransitionUtil;
import com.plexus.utils.DynamicTheme;
import com.plexus.utils.FullscreenHelper;
import com.plexus.utils.MappingModel;
import com.plexus.utils.WindowUtil;

import java.util.Collections;

public class ChatWallpaperPreviewActivity extends TracerActivity {

  public  static final String EXTRA_CHAT_WALLPAPER   = "extra.chat.wallpaper";
  private static final String EXTRA_DIM_IN_DARK_MODE = "extra.dim.in.dark.mode";
  private static final String EXTRA_RECIPIENT_ID     = "extra.recipient.id";

  private final DynamicTheme dynamicTheme = new DynamicTheme();

  public static @NonNull Intent create(@NonNull Context context, @NonNull ChatWallpaper selection, @NonNull User recipientId, boolean dimInDarkMode) {
    Intent intent = new Intent(context, ChatWallpaperPreviewActivity.class);

    intent.putExtra(EXTRA_CHAT_WALLPAPER, selection);
    intent.putExtra(EXTRA_DIM_IN_DARK_MODE, dimInDarkMode);
    intent.putExtra(EXTRA_RECIPIENT_ID, recipientId);

    return intent;
  }

  @SuppressLint("ResourceAsColor")
  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    dynamicTheme.onCreate(this);

    setContentView(R.layout.chat_wallpaper_preview_activity);

    ViewPager2                       viewPager  = findViewById(R.id.preview_pager);
    ChatWallpaperPreviewAdapter      adapter    = new ChatWallpaperPreviewAdapter();
    View                             submit     = findViewById(R.id.preview_set_wallpaper);
    ChatWallpaperRepository          repository = new ChatWallpaperRepository();
    ChatWallpaper                    selected   = getIntent().getParcelableExtra(EXTRA_CHAT_WALLPAPER);
    boolean                          dim        = getIntent().getBooleanExtra(EXTRA_DIM_IN_DARK_MODE, false);
    Toolbar                          toolbar    = findViewById(R.id.toolbar);
    View                             bubble1    = findViewById(R.id.preview_bubble_1);
    TextView                         bubble2    = findViewById(R.id.preview_bubble_2_text);

    toolbar.setNavigationOnClickListener(unused -> {
      finish();
      ActivityTransitionUtil.setSlideOutTransition(this);
    });

    viewPager.setAdapter(adapter);

    adapter.submitList(Collections.singletonList(new ChatWallpaperSelectionMappingModel(selected)));
    repository.getAllWallpaper(wallpapers -> adapter.submitList(Stream.of(wallpapers)
                                                                      .map(wallpaper -> ChatWallpaperFactory.updateWithDimming(wallpaper, dim ? ChatWallpaper.FIXED_DIM_LEVEL_FOR_DARK_THEME : 0f))
                                                                      .<MappingModel<?>>map(ChatWallpaperSelectionMappingModel::new)
                                                                      .toList()));

    submit.setOnClickListener(unused -> {
      ChatWallpaperSelectionMappingModel model = (ChatWallpaperSelectionMappingModel) adapter.getCurrentList().get(viewPager.getCurrentItem());

      setResult(RESULT_OK, new Intent().putExtra(EXTRA_CHAT_WALLPAPER, model.getWallpaper()));
      finish();
    });

    User recipientId = getIntent().getParcelableExtra(EXTRA_RECIPIENT_ID);
    if (recipientId != null) {
      bubble1.getBackground().setColorFilter(R.color.plexus, PorterDuff.Mode.SRC_IN);
      bubble2.setText(getString(R.string.ChatWallpaperPreviewActivity__set_wallpaper_for_s, recipientId.getName()));
    }

    new FullscreenHelper(this).showSystemUI();
    WindowUtil.setLightStatusBarFromTheme(this);
    WindowUtil.setLightNavigationBarFromTheme(this);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    ActivityTransitionUtil.setSlideOutTransition(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);
  }
}
