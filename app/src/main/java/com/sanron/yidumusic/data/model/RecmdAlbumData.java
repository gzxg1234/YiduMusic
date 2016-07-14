package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-7-14.
 */
public class RecmdAlbumData {

    @JsonProperty("error_code") public int errorCode;
    @JsonProperty("plaze_album_list") public PlazeAlbumList plazeAlbumList;

    public static class PlazeAlbumList {
        @JsonProperty("RM") public RM rm;

        public static class RM {
            @JsonProperty("album_list") public AlbumList albumList;

            public static class AlbumList {
                @JsonProperty("havemore") public int havemore;
                @JsonProperty("list") public List<Album> albums;
            }
        }
    }
}
