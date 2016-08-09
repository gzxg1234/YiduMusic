package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sanron on 16-7-13.
 */
public abstract class BaseFragment extends Fragment {

    private Unbinder mUnbinder;
    private CompositeSubscription mCompositeSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayout();
        if (layoutResId != 0) {
            View view = inflater.inflate(layoutResId, container, false);
            bindView(view);
            initView(view, savedInstanceState);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void bindView(View view){
        mUnbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if(mUnbinder!=null) {
            mUnbinder.unbind();
        }
        if (mCompositeSubscription != null
                && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroyView();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void addSub(Subscription subscription) {
        if (mCompositeSubscription == null
                || mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription = new CompositeSubscription();
            mCompositeSubscription.add(subscription);
        }
    }

    protected abstract int getLayout();

    protected void initView(View view, Bundle savedInstanceState) {
    }
}
