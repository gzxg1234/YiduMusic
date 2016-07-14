package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-5-5.
 */
public class OfficialSongListSongs {

    @JsonProperty("name")
    public String name;
    @JsonProperty("pic")
    public String pic;
    @JsonProperty("createTime")
    public String createTime;
    @JsonProperty("desc")
    public String desc;
    @JsonProperty("code")
    public String code;
    @JsonProperty("list")
    public List<Song> songs;
}
