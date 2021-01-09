package com.plexus.utils;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

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

public abstract class TextViewLinkHandler extends LinkMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    onLinkClick(link[0]);
//                    URLSpan[] linkUrlSpan = buffer.getSpans(off, off, URLSpan.class);
//                    if (linkUrlSpan[0].getURL().toString().startsWith(urlStartWith)) {
                    //onLinkClick(linkUrlSpan[0].getURL());
//                    }else {
//                        link[0].onClick(widget);
//                    }
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    abstract public void onLinkClick(ClickableSpan clickableSpan);


    //abstract public void onLinkClick(String url);


//    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
//        if (event.getAction() != MotionEvent.ACTION_UP)
//            return super.onTouchEvent(widget, buffer, event);
//
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        x -= widget.getTotalPaddingLeft();
//        y -= widget.getTotalPaddingTop();
//
//        x += widget.getScrollX();
//        y += widget.getScrollY();
//
//        Layout layout = widget.getLayout();
//        int line = layout.getLineForVertical(y);
//        int off = layout.getOffsetForHorizontal(line, x);
//
//        URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
//        if (link.length != 0) {
//            onLinkClick(link[0].getURL());
//        }
//        return true;
//    }

}
