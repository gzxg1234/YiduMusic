package com.sanron.yidumusic.ui.fragment.my_music;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sanron.yidumusic.ui.base.ViewPageFragment;

/**
 * Created by sanron on 16-7-13.
 */
public class MyMusicFragment extends ViewPageFragment {


    private static final String[] TITLES = {
            "我的歌单", "本地音乐"
    };

    private static final String[] FRAGMENTS = {
            PlayListFragment.class.getName(), LocalMusicFragment.class.getName()
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitles(TITLES);
        setFragments(FRAGMENTS);
    }
}
