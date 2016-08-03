package com.sanron.yidumusic.playback;

/**
 * Created by sanron on 16-7-20.
 */
public interface PlayTrack {

    int SOURCE_LOCAL = 1;
    int SOURCE_WEB = 2;

    long getSongId();

    long getLocalId();

    String getTitle();


    String getAlbum();


    String getArtist();


    String getPath();

    int getDuration();

    int getSourceType();

}
