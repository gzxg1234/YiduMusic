package com.sanron.yidumusic.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.LocalMusic_Table;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.adapter.SongInfoAdapter;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.ui.vo.SongInfoVO;
import com.sanron.yidumusic.util.FastBlur;
import com.sanron.yidumusic.util.StatusBarUtil;
import com.sanron.yidumusic.widget.StickNavHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 歌单详情界面
 * Created by sanron on 16-8-3.
 */
public abstract class SongListFragment extends BaseFragment {

    @BindView(R.id.iv_collect) protected ImageView mIvCollect;
    @BindView(R.id.iv_share) protected ImageView mIvShare;
    @BindView(R.id.toolbar_wrap) protected View mWrap;
    @BindView(R.id.tool_bar) protected Toolbar mToolbar;
    @BindView(R.id.recycler_view) protected RecyclerView mRecyclerView;
    @BindView(R.id.sticky_header) protected StickNavHeader mStickNavHeader;
    @BindView(R.id.iv_flur_bg) protected ImageView mIvFlur;
    @BindView(R.id.info_content) protected ViewGroup mViewInfo;
    @BindView(R.id.sticky_bar) protected ViewGroup mBar;
    @BindView(R.id.tv_play_all) protected TextView mTvPlayAll;
    @BindView(R.id.iv_picture) protected ImageView mIvPic;
    @BindView(R.id.tv_text1) protected TextView mText1;
    @BindView(R.id.tv_text2) protected TextView mText2;
    @BindView(R.id.tv_text3) protected TextView mText3;
    @BindView(R.id.ic) protected ImageView mIvIcon;

    private SongInfoAdapter mSongInfoAdapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_song_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        StatusBarUtil.applyInsertTop(getActivity(), mWrap);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(SongListFragment.this.getClass().getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mIvFlur.setColorFilter(getResources().getColor(R.color.darkGray), PorterDuff.Mode.MULTIPLY);
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
                        mViewInfo.setAlpha(1 - scrollOffset);
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
    }

    protected void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    protected void setData(final List<SongInfo> songInfoList) {
        Observable.just(songInfoList)
                .map(new Func1<List<SongInfo>, List<SongInfoVO>>() {
                    @Override
                    public List<SongInfoVO> call(List<SongInfo> songInfos) {
                        List<SongInfoVO> songInfoVOs = new ArrayList<>();
                        for (SongInfo songInfo : songInfos) {
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
                        return songInfoVOs;
                    }
                }).compose(TransformerUtil.<List<SongInfoVO>>io())
                .subscribe(new SubscriberAdapter<List<SongInfoVO>>() {
                    @Override
                    public void onNext(List<SongInfoVO> songInfoVOs) {
                        super.onNext(songInfoVOs);
                        mSongInfoAdapter.setItems(songInfoVOs);
                        mTvPlayAll.setText("播放全部(" + songInfoVOs.size() + ")");
                    }
                });
    }

    protected void loadPicture(String url) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        setFlurBG(resource);
                        return false;
                    }
                })
                .into(mIvPic);
    }

    //设置模糊背景
    protected void setFlurBG(final Bitmap img) {
        Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        //预先降低图片质量，模糊处理更快
                        Bitmap resizeBmp = Bitmap.createBitmap(img.getWidth() / 2, img.getHeight() / 2,
                                Bitmap.Config.RGB_565);
                        Canvas canvas = new Canvas(resizeBmp);
                        canvas.drawBitmap(img,
                                new Rect(0, 0, img.getWidth(), img.getHeight()),
                                new Rect(0, 0, resizeBmp.getWidth(), resizeBmp.getHeight()),
                                new Paint());
                        resizeBmp = FastBlur.doBlur(resizeBmp, 20, false);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(resizeBmp);
                        }
                    }
                })
                .compose(TransformerUtil.<Bitmap>io())
                .subscribe(new SubscriberAdapter<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        mIvFlur.setAlpha(0f);
                        mIvFlur.setImageBitmap(bitmap);
                        mIvFlur.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .start();
                    }
                });
    }

}
