package com.sanron.yidumusic.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.LocalMusic_Table;
import com.sanron.yidumusic.data.net.bean.Gedan;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.activity.MainActivity;
import com.sanron.yidumusic.ui.adapter.GedanItemAdapter;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.ui.vo.RemotePlayTrack;
import com.sanron.yidumusic.util.FastBlur;
import com.sanron.yidumusic.util.ToastUtil;
import com.sanron.yidumusic.widget.ItemClickHelper;
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
public class GedanDetailFragment extends BaseFragment implements ItemClickHelper.OnItemClickListener {

    @BindView(R.id.toolbar_wrap) View mWrap;
    @BindView(R.id.tool_bar) Toolbar mToolbar;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.sticky_header) StickNavHeader mStickNavHeader;
    @BindView(R.id.info_content) ViewGroup mViewInfo;
    @BindView(R.id.sticky_bar) ViewGroup mBar;
    @BindView(R.id.iv_picture) ImageView mIvPic;
    @BindView(R.id.tv_title) TextView mTvTitle;
    @BindView(R.id.tv_desc) TextView mTvDesc;
    @BindView(R.id.iv_flur_bg) ImageView mIvFlur;
    @BindView(R.id.tv_tags) TextView mTvTag;

    private long mListId;
    private GedanItemAdapter mGedanItemAdapter;

    public static final String ARG_LIST_ID = "list_id";

    @Override
    protected int getLayout() {
        return R.layout.fragment_gedan_detail;
    }

    public static GedanDetailFragment newInstance(long listid) {
        GedanDetailFragment gedanDetailFragment = new GedanDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_LIST_ID, listid);
        gedanDetailFragment.setArguments(bundle);
        return gedanDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListId = getArguments().getLong(ARG_LIST_ID);
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setupToolbarPadding();
        mIvFlur.setColorFilter(getResources().getColor(R.color.darkGray), PorterDuff.Mode.MULTIPLY);
        mGedanItemAdapter = new GedanItemAdapter(getContext(), null);
        mRecyclerView.setAdapter(mGedanItemAdapter);
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
        ItemClickHelper.setOnItemClickListener(mRecyclerView, this, R.id.iv_action);
        loadData();
    }

    //设置间距适应状态栏
    private void setupToolbarPadding() {
        SystemBarTintManager.SystemBarConfig systemBarConfig = ((MainActivity) getActivity()).getSystemBarConfig();
        if (systemBarConfig != null) {
            mWrap.setPadding(0, systemBarConfig.getPixelInsetTop(false), 0, 0);
        }
    }

    private void loadData() {
        addSub(YiduApp.get()
                .getDataRepository()
                .getGedanInfo(mListId)
                .subscribe(new SubscriberAdapter<GedanInfoData>() {

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.$("获取数据失败");
                    }

                    @Override
                    public void onNext(GedanInfoData data) {
                        setupUI(data.gedan);
                    }
                })
        );
    }

    //更新UI
    private void setupUI(Gedan gedan) {
        mTvDesc.setText(gedan.desc);
        mTvTitle.setText(gedan.title);
        mTvTag.setText(gedan.tag);
        Observable.just(gedan.songs)
                .map(new Func1<List<SongInfo>, List<RemotePlayTrack>>() {
                    @Override
                    public List<RemotePlayTrack> call(List<SongInfo> songInfos) {
                        List<RemotePlayTrack> remotePlayTracks = new ArrayList<>();
                        for (SongInfo songInfo : songInfos) {
                            RemotePlayTrack remotePlayTrack = new RemotePlayTrack();
                            remotePlayTrack.setSongInfo(songInfo);
                            //查找本地音乐是否存在
                            LocalMusic localMusic = SQLite.select()
                                    .from(LocalMusic.class)
                                    .where(LocalMusic_Table.songId.eq(songInfo.songId))
                                    .querySingle();
                            remotePlayTrack.setMatchLocalMusic(localMusic);
                            remotePlayTracks.add(remotePlayTrack);
                        }
                        return remotePlayTracks;
                    }
                })
                .compose(TransformerUtil.<List<RemotePlayTrack>>io())
                .subscribe(new SubscriberAdapter<List<RemotePlayTrack>>() {
                    @Override
                    public void onNext(List<RemotePlayTrack> remotePlayTracks) {
                        super.onNext(remotePlayTracks);
                        mGedanItemAdapter.setData(remotePlayTracks);
                    }
                });
        Glide.with(GedanDetailFragment.this)
                .load(gedan.pic300)
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
    private void setFlurBG(final Bitmap img) {
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

    @Override
    public void onItemClick(View view, boolean isItemView, int position) {
        if (isItemView) {
            PlayUtil.clearQueue();
            PlayUtil.enqueue(mGedanItemAdapter.getData());
            PlayUtil.play(position);
        } else if (view.getId() == R.id.iv_action) {
            ToastUtil.$("点急了菜单" + position);
        }
    }

}
