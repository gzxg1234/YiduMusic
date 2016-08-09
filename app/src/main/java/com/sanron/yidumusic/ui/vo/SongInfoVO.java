package com.sanron.yidumusic.ui.vo;

import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.playback.PlayTrack;

/**
 * Created by sanron on 16-8-3.
 */
public class SongInfoVO {

    private SongInfo mSongInfo;

    private LocalMusic mMatchLocalMusic;

    public SongInfo getSongInfo() {
        return mSongInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        mSongInfo = songInfo;
    }

    public LocalMusic getMatchLocalMusic() {
        return mMatchLocalMusic;
    }

    public void setMatchLocalMusic(LocalMusic matchLocalMusic) {
        mMatchLocalMusic = matchLocalMusic;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof PlayTrack) {
            long songId = ((PlayTrack) o).getSongId();
            if (songId == mSongInfo.songId) {
                return true;
            }
        }
        return super.equals(o);
    }
}
