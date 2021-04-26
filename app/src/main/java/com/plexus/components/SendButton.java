package com.plexus.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

import com.plexus.utils.ViewUtil;

public class SendButton extends AppCompatImageButton {

  @SuppressWarnings("unused")
  public SendButton(Context context) {
    super(context);
    ViewUtil.mirrorIfRtl(this, getContext());
  }

  @SuppressWarnings("unused")
  public SendButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    ViewUtil.mirrorIfRtl(this, getContext());
  }

  @SuppressWarnings("unused")
  public SendButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    ViewUtil.mirrorIfRtl(this, getContext());
  }
}
