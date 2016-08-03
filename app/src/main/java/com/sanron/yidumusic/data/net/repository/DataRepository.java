package com.sanron.yidumusic.data.net.repository;

import com.sanron.yidumusic.data.net.bean.response.BillCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.GedanListData;
import com.sanron.yidumusic.data.net.bean.response.HomeData;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanData;
import com.sanron.yidumusic.data.net.bean.response.SongInfoData;
import com.sanron.yidumusic.rx.TransformerUtil;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by sanron on 16-7-19.
 */
public class DataRepository implements DataSource {


    private LocalDataSource mLocal;
    private RemoteDataSource mRemote;

    public static final String TAG = "DataRepository";

    public DataRepository(LocalDataSource local, RemoteDataSource remote) {
        mLocal = local;
        mRemote = remote;
    }

    @Override
    public Observable<HomeData> getHomeData(final int foucsNum, final int hotGedanNum,
                                            final int recmdAlbumNum, final int recmdSongNum) {
        Observable<HomeData> localData = mLocal
                .getHomeData(foucsNum, hotGedanNum, recmdAlbumNum, recmdSongNum);
        Observable<HomeData> remoteData = mRemote
                .getHomeData(foucsNum, hotGedanNum, recmdAlbumNum, recmdSongNum)
                .doOnNext(new Action1<HomeData>() {
                    @Override
                    public void call(HomeData homeData) {
                        mLocal.putCache(
                                UrlGenerater.getHomeData(foucsNum, hotGedanNum, recmdAlbumNum, recmdSongNum),
                                homeData,
                                1000 * 30 * 60);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<HomeData>io());
    }

    @Override
    public Observable<BillCategoryData> getBillCategory() {
        Observable<BillCategoryData> localData = mLocal
                .getBillCategory()
                .compose(TransformerUtil.<BillCategoryData>checkError());
        Observable<BillCategoryData> remoteData = mRemote
                .getBillCategory()
                .compose(TransformerUtil.<BillCategoryData>checkError())
                .doOnNext(new Action1<BillCategoryData>() {
                    @Override
                    public void call(BillCategoryData data) {
                        mLocal.putCache(
                                UrlGenerater.getBillCategory(),
                                data,
                                2 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<BillCategoryData>io());
    }

    @Override
    public Observable<GedanCategoryData> getGedanCategory() {
        Observable<GedanCategoryData> localData = mLocal
                .getGedanCategory()
                .compose(TransformerUtil.<GedanCategoryData>checkError());
        Observable<GedanCategoryData> remoteData = mRemote
                .getGedanCategory()
                .compose(TransformerUtil.<GedanCategoryData>checkError())
                .doOnNext(new Action1<GedanCategoryData>() {
                    @Override
                    public void call(GedanCategoryData data) {
                        mLocal.putCache(
                                UrlGenerater.getGedanCategory(),
                                data,
                                24 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanCategoryData>io());
    }

    @Override
    public Observable<GedanListData> getGedanList(final int page, final int pageSize) {
        Observable<GedanListData> localData = mLocal
                .getGedanList(page, pageSize)
                .compose(TransformerUtil.<GedanListData>checkError());
        Observable<GedanListData> remoteData = mRemote
                .getGedanList(page, pageSize)
                .compose(TransformerUtil.<GedanListData>checkError())
                .doOnNext(new Action1<GedanListData>() {
                    @Override
                    public void call(GedanListData data) {
                        mLocal.putCache(
                                UrlGenerater.getGedanList(page, pageSize),
                                data,
                                24 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanListData>io());
    }

    @Override
    public Observable<GedanListData> getGedanListByTag(final String tagName, final int page,
                                                       final int pageSize) {
        Observable<GedanListData> localData = mLocal
                .getGedanListByTag(tagName, page, pageSize)
                .compose(TransformerUtil.<GedanListData>checkError());
        Observable<GedanListData> remoteData = mRemote
                .getGedanListByTag(tagName, page, pageSize)
                .compose(TransformerUtil.<GedanListData>checkError())
                .doOnNext(new Action1<GedanListData>() {
                    @Override
                    public void call(GedanListData data) {
                        mLocal.putCache(
                                UrlGenerater.getGedanListByTag(tagName, page, pageSize),
                                data,
                                12 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanListData>io());
    }

    @Override
    public Observable<OfficialGedanData> getOfficialGedan(final int offset, final int limit) {
        Observable<OfficialGedanData> localData = mLocal
                .getOfficialGedan(offset, limit);
        Observable<OfficialGedanData> remoteData = mRemote
                .getOfficialGedan(offset, limit)
                .doOnNext(new Action1<OfficialGedanData>() {
                    @Override
                    public void call(OfficialGedanData data) {
                        mLocal.putCache(
                                UrlGenerater.getOfficialGedan(offset, limit),
                                data,
                                12 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<OfficialGedanData>io());
    }

    @Override
    public Observable<LrcpicData> getLrcpic(final String word, final String artist) {
        Observable<LrcpicData> localData = mLocal
                .getLrcpic(word, artist)
                .compose(TransformerUtil.<LrcpicData>checkError());
        Observable<LrcpicData> remoteData = mRemote
                .getLrcpic(word, artist)
                .doOnNext(new Action1<LrcpicData>() {
                    @Override
                    public void call(LrcpicData data) {
                        mLocal.putCache(
                                UrlGenerater.getLrcpic(word, artist),
                                data,
                                24 * 60 * 60 * 1000);
                    }
                })
                .compose(TransformerUtil.<LrcpicData>checkError());
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<LrcpicData>io());
    }

    @Override
    public Observable<SongInfoData> getSongInfo(final long songid) {
        Observable<SongInfoData> localData = mLocal
                .getSongInfo(songid)
                .compose(TransformerUtil.<SongInfoData>checkError());
        Observable<SongInfoData> remoteData = mRemote
                .getSongInfo(songid)
                .doOnNext(new Action1<SongInfoData>() {
                    @Override
                    public void call(SongInfoData data) {
                        mLocal.putCache(
                                UrlGenerater.getSongInfo(songid),
                                data,
                                60 * 60 * 1000);
                    }
                })
                .compose(TransformerUtil.<SongInfoData>checkError());
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<SongInfoData>io());
    }

    @Override
    public Observable<GedanInfoData> getGedanInfo(final long listid) {
        Observable<GedanInfoData> localData = mLocal
                .getGedanInfo(listid)
                .compose(TransformerUtil.<GedanInfoData>checkError());
        Observable<GedanInfoData> remoteData = mRemote
                .getGedanInfo(listid)
                .doOnNext(new Action1<GedanInfoData>() {
                    @Override
                    public void call(GedanInfoData data) {
                        mLocal.putCache(
                                UrlGenerater.getGedanInfo(listid),
                                data,
                                60 * 60 * 1000);
                    }
                })
                .compose(TransformerUtil.<GedanInfoData>checkError());
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanInfoData>io());
    }

}
