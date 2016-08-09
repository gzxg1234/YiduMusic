package com.sanron.yidumusic.ui.fragment.my_music;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.Model;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.DBObserver;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayListMembers;
import com.sanron.yidumusic.data.db.model.PlayListMembers_Table;
import com.sanron.yidumusic.data.db.model.PlayList_Table;
import com.sanron.yidumusic.playback.PlayTrack;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.adapter.PlayListAdapter;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.ui.vo.PlayListVO;
import com.sanron.yidumusic.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by sanron on 16-7-20.
 */
public class PlayListFragment extends LazyLoadFragment implements PlayListAdapter.OnItemActionClickListener, PlayListAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PlayListAdapter mPlayListAdapter;
    private Subscription mTableSubscriber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPlayListAdapter = new PlayListAdapter(getContext(), null);
        mTableSubscriber = DBObserver.get().toObservable(PlayList.class, PlayListMembers.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Class<? extends Model>>() {
                    @Override
                    public void call(Class<? extends Model> aClass) {
                        loadData();
                    }
                });
    }

    @Override
    public void onDestroy() {
        mTableSubscriber.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, "新建")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: {
                new NewListDialog(getContext()).show();
            }
            break;
        }
        return true;
    }

    @Override
    protected int getLayout() {
        return R.layout.recycler_view;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mPlayListAdapter);
        mPlayListAdapter.setOnItemClickListener(this);
        mPlayListAdapter.setOnItemActionClickListener(this);
    }

    @Override
    protected void onLazyLoad() {
        loadData();
    }

    private void loadData() {
        Subscription subscription =
                Observable.create(new Observable.OnSubscribe<List<PlayList>>() {
                    @Override
                    public void call(Subscriber<? super List<PlayList>> subscriber) {
                        List<PlayList> playLists = SQLite.select()
                                .from(PlayList.class)
                                .orderBy(PlayList_Table.type, true)
                                .orderBy(PlayList_Table.addTime, true)
                                .queryList();
                        subscriber.onNext(playLists);
                        subscriber.onCompleted();
                    }
                }).map(new Func1<List<PlayList>, List<PlayListVO>>() {
                    @Override
                    public List<PlayListVO> call(List<PlayList> playLists) {
                        List<PlayListVO> list = new ArrayList<>();
                        for (PlayList playList : playLists) {
                            PlayListVO playListVO = new PlayListVO();
                            playListVO.setPlayList(playList);
                            playListVO.setMusicCount(SQLite.selectCountOf()
                                    .from(PlayListMembers.class)
                                    .where(PlayListMembers_Table.playList_id.eq(playList.getId()))
                                    .count());
                            list.add(playListVO);
                        }
                        return list;
                    }
                }).compose(TransformerUtil.<List<PlayListVO>>io()
                ).subscribe(new SubscriberAdapter<List<PlayListVO>>() {
                    @Override
                    public void onNext(List<PlayListVO> playListVOs) {
                        mPlayListAdapter.setItems(playListVOs);
                        if (!isFirstLoaded()) {
                            setState(STATE_SUCCESS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (!isFirstLoaded()) {
                            setState(STATE_FAILED);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        setFirstLoaded(true);
                    }
                });
        addSub(subscription);
    }

    @Override
    public void onItemActionClick(View view, int position) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.inflate(R.menu.playlist_action_menu);
        final PlayList playList = mPlayListAdapter.getItem(position).getPlayList();
        if (playList.getType() == PlayList.TYPE_FAVORITE) {
            menu.getMenu().removeItem(R.id.menu_delete);
        } else if (playList.getType() != PlayList.TYPE_USER) {
            menu.getMenu().removeItem(R.id.menu_rename);
        }
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_play_now: {
                        Observable.create(new Observable.OnSubscribe<List<PlayTrack>>() {
                            @Override
                            public void call(Subscriber<? super List<PlayTrack>> subscriber) {
                                List<PlayListMembers> memberses = SQLite.select()
                                        .from(PlayListMembers.class)
                                        .where(PlayListMembers_Table.playList_id.eq(playList.getId()))
                                        .queryList();

                                List<PlayTrack> tracks = new ArrayList<>();
                                for (PlayListMembers member : memberses) {
                                    PlayTrack playTrack = new PlayTrack();
                                    MusicInfo musicInfo = member.getMusicInfo();
                                    playTrack.setAlbum(musicInfo.getAlbum());
                                    playTrack.setTitle(musicInfo.getTitle());
                                    playTrack.setArtist(musicInfo.getArtist());
                                    playTrack.setSongId(musicInfo.getSongId());
                                    playTrack.setDuration(musicInfo.getDuration());
                                    playTrack.setPath(musicInfo.getPath());
                                    playTrack.setLocalId(musicInfo.getId());
                                    if (member.isLocal()) {
                                        playTrack.setPlayType(PlayTrack.SOURCE_LOCAL);
                                    } else {
                                        playTrack.setPlayType(PlayTrack.SOURCE_WEB);
                                    }
                                    tracks.add(playTrack);
                                }
                                subscriber.onNext(tracks);
                            }
                        }).compose(TransformerUtil.<List<PlayTrack>>io()
                        ).subscribe(new SubscriberAdapter<List<PlayTrack>>() {
                            @Override
                            public void onNext(List<PlayTrack> playTracks) {
                                if (playTracks.isEmpty()) {
                                    ToastUtil.$("歌单为空");
                                    return;
                                }
                                PlayUtil.clearQueue();
                                PlayUtil.enqueue(playTracks);
                                PlayUtil.play(0);
                            }
                        });
                    }
                    break;
                    case R.id.menu_rename: {

                    }
                    break;

                    case R.id.menu_delete: {

                    }
                    break;
                }
                return false;
            }
        });
        menu.show();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    static class NewListDialog extends Dialog {

        @BindView(R.id.et_input)
        EditText mEtInput;

        public NewListDialog(Context context) {
            super(context);
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dlg_input_listname, null);
            ButterKnife.bind(this, view);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(view);
            setCanceledOnTouchOutside(false);
        }

        @OnClick(R.id.btn_ok)
        void onOK() {
            String name = mEtInput.getText().toString();
            if (TextUtils.isEmpty(name)) {
                mEtInput.setError("请输入名称");
            } else {
                YiduDB.addUserPlayList(name)
                        .compose(TransformerUtil.<Integer>io())
                        .subscribe(new SubscriberAdapter<Integer>() {
                            @Override
                            public void onNext(Integer integer) {
                                super.onNext(integer);
                                if (integer == 0) {
                                    mEtInput.setError("歌单名已存在");
                                } else {
                                    cancel();
                                }
                            }
                        });
            }
        }

        @OnClick(R.id.btn_cancel)
        void onCancel() {
            cancel();
        }
    }
}
