package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.List;

/**
 * Created by sanron on 16-4-12.
 */
public class TagSongListData extends BaseData {

    @JsonProperty("taginfo")
    public Taginfo taginfo;

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
        public List<SongInfo> songs;
    }
}
