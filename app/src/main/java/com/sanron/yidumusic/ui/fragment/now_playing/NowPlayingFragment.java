package com.sanron.yidumusic.ui.fragment.now_playing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sanron.lyricview.model.Lyric;
import com.sanron.lyricview.view.LyricView;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.rxhttpclient.RxHttpClient;
import com.sanron.yidumusic.data.net.rxhttpclient.mapper.StringMapper;
import com.sanron.yidumusic.playback.PlayTrack;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.playback.Player;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.activity.MainActivity;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.ui.dialog.ShowPlayQueueWindow;
import com.sanron.yidumusic.util.FastBlur;
import com.sanron.yidumusic.util.ToastUtil;
import com.viewpagerindicator.PageIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.SerialSubscription;

/**
 * 播放界面
 * Created by Administrator on 2016/3/5.
 */
public class NowPlayingFragment extends BaseFragment implements View.OnClickListener,
        Player.OnPlayStateChangeListener, Player.OnBufferListener, SeekBar.OnSeekBarChangeListener, PlayUtil.OnPlayerReadyListener {

    @BindView(R.id.small_player) ViewGroup mSmallPlayer;
    @BindView(R.id.s_play_progress) ProgressBar mSplayProgress;
    @BindView(R.id.s_iv_song_pic) ImageView mSivSongPicture;
    @BindView(R.id.s_tv_title) TextView mStvTitle;
    @BindView(R.id.s_tv_artist) TextView mStvArtist;
    @BindView(R.id.s_iv_toggle_play) ImageView mSivTogglePlay;
    @BindView(R.id.s_iv_next) ImageView mSivNext;

    @BindView(R.id.big_player) ViewGroup mBigPlayer;
    @BindView(R.id.top_bar) ViewGroup mTopBar;
    @BindView(R.id.tv_music_title) TextView mTvTitle;
    @BindView(R.id.tv_music_artist) TextView mTvArtist;
    @BindView(R.id.seek_play_progress) SeekBar mPlayProgress;
    @BindView(R.id.tv_music_progress) TextView mTvPlayPosition;
    @BindView(R.id.tv_music_duration) TextView mTvDuration;
    @BindView(R.id.view_back) View mViewBack;
    @BindView(R.id.iv_play_mode) ImageView mIvChangeMode;
    @BindView(R.id.iv_previous) ImageView mIvPrevious;
    @BindView(R.id.fab_toggle_play) FloatingActionButton mFabTogglePlay;
    @BindView(R.id.iv_next) ImageView mIvNext;
    @BindView(R.id.iv_play_queue) ImageView mIvPlayQueue;

    @BindView(R.id.tv_buffering_hint) TextView mBufferingHint;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.page_indicator) PageIndicator mPageIndicator;
    @BindView(R.id.vs_blur_background) ViewSwitcher mViewSwitcher;

    ShowPlayQueueWindow mShowPlayQueueWindow;

    //1
    ListView mLvSimilarInfo;
    //2
    View mPagerView2;
    ImageView mIvDownload;
    ImageView mIvFavorite;
    ImageView mIvSongPicture;
    //3
    View mPagerView3;
    LyricView mLyricView;
    View mLyricSetting;

    private boolean mIsExpanded;
    private boolean mIsFavorite;
    private List<View> mPagerViews;
    private Toast mToast;
    private Subscription mSetDefaultImg;
    private Subscription mUpdateProgress;
    private SerialSubscription mLrcPicSubscription;
    private SerialSubscription mLyricSubscription;
    private SerialSubscription mBlurSubscription;
    private Target mSongImgTarget;


    @Override
    protected int getLayout() {
        return R.layout.now_playing;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        setListeners();

        //设置间距
        SystemBarTintManager.SystemBarConfig sbc = new SystemBarTintManager(getActivity()).getConfig();
        mTopBar.setPadding(0, sbc.getPixelInsetTop(false), 0, 0);

        setupViewPager();

        //设置颜色滤镜，调暗色调
        for (int i = 0; i < mViewSwitcher.getChildCount(); i++) {
            ((ImageView) mViewSwitcher.getChildAt(i)).setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }

        if (savedInstanceState != null) {
            mSmallPlayer.setVisibility(savedInstanceState.getInt("smallPlayerVisibility", View.VISIBLE));
            mBigPlayer.setVisibility(savedInstanceState.getInt("bigPlayerVisibility", View.VISIBLE));
        }

        PlayUtil.addOnPlayerBindListener(this);
    }

    private void setListeners() {
        mPlayProgress.setOnSeekBarChangeListener(this);
        mSivTogglePlay.setOnClickListener(this);
        mSivNext.setOnClickListener(this);
        mIvChangeMode.setOnClickListener(this);
        mIvPrevious.setOnClickListener(this);
        mFabTogglePlay.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        mIvPlayQueue.setOnClickListener(this);
        mViewBack.setOnClickListener(this);
    }

    private void setupViewPager() {
        mPagerViews = new ArrayList<>();

        mLvSimilarInfo = new ListView(getContext());
        mPagerViews.add(mLvSimilarInfo);

        mPagerView2 = LayoutInflater.from(getContext())
                .inflate(R.layout.now_playing_picture, null);
        mIvFavorite = ButterKnife.findById(mPagerView2, R.id.iv_favorite);
        mIvDownload = ButterKnife.findById(mPagerView2, R.id.iv_download);
        mIvSongPicture = ButterKnife.findById(mPagerView2, R.id.iv_song_picture);
        mIvFavorite.setOnClickListener(this);
        mPagerViews.add(mPagerView2);

        mPagerView3 = LayoutInflater.from(getContext())
                .inflate(R.layout.now_playing_lyric, null);
        mLyricView = ButterKnife.findById(mPagerView3, R.id.lyric_view);
        mLyricView.setEmptyTip(getString(R.string.app_name));
        mLyricSetting = ButterKnife.findById(mPagerView3, R.id.lyric_setting);
        mPagerViews.add(mPagerView3);

        mViewPager.setAdapter(new LocalPagerAdapter());
        mPageIndicator.setViewPager(mViewPager, 1);
    }

    @Override
    public void onReady(Player player) {
        PlayUtil.addPlayStateChangeListener(this);
        PlayUtil.addOnBufferListener(this);

        //设置UI
        final PlayTrack music = PlayUtil.getCurrentMusic();
        if (music != null) {
            setTitleText(music.getTitle());
            setArtistText(music.getArtist());
            mIvSongPicture.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mIvSongPicture.getViewTreeObserver().removeOnPreDrawListener(this);
                    loadLrcPic(music);
                    return true;
                }
            });
        }

        setPlayProgress(PlayUtil.getProgress());
        setSongDuration(PlayUtil.getDuration());
        if (PlayUtil.isPlaying()) {
            setIsPlaying(true);
        } else {
            setIsPlaying(false);
        }

        setModeIcon(PlayUtil.getPlayMode());
    }

    @Override
    public void onPlayStateChange(int state) {
        switch (state) {
            case Player.STATE_IDLE: {
                setTitleText(getContext().getString(R.string.app_name));
                setArtistText("");
                setSongPicture(null);
                mLyricView.setLyric(null);
                setSongDuration(0);
                setPlayProgress(0);
                setIsPlaying(false);
                stopUpdateProgress();
            }
            break;

            case Player.STATE_PAUSE: {
                setIsPlaying(false);
            }
            break;

            case Player.STATE_PLAYING: {
                setIsPlaying(true);
            }
            break;

            case Player.STATE_PREPARING: {
                PlayTrack music = PlayUtil.getCurrentMusic();
                if (music == null) {
                    return;
                }
                //3秒后设置默认图片
                setDefaultImgDelay();
                setTitleText(music.getTitle());
                setArtistText(music.getArtist());
                setSongDuration(0);
                setPlayProgress(0);
                mLyricView.setLyric(null);
                setIsPlaying(false);
                loadLrcPic(music);
            }
            break;

            case Player.STATE_PREPARED: {
                PlayTrack music = PlayUtil.getCurrentMusic();
                if (music == null) {
                    return;
                }
                setSongDuration(PlayUtil.getDuration());
                setPlayProgress(PlayUtil.getProgress());
                if (music.getSourceType() == PlayTrack.SOURCE_LOCAL) {
                    mPlayProgress.setSecondaryProgress(PlayUtil.getDuration());
                } else if (music.getSourceType() == PlayTrack.SOURCE_WEB) {
                    mPlayProgress.setSecondaryProgress(0);
                }
            }
            break;
        }
    }

    private void setDefaultImgDelay() {
        //3秒后设置默认图片
        removeSetDefaultImg();
        mSetDefaultImg = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberAdapter<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        setSongPicture(null);
                    }
                });
    }

    private void removeSetDefaultImg() {
        if (mSetDefaultImg != null
                && !mSetDefaultImg.isUnsubscribed()) {
            mSetDefaultImg.unsubscribe();
        }
    }

    private void setIsPlaying(boolean playing) {
        if (playing) {
            startUpdateProgress();
            mSivTogglePlay.setImageResource(R.mipmap.ic_pause_blue_24dp);
            mFabTogglePlay.setImageResource(R.mipmap.ic_pause_white_24dp);
        } else {
            stopUpdateProgress();
            mSivTogglePlay.setImageResource(R.mipmap.ic_play_arrow_blue_24dp);
            mFabTogglePlay.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
        }
    }

    private void loadLrcPic(final PlayTrack track) {
        if (mLrcPicSubscription == null) {
            mLrcPicSubscription = new SerialSubscription();
            addSub(mLrcPicSubscription);
        }
        String artist = track.getArtist();
        artist = MusicInfo.UNKNOWN.equals(artist) ? "" : artist;
        mLrcPicSubscription.set(
                YiduApp.get()
                        .getDataRepository()
                        .getLrcpic(track.getTitle(), artist)
                        .subscribe(new SubscriberAdapter<LrcpicData>() {
                            @Override
                            public void onNext(LrcpicData lrcpicData) {
                                //加载歌词
                                if (!TextUtils.isEmpty(lrcpicData.songinfo.lrclink)) {
                                    loadSongLyric(lrcpicData.songinfo.lrclink);
                                }
                                //加载图片
                                if (!TextUtils.isEmpty(lrcpicData.songinfo.picS500)) {
                                    loadSongImg(lrcpicData.songinfo.picS500);
                                }
                            }
                        })
        );
    }


    private void loadSongLyric(String lrcLink) {
        if (mLyricSubscription == null) {
            mLyricSubscription = new SerialSubscription();
            addSub(mLyricSubscription);
        }
        mLyricSubscription.set(RxHttpClient.get()
                .newRequest()
                .url(lrcLink)
                .execute()
                .flatMap(new StringMapper())
                .map(new Func1<String, Lyric>() {
                    @Override
                    public Lyric call(String s) {
                        return Lyric.read(s);
                    }
                })
                .compose(TransformerUtil.<Lyric>io())
                .subscribe(new SubscriberAdapter<Lyric>() {
                    @Override
                    public void onNext(Lyric s) {
                        mLyricView.setLyric(s);
                    }
                })
        );
    }

    private void loadSongImg(String url) {
        if (mSongImgTarget != null) {
            Glide.clear(mSongImgTarget);
        }
        mSongImgTarget = Glide.with(getContext())
                .load(url)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        setSongPicture(resource);
                        return false;
                    }
                })
                .into(mIvSongPicture.getWidth(), mIvSongPicture.getHeight());
    }

    @Override
    public void onBufferingUpdate(int bufferedPosition) {
        if (bufferedPosition > mPlayProgress.getSecondaryProgress()) {
            mPlayProgress.setSecondaryProgress(bufferedPosition);
        }
    }

    @Override
    public void onBufferStart() {
        mBufferingHint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferEnd() {
        mBufferingHint.setVisibility(View.INVISIBLE);
    }

    public void setSongPicture(final Bitmap img) {

        if (img == null) {
            mSivSongPicture.setImageResource(R.mipmap.default_song_pic);
            mIvSongPicture.setImageResource(R.mipmap.default_song_pic);
            setBlurBackground(null);
        } else {
            removeSetDefaultImg();
            mSivSongPicture.setImageBitmap(img);
            mIvSongPicture.setImageBitmap(img);
            if (mBlurSubscription == null) {
                mBlurSubscription = new SerialSubscription();
                addSub(mBlurSubscription);
            }
            mBlurSubscription.set(Observable.create(
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
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(resizeBmp);
                            }
                        }
                    })
                    .map(new Func1<Bitmap, Bitmap>() {
                        @Override
                        public Bitmap call(Bitmap bitmap) {
                            return FastBlur.doBlur(bitmap, 40, false);
                        }
                    })
                    .compose(TransformerUtil.<Bitmap>io())
                    .subscribe(new SubscriberAdapter<Bitmap>() {
                        @Override
                        public void onNext(Bitmap bitmap) {
                            setBlurBackground(bitmap);
                        }
                    })
            );
        }
    }

    //设置模糊背景
    private void setBlurBackground(Bitmap bitmap) {
        Drawable drawable;
        if (bitmap == null) {
            drawable = new ColorDrawable(0x88000000);
        } else {
            drawable = new BitmapDrawable(bitmap);
        }
        if (mViewSwitcher.getDisplayedChild() == 0) {
            ((ImageView) mViewSwitcher.getChildAt(1)).setImageDrawable(drawable);
            mViewSwitcher.showNext();
        } else {
            ((ImageView) mViewSwitcher.getChildAt(0)).setImageDrawable(drawable);
            mViewSwitcher.showPrevious();
        }
    }

    @Override
    public void onDestroyView() {
        if (mShowPlayQueueWindow != null && mShowPlayQueueWindow.isShowing()) {
            mShowPlayQueueWindow.dismiss();
        }
        stopUpdateProgress();
        PlayUtil.removePlayStateChangeListener(this);
        PlayUtil.removeBufferListener(this);
        PlayUtil.removeOnPlayerBindListener(this);
        super.onDestroyView();
    }

    private void setTitleText(String title) {
        mStvTitle.setText(title);
        mTvTitle.setText(title);
    }

    private void setArtistText(String artist) {
        if (TextUtils.isEmpty(artist)
                || MusicInfo.UNKNOWN.equals(artist)) {
            artist = "未知歌手";
        }
        mStvArtist.setText(artist);
        mTvArtist.setText(artist);
    }

    private void setPlayProgress(int position) {
        position = Math.max(0, position);
        mPlayProgress.setProgress(position);
        mSplayProgress.setProgress(position);
        mTvPlayPosition.setText(formatTime(position));
    }

    private void setSongDuration(int duration) {
        mPlayProgress.setMax(duration);
        mSplayProgress.setMax(duration);
        mTvDuration.setText(formatTime(duration));
    }

    private String formatTime(int millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return sdf.format(new Date(millis));
    }

    private void setModeIcon(int mode) {
        int iconId = 0;
        switch (mode) {
            case Player.MODE_IN_TURN:
                iconId = R.mipmap.ic_repeat_white_24dp;
                break;
            case Player.MODE_LOOP:
                iconId = R.mipmap.ic_repeat_one_white_24dp;
                break;
            case Player.MODE_RANDOM:
                iconId = R.mipmap.ic_shuffle_white_24dp;
                break;
        }
        mIvChangeMode.setImageResource(iconId);
    }

    private void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_play_mode: {
                int newMode = (PlayUtil.getPlayMode() + 1) % 3;
                PlayUtil.setPlayMode(newMode);
                String msg = "";
                setModeIcon(newMode);
                switch (newMode) {
                    case Player.MODE_IN_TURN:
                        msg = "顺序播放";
                        break;
                    case Player.MODE_LOOP:
                        msg = "单曲循环";
                        break;
                    case Player.MODE_RANDOM:
                        msg = "随机播放";
                        break;
                }
                showToast(msg);
            }
            break;

            case R.id.iv_previous: {
                PlayUtil.previous();
            }
            break;

            case R.id.s_iv_toggle_play:
            case R.id.fab_toggle_play: {
                if (PlayUtil.getState() == Player.STATE_IDLE) {
                    List<PlayTrack> queue = PlayUtil.getQueue();
                    if (queue != null
                            && queue.size() > 0) {
                        PlayUtil.play(0);
                    } else {
                        ToastUtil.$("播放列表为空");
                    }
                    return;
                }
                PlayUtil.togglePlayPause();
            }
            break;

            case R.id.s_iv_next:
            case R.id.iv_next: {
                PlayUtil.next();
            }
            break;

            case R.id.iv_play_queue: {
                mShowPlayQueueWindow = new ShowPlayQueueWindow(getActivity());
                mShowPlayQueueWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
            }
            break;

            case R.id.view_back: {
                getMainActivity().collapsePanel();
            }
            break;

            case R.id.iv_favorite: {
//                Music music = PlayUtil.getCurrentTrack();
//                if (music == null) {
//                    return;
//                }
//                if (mIsFavorite) {
//                    List<Music> musics = new LinkedList<>();
//                    musics.add(music);
//                    AppDB.get(getContext()).deleteMusicFromPlayList(PlayList.TYPE_FAVORITE_ID, musics, new ResultCallback<Integer>() {
//                        @Override
//                        public void onResult(Integer result) {
//                            if (result > 0) {
//                                showToast("取消收藏成功");
//                            }
//                            setIsFavorite(result == 0);
//                        }
//                    });
//                } else {
//                    AppDB.get(getContext()).addMusicToFavorite(PlayUtil.getCurrentTrack(),
//                            new ResultCallback<Boolean>() {
//                                @Override
//                                public void onResult(Boolean result) {
//                                    setIsFavorite(result);
//                                    if (result) {
//                                        showToast("收藏成功");
//                                    }
//                                }
//                            });
//                }
            }
            break;
        }
    }


    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public void setIsFavorite(boolean favorite) {
        mIsFavorite = favorite;
        if (favorite) {
            mIvFavorite.setImageResource(R.mipmap.ic_favorite_pink_600_24dp);
        } else {
            mIvFavorite.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
        }
    }

    //主界面底部的小控制面板可见性
    public void setExpanded(boolean expanded) {
        mSmallPlayer.setVisibility(expanded ? View.INVISIBLE : View.VISIBLE);
        mIsExpanded = expanded;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("smallPlayerVisibility", mSmallPlayer.getVisibility());
        outState.putInt("bigPlayerVisibility", mBigPlayer.getVisibility());
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        stopUpdateProgress();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (PlayUtil.isPlaying()) {
            startUpdateProgress();
        }
        PlayUtil.seekTo(seekBar.getProgress());
        if (seekBar.getProgress() <= seekBar.getSecondaryProgress()) {
            PlayUtil.seekTo(seekBar.getProgress());
            mLyricView.setCurrentTime(seekBar.getProgress());
        } else {
            seekBar.setProgress(PlayUtil.getProgress());
        }
    }

    private void startUpdateProgress() {
        stopUpdateProgress();
        mUpdateProgress = Observable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        int progress = PlayUtil.getProgress();
                        setPlayProgress(progress);
                        mLyricView.setCurrentTime(progress);
                    }
                });
    }

    private void stopUpdateProgress() {
        if (mUpdateProgress != null
                && !mUpdateProgress.isUnsubscribed()) {
            mUpdateProgress.unsubscribe();
        }
    }

    private class LocalPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPagerViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mPagerViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPagerViews.get(position));
        }
    }
}
