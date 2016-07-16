package com.sanron.yidumusic.data;

import com.sanron.yidumusic.data.model.response.BillCategoryData;
import com.sanron.yidumusic.data.model.response.FocusPicData;
import com.sanron.yidumusic.data.model.response.HotGedanData;
import com.sanron.yidumusic.data.model.response.HotTagData;
import com.sanron.yidumusic.data.model.response.RecmdAlbumData;
import com.sanron.yidumusic.data.model.response.RecmdSongData;
import com.sanron.yidumusic.data.model.response.SongListCategoryData;
import com.sanron.yidumusic.data.model.response.GedanListData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sanron on 16-7-13.
 */
public interface BaiduApiService {

    String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting/";

    /**
     * 焦点图
     *
     * @param num
     * @return
     */
    @GET("?method=baidu.ting.plaza.getFocusPic")
    Observable<FocusPicData> getFocusPic(@Query("num") int num);

    /**
     * 热门标签
     *
     * @param num
     * @return
     */
    @GET("?method=baidu.ting.tag.getHotTag")
    Observable<HotTagData> getHotTag(@Query("nums") int num);

    /**
     * 热门歌单
     *
     * @param num
     * @return
     */
    @GET("?method=baidu.ting.diy.getHotGeDanAndOfficial")
    Observable<HotGedanData> getHotSongList(@Query("num") int num);

    /**
     * 推荐专辑
     *
     * @param offset
     * @param limit
     * @return
     */
    @GET("?method=baidu.ting.plaza.getRecommendAlbum")
    Observable<RecmdAlbumData> getRecmdAlbum(@Query("offset") int offset,
                                             @Query("limit") int limit);

    /**
     * 推荐歌曲
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GET("?method=baidu.ting.song.userRecSongList")
    Observable<RecmdSongData> getRecmdSong(@Query("page_no") int page,
                                           @Query("page_size") int pageSize);

    //排行榜
    @GET("?method=baidu.ting.billboard.billCategory&kflag=1")
    Observable<BillCategoryData> getBillCategory();

    //歌单分类
    @GET("?method=baidu.ting.diy.gedanCategory")
    Observable<SongListCategoryData> getSongListCategory();

    //歌单列表
    @GET("?method=baidu.ting.diy.gedan")
    Observable<GedanListData> getGedanList(@Query("page_no") int page,
                                              @Query("page_size") int pageSize);

}
