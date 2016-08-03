package com.sanron.yidumusic.ui.vo;

import com.sanron.yidumusic.data.db.model.PlayList;

/**
 * Created by sanron on 16-7-23.
 */
public class PlayListVO {

    private PlayList mPlayList;

    private long mMusicCount;

    public PlayList getPlayList() {
        return mPlayList;
    }

    public void setPlayList(PlayList playList) {
        mPlayList = playList;
    }

    public long getMusicCount() {
        return mMusicCount;
    }

    public void setMusicCount(long musicCount) {
        mMusicCount = musicCount;
    }
}
