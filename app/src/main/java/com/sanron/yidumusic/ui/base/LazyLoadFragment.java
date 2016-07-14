package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sanron on 16-7-13.
 */
public abstract class LazyLoadFragment extends BaseFragment {

    private boolean mInitedView;
    private boolean mLoaded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mInitedView = true;
        if (getUserVisibleHint()
                && !mLoaded) {
            onLazyLoad();
            mLoaded = true;
        }
        return view;
    }

    protected abstract void onLazyLoad();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()
                && !mLoaded
                && mInitedView) {
            onLazyLoad();
            mLoaded = true;
        }
    }
}
