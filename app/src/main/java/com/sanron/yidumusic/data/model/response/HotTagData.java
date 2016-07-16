package com.sanron.yidumusic.data.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanron.yidumusic.data.model.Tag;

import java.util.List;

/**
 * Created by sanron on 16-7-16.
 */
public class HotTagData extends BaseData {

    @JsonProperty("taglist") public List<Tag> tags;
}
