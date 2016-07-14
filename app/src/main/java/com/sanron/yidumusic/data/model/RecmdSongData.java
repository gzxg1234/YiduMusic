package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-12.
 */
public class RecmdSongData {

    @JsonProperty("error_code")
    public int errorCode;
    @JsonProperty("content")
    public List<Content> content;

    public static class Content {
        @JsonProperty("title")
        public String title;
        @JsonProperty("song_list")
        public List<RecommendSong> songs;

        public static class RecommendSong extends Song {
            /**
             * 推荐理由
             */
            @JsonProperty("recommend_reason")
            public String recommendReason;
        }
    }
}
