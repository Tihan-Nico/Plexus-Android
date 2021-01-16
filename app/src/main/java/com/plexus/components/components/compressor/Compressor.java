package com.plexus.components.components.compressor;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class Compressor {

  // max width and height values of the compressed image is taken as 612x816
  private float maxWidth = 612.0f;
  private float maxHeight = 816.0f;
  private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.WEBP;
  private int quality = 80;
  private String destinationDirectoryPath;

  public Compressor(Context context) {
    destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";
  }

  public Compressor setMaxWidth(float maxWidth) {
    this.maxWidth = maxWidth;
    return this;
  }

  public Compressor setMaxHeight(float maxHeight) {
    this.maxHeight = maxHeight;
    return this;
  }

  public Compressor setCompressFormat(Bitmap.CompressFormat compressFormat) {
    this.compressFormat = compressFormat;
    return this;
  }

  public Compressor setQuality(int quality) {
    this.quality = quality;
    return this;
  }

  public Compressor setDestinationDirectoryPath(String destinationDirectoryPath) {
    this.destinationDirectoryPath = destinationDirectoryPath;
    return this;
  }

  public File compressToFile(File imageFile) throws IOException {
    return compressToFile(imageFile, imageFile.getName());
  }

  public File compressToFile(File imageFile, String compressedFileName) throws IOException {
    return ImageUtil.compressImage(
        imageFile,
        maxWidth,
        maxHeight,
        compressFormat,
        quality,
        destinationDirectoryPath + File.separator + compressedFileName);
  }

  public Bitmap compressToBitmap(File imageFile) throws IOException {
    return ImageUtil.decodeSampledBitmapFromFile(imageFile, maxWidth, maxHeight);
  }

  public Flowable<File> compressToFileAsFlowable(final File imageFile) {
    return compressToFileAsFlowable(imageFile, imageFile.getName());
  }

  public Flowable<File> compressToFileAsFlowable(
      final File imageFile, final String compressedFileName) {
    return Flowable.defer(
        (Callable<Flowable<File>>)
            () -> {
              try {
                return Flowable.just(compressToFile(imageFile, compressedFileName));
              } catch (IOException e) {
                return Flowable.error(e);
              }
            });
  }

  public Flowable<Bitmap> compressToBitmapAsFlowable(final File imageFile) {
    return Flowable.defer(
        (Callable<Flowable<Bitmap>>)
            () -> {
              try {
                return Flowable.just(compressToBitmap(imageFile));
              } catch (IOException e) {
                return Flowable.error(e);
              }
            });
  }
}
