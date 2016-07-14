package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-4-17.
 */
public class Singer {


    /**
     * 名字
     */
    @JsonProperty("name")
    public String name;

    /**
     * 性别
     */
    @JsonProperty("gender")
    public String gender;

    /**
     * 生日
     */
    @JsonProperty("birth")
    public String birth;

    /**
     * 星座
     */
    @JsonProperty("constellation")
    public String constellation;


    /**
     * tingUid，貌似也可作为歌手标识
     */
    @JsonProperty("ting_uid")
    public String tingUid;

    /**
     * 体重
     */
    @JsonProperty("weight")
    public String weight;

    /**
     * 身高
     */
    @JsonProperty("stature")
    public String stature;


    /**
     * 别名
     */
    @JsonProperty("aliasname")
    public String aliasname;

    /**
     * 国家
     */
    @JsonProperty("country")
    public String country;

    @JsonProperty("source")
    public String source;

    /**
     * 歌手简介
     */
    @JsonProperty("intro")
    public String intro;

    @JsonProperty("url")
    public String url;

    /**
     * 公司
     */
    @JsonProperty("company")
    public String company;

//    /**
//     * 血型
//     */
//    @JsonProperty("bloodtype")
//    public String bloodtype;

    /**
     * mv数量
     */
    @JsonProperty("mv_total")
    public String mvTotal;

    /**
     * 地区
     */
    @JsonProperty("area")
    public String area;

    /**
     * 姓名首字母
     */
    @JsonProperty("firstchar")
    public String firstchar;

    /**
     * id
     */
    @JsonProperty("artist_id")
    public String artistId;


    /**
     * 头像
     */
    @JsonProperty("avatar_mini")
    public String avatarMini;
    @JsonProperty("avatar_small")
    public String avatarSmall;
    @JsonProperty("avatar_middle")
    public String avatarMiddle;//120x120
    @JsonProperty("avatar_big")
    public String avatarBig;//240x240
    @JsonProperty("avatar_s180")
    public String avatarS180;
    @JsonProperty("avatar_s500")
    public String avatarS500;
    @JsonProperty("avatar_s1000")
    public String avatarS1000;

    /**
     * 专辑数
     */
    @JsonProperty("albums_total")
    public int albumsTotal;

    /**
     * 歌曲数
     */
    @JsonProperty("songs_total")
    public int songsTotal;
}
