package com.sanron.yidumusic.data.db.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.playback.PlayTrack;

/**
 * Created by sanron on 16-7-18.
 */

@Table(
        name = "local_music",
        database = YiduDB.class
)
public class LocalMusic extends Base implements PlayTrack {

    @Column
    private long addTime;
    @Column
    private long lastModifyTime;
    @Column
    private boolean isDeleted;

    @Column
    private long songId;
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

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getSongId() {
        return songId;
    }

    @Override
    public long getLocalId() {
        return getId();
    }

    @Override
    public String getTitle() {
        return musicInfo.getTitle();
    }

    @Override
    public String getAlbum() {
        return musicInfo.getAlbum();
    }

    @Override
    public String getArtist() {
        return musicInfo.getArtist();
    }

    @Override
    public String getPath() {
        return musicInfo.getPath();
    }

    @Override
    public int getDuration() {
        return musicInfo.getDuration();
    }

    @Override
    public int getSourceType() {
        return PlayTrack.SOURCE_LOCAL;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof PlayTrack) {
            long id = ((PlayTrack) o).getLocalId();
            if (id == getId()) {
                return true;
            }
        }
        return super.equals(o);
    }
}
