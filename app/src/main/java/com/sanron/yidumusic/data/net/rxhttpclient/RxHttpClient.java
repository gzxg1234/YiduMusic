package com.sanron.yidumusic.data.net.rxhttpclient;

import java.net.URL;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;

/**
 * Created by sanron on 16-7-25.
 */
public class RxHttpClient {

    private static volatile RxHttpClient INSTANCE;

    public static RxHttpClient get() {
        if (INSTANCE == null) {
            synchronized (RxHttpClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxHttpClient();
                }
            }
        }
        return INSTANCE;
    }

    private OkHttpClient mClient;

    private RxHttpClient() {
        mClient = new OkHttpClient.Builder()
                .build();
    }

    public RxRequest newRequest() {
        return new RxRequest();
    }

    public class RxRequest {
        private Request.Builder mBuilder;

        public RxRequest() {
            mBuilder = new Request.Builder();
        }

        public RxRequest url(HttpUrl url) {
            mBuilder.url(url);
            return this;
        }

        public RxRequest url(String url) {
            mBuilder.url(url);
            return this;
        }

        public RxRequest url(URL url) {
            mBuilder.url(url);
            return this;
        }

        public RxRequest header(String name, String value) {
            mBuilder.header(name, value);
            return this;
        }

        public RxRequest addHeader(String name, String value) {
            mBuilder.addHeader(name, value);
            return this;
        }

        public RxRequest removeHeader(String name) {
            mBuilder.removeHeader(name);
            return this;
        }

        public RxRequest headers(Headers headers) {
            mBuilder.headers(headers);
            return this;
        }

        public RxRequest cacheControl(CacheControl cacheControl) {
            mBuilder.cacheControl(cacheControl);
            return this;
        }

        public RxRequest get() {
            mBuilder.get();
            return this;
        }

        public RxRequest head() {
            mBuilder.head();
            return this;
        }

        public RxRequest post(RequestBody body) {
            mBuilder.post(body);
            return this;
        }

        public RxRequest delete(RequestBody body) {
            mBuilder.delete(body);
            return this;
        }

        public RxRequest delete() {
            mBuilder.delete();
            return this;
        }

        public RxRequest put(RequestBody body) {
            mBuilder.put(body);
            return this;
        }

        public RxRequest patch(RequestBody body) {
            mBuilder.patch(body);
            return this;
        }

        public RxRequest method(String method, RequestBody body) {
            mBuilder.method(method, body);
            return this;
        }

        public RxRequest tag(Object tag) {
            mBuilder.tag(tag);
            return this;
        }

        public Observable<Response> execute() {
            return Observable.create(new RequestSubscriber(mClient.newCall(mBuilder.build())));
        }
    }
}
