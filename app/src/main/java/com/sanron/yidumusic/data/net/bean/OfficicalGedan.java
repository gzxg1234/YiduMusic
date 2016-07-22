package com.sanron.yidumusic.data.net.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OfficicalGedan {
    @JsonProperty("name") public String name;
    @JsonProperty("pic") public String pic;
    @JsonProperty("createTime") public String createTime;
    @JsonProperty("desc") public String desc;
    @JsonProperty("code") public String code;
    @JsonProperty("list") public Object list;
    @JsonProperty("background") public Object background;
    @JsonProperty("pic_qq") public String picQq;
    @JsonProperty("pic_s640") public String picS640;
}