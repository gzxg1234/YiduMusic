package com.sanron.yidumusic.data.net.rxhttpclient.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Response;
import rx.Observable;
import rx.functions.Func1;

public class JsonMapper<T> implements Func1<Response, Observable<T>> {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Observable<T> call(Response response) {
        try {
            String text = response.body().string();
            T result = OBJECT_MAPPER.readValue(text, new TypeReference<T>() {
            });
            return Observable.just(result);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }
}