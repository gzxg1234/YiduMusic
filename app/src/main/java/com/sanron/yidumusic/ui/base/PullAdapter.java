package com.sanron.yidumusic.ui.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

public abstract class PullAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private boolean mIsLoading = false;
    private boolean mHasMore = true;
    private boolean mLoadEnable = true;
    private boolean mLastItemVisiable = true;
    private OnLoadMoreListener mOnLoadMoreListener;
    private static final int TYPE_FOOTER = -1;

    public abstract VH onCreateItemView(ViewGroup parent, int viewType);

    public abstract void onBindItemView(VH holder, int position);

    public abstract int getCount();

    public abstract RecyclerView.ViewHolder onCreateFooterView(ViewGroup parent);

    public abstract void onBindFooterView(RecyclerView.ViewHolder viewHolder, boolean hasMore);

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return onCreateFooterView(parent);
        } else {
            return onCreateItemView(parent, viewType);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            ((GridLayoutManager) lm).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooter(position)) {
                        return ((GridLayoutManager) lm).getSpanCount();
                    }
                    return 1;
                }
            });
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                int lastVisiablePosition = RecyclerView.NO_POSITION;
                if (lm instanceof StaggeredGridLayoutManager) {
                    int[] positions = ((StaggeredGridLayoutManager) lm).findLastVisibleItemPositions(null);
                    if (positions != null) {
                        lastVisiablePosition = positions[0];
                        for (int v : positions) {
                            if (v > lastVisiablePosition) {
                                lastVisiablePosition = v;
                            }
                        }
                    }
                } else if (lm instanceof GridLayoutManager) {
                    lastVisiablePosition = ((GridLayoutManager) lm).findLastVisibleItemPosition();
                } else if (lm instanceof LinearLayoutManager) {
                    lastVisiablePosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
                } else {
                    throw new IllegalStateException("not support " + lm.getClass().getName());
                }
                if (lastVisiablePosition != RecyclerView.NO_POSITION
                        && lastVisiablePosition >= getCount() - 1) {
                    if (mLoadEnable
                            && !mIsLoading
                            && mHasMore
                            && mOnLoadMoreListener != null
                            && !mLastItemVisiable) {
                        mLastItemVisiable = true;
                        mIsLoading = true;
                        mOnLoadMoreListener.onLoad();
                    }
                } else {
                    //当recyclerview滑动到最后时,此时如果没有网络,且只在最后一个view的高度范围内滑动,会一直触发onload
                    //设置此标识是否从最后一个view滑动到上方
                    mLastItemVisiable = false;
                }
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams
                && isFooter(holder.getAdapterPosition())) {
            ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
        }
    }

    private boolean isFooter(int position) {
        return getItemViewType(position) == TYPE_FOOTER;
    }

    public int getRealItemViewType(int position) {
        return 0;
    }

    public void onLoadComplete() {
        mIsLoading = false;
    }

    @Override
    public final int getItemViewType(int position) {
        if (mLoadEnable
                && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return getRealItemViewType(position);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_FOOTER) {
            onBindFooterView(holder, mHasMore);
        } else {
            onBindItemView((VH) holder, position);
        }
    }

    @Override
    public final int getItemCount() {
        int itemCount = getCount();
        if (itemCount > 0
                && mLoadEnable) {
            return 1 + itemCount;
        }
        return itemCount;
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }

    public boolean isLoadEnable() {
        return mLoadEnable;
    }

    public boolean isHasMore() {
        return mHasMore;
    }

    public void setHasMore(boolean hasMore) {
        if (mHasMore != hasMore) {
            mHasMore = hasMore;
            if (mLoadEnable) {
                notifyItemChanged(getCount());
            }
        }
    }

    public void setError(boolean error){

    }

    public void setLoadEnable(boolean loadEnable) {
        if (loadEnable != mLoadEnable) {
            mLoadEnable = loadEnable;
            if (mLoadEnable) {
                notifyItemInserted(getCount());
            } else {
                notifyItemRemoved(getCount());
            }
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }
}