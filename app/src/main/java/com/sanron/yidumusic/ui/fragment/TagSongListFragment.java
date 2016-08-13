package com.sanron.yidumusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.LocalMusic_Table;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.data.net.bean.response.TagSongListData;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.ui.adapter.SongInfoAdapter;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.ui.base.PullAdapter;
import com.sanron.yidumusic.ui.vo.SongInfoVO;
import com.sanron.yidumusic.util.StatusBarUtil;
import com.sanron.yidumusic.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;


/**
 * Created by sanron on 16-8-13.
 */
public class TagSongListFragment extends BaseFragment implements PullAdapter.OnLoadMoreListener {

    @BindView(R.id.toolbar_wrap) View mWrap;
    @BindView(R.id.tool_bar) Toolbar mToolbar;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private SongInfoAdapter mSongInfoAdapter;

    private int mPage;
    private static final int LIMIT = 20;
    private static final String ARG_TAG_NAME = "tagname";

    private String mTag;

    public static TagSongListFragment newInstance(String tagname) {
        TagSongListFragment tagSongListFragment = new TagSongListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TAG_NAME, tagname);
        tagSongListFragment.setArguments(args);
        return tagSongListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString(ARG_TAG_NAME);
    }

    @Override
    protected int getLayout() {
        return R.layout.toolbar_with_recycler_view;
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        StatusBarUtil.applyInsertTop(getActivity(), mWrap);
        mToolbar.setTitle(mTag);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(TagSongListFragment.this.getClass().getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mSongInfoAdapter = new SongInfoAdapter(getContext(), null);
        mSongInfoAdapter.setOnLoadMoreListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSongInfoAdapter);
        onLoad();
    }

    @Override
    public void onLoad() {
        Subscription subscription = YiduApp.get()
                .getDataRepository()
                .getTagSongList(mTag, LIMIT, mPage * LIMIT)
                .subscribe(new SubscriberAdapter<TagSongListData>() {
                    @Override
                    public void onNext(TagSongListData tagSongListData) {
                        mSongInfoAdapter.setHasMore(tagSongListData.taginfo.havemore == 1);
                        List<SongInfoVO> songInfoVOs = new ArrayList<>();
                        for (SongInfo songInfo : tagSongListData.taginfo.songs) {
                            SongInfoVO songInfoVO = new SongInfoVO();
                            songInfoVO.setSongInfo(songInfo);
                            //查找本地音乐是否存在
                            LocalMusic localMusic = SQLite.select()
                                    .from(LocalMusic.class)
                                    .where(LocalMusic_Table.songId.eq(songInfo.songId))
                                    .querySingle();
                            songInfoVO.setMatchLocalMusic(localMusic);
                            songInfoVOs.add(songInfoVO);
                        }
                        mSongInfoAdapter.addItems(songInfoVOs);
                        mPage++;
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mSongInfoAdapter.onLoadComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtil.$("加载失败");
                    }
                });
        addSub(subscription);
    }
}
