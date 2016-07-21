package com.sanron.yidumusic.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sanron on 16-7-21.
 */
public class LocalMusicConfig {

    private SharedPreferences mSharedPreferences;


    public static final String SORT_TYPE = "sort_type";
    public static final int SORT_BY_TITLE = 0;
    public static final int SORT_BY_ADD_TIME = 1;
    public static final int SORT_BY_ARTIST = 2;
    public static final int SORT_BY_ALBUM = 3;

    public LocalMusicConfig(Context context) {
        mSharedPreferences = context.getSharedPreferences("local_music", Context.MODE_PRIVATE);
    }


    public int getSortType() {
        return mSharedPreferences.getInt(SORT_TYPE, SORT_BY_TITLE);
    }

    public void setSortType(int type) {
        mSharedPreferences.edit()
                .putInt(SORT_TYPE, type)
                .apply();
    }
}
