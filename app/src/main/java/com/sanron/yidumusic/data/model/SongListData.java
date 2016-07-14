package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-5-2.
 */
public class SongListData {

    @JsonProperty("error_code")
    public int errorCode;

    @JsonProperty("total")
    public int total;

    @JsonProperty("havemore")
    public int havemore;

    @JsonProperty("content")
    public List<SongList> songLists;

}
