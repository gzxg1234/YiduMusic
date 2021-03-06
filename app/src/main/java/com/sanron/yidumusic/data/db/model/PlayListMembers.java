package com.sanron.yidumusic.data.db.model;

import com.raizlabs.android.dbflow.annotation.Column;
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

    @Column
    long addTime;

    @Column
    boolean isLocal;

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

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
