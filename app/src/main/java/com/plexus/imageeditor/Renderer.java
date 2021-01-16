package com.plexus.imageeditor;

import android.os.Parcelable;

import androidx.annotation.NonNull;

public interface Renderer extends Parcelable {

    /**
     * Draw self to the context.
     *
     * @param rendererContext The context to draw to.
     */
    void render(@NonNull RendererContext rendererContext);

    /**
     * @param x Local coordinate X
     * @param y Local coordinate Y
     * @return true iff hit.
     */
    boolean hitTest(float x, float y);
}
