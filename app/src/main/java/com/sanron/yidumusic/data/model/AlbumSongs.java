package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-23.
 */
public class AlbumSongs {

    @JsonProperty("albumInfo")
    public Album albumInfo;

    @JsonProperty("songlist")
    public List<Song> songs;

}
