package com.sanron.yidumusic.data.net.repository;

import com.sanron.yidumusic.data.net.model.response.BillCategoryData;
import com.sanron.yidumusic.data.net.model.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.model.response.GedanListData;
import com.sanron.yidumusic.data.net.model.response.HomeData;
import com.sanron.yidumusic.data.net.model.response.OfficialGedanData;
import com.sanron.yidumusic.rx.TransformerUtil;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sanron on 16-7-19.
 */
public class DataRepository implements DataResource {

    private static volatile DataRepository sInstance;

    private LocalDataResource mLocal;
    private RemoteDataResource mRemote;

    public static final String TAG = "DataRepository";

    public DataRepository(LocalDataResource local, RemoteDataResource remote) {
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
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<HomeData>() {
                    @Override
                    public void call(HomeData homeData) {
                        mLocal.saveData(
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
                .getBillCategory();
        Observable<BillCategoryData> remoteData = mRemote
                .getBillCategory()
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<BillCategoryData>() {
                    @Override
                    public void call(BillCategoryData data) {
                        mLocal.saveData(
                                UrlGenerater.getBillCategory(),
                                data,
                                2 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<BillCategoryData>net());
    }

    @Override
    public Observable<GedanCategoryData> getGedanCategory() {
        Observable<GedanCategoryData> localData = mLocal
                .getGedanCategory();
        Observable<GedanCategoryData> remoteData = mRemote
                .getGedanCategory()
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<GedanCategoryData>() {
                    @Override
                    public void call(GedanCategoryData data) {
                        mLocal.saveData(
                                UrlGenerater.getGedanCategory(),
                                data,
                                24 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanCategoryData>net());
    }

    @Override
    public Observable<GedanListData> getGedanList(final int page, final int pageSize) {
        Observable<GedanListData> localData = mLocal
                .getGedanList(page, pageSize);
        Observable<GedanListData> remoteData = mRemote
                .getGedanList(page, pageSize)
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<GedanListData>() {
                    @Override
                    public void call(GedanListData data) {
                        mLocal.saveData(
                                UrlGenerater.getGedanList(page, pageSize),
                                data,
                                24 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanListData>net());
    }

    @Override
    public Observable<GedanListData> getGedanListByTag(final String tagName, final int page,
                                                       final int pageSize) {
        Observable<GedanListData> localData = mLocal
                .getGedanListByTag(tagName, page, pageSize);
        Observable<GedanListData> remoteData = mRemote
                .getGedanListByTag(tagName, page, pageSize)
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<GedanListData>() {
                    @Override
                    public void call(GedanListData data) {
                        mLocal.saveData(
                                UrlGenerater.getGedanListByTag(tagName, page, pageSize),
                                data,
                                12 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<GedanListData>net());
    }

    @Override
    public Observable<OfficialGedanData> getOfficialGedan(final int offset, final int limit) {
        Observable<OfficialGedanData> localData = mLocal
                .getOfficialGedan(offset, limit);
        Observable<OfficialGedanData> remoteData = mRemote
                .getOfficialGedan(offset, limit)
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<OfficialGedanData>() {
                    @Override
                    public void call(OfficialGedanData data) {
                        mLocal.saveData(
                                UrlGenerater.getOfficialGedan(offset, limit),
                                data,
                                12 * 60 * 60 * 1000);
                    }
                });
        return Observable.concat(localData, remoteData)
                .first()
                .compose(TransformerUtil.<OfficialGedanData>io());
    }

}
