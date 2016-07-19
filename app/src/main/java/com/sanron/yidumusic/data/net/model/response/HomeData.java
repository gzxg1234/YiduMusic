package com.sanron.yidumusic.data.net.model.response;

import com.sanron.yidumusic.data.net.model.Album;
import com.sanron.yidumusic.data.net.model.FocusPic;
import com.sanron.yidumusic.data.net.model.Gedan;
import com.sanron.yidumusic.data.net.model.Song;

import java.util.List;

/**
 * Created by sanron on 16-7-14.
 */
public class HomeData {
    public List<FocusPic> mFocusPicDatas;
    public List<Gedan> hotGedans;
    public List<Album> recmdAlbums;
    public List<Song> recmdSongs;
}
