package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.OfficicalGedan;

import java.util.List;

/**
 * Created by sanron on 16-7-17.
 */
public class OfficialGedanListData {

    @JsonProperty("total") public int total;
    @JsonProperty("havemore") public int havemore;
    @JsonProperty("albumList") public List<OfficicalGedan> gedanList;
}
