package com.sanron.yidumusic.rx;

import android.content.Context;
import android.widget.Toast;

import rx.Subscriber;

/**
 * Created by sanron on 16-7-22.
 */
public class ToastSubscriber<T> extends Subscriber<T> {

    private Context mContext;

    protected ToastSubscriber(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(T t) {

    }
}
