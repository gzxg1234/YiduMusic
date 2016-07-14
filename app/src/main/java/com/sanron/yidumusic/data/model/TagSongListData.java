package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-5-5.
 */
public class TagSongListData {

    @JsonProperty("havemore")
    public int havemore;
    @JsonProperty("error_code")
    public int errorCode;
    @JsonProperty("total")
    public int total;
    @JsonProperty("content")
    public List<SongList> songLists;

}
