/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package cz.mpelant.deskclock;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Displays text with no padding at the top.
 */
public class ZeroTopPaddingTextView extends TextView {
    private static final float NORMAL_FONT_PADDING_RATIO = 0.328f;
    // the bold fontface has less empty space on the top
    private static final float BOLD_FONT_PADDING_RATIO = 0.208f;

    private static final float NORMAL_FONT_BOTTOM_PADDING_RATIO = 0.25f;
    // the bold fontface has less empty space on the top
    private static final float BOLD_FONT_BOTTOM_PADDING_RATIO = 0.208f;

    private static final Typeface SAN_SERIF_BOLD = Typeface.create("san-serif", Typeface.BOLD);
    private static final Typeface SAN_SERIF__CONDENSED_BOLD =
            Typeface.create("sans-serif-condensed", Typeface.BOLD);

    private static final Typeface SAN_SERIF_THIN = Typeface.create("san-serif", Typeface.NORMAL);
    private static final Typeface SAN_SERIF__CONDENSED_THIN =
            Typeface.create("sans-serif-thin", Typeface.NORMAL);

    private int mPaddingRight = 0;

    public ZeroTopPaddingTextView(Context context) {
        this(context, null);
    }

    public ZeroTopPaddingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZeroTopPaddingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode())
            return;
        if (Build.VERSION.SDK_INT >= 16)
            setIncludeFontPadding(false);
        updatePadding();//TODO: comment if buggy
    }

    public void updatePadding() {
        float paddingRatio = NORMAL_FONT_PADDING_RATIO;
        float bottomPaddingRatio = NORMAL_FONT_BOTTOM_PADDING_RATIO;
        if (getTypeface() != null && (getTypeface().equals(SAN_SERIF_BOLD) ||
                getTypeface().equals(SAN_SERIF__CONDENSED_BOLD)) || (getId()==R.id.timeDisplayHoursThin && Build.VERSION.SDK_INT>=18)) {
            paddingRatio = BOLD_FONT_PADDING_RATIO;
            bottomPaddingRatio = BOLD_FONT_BOTTOM_PADDING_RATIO;
        }
        // no need to scale by display density because getTextSize() already returns the font
        // height in px
        setPadding(0, (int) (-paddingRatio * getTextSize()), mPaddingRight,
                (int) (-bottomPaddingRatio * getTextSize()));
    }

    public void setPaddingRight(int padding) {
        mPaddingRight = padding;
        updatePadding();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updatePadding();
    }

    @Override
    protected void onAttachedToWindow() {
        updatePadding();
        super.onAttachedToWindow();
    }
}
