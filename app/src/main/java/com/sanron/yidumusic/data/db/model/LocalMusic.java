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
public class LocalMusic extends Base {

    @Column
    long addTime;
    @Column
    long lastModifyTime;
    @Column
    boolean isDeleted;

    @Column
    long songId;
    @ForeignKey
    MusicInfo musicInfo;

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


    public PlayTrack toPlayTrack() {
        PlayTrack playTrack = new PlayTrack();
        playTrack.setPath(musicInfo.getPath());
        playTrack.setTitle(musicInfo.getTitle());
        playTrack.setAlbum(musicInfo.getAlbum());
        playTrack.setDuration(musicInfo.getDuration());
        playTrack.setArtist(musicInfo.getArtist());
        playTrack.setPlayType(PlayTrack.SOURCE_LOCAL);
        return playTrack;
    }
}
