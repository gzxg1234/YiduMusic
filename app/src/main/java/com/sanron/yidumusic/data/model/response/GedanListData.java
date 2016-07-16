package com.sanron.yidumusic.data.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.model.Gedan;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class GedanListData extends BaseData {

    @JsonProperty("total") public int total;
    @JsonProperty("havemore") public int havemore;
    @JsonProperty("content") public List<Gedan> gedans;
}
