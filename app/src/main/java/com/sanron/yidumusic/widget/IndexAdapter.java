package com.sanron.yidumusic.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by sanron on 16-7-19.
 */
public abstract class IndexAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private SparseArray<Integer> mIndexPosMap = new SparseArray<>();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("only support LinearLayoutManager");
        }
    }

    public IndexAdapter() {
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mIndexPosMap.clear();
            }
        });
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    protected abstract int getSectionForPosition(int position);

    @Override
    public void onBindViewHolder(T holder, int position) {
        int section = getSectionForPosition(position);
        if (mIndexPosMap.indexOfKey(section) < 0) {
            mIndexPosMap.put(section, position);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
