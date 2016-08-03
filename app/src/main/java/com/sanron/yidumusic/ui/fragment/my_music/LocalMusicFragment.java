package com.sanron.yidumusic.ui.fragment.my_music;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.raizlabs.android.dbflow.structure.Model;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.config.LocalMusicConfig;
import com.sanron.yidumusic.data.db.DBObserver;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.activity.ScanMusicActivity;
import com.sanron.yidumusic.ui.adapter.LocalMusicAdapter;
import com.sanron.yidumusic.ui.base.BackPressHandler;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.ui.dialog.SelectPlayListDlg;
import com.sanron.yidumusic.widget.IndexBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2015/12/21.
 */
public class LocalMusicFragment extends LazyLoadFragment implements BackPressHandler, LocalMusicAdapter.OnItemClickListener, LocalMusicAdapter.OnItemActionClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.index_bar)
    IndexBar mIndexBar;
    @BindView(R.id.tv_index_indicator)
    TextView mTvIndicator;
    @BindView(R.id.normal_bar)
    View mNormalBar;
    @BindView(R.id.tv_play_all)
    TextView mPlayAll;
    @BindView(R.id.multi_bar)
    View mMultiBar;
    @BindView(R.id.cb_check_all)
    CheckBox mCbCheckAll;
    @BindView(R.id.tv_checked_count)
    TextView mTvCheckedCount;

    private DataRepository mDataRepository;
    private LocalMusicAdapter mLocalMusicAdapter;
    private Subscription mTableWatcher;
    private LocalMusicConfig mLocalMusicConfig;
    private int mCurrentSortType = -1;
    private Comparator<LocalMusic> mSortCompator;
    private ActionWindow mActionWindow;

    @Override
    protected void onLazyLoad() {
        loadData();
    }

    private Comparator<String> mPinyinComparator = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            lhs = TextUtils.isEmpty(lhs) ? " " : lhs;
            rhs = TextUtils.isEmpty(rhs) ? " " : rhs;
            if (lhs.equals(rhs)) {
                return 0;
            }
            char lc = PinyinHelper.convertToPinyinString(lhs.substring(0, 1), "", PinyinFormat.WITHOUT_TONE)
                    .toUpperCase().charAt(0);
            char rc = PinyinHelper.convertToPinyinString(rhs.substring(0, 1), "", PinyinFormat.WITHOUT_TONE)
                    .toUpperCase().charAt(0);
            if (lc < 'A' || lc > 'Z') {
                return 1;
            } else if (rc < 'A' || rc > 'z') {
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
            lt = MusicInfo.UNKNOWN.endsWith(lt) ? "" : lt;
            rt = MusicInfo.UNKNOWN.endsWith(rt) ? "" : rt;
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

    @Override
    protected int getLayout() {
        return R.layout.fragment_local_music;
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
                        loadData();
                    }
                });
        mLocalMusicAdapter.setMultiModeCallback(new LocalMusicAdapter.MultiModeCallback() {
            @Override
            public void onItemChecked(int position, boolean checked) {
                mTvCheckedCount.setText("已选择" + mLocalMusicAdapter.getCheckedItemCount() + "项");
                if (mLocalMusicAdapter.getCheckedItemCount() == mLocalMusicAdapter.getItemCount()) {
                    mCbCheckAll.setChecked(true);
                } else {
                    mCbCheckAll.setChecked(false);
                }
            }

            @Override
            public void onStart() {
                showActionWindow();
                setMultiBarVisiable(true);
            }
        });
        mLocalMusicAdapter.setOnItemClickListener(this);
        mLocalMusicAdapter.setOnItemActionClickListener(this);
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLocalMusicAdapter);
        mTvCheckedCount.setText(getString(R.string.play_all, mLocalMusicAdapter.getItemCount()));
        mCurrentSortType = mLocalMusicConfig.getSortType();
        configSortType();
        getBaseActivity().addBackPressHandler(this);
    }

    @Override
    public void onDestroyView() {
        endMultiMode();
        super.onDestroyView();
    }

    private void endMultiMode() {
        if (mLocalMusicAdapter != null) {
            mLocalMusicAdapter.setMultiMode(false);
            setMultiBarVisiable(false);
            if (mActionWindow != null) {
                mActionWindow.dismiss();
                mActionWindow = null;
            }
        }
    }


    @Override
    public void onDestroy() {
        mTableWatcher.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        clearSortMenuIcon(menu);
        int checkedMenuItem;
        if (mCurrentSortType == LocalMusicConfig.SORT_BY_ADD_TIME) {
            checkedMenuItem = R.id.menu_sort_by_add_time;
        } else if (mCurrentSortType == LocalMusicConfig.SORT_BY_ALBUM) {
            checkedMenuItem = R.id.menu_sort_by_album;
        } else if (mCurrentSortType == LocalMusicConfig.SORT_BY_ARTIST) {
            checkedMenuItem = R.id.menu_sort_by_artist;
        } else {
            checkedMenuItem = R.id.menu_sort_by_title;
        }
        menu.findItem(checkedMenuItem).setIcon(R.mipmap.ic_check_black_24dp);
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

    @OnCheckedChanged(R.id.cb_check_all)
    void onCheckallChanged(CompoundButton compoundButton, boolean checked) {
        if (checked
                || mLocalMusicAdapter.getCheckedItemCount() != mLocalMusicAdapter.getItemCount() - 1) {
            for (int i = 0; i < mLocalMusicAdapter.getItemCount(); i++) {
                mLocalMusicAdapter.setItemChecked(i, checked);
            }
        }
    }

    @Override
    public boolean onBackPress() {
        if (mLocalMusicAdapter.isMultiMode()) {
            endMultiMode();
            return true;
        }
        return false;
    }

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
        mLocalMusicAdapter.setSortType(mCurrentSortType);
        if (mCurrentSortType == LocalMusicConfig.SORT_BY_ADD_TIME) {
            mIndexBar.detach();
            mIndexBar.setIndicator(null);
        } else if (!mIndexBar.isAttach()) {
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
                .subscribe(new SubscriberAdapter<List<LocalMusic>>() {
                    @Override
                    public void onNext(List<LocalMusic> localMusics) {
                        mLocalMusicAdapter.setData(localMusics);
                        mPlayAll.setText(getString(R.string.play_all, localMusics.size()));
                    }
                })
        );
    }

    private void setMultiBarVisiable(boolean visiable) {
        mNormalBar.setVisibility(visiable ? View.INVISIBLE : View.VISIBLE);
        mMultiBar.setVisibility(!visiable ? View.INVISIBLE : View.VISIBLE);
    }

    private void showActionWindow() {
        if (mActionWindow == null) {
            mActionWindow = new ActionWindow(getContext());
        }
        mActionWindow.show();
    }


    private void clearSortMenuIcon(Menu menu) {
        menu.findItem(R.id.menu_sort_by_artist).setIcon(null);
        menu.findItem(R.id.menu_sort_by_album).setIcon(null);
        menu.findItem(R.id.menu_sort_by_add_time).setIcon(null);
        menu.findItem(R.id.menu_sort_by_title).setIcon(null);
    }

    private void gotoScanActivity() {
        Intent intent = new Intent(getContext(), ScanMusicActivity.class);
        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            endMultiMode();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        PlayUtil.clearQueue();
        PlayUtil.enqueue(mLocalMusicAdapter.getData());
        PlayUtil.play(position);
    }

    @Override
    public void onItemActionClick(View view, int position) {
        final LocalMusic localMusic = mLocalMusicAdapter.getItem(position);
        final List<LocalMusic> localMusics = new ArrayList<>();
        localMusics.add(localMusic);
        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_add_to_playlist: {
                        addToPlayList(localMusics);
                    }
                    break;

                    case R.id.menu_delete: {

                    }
                    break;
                }
                return false;
            }
        };
        BottomSheet bottomSheet = new BottomSheet.Builder(getActivity())
                .sheet(R.menu.local_music_action_menu)
                .title("歌曲:" + localMusic.getMusicInfo().getTitle())
                .listener(onMenuItemClickListener)
                .build();
        //设置菜单图标黑色
        int size = bottomSheet.getMenu().size();
        for (int i = 0; i < size; i++) {
            Drawable icon = bottomSheet.getMenu().getItem(i).getIcon();
            DrawableCompat.setTint(icon, Color.BLACK);
        }
        bottomSheet.show();
    }

    private void addToPlayList(List<LocalMusic> localMusics) {
        final List<MusicInfo> musicInfos = new ArrayList<>();
        for (LocalMusic localMusic : localMusics) {
            musicInfos.add(localMusic.getMusicInfo());
        }
        SelectPlayListDlg.show(getContext(), musicInfos);
    }

    class ActionWindow extends PopupWindow {

        public ActionWindow(Context context) {
            super(context);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.window_local_music_operator, null);
            ButterKnife.bind(this, view);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(getContext().getResources().getDimensionPixelOffset(R.dimen.small_player_height));
            setBackgroundDrawable(new ColorDrawable(0));
            setContentView(view);
            setTouchable(true);
            setOutsideTouchable(false);
        }

        public void show() {
            showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        }

        @OnClick({R.id.view_add_to_list, R.id.view_add_to_queue, R.id.view_delete})
        void onAddToList() {
            final List<MusicInfo> musicInfos = new ArrayList<>();
            for (int i = 0; i < mLocalMusicAdapter.getCount(); i++) {
                if (mLocalMusicAdapter.isItemChecked(i)) {
                    musicInfos.add(mLocalMusicAdapter.getItem(i).getMusicInfo());
                }
            }
            SelectPlayListDlg.show(getContext(), musicInfos);
            endMultiMode();
        }

        @OnClick(R.id.view_add_to_queue)
        void onAddToQuque() {
            final List<LocalMusic> localMusics = new ArrayList<>();
            for (int i = 0; i < mLocalMusicAdapter.getCount(); i++) {
                if (mLocalMusicAdapter.isItemChecked(i)) {
                    localMusics.add(mLocalMusicAdapter.getItem(i));
                }
            }
            PlayUtil.enqueue(localMusics);
            endMultiMode();
        }

        @OnClick(R.id.view_delete)
        void onDelete() {
            endMultiMode();
        }
    }
}
