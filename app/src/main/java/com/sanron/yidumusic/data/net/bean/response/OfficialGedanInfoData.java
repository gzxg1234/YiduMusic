package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by sanron on 16-8-9.
 */
public class OfficialGedanInfoData {

    @JsonProperty("name") public String name;
    @JsonProperty("pic") public String pic;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("createTime") public Date createTime;
    @JsonProperty("desc") public String desc;
    @JsonProperty("code") public String code;
    @JsonProperty("background") public Object background;
    @JsonProperty("list") public List<SongInfo> list;
}
