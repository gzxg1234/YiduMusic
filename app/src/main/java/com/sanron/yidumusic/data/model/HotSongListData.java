package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-12.
 */
public class HotSongListData {

    @JsonProperty("error_code")
    public int errorCode;
    @JsonProperty("content")
    public Content content;

    public static class Content {
        @JsonProperty("title")
        public String title;
        @JsonProperty("list")
        public List<SongList> songLists;
    }
}
