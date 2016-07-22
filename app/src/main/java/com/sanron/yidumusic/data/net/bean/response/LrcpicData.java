package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-7-20.
 */
public class LrcpicData extends BaseData{

    @JsonProperty("songinfo") public Songinfo songinfo;

    public static class Songinfo {
        @JsonProperty("pic_radio") public String picRadio;
        @JsonProperty("artist_480_800") public String artist480800;
        @JsonProperty("album_id") public int albumId;
        @JsonProperty("author") public String author;
        @JsonProperty("artist_1000_1000") public String artist10001000;
        @JsonProperty("artist_640_1136") public String artist6401136;
        @JsonProperty("artist_500_500") public String artist500500;
        @JsonProperty("lrc_md5") public String lrcMd5;
        @JsonProperty("artist_id") public int artistId;
        @JsonProperty("song_id") public int songId;
        @JsonProperty("song_title") public String songTitle;
        @JsonProperty("title") public String title;
        @JsonProperty("lrclink") public String lrclink;
        @JsonProperty("pic_type") public int picType;
        @JsonProperty("pic_s500") public String picS500;
        @JsonProperty("album_500_500") public String album500500;
        @JsonProperty("album_1000_1000") public String album10001000;
    }
}
