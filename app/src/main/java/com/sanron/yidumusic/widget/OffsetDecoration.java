package com.sanron.yidumusic.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 间隔修饰
 * Created by sanron on 16-7-16.
 */
public class OffsetDecoration extends RecyclerView.ItemDecoration {

    private int mHorizontalSpace;
    private int mVerticalSpace;

    public OffsetDecoration(int horizontalSpace, int verticalSpace) {
        mHorizontalSpace = horizontalSpace;
        mVerticalSpace = verticalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            resolveGrid(outRect, view, parent, state);
        } else if (lm instanceof LinearLayoutManager) {
            resolveLinear(outRect, view, parent, state);
        }
    }

    private void resolveLinear(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager lm = ((LinearLayoutManager) parent.getLayoutManager());
        int top = 0;
        int left = 0;
        int right = 0;
        int bottom = 0;
        int orientation = lm.getOrientation();
        boolean reserved = lm.getReverseLayout();
        if (orientation == LinearLayoutManager.VERTICAL) {
            if (reserved) {
                bottom = mVerticalSpace;
            } else {
                top = mVerticalSpace;
            }
        } else {
            if (reserved) {
                right = mHorizontalSpace;
            } else {
                left = mHorizontalSpace;
            }
        }
        int pos = parent.getChildViewHolder(view).getAdapterPosition();
        if (pos != 0) {
            outRect.set(left, top, right, bottom);
        }
    }

    private void resolveGrid(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager lm = ((GridLayoutManager) parent.getLayoutManager());
        int orientation = lm.getOrientation();
        int spanCount = lm.getSpanCount();
        boolean reserved = lm.getReverseLayout();
        int left = mHorizontalSpace / 2;
        int right = mHorizontalSpace / 2;
        int top = mVerticalSpace / 2;
        int bottom = mVerticalSpace / 2;
        if (orientation == GridLayoutManager.VERTICAL) {
            int pos = parent.getChildViewHolder(view).getAdapterPosition();
            if ((pos + 1) % spanCount == 0) {
                //最后一列
                right = 0;
            }
            if ((pos + 1) % spanCount == 1) {
                //第一列
                left = 0;
            }
            int row = (int) Math.ceil((pos + 1) / (float) spanCount);
            int rowTotal = (int) Math.ceil(parent.getAdapter().getItemCount() / (float) spanCount);
            if (row == rowTotal) {
                //最后一行
                if (reserved) {
                    top = 0;
                } else {
                    bottom = 0;
                }
            }
            if (row == 1) {
                //第一行
                if (reserved) {
                    bottom = 0;
                } else {
                    top = 0;
                }
            }
        } else {
            int pos = parent.getChildViewHolder(view).getAdapterPosition();
            if ((pos + 1) % spanCount == 0) {
                //最后一行
                bottom = 0;
            }
            if ((pos + 1) % spanCount == 1) {
                //第一行
                top = 0;
            }
            int col = (int) Math.ceil((pos + 1) / (float) spanCount);
            int colTotal = (int) Math.ceil(parent.getAdapter().getItemCount() / (float) spanCount);
            if (col == colTotal) {
                //最后一行
                if (reserved) {
                    left = 0;
                } else {
                    right = 0;
                }
            }
            if (col == 1) {
                //第一列
                if (reserved) {
                    right = 0;
                } else {
                    left = 0;
                }
            }
        }
        outRect.set(left, top, right, bottom);
    }
}