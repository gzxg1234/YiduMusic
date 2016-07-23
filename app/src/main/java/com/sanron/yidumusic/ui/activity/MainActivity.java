package com.sanron.yidumusic.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.BaseActivity;
import com.sanron.yidumusic.ui.base.ViewPageFragment;
import com.sanron.yidumusic.ui.fragment.now_playing.NowPlayingFragment;
import com.sanron.yidumusic.ui.fragment.music_bank.MusicBankFragment;
import com.sanron.yidumusic.ui.fragment.my_music.MyMusicFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPageFragment.NavigationClickCallback {
    @BindView(R.id.sliding_panel) SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.linear_layout) LinearLayout mLinearLayout;

    private NowPlayingFragment mNowPlayingFragment;
    private int mCurrentPage = -1;
    private static final String[] PAGES = new String[]{
            "my_music", "music_bank"
    };
    private static final String[] FRAGMENTS = new String[]{
            MyMusicFragment.class.getName(),
            MusicBankFragment.class.getName()
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        int page = 0;
        if (savedInstanceState != null) {
            page = savedInstanceState.getInt("CurrentPage", 0);
        }
        switchFragment(page);
        mNowPlayingFragment = (NowPlayingFragment) getSupportFragmentManager().findFragmentByTag(NowPlayingFragment.class.getName());
        if (mNowPlayingFragment == null) {
            mNowPlayingFragment = new NowPlayingFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_fragment_container, mNowPlayingFragment, NowPlayingFragment.class.getName())
                    .commit();
        }
    }


    private void initView() {
        setupStatusTintView();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void switchFragment(int p) {
        if (p == mCurrentPage) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment toFragment;
        //隐藏其他fragment
        for (int i = 0; i < PAGES.length; i++) {
            if (i != p) {
                Fragment f = fm.findFragmentByTag(PAGES[i]);
                if (f != null) {
                    ft.hide(f);
                }
            }
        }


        toFragment = fm.findFragmentByTag(PAGES[p]);
        if (toFragment == null) {
            toFragment = Fragment.instantiate(this, FRAGMENTS[p]);
            ft.add(R.id.fragment_container_1, toFragment, PAGES[p]);
        } else {
            ft.show(toFragment);
        }
        ft.commit();
        mCurrentPage = p;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentPage", mCurrentPage);
    }

    private void setupStatusTintView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            int statusBarHeight = systemBarTintManager.getConfig().getPixelInsetTop(false);
            View tintView = new View(this);
            tintView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    statusBarHeight));
            tintView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mLinearLayout.addView(tintView, 0);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my_music: {
                switchFragment(0);
            }
            break;
            case R.id.menu_web_music: {
                switchFragment(1);
            }
            break;
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationClick(View v) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }
}
