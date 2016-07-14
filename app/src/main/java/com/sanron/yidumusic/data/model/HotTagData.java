package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-3-21.
 */
public class HotTagData {

    @JsonProperty("error_code")
    public int errorCode;
    @JsonProperty("taglist")
    public List<Tag> tags;
}
