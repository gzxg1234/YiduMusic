package com.sanron.yidumusic.data.net;

import com.sanron.yidumusic.data.net.bean.response.AlbumDetailData;
import com.sanron.yidumusic.data.net.bean.response.AllTagData;
import com.sanron.yidumusic.data.net.bean.response.BillCategoryData;
import com.sanron.yidumusic.data.net.bean.response.BillSongListData;
import com.sanron.yidumusic.data.net.bean.response.FocusPicData;
import com.sanron.yidumusic.data.net.bean.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.GedanListData;
import com.sanron.yidumusic.data.net.bean.response.HotGedanData;
import com.sanron.yidumusic.data.net.bean.response.HotTagData;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanListData;
import com.sanron.yidumusic.data.net.bean.response.RecmdAlbumData;
import com.sanron.yidumusic.data.net.bean.response.RecmdSongData;
import com.sanron.yidumusic.data.net.bean.response.SingerListData;
import com.sanron.yidumusic.data.net.bean.response.SongInfoData;
import com.sanron.yidumusic.data.net.bean.response.TagSongListData;

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
     * 所有标签
     */
    @GET("ting?method=baidu.ting.tag.getAllTag")
    Observable<AllTagData> getAllTag();

    /**
     * 标签歌曲
     * @param tagname
     * @param limit
     * @param offset
     * @return
     */
    @GET("ting?method=baidu.ting.tag.songlist")
    Observable<TagSongListData> getTagSongList(@Query("tagname") String tagname,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset);

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

    /**
     * 排行榜
     *
     * @return
     */
    @GET("ting?method=baidu.ting.billboard.billCategory&kflag=1")
    Observable<BillCategoryData> getBillCategory();

    @GET("ting?method=baidu.ting.billboard.billList")
    Observable<BillSongListData> getBillSongList(@Query("type") int type,
                                                 @Query("offset") int offset,
                                                 @Query("size") int size);

    /**
     * 歌单分类
     *
     * @return
     */
    @GET("ting?method=baidu.ting.diy.gedanCategory")
    Observable<GedanCategoryData> getGedanCategory();

    /**
     * 歌单列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GET("ting?method=baidu.ting.diy.gedan")
    Observable<GedanListData> getGedanList(@Query("page_no") int page,
                                           @Query("page_size") int pageSize);

    /**
     * 标签歌单
     *
     * @param tagName
     * @param page
     * @param pageSize
     * @return
     */
    @GET("ting?method=baidu.ting.diy.search")
    Observable<GedanListData> getGedanListByTag(@Query("query") String tagName,
                                                @Query("page_no") int page,
                                                @Query("page_size") int pageSize);

    /**
     * 官方歌单
     *
     * @param offset
     * @param limit
     * @return
     */
    @GET("ting?method=baidu.ting.diy.getOfficialDiyList&ver=2&type=1")
    Observable<OfficialGedanListData> getOfficialGedan(@Query("pn") int offset,
                                                       @Query("rn") int limit);

    /**
     * 搜索歌词
     *
     * @param query
     * @param e
     * @param ts
     * @return
     */
    @GET("ting?method=baidu.ting.search.lrcpic")
    Observable<LrcpicData> getLrcpic(@Query("query") String query,
                                     @Query("e") String e,
                                     @Query("ts") long ts);

    /**
     * 歌曲详细详细(包括下载地址)
     *
     * @param songid
     * @param e
     * @param ts
     * @return
     */
    @GET("ting?method=baidu.ting.song.getInfos")
    Observable<SongInfoData> getSongInfo(@Query("songid") long songid,
                                         @Query("e") String e,
                                         @Query("ts") long ts);

    /**
     * 歌单信息(包括歌单下的歌曲)
     *
     * @param listid
     * @return
     */
    @GET("ting?method=baidu.ting.diy.gedanInfo")
    Observable<GedanInfoData> getGedanInfo(@Query("listid") long listid);

    /**
     * 官方歌单信息
     *
     * @param code
     * @return
     */
    @GET("ting?method=baidu.ting.diy.getSongFromOfficalList")
    Observable<OfficialGedanInfoData> getOfficialGedanInfo(@Query("code") String code);


    @GET("ting?method=baidu.ting.album.getAlbumInfo")
    Observable<AlbumDetailData> getAlbumInfo(@Query("album_id") long albumId);

    @GET("ting?method=baidu.ting.artist.getList")
    Observable<SingerListData> getSingerList(@Query("offset") int offset, @Query("limit") int limit, @Query("area") int area,
                                             @Query("sex") int sex,
                                             @Query("order") int order, @Query("abc") String abc);
}
