package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.Song;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class BillCategoryData extends BaseData {

    @JsonProperty("content") public List<BillCategory> content;

    public static class BillCategory {
        @JsonProperty("name") public String name;
        @JsonProperty("type") public int type;
        @JsonProperty("count") public int count;
        @JsonProperty("comment") public String comment;
        @JsonProperty("web_url") public String webUrl;
        @JsonProperty("pic_s192") public String picS192;
        @JsonProperty("pic_s444") public String picS444;
        @JsonProperty("pic_s260") public String picS260;
        @JsonProperty("pic_s210") public String picS210;
        @JsonProperty("content") public List<Song> top3;
    }
}
