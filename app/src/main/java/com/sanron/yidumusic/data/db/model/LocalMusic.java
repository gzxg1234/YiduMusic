package com.sanron.yidumusic.data.db.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.sanron.yidumusic.data.db.YiduDB;

/**
 * Created by sanron on 16-7-18.
 */

@Table(
        name = "local_music",
        database = YiduDB.class
)
public class LocalMusic extends Base {

    @Column
    private long addTime;
    @Column
    private long lastModifyTime;
    @Column
    private boolean isDeleted;
    @ForeignKey
    private MusicInfo musicInfo;

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public MusicInfo getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(MusicInfo musicInfo) {
        this.musicInfo = musicInfo;
    }

    public static final String TABLE = "local_music";
    public static final String COL_MUSIC_ID = "music_id";
    public static final String COL_ADD_TIME = "add_time";
    public static final String COL_LAST_MODIFY = "last_modify";
    public static final String COL_IS_DELETED = "is_deleted";

}
