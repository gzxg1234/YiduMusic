package com.sanron.yidumusic.data.db.bean;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.sanron.yidumusic.data.db.YiduDB;

/**
 * Created by sanron on 16-7-18.
 */
@Table(name = "music_info", database = YiduDB.class)
public class MusicInfo extends Base {

    @Column
    private String name;

    @Column
    private String title;

    @Column
    private String album;

    @Column
    private String artist;

    @Column
    private String path;

    @Column
    private long lastModifyTime;

    @Column
    private int duration;

    @Column
    private String songId;

    @Column
    private int bitrate;

    public static final String UNKNOWN = "<unknown>";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }


//    public static final String TABLE = "music_info";
//    public static final String COL_NAME = "name";
//    public static final String COL_TITLE = "title";
//    public static final String COL_TITLE_KEY = "title_key";
//    public static final String COL_ALBUM = "album";
//    public static final String COL_ARTIST = "artist";
//    public static final String COL_LAST_MODIFY = "last_modify";
//    public static final String COL_PATH = "path";
//    public static final String COL_DURATION = "duration";
//    public static final String COL_SONG_ID = "song_id";
//    public static final String COL_BITRATE = "bitrate";

//
//    public static final Func1<Cursor, MusicInfo> MAPER = new Func1<Cursor, MusicInfo>() {
//        @Override
//        public MusicInfo call(Cursor cursor) {
//            MusicInfo musicInfo = new MusicInfo();
//            musicInfo.setId(Cur.getLong(cursor, MusicInfo.COL_ID));
//            musicInfo.setTitle(Cur.getString(cursor, MusicInfo.COL_TITLE));
//            musicInfo.setBitrate(Cur.getInt(cursor, MusicInfo.COL_BITRATE));
//            musicInfo.setAlbum(Cur.getString(cursor, MusicInfo.COL_ALBUM));
//            musicInfo.setTitleKey(Cur.getString(cursor, MusicInfo.COL_TITLE_KEY));
//            musicInfo.setArtist(Cur.getString(cursor, MusicInfo.COL_ARTIST));
//            musicInfo.setPath(Cur.getString(cursor, MusicInfo.COL_PATH));
//            musicInfo.setSongId(Cur.getString(cursor, MusicInfo.COL_SONG_ID));
//            musicInfo.setLastModifyTime(Cur.getLong(cursor, MusicInfo.COL_LAST_MODIFY));
//            musicInfo.setDuration(Cur.getInt(cursor, MusicInfo.COL_DURATION));
//            musicInfo.setBitrate(Cur.getInt(cursor, MusicInfo.COL_BITRATE));
//            return musicInfo;
//        }
//    };
//
//    public static ContentValues toContentValues(MusicInfo musicInfo) {
//        ContentValues values = new ContentValues();
//        values.put(MusicInfo.COL_ID, musicInfo.getId());
//        values.put(MusicInfo.COL_TITLE_KEY, musicInfo.getTitleKey());
//        values.put(MusicInfo.COL_LAST_MODIFY, musicInfo.getLastModifyTime());
//        values.put(MusicInfo.COL_PATH, musicInfo.getPath());
//        values.put(MusicInfo.COL_SONG_ID, musicInfo.getSongId());
//        values.put(MusicInfo.COL_NAME, musicInfo.getName());
//        values.put(MusicInfo.COL_DURATION, musicInfo.getDuration());
//        values.put(MusicInfo.COL_ARTIST, musicInfo.getArtist());
//        values.put(MusicInfo.COL_ALBUM, musicInfo.getAlbum());
//        values.put(MusicInfo.COL_TITLE, musicInfo.getTitle());
//        values.put(MusicInfo.COL_BITRATE, musicInfo.getBitrate());
//        return values;
//    }
}
