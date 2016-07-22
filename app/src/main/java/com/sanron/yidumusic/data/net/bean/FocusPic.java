package com.sanron.yidumusic.data.net.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-7-16.
 */
public class FocusPic {
    //图片
    @JsonProperty("randpic") public String randpic;
    @JsonProperty("randpic_ios5") public String randpicIos5;
    @JsonProperty("randpic_ipad") public String randpicIpad;
    @JsonProperty("randpic_qq") public String randpicQq;
    @JsonProperty("randpic_iphone6") public String randpicIphone6;

    //描述
    @JsonProperty("randpic_desc") public String randpicDesc;
    @JsonProperty("ipad_desc") public String ipadDesc;

    //类型
    @JsonProperty("type") public int type;

    //代码
    @JsonProperty("code") public String code;

    @JsonProperty("mo_type") public String moType;
    @JsonProperty("randpic_2") public String randpic2;
    @JsonProperty("special_type") public int specialType;
    @JsonProperty("is_publish") public String isPublish;

    //专辑类型,此时code是专辑id
    public static final int TYPE_ALBUM = 2;
    //web页面,code是url
    public static final int TYPE_WEB = 6;
    //歌单,code是歌单id
    public static final int TYPE_SONGLIST = 7;

    //歌手(未出现过,在官方app代码中是存在的)
    public static final int TYPE_ARTIST = 1;
    public static final int TYPE_RADIO_TRACK = 9;
    public static final int TYPE_TOPIC = 3;
    public static final int TYPE_RADIO_ALBUM = 10;
}
