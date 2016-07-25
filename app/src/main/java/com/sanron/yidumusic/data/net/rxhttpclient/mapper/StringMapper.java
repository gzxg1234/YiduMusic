package com.sanron.yidumusic.data.net.rxhttpclient.mapper;

import okhttp3.Response;
import rx.Observable;
import rx.functions.Func1;

public class StringMapper implements Func1<Response, Observable<String>> {
    @Override
    public Observable<String> call(Response response) {
        try {
            String text = response.body().string();
            return Observable.just(text);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }
}

