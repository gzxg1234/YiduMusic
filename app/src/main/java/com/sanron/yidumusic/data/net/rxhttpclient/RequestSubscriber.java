package com.sanron.yidumusic.data.net.rxhttpclient;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RequestSubscriber implements Observable.OnSubscribe<Response> {

    private Call mCall;

    public RequestSubscriber(Call call) {
        mCall = call;
    }

    @Override
    public void call(Subscriber<? super Response> subscriber) {

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                mCall.cancel();
            }
        });

        if (!subscriber.isUnsubscribed()) {
            subscriber.onStart();
        }
        try {
            Response response = mCall.execute();
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(response);
            }
        } catch (Exception e) {
            if (subscriber.isUnsubscribed()) {
                subscriber.onError(e);
            }
        }
        if (!subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
        }
    }
}