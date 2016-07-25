package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.List;

/**
 * Created by sanron on 16-7-25.
 */
public class SongInfoData extends BaseData {

    @JsonProperty("songUrls") public Songurl songUrls;
    @JsonProperty("songinfo") public SongInfo songinfo;

    public static class Songurl {
        @JsonProperty("urls") public List<Url> urls;

        public static class Url {
            //下载地址
            @JsonProperty("show_link") public String showLink;
            //文件id
            @JsonProperty("song_file_id") public int songFileId;
            //大小
            @JsonProperty("file_size") public int fileSize;
            //拓展名
            @JsonProperty("file_extension") public String fileExtension;
            //长度
            @JsonProperty("file_duration") public int fileDuration;
            //
            @JsonProperty("hash") public String hash;
            //比特率
            @JsonProperty("file_bitrate") public int fileBitrate;
            @JsonProperty("down_type") public int downType;
            @JsonProperty("original") public int original;
            @JsonProperty("free") public int free;
            @JsonProperty("replay_gain") public String replayGain;
            @JsonProperty("can_see") public int canSee;
            @JsonProperty("can_load") public boolean canLoad;
            @JsonProperty("preload") public int preload;
            @JsonProperty("file_link") public String fileLink;
            @JsonProperty("is_udition_url") public int isUditionUrl;
        }
    }
}
