package com.plexus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.model.UnitModelLoader;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.module.AppGlideModule;
import com.plexus.blurhash.BlurHash;
import com.plexus.blurhash.BlurHashResourceDecoder;
import com.plexus.crypto.AttachmentSecret;
import com.plexus.glide.apng.decode.APNGDecoder;
import com.plexus.glide.cache.ApngBufferCacheDecoder;
import com.plexus.glide.cache.ApngFrameDrawableTranscoder;
import com.plexus.glide.cache.ApngStreamCacheDecoder;
import com.plexus.glide.cache.EncryptedApngCacheEncoder;
import com.plexus.glide.cache.EncryptedBitmapResourceEncoder;
import com.plexus.glide.cache.EncryptedCacheDecoder;
import com.plexus.glide.cache.EncryptedCacheEncoder;
import com.plexus.providers.AttachmentSecretProvider;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

@GlideModule
public class PlexusGlideModule extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setLogLevel(Log.ERROR);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        AttachmentSecret attachmentSecret = AttachmentSecretProvider.getInstance(context).getOrCreateAttachmentSecret();
        byte[]           secret           = attachmentSecret.getModernKey();

        registry.prepend(File.class, File.class, UnitModelLoader.Factory.getInstance());

        registry.prepend(InputStream.class, new EncryptedCacheEncoder(secret, glide.getArrayPool()));

        registry.prepend(Bitmap.class, new EncryptedBitmapResourceEncoder(secret));
        registry.prepend(File.class, Bitmap.class, new EncryptedCacheDecoder<>(secret, new StreamBitmapDecoder(new Downsampler(registry.getImageHeaderParsers(), context.getResources().getDisplayMetrics(), glide.getBitmapPool(), glide.getArrayPool()), glide.getArrayPool())));

        ApngBufferCacheDecoder apngBufferCacheDecoder = new ApngBufferCacheDecoder();
        ApngStreamCacheDecoder apngStreamCacheDecoder = new ApngStreamCacheDecoder(apngBufferCacheDecoder);

        registry.prepend(InputStream.class, APNGDecoder.class, apngStreamCacheDecoder);
        registry.prepend(ByteBuffer.class, APNGDecoder.class, apngBufferCacheDecoder);
        registry.prepend(APNGDecoder.class, new EncryptedApngCacheEncoder(secret));
        registry.prepend(File.class, APNGDecoder.class, new EncryptedCacheDecoder<>(secret, apngStreamCacheDecoder));
        registry.register(APNGDecoder.class, Drawable.class, new ApngFrameDrawableTranscoder());

        registry.prepend(BlurHash.class, Bitmap.class, new BlurHashResourceDecoder());
    }

    public static class NoopDiskCacheFactory implements DiskCache.Factory {
        @Override
        public DiskCache build() {
            return new DiskCacheAdapter();
        }
    }
}
