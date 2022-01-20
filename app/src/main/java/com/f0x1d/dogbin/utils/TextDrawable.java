package com.f0x1d.dogbin.utils;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class TextDrawable extends Drawable {

    private final Paint mPaint;
    private final CharSequence mText;
    private final int mIntrinsicWidth;
    private final int mIntrinsicHeight;

    private ColorStateList mColorStateList = null;

    public TextDrawable(CharSequence text) {
        mText = text;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Align.CENTER);

        mIntrinsicWidth = (int) (mPaint.measureText(mText, 0, mText.length()) + .5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mColorStateList != null)
            mPaint.setColor(mColorStateList.getColorForState(getState(), Color.BLACK));

        Rect bounds = getBounds();
        canvas.drawText(mText, 0, mText.length(),
                bounds.centerX(), bounds.centerY() - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        mPaint.setColorFilter(filter);
    }

    @Override
    public void setTintList(@Nullable ColorStateList tint) {
        mColorStateList = tint;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        return true;
    }
}