package com.sanron.yidumusic.playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.ui.vo.SongInfoVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sanron on 16-4-18.
 */
public class PlayUtil {
    private static Player sPlayer;
    private static Map<Context, ServiceBinder> sBinders = new HashMap<>();
    private static List<OnPlayerBindListener> sOnPlayBindListeners = new CopyOnWriteArrayList<>();

    public interface OnPlayerBindListener {
        void onReady(Player player);
    }

    public static boolean bindService(Context context, ServiceConnection callback) {
        Intent intent = new Intent(context, PlayService.class);
        context.startService(intent);
        ServiceBinder binder = new ServiceBinder(callback);
        boolean isSuccess = context.bindService(intent,
                binder, Context.BIND_AUTO_CREATE);
        if (isSuccess) {
            sBinders.put(context, binder);
        }
        return isSuccess;
    }

    public static void unbindService(Context context) {
        ServiceBinder binder = sBinders.remove(context);
        if (binder != null) {
            context.unbindService(binder);
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, PlayService.class);
        context.stopService(intent);
        for (Map.Entry<Context, ServiceBinder> entry : sBinders.entrySet()) {
            context.unbindService(entry.getValue());
        }
        sBinders.clear();
        sPlayer = null;
    }

    public static void addOnPlayerBindListener(OnPlayerBindListener onPlayerBindListener) {
        if (sPlayer != null) {
            onPlayerBindListener.onReady(sPlayer);
        }
        sOnPlayBindListeners.add(onPlayerBindListener);
    }

    public static void removeOnPlayerBindListener(OnPlayerBindListener onPlayerBindListener) {
        sOnPlayBindListeners.remove(onPlayerBindListener);
    }

    public static class ServiceBinder implements ServiceConnection {
        private ServiceConnection callback;

        public ServiceBinder(ServiceConnection callback) {
            this.callback = callback;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (sPlayer == null) {
                sPlayer = (Player) service;
                for (OnPlayerBindListener listener : sOnPlayBindListeners) {
                    listener.onReady(sPlayer);
                }
            }
            if (callback != null) {
                callback.onServiceConnected(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Context context = null;
            for (Map.Entry<Context, ServiceBinder> entry : sBinders.entrySet()) {
                if (this == entry.getValue()) {
                    context = entry.getKey();
                    break;
                }
            }
            sBinders.remove(context);
            if (sBinders.size() == 0) {
                sPlayer = null;
            }
        }
    }


    public static List<PlayTrack> getQueue() {
        if (sPlayer != null) {
            return sPlayer.getQueue();
        }
        return null;
    }

    public static void enqueue(List<? extends PlayTrack> musics) {
        if (sPlayer != null) {
            sPlayer.enqueue(musics);
        }
    }

    public static void enqueueSongInfoVOs(List<SongInfoVO> songInfoVOs) {
        List<PlayTrack> playTracks = new ArrayList<>();
        for (SongInfoVO songInfoVO : songInfoVOs) {
            SongInfo songInfo = songInfoVO.getSongInfo();
            LocalMusic localMusic = songInfoVO.getMatchLocalMusic();
            PlayTrack playTrack = new PlayTrack();
            playTrack.setTitle(songInfo.title);
            playTrack.setAlbum(songInfo.albumTitle);
            playTrack.setArtist(songInfo.author);
            playTrack.setDuration(songInfo.fileDuration);
            playTrack.setSongId(songInfo.songId);
            if (localMusic != null) {
                playTrack.setDuration(localMusic.getMusicInfo().getDuration());
                playTrack.setPath(localMusic.getMusicInfo().getPath());
                playTrack.setPlayType(PlayTrack.SOURCE_LOCAL);
            } else {
                playTrack.setPlayType(PlayTrack.SOURCE_WEB);
            }
            playTracks.add(playTrack);
        }
        enqueue(playTracks);
    }

    public static void dequeue(int position) {
        if (sPlayer != null) {
            sPlayer.dequeue(position);
        }
    }

    public static void clearQueue() {
        if (sPlayer != null) {
            sPlayer.clearQueue();
        }
    }

    public static void play(int position) {
        if (sPlayer != null) {
            sPlayer.play(position);
        }
    }

    public static int getCurrentIndex() {
        if (sPlayer != null) {
            return sPlayer.getCurrentPosition();
        }
        return -1;
    }

    public static PlayTrack getCurrentMusic() {
        if (sPlayer != null) {
            return sPlayer.getCurrentTrack();
        }
        return null;
    }

    public static void togglePlayPause() {
        if (sPlayer != null) {
            sPlayer.togglePlayPause();
        }
    }

    public static void next() {
        if (sPlayer != null) {
            sPlayer.next();
        }
    }

    public static void previous() {
        if (sPlayer != null) {
            sPlayer.previous();
        }
    }

    public static int getState() {
        if (sPlayer != null) {
            return sPlayer.getState();
        }
        return Player.STATE_IDLE;
    }

    public static void setPlayMode(int mode) {
        if (sPlayer != null) {
            sPlayer.setPlayMode(mode);
        }
    }

    public static int getPlayMode() {
        if (sPlayer != null) {
            return sPlayer.getPlayMode();
        }
        return Player.MODE_IN_TURN;
    }

    public static void addPlayStateChangeListener(Player.OnPlayStateChangeListener listener) {
        if (sPlayer != null) {
            sPlayer.addPlayStateChangeListener(listener);
        }
    }

    public static void removePlayStateChangeListener(Player.OnPlayStateChangeListener listener) {
        if (sPlayer != null) {
            sPlayer.removePlayStateChangeListener(listener);
        }
    }

    public static void addOnBufferListener(Player.OnBufferListener listener) {
        if (sPlayer != null) {
            sPlayer.addOnBufferListener(listener);
        }
    }

    public static void addOnCompletedListener(Player.OnCompletedListener listener) {
        if (sPlayer != null) {
            sPlayer.addOnCompletedListener(listener);
        }
    }

    public static void removeOnCompletedListener(Player.OnCompletedListener listener) {
        if (sPlayer != null) {
            sPlayer.removeOnCompletedListener(listener);
        }
    }


    public static void removeBufferListener(Player.OnBufferListener listener) {
        if (sPlayer != null) {
            sPlayer.removeBufferListener(listener);
        }
    }

    public static boolean isPlaying() {
        if (sPlayer != null) {
            return sPlayer.isPlaying();
        }
        return false;
    }

    public static int getProgress() {
        if (sPlayer != null) {
            return sPlayer.getProgress();
        }
        return 0;
    }

    public static int getDuration() {
        if (sPlayer != null) {
            return sPlayer.getDuration();
        }
        return 0;
    }

    public static void seekTo(int position) {
        if (sPlayer != null) {
            sPlayer.seekTo(position);
        }
    }

}
