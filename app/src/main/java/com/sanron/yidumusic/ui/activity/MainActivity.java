package com.sanron.yidumusic.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.BaseActivity;
import com.sanron.yidumusic.ui.fragment.AlbumDetailFragment;
import com.sanron.yidumusic.ui.fragment.AllTagFragment;
import com.sanron.yidumusic.ui.fragment.GedanDetailFragment;
import com.sanron.yidumusic.ui.fragment.OfficialGedanDetailFragment;
import com.sanron.yidumusic.ui.fragment.SingerCategoryFragment;
import com.sanron.yidumusic.ui.fragment.SingerListFragment;
import com.sanron.yidumusic.ui.fragment.music_bank.MusicBankFragment;
import com.sanron.yidumusic.ui.fragment.my_music.MyMusicFragment;
import com.sanron.yidumusic.ui.fragment.now_playing.NowPlayingFragment;
import com.sanron.yidumusic.util.StatusBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.sliding_panel) SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.linear_layout) LinearLayout mLinearLayout;
    @BindView(R.id.tool_bar) Toolbar mToolbar;

    private FragmentManager mFm;
    private NowPlayingFragment mNowPlayingFragment;
    private int mCurrentPage = -1;
    private static final String[] PAGES = new String[]{
            "my_music", "music_bank"
    };
    private static final String[] TITLES = new String[]{
            "我的音乐", "音乐库"
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

        mFm = getSupportFragmentManager();
        int page = 0;
        if (savedInstanceState != null) {
            page = savedInstanceState.getInt("CurrentPage", 0);
        }
        switchFragment(page);
        mNowPlayingFragment = (NowPlayingFragment) mFm.findFragmentByTag(NowPlayingFragment.class.getName());
        if (mNowPlayingFragment == null) {
            mNowPlayingFragment = new NowPlayingFragment();
            mFm.beginTransaction()
                    .add(R.id.player_fragment_container, mNowPlayingFragment, NowPlayingFragment.class.getName())
                    .commit();
        }
    }

    private void initView() {
        setupStatusTintView();
        mNavigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED
                        && newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    mNowPlayingFragment.setExpanded(true);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mNowPlayingFragment.setExpanded(false);
                    mSlidingUpPanelLayout.setSlideViewClickable(true);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mSlidingUpPanelLayout.setSlideViewClickable(false);
                }
                setupDrawerLayoutLock();
            }
        });
        getSupportFragmentManager()
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        setupDrawerLayoutLock();
                    }
                });
    }

    private void setupDrawerLayoutLock() {
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                || mFm.getBackStackEntryCount() > 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void switchFragment(int p) {
        if (p == mCurrentPage) {
            return;
        }
        FragmentTransaction ft = mFm.beginTransaction();
        Fragment toFragment;
        //隐藏其他fragment
        for (int i = 0; i < PAGES.length; i++) {
            if (i != p) {
                Fragment f = mFm.findFragmentByTag(PAGES[i]);
                if (f != null) {
                    ft.hide(f);
                }
            }
        }

        toFragment = mFm.findFragmentByTag(PAGES[p]);
        if (toFragment == null) {
            toFragment = Fragment.instantiate(this, FRAGMENTS[p]);
            ft.add(R.id.fragment_container_1, toFragment, PAGES[p]);
        } else {
            ft.show(toFragment);
        }
        ft.commit();
        mCurrentPage = p;
        mToolbar.setTitle(TITLES[mCurrentPage]);
    }

    public void collapsePanel() {
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void setupStatusTintView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
//            mSystemBarConfig = systemBarTintManager.getConfig();
//            int statusBarHeight = mSystemBarConfig.getPixelInsetTop(false);
            View tintView = new View(this);
            tintView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    StatusBarUtil.getStatusBarHeight()));
            tintView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mLinearLayout.addView(tintView, 0);
        }
    }

    public void showAlbumInfo(long albumId) {
        addFragmentToFront(AlbumDetailFragment.newInstance(albumId));
    }

    public void showAllTag() {
        addFragmentToFront(AllTagFragment.newInstance());
    }

    public void showGedanDetail(long listid) {
        addFragmentToFront(GedanDetailFragment.newInstance(listid));
    }

    public void showSingerList(String title, int area, int sex) {
        addFragmentToFront(SingerListFragment.newInstance(title, area, sex));
    }

    public void showOfficialGedanDetail(String code) {
        addFragmentToFront(OfficialGedanDetailFragment.newInstance(code));
    }

    public void showSingerCategory() {
        addFragmentToFront(SingerCategoryFragment.newInstance());
    }

    public void addFragmentToFront(Fragment fragment) {
        mFm.beginTransaction()
                .add(R.id.fragment_front, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my_music: {
                invalidateOptionsMenu();
                switchFragment(0);
            }
            break;
            case R.id.menu_web_music: {
                invalidateOptionsMenu();
                switchFragment(1);
            }
            break;
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentPage", mCurrentPage);
    }

}
