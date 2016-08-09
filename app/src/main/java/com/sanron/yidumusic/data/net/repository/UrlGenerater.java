package com.sanron.yidumusic.data.net.repository;

/**
 * 生产缓存url,只在数据库缓存中使用
 * Created by sanron on 16-7-19.
 */
public abstract class UrlGenerater {

    public static final String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.8.0.1&format=json";

    public static String getHomeData(int foucsNum, int hotGedanNum,
                                     int recmdAlbumNum, int recmdSongNum) {
        return BASE
                + "&method=baidu.ting.home"
                + "&focusNum=" + foucsNum
                + "&hotGedanNum=" + hotGedanNum
                + "&recmdAlbumNum=" + recmdAlbumNum
                + "&recmdSongNum=" + recmdSongNum;
    }

    public static String getBillCategory() {
        return BASE
                + "&method=baidu.ting.billboard.billCategory&kflag=1";
    }

    public static String getGedanCategory() {
        return BASE
                + "&method=getGedanCategory";
    }

    public static String getGedanList(int page, int pageSize) {
        return BASE
                + "&method=baidu.ting.diy.gedan"
                + "&page=" + page
                + "&pageSize=" + pageSize;
    }

    public static String getGedanListByTag(String tagName, int page, int pageSize) {
        return BASE
                + "&method=baidu.ting.diy.search"
                + "&query=" + tagName
                + "&page_no=" + tagName
                + "&page_size=" + page;
    }

    public static String getOfficialGedan(int offset, int limit) {
        return BASE
                + "&method=baidu.ting.diy.search"
                + "&pn=" + offset
                + "&rn=" + limit;
    }

    public static String getLrcpic(String word, String artist) {
        return BASE
                + "&method=baidu.ting.search.lrcpic"
                + "&word=" + word
                + "&artist=" + artist;
    }

    public static String getSongInfo(long songid) {
        return BASE
                + "&method=baidu.ting.song.getInfos"
                + "&songid=" + songid;
    }

    public static String getGedanInfo(long listid) {
        return BASE
                + "&method=baidu.ting.diy.gedanInfo"
                + "&listid=" + listid;
    }

    public static String getAlbumInfo(long albumId) {
        return BASE
                + "&method=baidu.ting.album.getAlbumInfo"
                + "&album_id=" + albumId;
    }

    public static String getHotTag(int nums) {
        return BASE
                + "&method=baidu.ting.tag.getHotTag"
                + "&nums=" + nums;
    }

    public static String getAllTag() {
        return BASE
                + "&method=baidu.ting.tag.getAllTag";
    }

    public static String getOfficialGedanInfo(String code) {
        return BASE
                + "&method=baidu.ting.diy.getSongFromOfficalList"
                + "&code=" + code;
    }

    public static String getSingerList(int offset, int limit, int area, int sex, int order, String abc) {
        return BASE
                + "&method=baidu.ting.artist.getList"
                + "&offset=" + offset
                + "&limit=" + limit
                + "&order=" + order
                + "&sex=" + sex
                + "&area=" + area
                + "&abc=" + abc;
    }
}
