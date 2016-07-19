package com.sanron.yidumusic.ui.fragment.my_music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.bean.LocalMusic;
import com.sanron.yidumusic.data.db.bean.MusicInfo;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.activity.ScanMusicActivity;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Administrator on 2015/12/21.
 */
public class LocalMusicFragment extends LazyLoadFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private LocalMusicAdapter mLocalMusicAdapter;
    private FlowContentObserver observer = new FlowContentObserver(new Handler(Looper.getMainLooper()));
    private FlowContentObserver.OnTableChangedListener mChangedListener = new FlowContentObserver.OnTableChangedListener() {
        @Override
        public void onTableChanged(@Nullable Class<? extends Model> tableChanged, BaseModel.Action action) {
            loadData();
        }
    };

    @Override
    protected void onLazyLoad() {
        loadData();
    }

    private void loadData() {
        YiduDB.getLocalMusic()
                .compose(TransformerUtil.<List<LocalMusic>>io())
                .subscribe(new Action1<List<LocalMusic>>() {
                    @Override
                    public void call(List<LocalMusic> localMusics) {
                        mLocalMusicAdapter.setData(localMusics);
                    }
                });
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLocalMusicAdapter);
        mRefreshLayout.setEnabled(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLocalMusicAdapter = new LocalMusicAdapter(getContext());
        observer = new FlowContentObserver();
        observer.registerForContentChanges(getContext(), LocalMusic.class);
        observer.setNotifyAllUris(false);
        observer.addOnTableChangedListener(mChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        observer.removeTableChangedListener(mChangedListener);
        observer.unregisterForContentChanges(getContext());
    }

    @Override
    protected int getLayout() {
        return R.layout.refresh_with_recycler;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.local_music_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_type: {
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

    static class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.MusicInfoHolder> {

        private Context mContext;
        private List<LocalMusic> mData;

        public LocalMusicAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<LocalMusic> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public MusicInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_local_music_item, parent, false);
            return new MusicInfoHolder(view);
        }

        @Override
        public void onBindViewHolder(MusicInfoHolder holder, int position) {
            holder.setData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }


        class MusicInfoHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_title)
            TextView tvTitle;
            @BindView(R.id.tv_artist)
            TextView tvArtist;
            @BindView(R.id.iv_operator)
            ImageView ivOperator;

            public MusicInfoHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(LocalMusic data) {
                MusicInfo musicInfo = data.getMusicInfo();
                String artist = musicInfo.getArtist();
                artist = TextUtils.isEmpty(artist) || "<unknown>".equals(artist) ? "未知" : artist;
                String album = musicInfo.getAlbum();
                album = TextUtils.isEmpty(album) ? "未知" : album;
                tvArtist.setText(artist + " - " + album);
                tvTitle.setText(musicInfo.getTitle());
            }
        }
    }
}
