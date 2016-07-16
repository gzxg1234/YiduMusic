package com.sanron.yidumusic.ui.fragment.music_bank;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;

import butterknife.BindView;

/**
 * Created by sanron on 16-7-16.
 */
public class SingerFragment extends LazyLoadFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected int getLayout() {
        return R.layout.refresh_with_recycler;
    }
}
