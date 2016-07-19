package com.sanron.yidumusic.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sanron.yidumusic.R;

import butterknife.BindView;

/**
 * Created by sanron on 16-7-18.
 */
public class ViewPageFragment extends BaseFragment {

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String[] mTitles;
    private String[] mFragments;
    private String mTitle;

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

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("titles", mTitles);
        outState.putStringArray("fragments", mFragments);
        outState.putString("title", mTitle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTitles = savedInstanceState.getStringArray("titles");
            mFragments = savedInstanceState.getStringArray("fragments");
            mTitle = savedInstanceState.getString("title");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationClickCallback) {
            mNavigationClickCallback = (NavigationClickCallback) activity;
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mToolbar.setTitle(mTitle);
        mViewPager.setAdapter(new LocalPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNavigationClickCallback != null) {
                    mNavigationClickCallback.onNavigationClick(v);
                }
            }
        });
    }

    public interface NavigationClickCallback {
        void onNavigationClick(View v);
    }

    private NavigationClickCallback mNavigationClickCallback;


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
