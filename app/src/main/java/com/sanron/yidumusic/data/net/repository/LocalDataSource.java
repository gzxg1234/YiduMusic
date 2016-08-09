package com.sanron.yidumusic.data.net.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanron.yidumusic.data.db.HttpCache;
import com.sanron.yidumusic.data.net.bean.response.AlbumDetailData;
import com.sanron.yidumusic.data.net.bean.response.AllTagData;
import com.sanron.yidumusic.data.net.bean.response.BillCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.GedanListData;
import com.sanron.yidumusic.data.net.bean.response.HomeData;
import com.sanron.yidumusic.data.net.bean.response.HotTagData;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanListData;
import com.sanron.yidumusic.data.net.bean.response.SingerListData;
import com.sanron.yidumusic.data.net.bean.response.SongInfoData;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by sanron on 16-7-19.
 */
public class LocalDataSource implements DataSource {

    private HttpCache mHttpCache;
    private ObjectMapper objectMapper;

    public LocalDataSource(HttpCache httpCache) {
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
    public Observable<OfficialGedanListData> getOfficialGedan(int offset, int limit) {
        String url = UrlGenerater.getOfficialGedan(offset, limit);
        return Observable.create(new GetCacheOnSubscriber<>(url, OfficialGedanListData.class));
    }

    @Override
    public Observable<LrcpicData> getLrcpic(String word, String artist) {
        String url = UrlGenerater.getLrcpic(word, artist);
        return Observable.create(new GetCacheOnSubscriber<>(url, LrcpicData.class));
    }

    @Override
    public Observable<SongInfoData> getSongInfo(long songid) {
        String url = UrlGenerater.getSongInfo(songid);
        return Observable.create(new GetCacheOnSubscriber<>(url, SongInfoData.class));
    }

    @Override
    public Observable<GedanInfoData> getGedanInfo(long listid) {
        String url = UrlGenerater.getGedanInfo(listid);
        return Observable.create(new GetCacheOnSubscriber<>(url, GedanInfoData.class));
    }

    @Override
    public Observable<AlbumDetailData> getAlbumInfo(long albumId) {
        String url = UrlGenerater.getGedanInfo(albumId);
        return Observable.create(new GetCacheOnSubscriber<>(url, AlbumDetailData.class));
    }

    @Override
    public Observable<HotTagData> getHotTag(int num) {
        String url = UrlGenerater.getHotTag(num);
        return Observable.create(new GetCacheOnSubscriber<>(url, HotTagData.class));
    }

    @Override
    public Observable<AllTagData> getAllTag() {
        String url = UrlGenerater.getAllTag();
        return Observable.create(new GetCacheOnSubscriber<>(url, AllTagData.class));
    }

    @Override
    public Observable<OfficialGedanInfoData> getOfficialGedanInfo(String code) {
        String url = UrlGenerater.getOfficialGedanInfo(code);
        return Observable.create(new GetCacheOnSubscriber<>(url, OfficialGedanInfoData.class));
    }

    @Override
    public Observable<SingerListData> getSingerList(int offset, int limit, int area, int sex, int order, String abc) {
        String url = UrlGenerater.getSingerList(offset, limit, area, sex, order, abc);
        return Observable.create(new GetCacheOnSubscriber<>(url, SingerListData.class));
    }


    public <T> void putCache(String url, T t, long maxAge) {
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
