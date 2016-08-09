package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Artist;

import java.util.List;

/**
 * Created by sanron on 16-8-9.
 */
public class SingerListData {
    @JsonProperty("nums") public int nums;
    @JsonProperty("noFirstChar") public String noFirstChar;
    @JsonProperty("havemore") public int havemore;
    @JsonProperty("artist") public List<Artist> artists;
}
