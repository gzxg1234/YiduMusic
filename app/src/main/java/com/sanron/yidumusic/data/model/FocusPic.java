package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FocusPic {

    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_SONG_LIST = 7;

    /**
     * 图片URL
     */
    @JsonProperty("randpic")
    public String picUrl;

    /**
     * 描述
     */
    @JsonProperty("randpic_desc")
    public String desc;

    /**
     * 类型
     */
    @JsonProperty("type")
    public int type;

    /**
     * 代码
     */
    @JsonProperty("code")
    public String code;

}
