package com.sanron.yidumusic.data.net.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-7-16.
 */
public class SongInfo {


    //歌曲名
    @JsonProperty("title") public String title;

    //歌曲id
    @JsonProperty("song_id") public long songId;

    //歌曲图片
    @JsonProperty("pic_small") public String picSmall;
    @JsonProperty("pic_huge") public String picHuge;
    @JsonProperty("pic_radio") public String picRadio;
    @JsonProperty("pic_big") public String picBig;
    @JsonProperty("pic_premium") public String picPremium;

    //歌手名
    @JsonProperty("author") public String author;

    //主要歌手id
    @JsonProperty("artist_id") public String artistId;
    //所有歌手id(合唱情况下)
    @JsonProperty("all_artist_id") public String allArtistId;

    //歌手图片
    @JsonProperty("pic_singer") public String picSinger;

    //专辑名
    @JsonProperty("album_title") public String albumTitle;

    //专辑id
    @JsonProperty("album_id") public String albumId;

    //专辑图片
    @JsonProperty("album_1000_1000") public String album10001000;
    @JsonProperty("album_500_500") public String album500500;

    //全部码率
    @JsonProperty("all_rate") public String allRate;

    //歌曲时长
    @JsonProperty("file_duration") public int fileDuration;

    //是否有mv
    @JsonProperty("has_mv") public int hasMv;
    @JsonProperty("has_mv_mobile") public int hasMvMobile;

    //地区
    @JsonProperty("area") public String area;

    //热度
    @JsonProperty("hot") public String hot;

    //分享地址
    @JsonProperty("share_url") public String shareUrl;

    //语言
    @JsonProperty("language") public String language;

    //国家
    @JsonProperty("country") public String country;

    //歌手图片
    @JsonProperty("artist_500_500") public String artist500500;
    @JsonProperty("artist_1000_1000") public String artist10001000;
    @JsonProperty("artist_480_800") public String artist480800;
    @JsonProperty("artist_640_1136") public String artist6401136;

    //歌词链接
    @JsonProperty("lrclink") public String lrclink;

    //发布时间
    @JsonProperty("publishtime") public String publishtime;

    //别名
    @JsonProperty("aliasname") public String aliasname;

    //未知
    @JsonProperty("album_no") public String albumNo;
    @JsonProperty("resource_type_ext") public String resourceTypeExt;
    @JsonProperty("resource_type") public String resourceType;
    @JsonProperty("del_status") public String delStatus;
    @JsonProperty("havehigh") public int havehigh;
    @JsonProperty("piao_id") public String piaoId;
    @JsonProperty("song_source") public String songSource;
    @JsonProperty("korean_bb_song") public String koreanBbSong;
    @JsonProperty("compose") public String compose;
    @JsonProperty("toneid") public String toneid;
    @JsonProperty("original_rate") public String originalRate;
    @JsonProperty("bitrate") public String bitrate;
    @JsonProperty("multiterminal_copytype") public String multiterminalCopytype;
    @JsonProperty("sound_effect") public String soundEffect;
    @JsonProperty("high_rate") public String highRate;
    @JsonProperty("is_first_publish") public int isFirstPublish;
    @JsonProperty("distribution") public String distribution;
    @JsonProperty("relate_status") public String relateStatus;
    @JsonProperty("learn") public int learn;
    @JsonProperty("play_type") public int playType;
    @JsonProperty("original") public int original;
    @JsonProperty("compress_status") public String compressStatus;
    @JsonProperty("versions") public String versions;
    @JsonProperty("expire") public int expire;
    @JsonProperty("ting_uid") public String tingUid;
    @JsonProperty("charge") public int charge;
    @JsonProperty("copy_type") public String copyType;
    @JsonProperty("songwriting") public String songwriting;
    @JsonProperty("is_charge") public String isCharge;

}
