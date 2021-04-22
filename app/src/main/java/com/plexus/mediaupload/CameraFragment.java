package com.plexus.mediaupload;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.plexus.mediaupload.camerax.CameraXUtil;

import java.io.FileDescriptor;

public interface CameraFragment {

    @SuppressLint("RestrictedApi")
    static Fragment newInstance() {
        if (CameraXUtil.isSupported()) {
            return CameraXFragment.newInstance();
        } else {
            return Camera1Fragment.newInstance();
        }
    }

    @SuppressLint("RestrictedApi")
    static Fragment newInstanceForAvatarCapture() {
        return Camera1Fragment.newInstance();
    }

    interface Controller {
        void onCameraError();
        void onImageCaptured(@NonNull byte[] data, int width, int height);
        void onVideoCaptured(@NonNull FileDescriptor fd);
        void onVideoCaptureError();
        void onGalleryClicked();
        int getDisplayRotation();
        void onCameraCountButtonClicked();
    }
}
