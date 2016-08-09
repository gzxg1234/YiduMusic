package com.sanron.yidumusic.playback;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.db.model.PlayListMembers;
import com.sanron.yidumusic.data.net.bean.response.SongInfoData;
import com.sanron.yidumusic.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanron on 16-7-25.
 */
public class PlayHelper {

    //根据网络自动选则
    public static final int SELECT_AUTO = 1;
    //低质量
    public static final int SELECT_LOW = 2;
    //高质量
    public static final int SELECT_HIGH = 3;

    //选择合适质量的音乐文件
    public static SongInfoData.Songurl.Url selectFileUrl(Context context, SongInfoData songInfoData, int selectType) {
        if (songInfoData.songUrls == null
                || songInfoData.songUrls.urls == null
                || songInfoData.songUrls.urls.size() == 0) {
            return null;
        }
        SongInfoData.Songurl.Url max = null;
        SongInfoData.Songurl.Url min = null;
        for (int i = 1; i < songInfoData.songUrls.urls.size(); i++) {
            SongInfoData.Songurl.Url url = songInfoData.songUrls.urls.get(i);
            if (TextUtils.isEmpty(url.showLink)) {
                continue;
            } else if (max == null) {
                min = max = url;
                continue;
            }
            if (url.fileBitrate > max.fileBitrate) {
                max = url;
            }
            if (url.fileBitrate < min.fileBitrate) {
                min = url;
            }
        }
        if (selectType == SELECT_AUTO) {
            int netType = NetUtil.getNetType(context);
            if (netType == ConnectivityManager.TYPE_MOBILE) {
                return min;
            } else {
                return max;
            }
        } else if (selectType == SELECT_HIGH) {
            return max;
        } else {
            return min;
        }
    }
}
