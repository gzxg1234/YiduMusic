package com.sanron.yidumusic.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sanron.yidumusic.R;

import butterknife.BindView;

/**
 * Created by sanron on 16-7-18.
 */
public class ViewPageFragment extends BaseFragment {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String[] mTitles;
    private String[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.fragment_pager;
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
    }

    public void setFragments(String[] fragments) {
        mFragments = fragments;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("titles", mTitles);
        outState.putStringArray("fragments", mFragments);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTitles = savedInstanceState.getStringArray("titles");
            mFragments = savedInstanceState.getStringArray("fragments");
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mViewPager.setAdapter(new LocalPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    private class LocalPagerAdapter extends FragmentPagerAdapter {

        public LocalPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(getContext(), mFragments[position]);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
