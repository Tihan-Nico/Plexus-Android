package com.plexus.core.editor.image;

import android.graphics.Paint;
import android.graphics.Path;

public class LinePath {
    private final Paint mDrawPaint;
    private final Path mDrawPath;

    public LinePath(final Path drawPath, final Paint drawPaints) {
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
    }

    public Paint getDrawPaint() {
        return mDrawPaint;
    }

    public Path getDrawPath() {
        return mDrawPath;
    }
}