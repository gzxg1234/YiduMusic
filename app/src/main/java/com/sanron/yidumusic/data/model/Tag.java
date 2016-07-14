package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-3-21.
 */
public class Tag {
    @JsonProperty("title")
    public String title;

    @JsonProperty("hot")
    public int isHot;
}
