package com.sanron.yidumusic.playback;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.rx.SubscriberAdapter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2015/12/16.
 */
public class PlayService extends Service implements Player.OnPlayStateChangeListener {

    public static final String TAG = PlayService.class.getSimpleName();


    private YiduNotificationManager mNotificationManager;
    private YiduPlayer mYiduPlayer;
    private Subscription mLyricSubscrption;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service create");
        mYiduPlayer = new YiduPlayer(this);
        mNotificationManager = new YiduNotificationManager(this);
        mNotificationManager.startNotification();
        mYiduPlayer.addPlayStateChangeListener(this);
    }

    public YiduPlayer getYiduPlayer() {
        return mYiduPlayer;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service destroy");
        mNotificationManager.stopNotification();
        mNotificationManager = null;
        mYiduPlayer.removePlayStateChangeListener(this);
        mYiduPlayer.release();
        mYiduPlayer = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mYiduPlayer;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onPlayStateChange(int state) {
        mNotificationManager.updateNotification();
        switch (state) {
            case Player.STATE_PREPARING: {
                updateSongImg();
            }
            break;
        }
    }

    private void updateSongImg() {
        mNotificationManager.updateImage(null);
        String artist = mYiduPlayer.getCurrentTrack().getArtist();
        artist = MusicInfo.UNKNOWN.equals(artist) ? "" : artist;
        if (mLyricSubscrption != null
                && !mLyricSubscrption.isUnsubscribed()) {
            mLyricSubscrption.unsubscribe();
        }
        mLyricSubscrption = YiduApp.get()
                .getDataRepository()
                .getLrcpic(mYiduPlayer.getCurrentTrack().getTitle(), artist)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<LrcpicData, Observable<Bitmap>>() {
                    @Override
                    public Observable<Bitmap> call(LrcpicData lrcPic) {
                        try {
                            return Observable.just(Glide.with(getApplicationContext())
                                    .load(lrcPic.songinfo.picS500)
                                    .asBitmap()
                                    .into(500, 500)
                                    .get());
                        } catch (Exception e) {
                            return Observable.empty();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberAdapter<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        mNotificationManager.updateImage(bitmap);
                    }
                });
    }
}
