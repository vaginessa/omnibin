package com.f0x1d.dogbin.utils;

import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.f0x1d.dogbin.App;

public class ListSpacingDecoration extends RecyclerView.ItemDecoration {

    private static final int VERTICAL = OrientationHelper.VERTICAL;

    private int mOrientation = -1;
    private int mSpanCount = -1;
    private int mSpacing;
    private int mHalfSpacing;

    public ListSpacingDecoration(int spacingDp) {
        mSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spacingDp, App.getInstance().getResources().getDisplayMetrics());
        mHalfSpacing = mSpacing / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mOrientation == -1) {
            mOrientation = getOrientation(parent);
        }

        if (mSpanCount == -1) {
            mSpanCount = getTotalSpan(parent);
        }

        int childCount = parent.getLayoutManager().getItemCount();
        int childIndex = parent.getChildAdapterPosition(view);

        int itemSpanSize = getItemSpanSize();
        int spanIndex = getItemSpanIndex(childIndex);

        /* INVALID SPAN */
        if (mSpanCount < 1) return;

        setSpacings(outRect, childCount, childIndex, itemSpanSize, spanIndex);
    }

    protected void setSpacings(Rect outRect, int childCount, int childIndex, int itemSpanSize, int spanIndex) {
        outRect.top = mHalfSpacing;
        outRect.bottom = mHalfSpacing;
        outRect.left = mHalfSpacing;
        outRect.right = mHalfSpacing;

        if (isTopEdge(childIndex, spanIndex)) {
            outRect.top = mSpacing;
        }

        if (isLeftEdge(childIndex, spanIndex)) {
            outRect.left = mSpacing;
        }

        if (isRightEdge(childCount, childIndex, itemSpanSize, spanIndex)) {
            outRect.right = mSpacing;
        }

        if (isBottomEdge(childCount, childIndex, itemSpanSize, spanIndex)) {
            outRect.bottom = mSpacing;
        }
    }

    @SuppressWarnings("all")
    protected int getTotalSpan(RecyclerView parent) {
        RecyclerView.LayoutManager mgr = parent.getLayoutManager();
        return ((StaggeredGridLayoutManager) mgr).getSpanCount();
    }

    private int getItemSpanSize() {
        return 1;
    }

    private int getItemSpanIndex(int childIndex) {
        return childIndex % mSpanCount;
    }

    @SuppressWarnings("all")
    protected int getOrientation(RecyclerView parent) {
        RecyclerView.LayoutManager mgr = parent.getLayoutManager();
        return ((StaggeredGridLayoutManager) mgr).getOrientation();
    }

    private boolean isLeftEdge(int childIndex, int spanIndex) {
        if (mOrientation == VERTICAL) {
            return spanIndex == 0;
        } else {
            return (childIndex == 0) || isFirstItemEdgeValid((childIndex < mSpanCount), childIndex);
        }
    }

    private boolean isRightEdge(int childCount, int childIndex, int itemSpanSize, int spanIndex) {
        if (mOrientation == VERTICAL) {
            return (spanIndex + itemSpanSize) == mSpanCount;
        } else {
            return isLastItemEdgeValid((childIndex >= childCount - mSpanCount), childCount, childIndex, spanIndex);
        }
    }

    private boolean isTopEdge(int childIndex, int spanIndex) {
        if (mOrientation == VERTICAL) {
            return (childIndex == 0) || isFirstItemEdgeValid((childIndex < mSpanCount), childIndex);
        } else {
            return spanIndex == 0;
        }
    }

    private boolean isBottomEdge(int childCount, int childIndex, int itemSpanSize, int spanIndex) {
        if (mOrientation == VERTICAL) {
            return isLastItemEdgeValid((childIndex >= childCount - mSpanCount), childCount, childIndex, spanIndex);
        } else {
            return (spanIndex + itemSpanSize) == mSpanCount;
        }
    }

    private boolean isFirstItemEdgeValid(boolean isOneOfFirstItems, int childIndex) {
        int totalSpanArea = 0;
        if (isOneOfFirstItems) {
            for (int i = childIndex; i >= 0; i--) {
                totalSpanArea = totalSpanArea + getItemSpanSize();
            }
        }

        return isOneOfFirstItems && totalSpanArea <= mSpanCount;
    }

    protected boolean isLastItemEdgeValid(boolean isOneOfLastItems, int childCount, int childIndex, int spanIndex) {
        int totalSpanRemaining = 0;
        if (isOneOfLastItems) {
            for (int i = childIndex; i < childCount; i++) {
                totalSpanRemaining = totalSpanRemaining + getItemSpanSize();
            }
        }

        return isOneOfLastItems && (totalSpanRemaining <= mSpanCount - spanIndex);
    }
}