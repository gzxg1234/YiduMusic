package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by sanron on 16-4-14.
 */
public class AllTag {


    @JsonProperty("error_code")
    public int errorCode;

    @JsonProperty("taglist")
    public Map<String,List<Tag>> tagList;

    @JsonProperty("tags")
    public List<String> categories;
}
