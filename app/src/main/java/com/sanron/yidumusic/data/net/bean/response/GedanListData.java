package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Gedan;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class GedanListData extends BaseData {

    @JsonProperty("total") public int total;
    @JsonProperty("havemore") public int havemore;
    @JsonProperty("content") public List<Gedan> gedans;
}
