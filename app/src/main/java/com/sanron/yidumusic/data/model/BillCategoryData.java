package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-24.
 */
public class BillCategoryData {

    @JsonProperty("error_code")
    public int errorCode;

    @JsonProperty("content")
    public List<BillCategory> billCategories;

    public static class BillCategory {

        @JsonProperty("name")
        public String name;

        @JsonProperty("type")
        public int type;

        @JsonProperty("count")
        public int count;

        @JsonProperty("comment")
        public String comment;

        @JsonProperty("web_url")
        public String webUrl;

        //546x546
        @JsonProperty("pic_s192")
        public String picS192;

        //444x260
        @JsonProperty("pic_s444")
        public String picS444;

        //260x260
        @JsonProperty("pic_s260")
        public String picS260;

        //210x130
        @JsonProperty("pic_s210")
        public String picS210;

        @JsonProperty("content")
        public List<TopSong> topSongs;

        public static class TopSong {
            @JsonProperty("title")
            public String title;
            @JsonProperty("author")
            public String author;
            @JsonProperty("song_id")
            public String songId;
            @JsonProperty("album_id")
            public String albumId;
            @JsonProperty("album_title")
            public String albumTitle;
            @JsonProperty("rank_change")
            public String rankChange;
            @JsonProperty("all_rate")
            public String allRate;
        }
    }
}
