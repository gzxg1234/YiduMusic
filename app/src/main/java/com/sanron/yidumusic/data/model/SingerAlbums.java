package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-4-19.
 */
public class SingerAlbums {

    @JsonProperty("albumnums")
    public String albumnums;

    @JsonProperty("havemore")
    public int havemore;

    @JsonProperty("albumlist")
    public List<Album> albums;
}
