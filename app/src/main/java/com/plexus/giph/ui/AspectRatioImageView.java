package com.plexus.giph.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * AspectRatioImageView maintains an aspect ratio by adjusting the width or height dimension. The
 * aspect ratio (width to height ratio) and adjustment dimension can be configured.
 */
public class AspectRatioImageView extends AppCompatImageView {

    private static final float DEFAULT_ASPECT_RATIO = 1.0f;
    private static final int DEFAULT_ADJUST_DIMENSION = 0;
    // defined by attrs.xml enum
    static final int ADJUST_DIMENSION_HEIGHT = 0;
    static final int ADJUST_DIMENSION_WIDTH = 1;

    private double aspectRatio;         // width to height ratio
    private int dimensionToAdjust;      // ADJUST_DIMENSION_HEIGHT or ADJUST_DIMENSION_WIDTH

    public AspectRatioImageView(Context context) {
        this(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public int getDimensionToAdjust() {
        return dimensionToAdjust;
    }

    /**
     * Sets the aspect ratio that should be respected during measurement.
     *
     * @param aspectRatio desired width to height ratio
     */
    public void setAspectRatio(final double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    /**
     * Resets the size to 0.
     */
    public void resetSize() {
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            return;
        }
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
        layout(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (dimensionToAdjust == ADJUST_DIMENSION_HEIGHT) {
            height = calculateHeight(width, aspectRatio);
        } else {
            width = calculateWidth(height, aspectRatio);
        }
        setMeasuredDimension(width, height);
    }

    /**
     * Returns the height that will satisfy the width to height aspect ratio, keeping the given
     * width fixed.
     */
    int calculateHeight(int width, double ratio) {
        if (ratio == 0) {
            return 0;
        }
        return (int) Math.round(width / ratio);
    }

    /**
     * Returns the width that will satisfy the width to height aspect ratio, keeping the given
     * height fixed.
     */
    int calculateWidth(int height, double ratio) {
        return (int) Math.round(height * ratio);
    }
}