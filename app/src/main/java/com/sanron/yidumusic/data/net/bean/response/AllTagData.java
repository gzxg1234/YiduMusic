package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Tag;

import java.util.List;
import java.util.Map;

/**
 * Created by sanron on 16-4-14.
 */
public class AllTagData extends BaseData{

    @JsonProperty("taglist")
    public Map<String,List<Tag>> tagList;

    @JsonProperty( "tags")
    public List<String> categories;
}
