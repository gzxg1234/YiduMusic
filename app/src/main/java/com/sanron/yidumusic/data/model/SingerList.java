package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-17.
 */
public class SingerList {

    /**
     * 数量
     */
    @JsonProperty("nums")
    public String nums;

    /**
     * 是否还有更多
     */
    @JsonProperty("havemore")
    public int havemore;

    @JsonProperty("artist")
    public List<Singer> singers;
}
