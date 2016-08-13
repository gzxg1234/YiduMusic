package com.sanron.yidumusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.LocalMusic_Table;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.data.net.bean.response.BillSongListData;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.ui.adapter.SongInfoAdapter;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.ui.vo.SongInfoVO;
import com.sanron.yidumusic.util.StatusBarUtil;
import com.sanron.yidumusic.util.ToastUtil;
import com.sanron.yidumusic.widget.StickNavHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;

/**
 * 排行榜详情界面
 * Created by sanron on 16-8-3.
 */
public class BillInfoFragment extends BaseFragment {

    @BindView(R.id.toolbar_wrap) View mWrap;
    @BindView(R.id.tool_bar) Toolbar mToolbar;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.sticky_header) StickNavHeader mStickNavHeader;
    @BindView(R.id.sticky_bar) ViewGroup mBar;
    @BindView(R.id.tv_play_all) TextView mTvPlayAll;
    @BindView(R.id.iv_picture) ImageView mIvPic;
    @BindView(R.id.tv_desc) TextView mTvDesc;
    @BindView(R.id.tv_time) TextView mTvTime;

    private SongInfoAdapter mSongInfoAdapter;
    private int mType;

    private static final String ARG_TYPE = "type";

    public static BillInfoFragment newInstance(int type) {
        BillInfoFragment billInfoFragment = new BillInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        billInfoFragment.setArguments(args);
        return billInfoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(ARG_TYPE);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_bill_song_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        StatusBarUtil.applyInsertTop(getActivity(), mWrap);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(BillInfoFragment.this.getClass().getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mSongInfoAdapter = new SongInfoAdapter(getContext(), null);
        mSongInfoAdapter.setLoadEnable(false);
        mRecyclerView.setAdapter(mSongInfoAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mStickNavHeader.attachRecyclerView(mRecyclerView);
        mStickNavHeader.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mStickNavHeader.getViewTreeObserver().removeOnPreDrawListener(this);
                int toolbarHeight = mWrap.getHeight();
                int height = mStickNavHeader.getHeight();
                int barHeight = mBar.getHeight();
                mStickNavHeader.setMaxScrollY(height - toolbarHeight - barHeight);
                mStickNavHeader.setOnScrollListener(new StickNavHeader.OnScrollListener() {
                    @Override
                    public void onScroll(int scrollPixel, float scrollOffset) {
                        mTvDesc.setAlpha(1 - scrollOffset);
                        mTvTime.setAlpha(1 - scrollOffset);
                    }
                });
                return false;
            }
        });

        mBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayUtil.clearQueue();
                PlayUtil.enqueueSongInfoVOs(mSongInfoAdapter.getItems());
                PlayUtil.play(0);
            }
        });
        loadData();
    }

    private void loadData() {
        Subscription subscription = YiduApp.get()
                .getDataRepository()
                .getBillSongList(mType, 0, 100)
                .subscribe(new SubscriberAdapter<BillSongListData>() {
                    @Override
                    public void onNext(BillSongListData billSongListData) {
                        super.onNext(billSongListData);
                        updateUI(billSongListData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtil.$("获取数据失败");
                    }
                });
        addSub(subscription);
    }

    private void updateUI(BillSongListData billSongListData) {
        Glide.with(this)
                .load(billSongListData.billboard.picS640)
                .into(mIvPic);
        mToolbar.setTitle(billSongListData.billboard.name);
        mTvDesc.setText(billSongListData.billboard.comment);
        if (!TextUtils.isEmpty(billSongListData.billboard.updateDate)) {
            mTvTime.setText("更新时间:" + billSongListData.billboard.updateDate);
        }
        setData(billSongListData.songs);
    }

    protected void setData(final List<SongInfo> songInfoList) {
        List<SongInfoVO> songInfoVOs = new ArrayList<>();
        for (SongInfo songInfo : songInfoList) {
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
        mSongInfoAdapter.setItems(songInfoVOs);
        mTvPlayAll.setText("播放全部(" + songInfoVOs.size() + ")");
    }

}
