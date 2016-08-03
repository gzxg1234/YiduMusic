package com.sanron.yidumusic.data.net.bean.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.sanron.yidumusic.data.net.bean.Gedan;

/**
 * Created by sanron on 16-8-3.
 */
public class GedanInfoData extends BaseData {
    @JsonUnwrapped public Gedan gedan;
}
