package com.sanron.yidumusic.data.model.response;

import com.sanron.yidumusic.data.model.Album;
import com.sanron.yidumusic.data.model.FocusPic;
import com.sanron.yidumusic.data.model.Gedan;
import com.sanron.yidumusic.data.model.Song;

import java.util.List;

/**
 * Created by sanron on 16-7-14.
 */
public class RecmdData {
    public List<FocusPic> mFocusPicDatas;
    public List<Gedan> hotGedans;
    public List<Album> recmdAlbums;
    public List<Song> recmdSongs;
}
