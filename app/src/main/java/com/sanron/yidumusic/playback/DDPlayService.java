package com.sanron.yidumusic.playback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sanron.yidumusic.data.db.model.MusicInfo;

/**
 * Created by Administrator on 2015/12/16.
 */
public class DDPlayService extends Service implements Player.OnPlayStateChangeListener{

    public static final String TAG = DDPlayService.class.getSimpleName();


    private PlayNotificationManager mNotificationManager;

    private DDPlayer mDDPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service create");
        mDDPlayer = new DDPlayer(this);
        mNotificationManager = new PlayNotificationManager(this);
        mNotificationManager.startNotification();
        mDDPlayer.addPlayStateChangeListener(this);
    }

    public DDPlayer getDDPlayer() {
        return mDDPlayer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "service destroy");
        mDDPlayer.removePlayStateChangeListener(this);
        mDDPlayer.release();
        mDDPlayer = null;
        mNotificationManager.stopNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDDPlayer;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onPlayStateChange(int state) {
        switch (state) {
            case Player.STATE_PLAYING:
            case Player.STATE_PAUSE:
            case Player.STATE_IDLE: {
                mNotificationManager.updateNotification();
            }
            break;

            case Player.STATE_PREPARING: {
                mNotificationManager.updateImage(null);
                MusicInfo music = mDDPlayer.getCurrentMusic();
                String artist = music.getArtist();

            }
            break;
        }
    }

}
