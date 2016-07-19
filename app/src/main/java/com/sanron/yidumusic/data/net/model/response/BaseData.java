package com.sanron.yidumusic.data.net.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanron on 16-7-16.
 */
public class BaseData {

    @JsonProperty("error_code")
    public int errorCode;

    @JsonProperty("error_message")
    public String errorMsg;

    public static final int CODE_SUCCES = 22000;
}
