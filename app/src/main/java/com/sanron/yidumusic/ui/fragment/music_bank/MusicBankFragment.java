package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.ViewPageFragment;

/**
 * Created by sanron on 16-7-13.
 */
public class MusicBankFragment extends ViewPageFragment {

    private static final String[] TITLES = {
            "推荐", "排行", "歌单", "电台", "MV"
    };

    private static final String[] FRAGMENTS = {
            RecmdFragment.class.getName(),
            BillFragment.class.getName(),
            GedanListFragment.class.getName(),
            RadioFragment.class.getName(),
            MVFragment.class.getName()
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitles(TITLES);
        setFragments(FRAGMENTS);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "搜索")
                .setIcon(R.mipmap.ic_search_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
}
