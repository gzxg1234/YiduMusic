package com.sanron.yidumusic.data.net.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-7-16.
 */
public class Gedan {

    //名字
    @JsonProperty("title") public String title;

    //歌单id
    @JsonProperty("listid") public String listid;

    //图片
    @JsonProperty("pic") public String pic;
    @JsonProperty("pic_300") public String pic300;
    @JsonProperty("pic_w300") public String picW300;

    //收听人数
    @JsonProperty("listenum") public int listenum;

    //收藏数
    @JsonProperty("collectnum") public int collectnum;

    //歌单标签
    @JsonProperty("tag") public String tag;

    //描述
    @JsonProperty("desc") public String desc;

}
