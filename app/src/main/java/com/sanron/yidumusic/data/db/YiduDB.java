package com.sanron.yidumusic.data.db;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.sanron.yidumusic.data.db.bean.LocalMusic;
import com.sanron.yidumusic.data.db.bean.LocalMusic_Table;
import com.sanron.yidumusic.data.db.bean.MusicInfo;
import com.sanron.yidumusic.data.db.bean.MusicInfo_Table;

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
     *
     * @param musicInfos
     * @return
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
                    e.printStackTrace();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }
}
