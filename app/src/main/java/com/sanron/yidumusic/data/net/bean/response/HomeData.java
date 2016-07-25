package com.sanron.yidumusic.data.net.bean.response;

import com.sanron.yidumusic.data.net.bean.Album;
import com.sanron.yidumusic.data.net.bean.FocusPic;
import com.sanron.yidumusic.data.net.bean.Gedan;
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.List;

/**
 * Created by sanron on 16-7-14.
 */
public class HomeData {
    public List<FocusPic> mFocusPicDatas;
    public List<Gedan> hotGedans;
    public List<Album> recmdAlbums;
    public List<SongInfo> mRecmdSongInfos;
}
