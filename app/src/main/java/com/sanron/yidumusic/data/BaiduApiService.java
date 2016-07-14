package com.sanron.yidumusic.data;

import com.sanron.yidumusic.data.model.FocusPicData;
import com.sanron.yidumusic.data.model.HotSongListData;
import com.sanron.yidumusic.data.model.HotTagData;
import com.sanron.yidumusic.data.model.RecmdAlbumData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sanron on 16-7-13.
 */
public interface BaiduApiService {

    String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting/";

    @GET("?method=baidu.ting.plaza.getFocusPic")
    Observable<FocusPicData> getFocusPic(@Query("num") int num);

    @GET("?method=baidu.ting.tag.getHotTag")
    Observable<HotTagData> getHotTag(@Query("nums") int num);

    @GET("?method=baidu.ting.diy.getHotGeDanAndOfficial")
    Observable<HotSongListData> getHotSongList(@Query("num") int num);

    @GET("?method=baidu.ting.plaza.getRecommendAlbum")
    Observable<RecmdAlbumData> getRecmdAlbum(@Query("offset") int offset, @Query("limit") int limit);
}
