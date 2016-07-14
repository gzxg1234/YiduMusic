package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-5-5.
 */
public class OfficialSongListData {

    @JsonProperty("total")
    public String total;
    @JsonProperty("havemore")
    public int havemore;
    @JsonProperty("albumList")
    public List<SongList> songLists;

    public static class SongList {
        @JsonProperty("name")
        public String name;
        @JsonProperty("createTime")
        public String createTime;
        @JsonProperty("desc")
        public String desc;
        @JsonProperty("code")
        public String code;
        @JsonProperty("pic")
        public String pic;
        @JsonProperty("pic_qq")
        public String picQq;
        @JsonProperty("pic_s640")
        public String picS640;
    }
}
