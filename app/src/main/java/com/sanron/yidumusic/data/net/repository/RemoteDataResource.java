package com.sanron.yidumusic.data.net.repository;

import com.sanron.yidumusic.data.net.BaiduApiService;
import com.sanron.yidumusic.data.net.model.Album;
import com.sanron.yidumusic.data.net.model.FocusPic;
import com.sanron.yidumusic.data.net.model.Gedan;
import com.sanron.yidumusic.data.net.model.Song;
import com.sanron.yidumusic.data.net.model.response.BillCategoryData;
import com.sanron.yidumusic.data.net.model.response.FocusPicData;
import com.sanron.yidumusic.data.net.model.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.model.response.GedanListData;
import com.sanron.yidumusic.data.net.model.response.HomeData;
import com.sanron.yidumusic.data.net.model.response.HotGedanData;
import com.sanron.yidumusic.data.net.model.response.OfficialGedanData;
import com.sanron.yidumusic.data.net.model.response.RecmdAlbumData;
import com.sanron.yidumusic.data.net.model.response.RecmdSongData;
import com.sanron.yidumusic.rx.TransformerUtil;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func4;

/**
 * Created by sanron on 16-7-19.
 */
public class RemoteDataResource implements DataResource {


    private BaiduApiService mApiService;

    public RemoteDataResource(BaiduApiService apiService) {
        mApiService = apiService;
    }

    @Override
    public Observable<HomeData> getHomeData(int foucsNum, int hotGedanNum, int recmdAlbumNum, int recmdSongNum) {

        //循环图
        Observable<List<FocusPic>> focus = mApiService.getFocusPic(foucsNum)
                .compose(TransformerUtil.<FocusPicData>checkError())
                .map(new Func1<FocusPicData, List<FocusPic>>() {
                    @Override
                    public List<FocusPic> call(FocusPicData focusPicData) {
                        return focusPicData.focusPics;
                    }
                })
                .flatMap(new Func1<List<FocusPic>, Observable<FocusPic>>() {
                    @Override
                    public Observable<FocusPic> call(List<FocusPic> focusPics) {
                        return Observable.from(focusPics);
                    }
                })
                .filter(new Func1<FocusPic, Boolean>() {
                    @Override
                    public Boolean call(FocusPic focusPic) {
                        return focusPic.type == FocusPic.TYPE_ALBUM || focusPic.type == FocusPic.TYPE_SONGLIST;
                    }
                }).toList();

        //热门歌单
        Observable<List<Gedan>> hotSongList = mApiService.getHotSongList(hotGedanNum)
                .compose(TransformerUtil.<HotGedanData>checkError())
                .map(new Func1<HotGedanData, List<Gedan>>() {
                    @Override
                    public List<Gedan> call(HotGedanData hotSongListData) {
                        return hotSongListData.content.gedanList;
                    }
                });

        //推荐专辑
        Observable<List<Album>> recmdAlbum = mApiService.getRecmdAlbum(0, recmdAlbumNum)
                .compose(TransformerUtil.<RecmdAlbumData>checkError())
                .map(new Func1<RecmdAlbumData, List<Album>>() {
                    @Override
                    public List<Album> call(RecmdAlbumData recmdAlbumData) {
                        return recmdAlbumData.plazeAlbumList.RM.albumList.albums;
                    }
                });

        //推荐歌曲
        Observable<List<Song>> recmdSong = mApiService.getRecmdSong(0, recmdSongNum)
                .compose(TransformerUtil.<RecmdSongData>checkError())
                .map(new Func1<RecmdSongData, List<Song>>() {
                    @Override
                    public List<Song> call(RecmdSongData recmdSongData) {
                        return recmdSongData.result.songs;
                    }
                });

        //压缩
        return Observable.zip(focus, hotSongList, recmdAlbum, recmdSong,
                new Func4<List<FocusPic>, List<Gedan>, List<Album>, List<Song>, HomeData>() {
                    @Override
                    public HomeData call(List<FocusPic> focusPics, List<Gedan> gedens, List<Album> albums, List<Song> songs) {
                        HomeData homeData = new HomeData();
                        homeData.mFocusPicDatas = focusPics;
                        homeData.hotGedans = gedens;
                        homeData.recmdAlbums = albums;
                        homeData.recmdSongs = songs;
                        return homeData;
                    }
                });
    }

    @Override
    public Observable<BillCategoryData> getBillCategory() {
        return mApiService.getBillCategory();
    }

    @Override
    public Observable<GedanCategoryData> getGedanCategory() {
        return mApiService.getGedanCategory();
    }

    @Override
    public Observable<GedanListData> getGedanList(int page, int pageSize) {
        return mApiService.getGedanList(page, pageSize);
    }

    @Override
    public Observable<GedanListData> getGedanListByTag(String tagName, int page, int pageSize) {
        return mApiService.getGedanListByTag(tagName, page, pageSize);
    }

    @Override
    public Observable<OfficialGedanData> getOfficialGedan(int offset, int limit) {
        return mApiService.getOfficialGedan(offset, limit);
    }

}
