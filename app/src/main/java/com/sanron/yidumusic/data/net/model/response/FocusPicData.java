package com.sanron.yidumusic.data.net.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.net.model.FocusPic;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class FocusPicData extends BaseData {

    @JsonProperty("pic") public List<FocusPic> focusPics;
}
