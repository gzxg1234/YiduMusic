package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sanron.yidumusic.AppManager;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sanron on 16-7-13.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Set<BackPressHandler> mBackPressHandlers;

    public void addBackPressHandler(BackPressHandler backPressHandler) {
        if (mBackPressHandlers == null) {
            mBackPressHandlers = new HashSet<>();
        }
        if (mBackPressHandlers.contains(backPressHandler)) {
            return;
        }
        mBackPressHandlers.add(backPressHandler);
    }

    private CompositeSubscription mCompositeSubscription;

    protected void addSub(Subscription subscription) {
        if (mCompositeSubscription == null
                || mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription = new CompositeSubscription();
            mCompositeSubscription.add(subscription);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressHandlers != null) {
            for (BackPressHandler backPressHandler : mBackPressHandlers) {
                if (backPressHandler.onBackPress()) {
                    return;
                }
            }
        }
        super.onBackPressed();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.instance().addActivity(this);
        setContentView(getLayout());
        ButterKnife.bind(this);
    }

    protected abstract int getLayout();

    @Override
    protected void onDestroy() {
        AppManager.instance().removeActivity(this);
        if (mCompositeSubscription != null
                && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

}
