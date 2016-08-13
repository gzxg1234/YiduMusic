package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Billboard;
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.List;

/**
 * Created by sanron on 16-4-24.
 */
public class BillSongListData extends BaseData {

    @JsonProperty("billboard")
    public Billboard billboard;

    @JsonProperty("song_list")
    public List<SongInfo> songs;
}
