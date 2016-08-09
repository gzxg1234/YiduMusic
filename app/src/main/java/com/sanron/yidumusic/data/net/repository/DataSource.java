package com.sanron.yidumusic.data.net.repository;

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

import rx.Observable;

/**
 * Created by sanron on 16-7-19.
 */
public interface DataSource {

    // 获取首页数据
    Observable<HomeData> getHomeData(int foucsNum, int hotGedanNum, int recmdAlbumNum,
                                     int recmdSongNum);

    //排行榜
    Observable<BillCategoryData> getBillCategory();

    //歌单分类
    Observable<GedanCategoryData> getGedanCategory();

    //歌单列表
    Observable<GedanListData> getGedanList(int page, int pageSize);

    //标签歌单
    Observable<GedanListData> getGedanListByTag(String tagName, int page, int pageSize);

    //官方歌单
    Observable<OfficialGedanListData> getOfficialGedan(int offset, int limit);

    Observable<LrcpicData> getLrcpic(String word, String artist);

    //获取音乐信息
    Observable<SongInfoData> getSongInfo(long songid);

    //歌单信息
    Observable<GedanInfoData> getGedanInfo(long listid);

    //专辑信息
    Observable<AlbumDetailData> getAlbumInfo(long albumId);

    //热门标签
    Observable<HotTagData> getHotTag(int num);

    //全部标签
    Observable<AllTagData> getAllTag();

    //全部标签
    Observable<OfficialGedanInfoData> getOfficialGedanInfo(String code);

    Observable<SingerListData> getSingerList(int offset, int limit, int area,
                                             int sex,
                                             int order, String abc);
}
