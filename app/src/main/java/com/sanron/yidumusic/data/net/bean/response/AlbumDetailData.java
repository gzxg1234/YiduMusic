package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Album;
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.List;

/**
 * Created by sanron on 16-8-4.
 */
public class AlbumDetailData {
    @JsonProperty("albumInfo") public Album album;
    @JsonProperty("songlist") public List<SongInfo> songlist;
}
