package com.sanron.yidumusic.data.net.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.model.Gedan;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class HotGedanData extends BaseData {

    @JsonProperty("content") public Content content;

    public static class Content {
        @JsonProperty("title") public String title;
        @JsonProperty("list") public List<Gedan> gedanList;
    }
}
