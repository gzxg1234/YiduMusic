package com.sanron.yidumusic.data.net.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by sanron on 16-7-16.
 */
public class Album {

    //专辑名
    @JsonProperty("title") public String title;

    //专辑id
    @JsonProperty("album_id") public long albumId;

    //专辑信息
    @JsonProperty("info") public String info;

    //风格
    @JsonProperty("styles") public String styles;

    //歌手名
    @JsonProperty("author") public String author;

    //艺术家id
    @JsonProperty("artist_id") public long artistId;

    //全部艺术家id
    @JsonProperty("all_artist_id") public String allArtistId;

    //国家
    @JsonProperty("country") public String country;

    //语言
    @JsonProperty("language") public String language;

    //专辑歌曲数量
    @JsonProperty("songs_total") public int songsTotal;

    //发行时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("publishtime")
    public Date publishtime;

    //发行公司
    @JsonProperty("publishcompany") public String publishcompany;

    //热度
    @JsonProperty("hot") public String hot;

    //风格id
    @JsonProperty("style_id") public String styleId;

    //歌手性别,0男,1女,2组合
    @JsonProperty("gender") public String gender;

    //专辑图片
    @JsonProperty("pic_small") public String picSmall;
    @JsonProperty("pic_big") public String picBig;
    @JsonProperty("pic_radio") public String picRadio;
    @JsonProperty("pic_s500") public String picS500;
    @JsonProperty("pic_s1000") public String picS1000;

    //未知
    @JsonProperty("prodcompany") public String prodcompany;
    @JsonProperty("artist_ting_uid") public String artistTingUid;
    @JsonProperty("all_artist_ting_uid") public Object allArtistTingUid;
    @JsonProperty("area") public String area;
    @JsonProperty("favorites_num") public int favoritesNum;
    @JsonProperty("recommend_num") public int recommendNum;
    @JsonProperty("ai_presale_flag") public String aiPresaleFlag;
    @JsonProperty("resource_type_ext") public String resourceTypeExt;
    @JsonProperty("buy_url") public String buyUrl;
}
