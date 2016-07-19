package com.sanron.yidumusic.data.net.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.model.Album;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class RecmdAlbumData extends BaseData {

    @JsonProperty("plaze_album_list") public PlazeAlbumList plazeAlbumList;

    public static class PlazeAlbumList {
        @JsonProperty("RM") public RM RM;

        public static class RM {
            @JsonProperty("album_list") public AlbumList albumList;

            public static class AlbumList {
                @JsonProperty("havemore") public int havemore;
                @JsonProperty("list") public List<Album> albums;
            }
        }
    }
}
