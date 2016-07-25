package com.sanron.yidumusic.data.net.rxhttpclient;

import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class RequestSubscriber implements Observable.OnSubscribe<Response> {

    private Call mCall;

    public RequestSubscriber(Call call) {
        mCall = call;
    }

    @Override
    public void call(Subscriber<? super Response> subscriber) {

        subscriber.add(new RSubscrption() {
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
            if (response.isSuccessful()) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(response);
                }
            } else {
                throw new HttpException(response.code());
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

    static abstract class RSubscrption implements Subscription {
        private AtomicBoolean mAtomicBoolean = new AtomicBoolean();

        @Override
        public void unsubscribe() {
            if (mAtomicBoolean.compareAndSet(false, true)) {
                onUnsubscribe();
            }
        }

        protected abstract void onUnsubscribe();

        @Override
        public boolean isUnsubscribed() {
            return mAtomicBoolean.get();
        }
    }
}