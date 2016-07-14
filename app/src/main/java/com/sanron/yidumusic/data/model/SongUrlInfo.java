package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-3-29.
 */
public class SongUrlInfo {

    @JsonProperty("songurl")
    public SongUrl songUrl;

    public static class SongUrl {

        @JsonProperty("url")
        public List<Url> urls;

        public static class Url {
            /**
             * 比特率
             */
            @JsonProperty("file_bitrate")
            public int fileBitrate;

            /**
             * 下载链接
             */
            @JsonProperty("file_link")
            public String fileLink;

            @JsonProperty("show_link")
            public String showLink;

            /**
             * 是否试听地址
             */
            @JsonProperty("is_udition_url")
            public int isAudition;

            @JsonProperty("file_size")
            public int fileSize;

        }
    }
}
