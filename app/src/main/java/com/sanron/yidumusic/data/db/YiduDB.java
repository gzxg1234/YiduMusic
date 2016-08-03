package com.sanron.yidumusic.data.db;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.LocalMusic_Table;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.db.model.MusicInfo_Table;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayListMembers;
import com.sanron.yidumusic.data.db.model.PlayListMembers_Table;
import com.sanron.yidumusic.data.db.model.PlayList_Table;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by sanron on 16-7-18.
 */
@Database(name = YiduDB.NAME, version = YiduDB.VERSION)
public class YiduDB {
    public static final String NAME = "yidumusic";
    public static final int VERSION = 1;

    /**
     * 更新本地歌曲
     */
    public static Observable<Boolean> updateLocalMusic(final List<MusicInfo> musicInfos) {
        return createObservable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                DatabaseWrapper database = FlowManager.getDatabase(YiduDB.class).getWritableDatabase();
                database.beginTransaction();
                DBObserver.get().beginTransaction();
                try {
                    for (MusicInfo musicInfo : musicInfos) {
                        //查找信息表是否已有歌曲信息
                        MusicInfo music = SQLite.select(MusicInfo_Table.id)
                                .from(MusicInfo.class)
                                .where(MusicInfo_Table.path.eq(musicInfo.getPath()))
                                .and(MusicInfo_Table.lastModifyTime.eq(musicInfo.getLastModifyTime()))
                                .querySingle();
                        if (music == null) {
                            //歌曲信息表中没有，则先添加
                            musicInfo.save(database);
                            music = musicInfo;
                        }

                        //查找本地音乐表是否已经添加
                        LocalMusic localMusic = SQLite.select(LocalMusic_Table.id)
                                .from(LocalMusic.class)
                                .where(LocalMusic_Table.musicInfo_id.eq(music.getId()))
                                .querySingle();
                        if (localMusic == null) {
                            //没有，则插入
                            localMusic = new LocalMusic();
                            localMusic.setDeleted(false);
                            localMusic.setLastModifyTime(musicInfo.getLastModifyTime());
                            localMusic.setAddTime(System.currentTimeMillis());
                            localMusic.setMusicInfo(music);
                            localMusic.save(database);
                        } else {
                            //否则设置为未删除
                            if (localMusic.isDeleted()) {
                                localMusic.setDeleted(false);
                                localMusic.update(database);
                            }
                        }
                    }
                    database.setTransactionSuccessful();
                    return true;
                } catch (Exception e) {
                    return false;
                } finally {
                    database.endTransaction();
                    DBObserver.get().endTranscaction();
                }
            }
        });
    }

    //获取本地歌曲
    public static Observable<List<LocalMusic>> getLocalMusic() {
        return createObservable(new Callable<List<LocalMusic>>() {
            @Override
            public List<LocalMusic> call() throws Exception {
                return SQLite.select()
                        .from(LocalMusic.class)
                        .where(LocalMusic_Table.isDeleted.eq(false))
                        .orderBy(LocalMusic_Table.addTime, false)
                        .queryList();
            }
        });
    }

    //获取歌单
    public static Observable<List<PlayList>> getPlayList() {
        return createObservable(new Callable<List<PlayList>>() {
            @Override
            public List<PlayList> call() throws Exception {
                return SQLite.select()
                        .from(PlayList.class)
                        .orderBy(PlayList_Table.type, true)
                        .orderBy(PlayList_Table.addTime, false)
                        .queryList();
            }
        });
    }

    //添加歌曲到歌单
    public static Observable<int[]> addToPlayList(final List<MusicInfo> musicInfos, final PlayList playList) {
        return createObservable(new Callable<int[]>() {
            @Override
            public int[] call() throws Exception {
                //记录添加成功和已存在的数量
                int[] result = new int[2];
                DatabaseWrapper database = FlowManager.getDatabase(YiduDB.class)
                        .getWritableDatabase();
                DBObserver.get().beginTransaction();
                database.beginTransaction();
                try {
                    long time = new Date().getTime();
                    for (MusicInfo musicInfo : musicInfos) {
                        //是否已添加过
                        boolean exists = SQLite.selectCountOf()
                                .from(PlayListMembers.class)
                                .where(PlayListMembers_Table.playList_id.eq(playList.getId()))
                                .and(PlayListMembers_Table.musicInfo_id.eq(musicInfo.getId()))
                                .count() > 0;
                        if (!exists) {
                            PlayListMembers playListMembers = new PlayListMembers();
                            playListMembers.setAddTime(time);
                            playListMembers.setMusicInfo(musicInfo);
                            playListMembers.setPlayList(playList);
                            playListMembers.save(database);
                            result[0]++;
                        } else {
                            result[1]++;
                        }
                    }
                    database.setTransactionSuccessful();
                } catch (Exception e) {

                } finally {
                    database.endTransaction();
                    DBObserver.get().endTranscaction();
                }
                return result;
            }
        });
    }


    public static <T> Observable<T> createObservable(final Callable<T> callable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onStart();
                }
                try {
                    T result = callable.call();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(result);
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }
}
