package com.plexus.components.components;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.plexus.R;

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

@SuppressLint("AppCompatCustomView")
public class PrideColorTextView extends TextView {

    int c1, c2, c3, c4, c5, c6;
    Shader shader;

    public PrideColorTextView(Context context) {
        super(context);
    }

    public PrideColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createShader(context, attrs, 0);
    }

    public PrideColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createShader(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrideColorTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createShader(context, attrs, defStyleAttr);
    }


    public void createShader(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PrideColorTextView, defStyleAttr, 0);
        try {
            shader = new RadialGradient(0, 5, getTextSize(),
                    new int[]{
                            Color.parseColor("#E70000"),
                            Color.parseColor("#FF8C00"),
                            Color.parseColor("#FFEF00"),
                            Color.parseColor("#00811F"),
                            Color.parseColor("#0044FF"),
                            Color.parseColor("#760098")},
                    null, Shader.TileMode.REPEAT);
            getPaint().setShader(shader);
        } finally {
            a.recycle();
        }
    }

}
