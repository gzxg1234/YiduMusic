package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sanron.yidumusic.AppManager;
import com.sanron.yidumusic.playback.PlayUtil;

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

    private CompositeSubscription mCompositeSubscription;

    protected abstract int getLayout();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.instance().addActivity(this);
        setContentView(getLayout());
        ButterKnife.bind(this);
        PlayUtil.bindService(this, null);
    }

    @Override
    protected void onDestroy() {
        PlayUtil.unbindService(this);
        AppManager.instance().removeActivity(this);
        if (mCompositeSubscription != null
                && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

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

    public void addBackPressHandler(BackPressHandler backPressHandler) {
        if (mBackPressHandlers == null) {
            mBackPressHandlers = new HashSet<>();
        }
        if (mBackPressHandlers.contains(backPressHandler)) {
            return;
        }
        mBackPressHandlers.add(backPressHandler);
    }
}
