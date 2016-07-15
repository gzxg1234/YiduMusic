package com.sanron.yidumusic.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.BaseActivity;
import com.sanron.yidumusic.ui.fragment.music_bank.MusicBankFragment;
import com.sanron.yidumusic.util.StatusBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.sliding_panel) SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.linear_layout) LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupTintView();
        MusicBankFragment fragment = MusicBankFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_1, fragment, "music_bank")
                .commit();
    }

    private void setupTintView() {
        if (Build.VERSION.SDK_INT > 19) {
            View tintView = new View(this);
            tintView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    StatusBarUtil.getStatusBarHeight()));
            tintView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mLinearLayout.addView(tintView, 0);
        }
    }
}
