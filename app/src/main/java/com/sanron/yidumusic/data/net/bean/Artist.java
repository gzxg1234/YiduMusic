package com.sanron.yidumusic.data.net.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-7-16.
 */
public class Artist {

    @JsonProperty("ting_uid") public String tingUid;
    @JsonProperty("name") public String name;
    @JsonProperty("firstchar") public String firstchar;
    @JsonProperty("gender") public String gender;
    @JsonProperty("area") public String area;
    @JsonProperty("country") public String country;
    @JsonProperty("avatar_big") public String avatarBig;
    @JsonProperty("avatar_middle") public String avatarMiddle;
    @JsonProperty("avatar_small") public String avatarSmall;
    @JsonProperty("avatar_mini") public String avatarMini;
    @JsonProperty("albums_total") public String albumsTotal;
    @JsonProperty("songs_total") public String songsTotal;
    @JsonProperty("artist_id") public String artistId;
    @JsonProperty("piao_id") public String piaoId;
}
