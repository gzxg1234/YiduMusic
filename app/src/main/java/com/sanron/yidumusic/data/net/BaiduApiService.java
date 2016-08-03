package com.sanron.yidumusic.data.net;

import com.sanron.yidumusic.data.net.bean.response.BillCategoryData;
import com.sanron.yidumusic.data.net.bean.response.FocusPicData;
import com.sanron.yidumusic.data.net.bean.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.GedanListData;
import com.sanron.yidumusic.data.net.bean.response.HotGedanData;
import com.sanron.yidumusic.data.net.bean.response.HotTagData;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanData;
import com.sanron.yidumusic.data.net.bean.response.RecmdAlbumData;
import com.sanron.yidumusic.data.net.bean.response.RecmdSongData;
import com.sanron.yidumusic.data.net.bean.response.SongInfoData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sanron on 16-7-13.
 */
public interface BaiduApiService {

    String BASE = "http://tingapi.ting.baidu.com/v1/restserver/";

    /**
     * 焦点图
     *
     * @param num
     * @return
     */
    @GET("ting?method=baidu.ting.plaza.getFocusPic")
    Observable<FocusPicData> getFocusPic(@Query("num") int num);

    /**
     * 热门标签
     *
     * @param num
     * @return
     */
    @GET("ting?method=baidu.ting.tag.getHotTag")
    Observable<HotTagData> getHotTag(@Query("nums") int num);

    /**
     * 热门歌单
     *
     * @param num
     * @return
     */
    @GET("ting?method=baidu.ting.diy.getHotGeDanAndOfficial")
    Observable<HotGedanData> getHotSongList(@Query("num") int num);

    /**
     * 推荐专辑
     *
     * @param offset
     * @param limit
     * @return
     */
    @GET("ting?method=baidu.ting.plaza.getRecommendAlbum")
    Observable<RecmdAlbumData> getRecmdAlbum(@Query("offset") int offset,
                                             @Query("limit") int limit);

    /**
     * 推荐歌曲
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GET("ting?method=baidu.ting.song.userRecSongList")
    Observable<RecmdSongData> getRecmdSong(@Query("page_no") int page,
                                           @Query("page_size") int pageSize);

    //排行榜
    @GET("ting?method=baidu.ting.billboard.billCategory&kflag=1")
    Observable<BillCategoryData> getBillCategory();

    //歌单分类
    @GET("ting?method=baidu.ting.diy.gedanCategory")
    Observable<GedanCategoryData> getGedanCategory();

    //歌单列表
    @GET("ting?method=baidu.ting.diy.gedan")
    Observable<GedanListData> getGedanList(@Query("page_no") int page,
                                           @Query("page_size") int pageSize);

    //标签歌单
    @GET("ting?method=baidu.ting.diy.search")
    Observable<GedanListData> getGedanListByTag(@Query("query") String tagName,
                                                @Query("page_no") int page,
                                                @Query("page_size") int pageSize);

    //官方歌单
    @GET("ting?method=baidu.ting.diy.getOfficialDiyList&ver=2&type=1")
    Observable<OfficialGedanData> getOfficialGedan(@Query("pn") int offset,
                                                   @Query("rn") int limit);

    @GET("ting?method=baidu.ting.search.lrcpic")
    Observable<LrcpicData> getLrcpic(@Query("query") String query,
                                     @Query("e") String e,
                                     @Query("ts") long ts);

    @GET("ting?method=baidu.ting.song.getInfos")
    Observable<SongInfoData> getSongInfo(@Query("songid") long songid,
                                         @Query("e") String e,
                                         @Query("ts") long ts);

    @GET("ting?method=baidu.ting.diy.gedanInfo")
    Observable<GedanInfoData> getGedanInfo(@Query("listid") long listid);
}
