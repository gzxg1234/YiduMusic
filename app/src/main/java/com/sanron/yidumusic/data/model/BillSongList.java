package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-24.
 */
public class BillSongList {

    @JsonProperty("billboard")
    public Billboard billboard;

    @JsonProperty("error_code")
    public int errorCode;

    @JsonProperty("song_list")
    public List<RankSong> songs;

    public static class Billboard {
        @JsonProperty("billboard_type")
        public String billboardType;
        @JsonProperty("billboard_no")
        public String billboardNo;
        /**
         * 更新时间
         */
        @JsonProperty("update_date")
        public String updateDate;

        /**
         * 是否有更多(无效，是错误的数据）
         */
        @JsonProperty("havemore")
        public int havemore;

        @JsonProperty("name")
        public String name;

        @JsonProperty("comment")
        public String comment;

        //640x640
        @JsonProperty("pic_s640")
        public String picS640;
        @JsonProperty("pic_s444")
        public String picS444;
        @JsonProperty("pic_s260")
        public String picS260;
        @JsonProperty("pic_s210")
        public String picS210;
        @JsonProperty("web_url")
        public String webUrl;
    }

    public static class RankSong extends Song {
        //排名变化
        @JsonProperty("rank_change")
        public String rankChange;

        //排名
        @JsonProperty("rank")
        public String rank;
    }
}
