package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-12.
 */
public class TagSongs {

    @JsonProperty("taginfo")
    public Taginfo taginfo;

    @JsonProperty("error_code")
    public int errorCode;

    public static class Taginfo {
        /**
         * 歌曲总数
         */
        @JsonProperty("count")
        public int count;
        /**
         * 是否有更多
         */
        @JsonProperty("havemore")
        public int havemore;

        /**
         * 歌曲
         */
        @JsonProperty("songlist")
        public List<Song> songs;
    }
}
