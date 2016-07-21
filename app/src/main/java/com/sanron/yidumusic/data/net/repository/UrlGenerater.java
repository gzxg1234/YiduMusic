package com.sanron.yidumusic.data.net.repository;

/**
 * 生产缓存url,只在数据库缓存中使用
 * Created by sanron on 16-7-19.
 */
public abstract class UrlGenerater {

    public static final String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.8.0.1&format=json";

    public static String getHomeData(int foucsNum, int hotGedanNum,
                                     int recmdAlbumNum, int recmdSongNum) {
        return new StringBuilder(BASE)
                .append("&method=baidu.ting.home")
                .append("&focusNum=").append(foucsNum)
                .append("&hotGedanNum=").append(hotGedanNum)
                .append("&recmdAlbumNum=").append(recmdAlbumNum)
                .append("&recmdSongNum=").append(recmdSongNum)
                .toString();
    }


    //排行榜
    public static String getBillCategory() {
        return new StringBuilder(BASE)
                .append("&method=baidu.ting.billboard.billCategory&kflag=1")
                .toString();
    }

    //歌单分类
    public static String getGedanCategory() {
        return new StringBuilder(BASE)
                .append("&method=getGedanCategory")
                .toString();
    }

    //歌单列表
    public static String getGedanList(int page, int pageSize) {
        return new StringBuilder(BASE)
                .append("&method=baidu.ting.diy.gedan")
                .append("&page=").append(page)
                .append("&pageSize=").append(pageSize)
                .toString();
    }

    //标签歌单
    public static String getGedanListByTag(String tagName, int page, int pageSize) {
        return new StringBuilder(BASE)
                .append("&method=baidu.ting.diy.search")
                .append("&query=").append(tagName)
                .append("&page_no=").append(tagName)
                .append("&page_size=").append(page)
                .toString();
    }

    //官方歌单
    public static String getOfficialGedan(int offset, int limit) {
        return new StringBuilder(BASE)
                .append("&method=baidu.ting.diy.search")
                .append("&pn=").append(offset)
                .append("&rn=").append(limit)
                .toString();
    }

    //官方歌单
    public static String getLrcpic(String word, String artist) {
        return new StringBuilder(BASE)
                .append("&method=baidu.ting.search.lrcpic")
                .append("&word=").append(word)
                .append("&artist=").append(artist)
                .toString();
    }
}
