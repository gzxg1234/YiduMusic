package com.sanron.yidumusic.data.net.repository;

import com.sanron.yidumusic.data.net.bean.response.BillCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.data.net.bean.response.GedanListData;
import com.sanron.yidumusic.data.net.bean.response.HomeData;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanData;
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
    Observable<OfficialGedanData> getOfficialGedan(int offset, int limit);

    Observable<LrcpicData> getLrcpic(String word, String artist);

    //获取音乐信息
    Observable<SongInfoData> getSongInfo(long songid);

    //
    Observable<GedanInfoData> getGedanInfo(long listid);
}
