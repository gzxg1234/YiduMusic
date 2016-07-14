package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-4-19.
 */
public class Album {

    /**
     * 名
     */
    @JsonProperty("title")
    public String title;

    /**
     * id
     */
    @JsonProperty("album_id")
    public String albumId;

    /**
     * 歌手
     */
    @JsonProperty("author")
    public String author;

    /**
     * 歌手Id
     */
    @JsonProperty("artist_id")
    public String artistId;

    /**
     * 所有歌手id
     */
    @JsonProperty("all_artist_id")
    public String allArtistId;

    /**
     * 发行公司
     */
    @JsonProperty("publishcompany")
    public String publishcompany;

    @JsonProperty("prodcompany")
    public String prodcompany;

    /**
     * 国家
     */
    @JsonProperty("country")
    public String country;

    /**
     * 语言
     */
    @JsonProperty("language")
    public String language;

    /**
     * 歌曲数
     */
    @JsonProperty("songs_total")
    public String songsTotal;

    @JsonProperty("info")
    public String info;

    /**
     * 专辑风格
     */
    @JsonProperty("styles")
    public String styles;

    /**
     * 发行时间
     * 不用date,因为有时会返回2000-00-00类似的数据,解析会出错
     */
    @JsonProperty("publishtime")
    public String publishtime;

    /**
     * 应该是热度
     */
    @JsonProperty("hot")
    public String hot;

    /**
     * 90x90
     */
    @JsonProperty("pic_small")
    public String picSmall;
    /**
     * 150x150
     */
    @JsonProperty("pic_big")
    public String picBig;
    /**
     * 300x300
     */
    @JsonProperty("pic_radio")
    public String picRadio;
    /**
     * 180x180
     */
    @JsonProperty("pic_s180")
    public String picS180;
    @JsonProperty("pic_300")
    public String pic300;
    @JsonProperty("pic_s500")
    public String picS500;
    @JsonProperty("pic_w700")
    public String picW700;
    @JsonProperty("pic_s1000")
    public String picS1000;
}
