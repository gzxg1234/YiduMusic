package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Song;

import java.util.List;

/**
 * Created by sanron on 16-4-12.
 */
public class RecmdSongData extends BaseData {

    @JsonProperty("result") public Result result;

    public static class Result {
        @JsonProperty("total") public int total;
        @JsonProperty("havemore") public int havemore;
        @JsonProperty("date") public long date;
        @JsonProperty("list") public List<Song> songs;
    }
}
