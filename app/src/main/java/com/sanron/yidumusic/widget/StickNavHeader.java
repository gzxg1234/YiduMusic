package com.sanron.yidumusic.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * 粘性头部
 * Created by sanron on 16-7-16.
 */
public class StickNavHeader extends FrameLayout {

    //是否已绑定recyclerview;
    private boolean mAttached;

    private RecyclerView mRecyclerView;

    private ValueAnimator mAnimator;

    private int mScrollY;

    private int mMaxScrollY;

    private HeaderMarginDecoration mDecoration = new HeaderMarginDecoration();

    private RecyclerView.OnScrollListener mListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            onScroll(dx, dy);
        }
    };

    public StickNavHeader(Context context) {
        this(context, null);
    }

    public StickNavHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickNavHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void attachRecyclerView(RecyclerView recyclerView) {
        validate(recyclerView);
        mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(mListener);
        mAttached = true;
    }

    public void setMaxScrollY(int t) {
        //停留时剩与高度
        mMaxScrollY = t;
    }

    public void detach() {
        if (mRecyclerView != null) {
            mRecyclerView.removeItemDecoration(mDecoration);
            mRecyclerView.removeOnScrollListener(mListener);
            mAttached = false;
        }
    }

    private void validate(RecyclerView recyclerView) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null || !(lm instanceof LinearLayoutManager)) {
            throw new IllegalStateException("only support LinearLayoutManager and GridLayoutManager");
        }
    }

    private class HeaderMarginDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            RecyclerView.LayoutManager lm = parent.getLayoutManager();
            if (lm instanceof LinearLayoutManager) {
                int orientaion = ((LinearLayoutManager) lm).getOrientation();
                int spanCount = 1;
                if (lm instanceof GridLayoutManager) {
                    spanCount = ((GridLayoutManager) lm).getSpanCount();
                }
                boolean reversed = ((LinearLayoutManager) lm).getReverseLayout();
                int left = 0;
                int top = 0;
                int bottom = 0;
                int right = 0;
                if (orientaion == LinearLayoutManager.VERTICAL) {
                    if (reversed) {
                        bottom = getHeight();
                    } else {
                        top = getHeight();
                    }
                } else {
                    if (reversed) {
                        right = getWidth();
                    } else {
                        left = getWidth();
                    }
                }
                int pos = parent.getChildAdapterPosition(view);
                //如果是第一行添加间距
                if (pos < spanCount) {
                    outRect.set(left, top, right, bottom);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRecyclerView != null) {
            mRecyclerView.removeItemDecoration(mDecoration);
            mRecyclerView.addItemDecoration(mDecoration);
        }
    }

    private void onScroll(int dx, int dy) {
        mScrollY += dy;
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            int orientaion = ((LinearLayoutManager) lm).getOrientation();
            boolean reversed = ((LinearLayoutManager) lm).getReverseLayout();
            if (orientaion == LinearLayoutManager.VERTICAL
                    && !reversed) {
                if (mScrollY < mMaxScrollY) {
                    scroll(-mScrollY);
                } else {
                    scroll(-mMaxScrollY);
                }
            }
        }
    }

    private void scroll(int y) {
        setTranslationY(y);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(y, Math.abs(y) / (float) mMaxScrollY);
        }
    }

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(int scrollPixel, float scrollOffset);
    }
}
