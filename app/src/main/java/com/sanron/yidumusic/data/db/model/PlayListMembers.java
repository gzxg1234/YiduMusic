package com.sanron.yidumusic.data.db.model;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.Table;
import com.sanron.yidumusic.data.db.YiduDB;

/**
 * Created by sanron on 16-7-23.
 */
@Table(
        name = "playlist_members",
        database = YiduDB.class
)
public class PlayListMembers extends Base {

    @ForeignKey(
            tableClass = PlayList.class,
            onDelete = ForeignKeyAction.CASCADE
    )
    PlayList playList;

    @ForeignKey(
            tableClass = MusicInfo.class
    )
    MusicInfo musicInfo;

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    public MusicInfo getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(MusicInfo musicInfo) {
        this.musicInfo = musicInfo;
    }
}
