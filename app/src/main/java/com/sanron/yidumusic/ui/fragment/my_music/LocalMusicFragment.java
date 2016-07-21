package com.sanron.yidumusic.ui.fragment.my_music;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.raizlabs.android.dbflow.structure.Model;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.config.LocalMusicConfig;
import com.sanron.yidumusic.data.db.DBObserver;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.bean.LocalMusic;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.activity.ScanMusicActivity;
import com.sanron.yidumusic.ui.adapter.LocalMusicAdapter;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.widget.IndexBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2015/12/21.
 */
public class LocalMusicFragment extends LazyLoadFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.index_bar)
    IndexBar mIndexBar;
    @BindView(R.id.tv_index_indicator)
    TextView mTvIndicator;

    private DataRepository mDataRepository;
    private LocalMusicAdapter mLocalMusicAdapter;
    private Subscription mTableWatcher;
    private LocalMusicConfig mLocalMusicConfig;
    private int mCurrentSortType = -1;
    private Comparator<LocalMusic> mSortCompator;

    @Override
    protected void onLazyLoad() {
        loadData();
    }

    private Comparator<String> mPinyinComparator = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            if (TextUtils.isEmpty(lhs)) {
                return -1;
            }
            if (TextUtils.isEmpty(rhs)) {
                return 1;
            }
            char lc = PinyinHelper.convertToPinyinString(lhs.substring(0, 1), "", PinyinFormat.WITHOUT_TONE)
                    .toUpperCase().charAt(0);
            char rc = PinyinHelper.convertToPinyinString(rhs.substring(0, 1), "", PinyinFormat.WITHOUT_TONE)
                    .toUpperCase().charAt(0);
            if (lc < 'A' || lc > 'Z') {
                return 1;
            }
            if (rc < 'A' || rc > 'Z') {
                return -1;
            }
            return lc - rc;
        }
    };

    private Comparator<LocalMusic> mTitleComparator = new Comparator<LocalMusic>() {
        @Override
        public int compare(LocalMusic lhs, LocalMusic rhs) {
            String lt = lhs.getMusicInfo().getTitle();
            String rt = rhs.getMusicInfo().getTitle();
            return mPinyinComparator.compare(lt, rt);
        }
    };


    private Comparator<LocalMusic> mArtistComparator = new Comparator<LocalMusic>() {
        @Override
        public int compare(LocalMusic lhs, LocalMusic rhs) {
            String lt = lhs.getMusicInfo().getArtist();
            String rt = rhs.getMusicInfo().getArtist();
            return mPinyinComparator.compare(lt, rt);
        }
    };

    private Comparator<LocalMusic> mAlbumComparator = new Comparator<LocalMusic>() {
        @Override
        public int compare(LocalMusic lhs, LocalMusic rhs) {
            String lt = lhs.getMusicInfo().getAlbum();
            String rt = rhs.getMusicInfo().getAlbum();
            return mPinyinComparator.compare(lt, rt);
        }
    };

    private Comparator<LocalMusic> mTimeComparator = new Comparator<LocalMusic>() {
        @Override
        public int compare(LocalMusic lhs, LocalMusic rhs) {
            return (int) (lhs.getAddTime() - rhs.getAddTime());
        }
    };


    private void setSortType(int sortType) {
        if (sortType == mCurrentSortType) {
            return;
        }
        mLocalMusicConfig.setSortType(sortType);
        mCurrentSortType = sortType;
        configSortType();
        loadData();
    }

    private void configSortType() {
        if (mCurrentSortType == LocalMusicConfig.SORT_BY_ADD_TIME) {
            mIndexBar.detach();
            mIndexBar.setIndicator(null);
        } else {
            mIndexBar.attach(mRecyclerView);
            mIndexBar.setIndicator(mTvIndicator);
        }

        if (mCurrentSortType == LocalMusicConfig.SORT_BY_ADD_TIME) {
            mSortCompator = mTimeComparator;
        } else if (mCurrentSortType == LocalMusicConfig.SORT_BY_ALBUM) {
            mSortCompator = mAlbumComparator;
        } else if (mCurrentSortType == LocalMusicConfig.SORT_BY_ARTIST) {
            mSortCompator = mArtistComparator;
        } else {
            mSortCompator = mTitleComparator;
        }
        mLocalMusicAdapter.setSortType(mCurrentSortType);
    }

    private void loadData() {
        addSub(YiduDB.getLocalMusic()
                .map(new Func1<List<LocalMusic>, List<LocalMusic>>() {
                    @Override
                    public List<LocalMusic> call(List<LocalMusic> localMusics) {
                        Collections.sort(localMusics, mSortCompator);
                        return localMusics;
                    }
                })
                .compose(TransformerUtil.<List<LocalMusic>>io())
                .subscribe(new Action1<List<LocalMusic>>() {
                    @Override
                    public void call(List<LocalMusic> localMusics) {
                        mLocalMusicAdapter.setData(localMusics);
                    }
                })
        );
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLocalMusicAdapter);
        mCurrentSortType = mLocalMusicConfig.getSortType();
        configSortType();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLocalMusicConfig = new LocalMusicConfig(getContext());
        mCurrentSortType = mLocalMusicConfig.getSortType();
        mDataRepository = YiduApp.get().getDataRepository();
        mLocalMusicAdapter = new LocalMusicAdapter(getContext(), mDataRepository);
        mLocalMusicAdapter.setSortType(mCurrentSortType);
        mTableWatcher = DBObserver.get()
                .toObservable(LocalMusic.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Class<? extends Model>>() {
                    @Override
                    public void call(Class<? extends Model> aClass) {
                        System.out.println("load");
                        loadData();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTableWatcher.unsubscribe();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_local_music;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.local_music_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_title: {
                setSortType(LocalMusicConfig.SORT_BY_TITLE);
            }
            break;

            case R.id.menu_sort_by_add_time: {
                setSortType(LocalMusicConfig.SORT_BY_ADD_TIME);

            }
            break;

            case R.id.menu_sort_by_album: {
                setSortType(LocalMusicConfig.SORT_BY_ALBUM);

            }
            break;

            case R.id.menu_sort_by_artist: {
                setSortType(LocalMusicConfig.SORT_BY_ARTIST);
            }
            break;

            case R.id.menu_scan: {
                gotoScanActivity();
            }
            break;
        }
        return true;
    }

    private void gotoScanActivity() {
        Intent intent = new Intent(getContext(), ScanMusicActivity.class);
        startActivity(intent);
    }

}
