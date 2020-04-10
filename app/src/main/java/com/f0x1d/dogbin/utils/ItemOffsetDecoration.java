package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public ItemOffsetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public ItemOffsetDecoration(@NonNull Context context, int dp) {
        this((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        int left = mItemOffset;
        int top = mItemOffset / 2;
        int right = mItemOffset;
        int bottom = mItemOffset / 2;

        if (position == 0 || position == 1)
            top = mItemOffset;

        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            // It seems that it is a left item
            outRect.set(left, top, right / 2, bottom);
        } else {
            // It seems that it is a right item
            outRect.set(left / 2, top, right, bottom);
        }
    }
}