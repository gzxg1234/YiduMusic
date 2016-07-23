package com.sanron.yidumusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanron on 16-7-23.
 */
public abstract class SectionAdapter<S extends RecyclerView.ViewHolder, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    //真实位置
    List<Integer> mRealPosition;
    //存储每个section的位置
    private SparseArray<Integer> mSectionPositions;
    private static final int TYPE_SECTION = -1;

    @Override
    public int getItemViewType(int position) {
        Integer realPos = mRealPosition.get(position);
        if (realPos == null) {
            return TYPE_SECTION;
        }
        return getRealItemType(realPos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            return onCreateSectionViewHolder(parent);
        }
        return onCreateRealItemViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SECTION) {
            onBindSectionViewHolder((S) holder, mSectionPositions.keyAt(mSectionPositions.indexOfValue(position)));
        } else {
            onBindRealItemViewHoler((VH) holder, mRealPosition.get(position));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        setupSection();
        registerAdapterDataObserver(new SectionObserver());
    }

    private void setupSection() {
        if (mSectionPositions == null) {
            mSectionPositions = new SparseArray<>();
        } else {
            mSectionPositions.clear();
        }

        if (mRealPosition == null) {
            mRealPosition = new ArrayList<>();
        }
        mRealPosition.clear();

        for (int i = 0; i < getRealItemCount(); i++) {
            int section = getSectionForPosition(i);
            if (mSectionPositions.indexOfKey(section) < 0) {
                mSectionPositions.put(section, i + mSectionPositions.size());
                mRealPosition.add(null);
                mRealPosition.add(i);
            } else {
                mRealPosition.add(i);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + (mSectionPositions == null ? 0 : mSectionPositions.size());
    }

    public int getRealItemType(int position) {
        return 0;
    }

    public abstract S onCreateSectionViewHolder(ViewGroup parent);

    public abstract void onBindSectionViewHolder(S holder, int section);

    public abstract VH onCreateRealItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindRealItemViewHoler(VH holder, int position);

    public abstract int getRealItemCount();

    public abstract int getSectionForPosition(int position);

    class SectionObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            setupSection();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            setupSection();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            setupSection();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            setupSection();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            setupSection();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            setupSection();
        }
    }
}
