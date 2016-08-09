package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.util.StatusBarUtil;

import butterknife.ButterKnife;

/**
 * Created by sanron on 16-8-8.
 */
public abstract class ToolbarLoadFragment extends LoadFragment {

    Toolbar mToolbar;

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_with_toolbar, container, false);
        mContentContainer = ButterKnife.findById(view, R.id.content_container);
        mFailed = ButterKnife.findById(view, R.id.view_failed);
        mLoading = ButterKnife.findById(view, R.id.view_loading);
        mToolbar = ButterKnife.findById(view, R.id.tool_bar);
        View wrap = ButterKnife.findById(view, R.id.toolbar_wrap);
        StatusBarUtil.applyInsertTop(getActivity(), wrap);
        mFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_LOADING);
                onRetry();
            }
        });
        setContentView(getLayout());
        bindView(view);
        initView(view, savedInstanceState);
        return view;
    }
}
