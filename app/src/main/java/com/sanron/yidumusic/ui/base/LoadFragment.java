package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanron.yidumusic.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sanron on 16-8-8.
 */
public abstract class LoadFragment extends BaseFragment {

    View mContent;
    ViewGroup mContentContainer;
    View mFailed;
    View mLoading;

    private Unbinder mUnbinder;
    private int state = STATE_LOADING;

    public static final int STATE_LOADING = 1;

    public static final int STATE_FAILED = 2;

    public static final int STATE_SUCCESS = 3;

    public int getState() {
        return state;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setState(state);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        mContentContainer = ButterKnife.findById(view, R.id.content_container);
        mFailed = ButterKnife.findById(view, R.id.view_failed);
        mLoading = ButterKnife.findById(view, R.id.view_loading);
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

    protected void onRetry() {
    }

    protected void setContentView(int layoutId) {
        if (mContentContainer.getChildCount() > 0) {
            mContentContainer.removeAllViews();
        }
        mContent = getLayoutInflater(null).inflate(layoutId, (ViewGroup) getView(), false);
        mContentContainer.addView(mContent);
    }

    public void setState(int state) {
        this.state = state;
        switch (state) {
            case STATE_FAILED: {
                mFailed.setVisibility(View.VISIBLE);
                mContent.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.INVISIBLE);
            }
            break;
            case STATE_SUCCESS: {
                mFailed.setVisibility(View.INVISIBLE);
                mContent.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.INVISIBLE);
            }
            break;
            case STATE_LOADING: {
                mFailed.setVisibility(View.VISIBLE);
                mContent.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.VISIBLE);
            }
            break;
        }
    }
}
