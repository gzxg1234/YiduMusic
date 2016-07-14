package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-5-5.
 */
public class SongListCategory {

    @JsonProperty("error_code")
    public int errorCode;
    @JsonProperty("content")
    public List<Content> content;

    public static class Content {
        @JsonProperty("title")
        public String title;
        @JsonProperty("num")
        public int num;
        @JsonProperty("tags")
        public List<Tag> tags;

        public static class Tag {
            @JsonProperty("tag")
            public String tag;
            @JsonProperty("type")
            public String type;
        }
    }
}
