package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sanron on 16-7-13.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private CompositeSubscription mCompositeSubscription;

    protected void addSub(Subscription subscription) {
        if (mCompositeSubscription == null
                || mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription = new CompositeSubscription();
            mCompositeSubscription.add(subscription);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
    }

    protected abstract int getLayout();

    @Override
    protected void onDestroy() {
        if (mCompositeSubscription != null
                && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }
}
