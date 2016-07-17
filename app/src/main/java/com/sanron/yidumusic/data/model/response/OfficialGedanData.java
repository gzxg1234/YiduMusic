package com.sanron.yidumusic.data.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.model.OfficicalGedan;

import java.util.List;

/**
 * Created by sanron on 16-7-17.
 */
public class OfficialGedanData {

    @JsonProperty("total") public int total;
    @JsonProperty("havemore") public int havemore;
    @JsonProperty("albumList") public List<OfficicalGedan> gedanList;
}
