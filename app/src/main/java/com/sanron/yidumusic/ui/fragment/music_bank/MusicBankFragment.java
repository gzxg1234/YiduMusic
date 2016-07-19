package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.ViewPageFragment;

import butterknife.BindView;

/**
 * Created by sanron on 16-7-13.
 */
public class MusicBankFragment extends ViewPageFragment {

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private static final String[] TITLES = {
            "推荐", "排行", "歌单", "电台", "MV"
    };

    private static final String[] FRAGMENTS = {
            RecmdFragment.class.getName(),
            BillFragment.class.getName(),
            GedanFragment.class.getName(),
            RadioFragment.class.getName(),
            MVFragment.class.getName()
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitles(TITLES);
        setFragments(FRAGMENTS);
        setTitle("音乐库");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "搜索")
                .setIcon(R.mipmap.ic_search_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
}
