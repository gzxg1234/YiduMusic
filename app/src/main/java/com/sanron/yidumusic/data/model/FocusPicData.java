package com.sanron.yidumusic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sanron on 16-3-19.
 */

public class FocusPicData {

    @JsonProperty("error_code")
    public int errorCode;
    @JsonProperty("pic")
    public List<FocusPic> pics;
}
