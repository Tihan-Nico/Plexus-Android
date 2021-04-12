package com.plexus.glide.cache;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.plexus.core.utils.StreamUtil;
import com.plexus.glide.apng.decode.APNGDecoder;
import com.plexus.glide.common.loader.Loader;
import com.plexus.utils.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EncryptedApngCacheEncoder extends EncryptedCoder implements ResourceEncoder<APNGDecoder> {

  private static final String TAG = Log.tag(EncryptedApngCacheEncoder.class);

  private final byte[] secret;

  public EncryptedApngCacheEncoder(@NonNull byte[] secret) {
    this.secret = secret;
  }

  @Override
  public @NonNull EncodeStrategy getEncodeStrategy(@NonNull Options options) {
    return EncodeStrategy.SOURCE;
  }

  @Override
  public boolean encode(@NonNull Resource<APNGDecoder> data, @NonNull File file, @NonNull Options options) {
    try {
      Loader loader = data.get().getLoader();
      InputStream  input  = loader.obtain().toInputStream();
      OutputStream output = createEncryptedOutputStream(secret, file);

      StreamUtil.copy(input, output);
      return true;
    } catch (IOException e) {
      Log.w(TAG, e);
    }

    return false;
  }
}

