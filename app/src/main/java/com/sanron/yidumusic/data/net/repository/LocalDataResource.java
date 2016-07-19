package com.sanron.yidumusic.data.net.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanron.yidumusic.data.db.HttpCache;
import com.sanron.yidumusic.data.net.model.response.BillCategoryData;
import com.sanron.yidumusic.data.net.model.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.model.response.GedanListData;
import com.sanron.yidumusic.data.net.model.response.HomeData;
import com.sanron.yidumusic.data.net.model.response.OfficialGedanData;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by sanron on 16-7-19.
 */
public class LocalDataResource implements DataResource {

    private HttpCache mHttpCache;
    private ObjectMapper objectMapper;

    public LocalDataResource(HttpCache httpCache) {
        mHttpCache = httpCache;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    public Observable<HomeData> getHomeData(int foucsNum, int hotGedanNum, int recmdAlbumNum, int recmdSongNum) {
        String url = UrlGenerater.getHomeData(foucsNum, hotGedanNum, recmdAlbumNum, recmdSongNum);
        return Observable.create(new GetCacheOnSubscriber<>(url, HomeData.class));
    }

    @Override
    public Observable<BillCategoryData> getBillCategory() {
        String url = UrlGenerater.getBillCategory();
        return Observable.create(new GetCacheOnSubscriber<>(url, BillCategoryData.class));
    }

    @Override
    public Observable<GedanCategoryData> getGedanCategory() {
        String url = UrlGenerater.getGedanCategory();
        return Observable.create(new GetCacheOnSubscriber<>(url, GedanCategoryData.class));
    }

    @Override
    public Observable<GedanListData> getGedanList(int page, int pageSize) {
        String url = UrlGenerater.getGedanList(page, pageSize);
        return Observable.create(new GetCacheOnSubscriber<>(url, GedanListData.class));
    }

    @Override
    public Observable<GedanListData> getGedanListByTag(String tagName, int page, int pageSize) {
        String url = UrlGenerater.getGedanListByTag(tagName, page, pageSize);
        return Observable.create(new GetCacheOnSubscriber<>(url, GedanListData.class));
    }

    @Override
    public Observable<OfficialGedanData> getOfficialGedan(int offset, int limit) {
        String url = UrlGenerater.getOfficialGedan(offset, limit);
        return Observable.create(new GetCacheOnSubscriber<>(url, OfficialGedanData.class));
    }


    public <T> void saveData(String url, T t, long maxAge) {
        try {
            String json = objectMapper.writeValueAsString(t);
            mHttpCache.put(url, json, maxAge);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private class GetCacheOnSubscriber<T> implements Observable.OnSubscribe<T> {

        private String mUrl;
        private Class<T> mClazz;

        public GetCacheOnSubscriber(String url, Class<T> clazz) {
            mUrl = url;
            mClazz = clazz;
        }

        @Override
        public void call(Subscriber<? super T> subscriber) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onStart();
            }
            try {
                String data = mHttpCache.get(mUrl);
                if (data != null) {
                    T result = objectMapper.readValue(data, mClazz);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(result);
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
            }
        }
    }
}
