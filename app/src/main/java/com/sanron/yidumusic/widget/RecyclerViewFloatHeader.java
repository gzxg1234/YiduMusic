package com.sanron.yidumusic.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * 粘性头部
 * Created by sanron on 16-7-16.
 */
public class RecyclerViewFloatHeader extends FrameLayout {

    //触发隐藏最小距离,默认为宽或高的2倍
    private int mHideDistance;

    //是否已绑定recyclerview;
    private boolean mAttached;

    //已滑动距离
    private int mScrollDistance;

    private RecyclerView mRecyclerView;

    private ValueAnimator mAnimator;

    private HeaderMarginDecoration mDecoration = new HeaderMarginDecoration();

    private RecyclerView.OnScrollListener mListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            onScroll(dx, dy);
        }
    };

    public RecyclerViewFloatHeader(Context context) {
        this(context, null);
    }

    public RecyclerViewFloatHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewFloatHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void attachRecyclerView(RecyclerView recyclerView) {
        validate(recyclerView);
        mRecyclerView = recyclerView;
        recyclerView.addItemDecoration(mDecoration);
        recyclerView.addOnScrollListener(mListener);
        mAttached = true;
        updateHideDistance();
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

    private void onScroll(int dx, int dy) {
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            int orientaion = ((LinearLayoutManager) lm).getOrientation();
            boolean reversed = ((LinearLayoutManager) lm).getReverseLayout();
            int distance;
            if (orientaion == LinearLayoutManager.VERTICAL) {
                distance = dy;
            } else {
                distance = dx;
            }
            if (reversed) {
                distance = -distance;
            }
            if (isShown() && distance > 0) {
                if (mScrollDistance < 0) {
                    mScrollDistance = 0;
                }
            } else if (!isShown() && distance < 0) {
                if (mScrollDistance > 0) {
                    mScrollDistance = 0;
                }
            }
            mScrollDistance += distance;
            if (Math.abs(mScrollDistance) >= mHideDistance) {
                if (mScrollDistance > 0) {
                    animateHide(orientaion, reversed);
                } else {
                    animateShow(orientaion);
                }
                mScrollDistance = 0;
            }
        }
    }

    private void animateShow(int orientation) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        setVisibility(View.VISIBLE);
        String property;
        float from;
        if (orientation == LinearLayoutManager.VERTICAL) {
            property = "translationY";
            from = getTranslationY();
        } else {
            property = "translationX";
            from = getTranslationX();
        }
        mAnimator = ObjectAnimator.ofFloat(this, property, from, 0);
        mAnimator.setDuration(300)
                .start();
    }

    private void animateHide(int orientation, boolean reversed) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        String property;
        float to;
        float from;
        if (orientation == LinearLayoutManager.VERTICAL) {
            property = "translationY";
            from = getTranslationY();
            if (reversed) {
                to = getHeight();
            } else {
                to = -getHeight();
            }
        } else {
            property = "translationX";
            from = getTranslationX();
            if (reversed) {
                to = getWidth();
            } else {
                to = -getWidth();
            }
        }
        mAnimator = ObjectAnimator.ofFloat(this, property, from, to);
        mAnimator.setDuration(300)
                .addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(View.INVISIBLE);
                    }
                });
        mAnimator.start();
    }

    private void updateHideDistance() {
        if (mAttached) {
            int orientation = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                mHideDistance = getHeight() * 2;
            } else {
                mHideDistance = getWidth() * 2;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateHideDistance();
    }
}
