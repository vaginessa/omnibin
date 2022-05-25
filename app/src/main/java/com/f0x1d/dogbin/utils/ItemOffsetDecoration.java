package com.f0x1d.dogbin.utils;

import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.f0x1d.dogbin.App;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public ItemOffsetDecoration(int dp) {
        mItemOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getInstance().getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        boolean leftColumn = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex() == 0;
        int position = parent.getChildAdapterPosition(view);

        int left = mItemOffset;
        int top = mItemOffset / 2;
        int right = mItemOffset;
        int bottom = mItemOffset / 2;

        if (position == 0 || position == 1)
            top = mItemOffset;

        if (leftColumn) {
            outRect.set(left, top, right / 2, bottom);
        } else {
            outRect.set(left / 2, top, right, bottom);
        }
    }
}