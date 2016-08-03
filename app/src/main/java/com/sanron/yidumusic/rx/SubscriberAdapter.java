package com.sanron.yidumusic.rx;

import rx.Subscriber;

/**
 * Created by sanron on 16-7-22.
 */
public class SubscriberAdapter<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
//        e.printStackTrace();
    }

    @Override
    public void onNext(T t) {

    }
}
