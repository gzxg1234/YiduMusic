package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-18.
 */
public class SingerSongs {

    /**
     * 歌曲总数
     */
    @JsonProperty("songnums")
    public String songnums;

    @JsonProperty("havemore")
    public int havemore;

    @JsonProperty("error_code")
    public int errorCode;

    @JsonProperty("songlist")
    public List<Song> songs;
}
