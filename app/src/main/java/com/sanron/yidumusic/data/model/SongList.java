package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-3-20.
 */
public class SongList {

    /**
     * 歌单名
     */
    @JsonProperty("title")
    public String title;

    /**
     * 歌单id
     */
    @JsonProperty("listid")
    public String listId;

    /**
     * 歌单标签
     */
    @JsonProperty("tag")
    public String tag;

    /**
     * 描述
     */
    @JsonProperty("desc")
    public String desc;

    /**
     * 图片
     */
    @JsonProperty("pic")
    public String pic;

    @JsonProperty("pic_300")
    public String pic300;

    @JsonProperty("pic_500")
    public String pic500;

    @JsonProperty("pic_w700")
    public String picW700;

    /**
     * 链接
     */
    @JsonProperty("url")
    public String url;

    /**
     * 音乐
     */
    @JsonProperty("content")
    public List<Song> songs;

}
