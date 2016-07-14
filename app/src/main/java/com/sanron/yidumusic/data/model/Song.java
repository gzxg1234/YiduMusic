package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-3-19.
 */
public class Song {

    /**
     * 歌名
     */
    @JsonProperty("title")
    public String title;

    /**
     * 歌曲id
     */
    @JsonProperty("song_id")
    public String songId;

    /**
     * 所有歌手名
     */
    @JsonProperty("author")
    public String author;

    /**
     * 所有歌手id
     */
    @JsonProperty("all_artist_id")
    public String allArtistId;

    /**
     * 主要歌手id
     */
    @JsonProperty("artist_id")
    public String artistId;

    /**
     * 专辑名
     */
    @JsonProperty("album_title")
    public String albumTitle;

    /**
     * 专辑id
     */
    @JsonProperty("album_id")
    public String albumId;

    /**
     * 是否有mv
     */
    @JsonProperty("has_mv")
    public int hasMv;

    /**
     * 小图,90x90
     */
    @JsonProperty("pic_small")
    public String picSmall;

    /**
     * 大图,150x150
     */
    @JsonProperty("pic_big")
    public String picBig;

    /**
     * 更大图,500x500
     */
    @JsonProperty("pic_premium")
    public String picPremium;

    /**
     * 巨大图,1000x1000
     */
    @JsonProperty("pic_huge")
    public String picHuge;

    /**
     * 国家
     */
    @JsonProperty("country")
    public String country;

    /**
     * 地区
     */
    @JsonProperty("area")
    public String area;

    /**
     * 语言
     */
    @JsonProperty("language")
    public String language;

    /**
     * 发表时间
     */
    @JsonProperty("publishtime")
    public String publishtime;

    /**
     * 收听总数
     */
    @JsonProperty("listen_total")
    public String listenTotal;


    /**
     * 歌曲版本,已知有 "混音"
     */
    @JsonProperty("versions")
    public String versions;


    @Override
    public String toString() {
        return "title:" + title
                + " id:" + songId
                + " album:" + albumTitle
                + " albumId:" + albumId
                + " artist:" + albumId
                + " artistId:" + artistId
                + " artistIds:" + allArtistId
                + " picUrl:" + picBig;
    }

}
