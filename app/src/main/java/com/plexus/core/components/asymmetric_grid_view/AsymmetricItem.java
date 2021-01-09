package com.plexus.core.components.asymmetric_grid_view;

import android.os.Parcelable;

public interface AsymmetricItem extends Parcelable {
  int getColumnSpan();
  int getRowSpan();
}
