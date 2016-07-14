package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by sanron on 16-7-13.
 */
public class MusicBankFragment extends BaseFragment {

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private static final String[] TITLES = {
            "推荐"
    };

    public static MusicBankFragment newInstance() {
        return new MusicBankFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_music_bank;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mToolbar.setTitle("音乐库");
        mViewPager.setAdapter(new LocalPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    private class LocalPagerAdapter extends FragmentPagerAdapter {

        public LocalPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RecmdFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}
