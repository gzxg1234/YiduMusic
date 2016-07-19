package com.sanron.yidumusic.ui.fragment.my_music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.ViewPageFragment;

import butterknife.BindView;

/**
 * Created by sanron on 16-7-13.
 */
public class MyMusicFragment extends ViewPageFragment {

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private static final String[] TITLES = {
            "本地音乐"
    };

    private static final String[] FRAGMENTS = {
            LocalMusicFragment.class.getName()
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的音乐");
        setTitles(TITLES);
        setFragments(FRAGMENTS);
    }

}
