package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-10.
 */
public class LrcPicData {

    @JsonProperty("songinfo")
    public List<LrcPic> lrcPics;

    public static class LrcPic {
        /**
         * 歌词
         */
        @JsonProperty("lrclink")
        public String lrc;

        @JsonProperty("song_id")
        public String songId;

        @JsonProperty("author")
        public String author;

        @JsonProperty("song_title")
        public String title;

        /**
         * 艺术家图片
         */
        @JsonProperty("artist_480_480")
        public String artist480x480;
        @JsonProperty("artist_640_1136")
        public String artist640x1136;
        @JsonProperty("artist_1000_1000")
        public String artist1000x1000;

        /**
         * 歌曲相关图片
         */
        @JsonProperty("pic_s180")
        public String pic180x180;
        @JsonProperty("pic_s500")
        public String pic500x500;
        @JsonProperty("pic_s1000")
        public String pic1000x1000;

        /**
         * 头像
         */
        @JsonProperty("avatar_s180")
        public String avatar180x180;
        @JsonProperty("avatar_s500")
        public String avatar500x500;

    }
}
