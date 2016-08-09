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
import com.sanron.yidumusic.data.net.bean.SongInfo;

import java.util.Date;
import java.util.List;

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
    public static Observable<Void> updateLocalMusic(final List<MusicInfo> musicInfos) {
        return createObservable(new Callable<Void>() {
            @Override
            public Void call(DatabaseWrapper database) throws Exception {
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
                return null;
            }
        });
    }

    /**
     * 添加歌曲到歌单
     *
     * @param musicInfos
     * @param playList
     * @return
     */
    public static Observable<int[]> addToPlayList(final List<MusicInfo> musicInfos, final PlayList playList) {
        return createObservable(new Callable<int[]>() {
            @Override
            public int[] call(DatabaseWrapper database) throws Exception {
                //记录添加成功和已存在的数量
                int[] result = new int[2];
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
                return result;
            }
        });
    }


    /**
     * 添加用户歌单
     */
    public static Observable<Integer> addUserPlayList(final String name) {
        return createObservable(new Callable<Integer>() {
            @Override
            public Integer call(DatabaseWrapper database) throws Exception {
                //是否重名
                boolean exists = SQLite.selectCountOf()
                        .from(PlayList.class)
                        .where(PlayList_Table.name.eq(name))
                        .and(PlayList_Table.type.eq(PlayList.TYPE_USER))
                        .count() > 0;
                if (exists) {
                    return 0;
                } else {
                    PlayList playList = new PlayList();
                    playList.setName(name);
                    playList.setAddTime(System.currentTimeMillis());
                    playList.setType(PlayList.TYPE_USER);
                    playList.save(database);
                    return 1;
                }
            }
        });
    }

    /**
     * 收藏网络歌单
     *
     * @param
     */
    public static Observable<Integer> addWebPlayList(final PlayList playList, final List<SongInfo> songInfos) {
        return createObservable(new Callable<Integer>() {
            @Override
            public Integer call(DatabaseWrapper db) throws Exception {
                //检查是否已收藏过
                boolean exist = SQLite.selectCountOf()
                        .from(PlayList.class)
                        .where(PlayList_Table.type.eq(playList.getType()))
                        .and(PlayList_Table.code.eq(playList.getCode()))
                        .count(db) > 0;
                if (exist) {
                    return 0;
                }

                //
                playList.save(db);
                long time = new Date().getTime();
                for (SongInfo songInfo : songInfos) {
                    PlayListMembers playListMembers = new PlayListMembers();
                    playListMembers.setAddTime(time);
                    playListMembers.setPlayList(playList);
                    MusicInfo musicInfo = SQLite.select(MusicInfo_Table.id)
                            .from(MusicInfo.class)
                            .where(MusicInfo_Table.songId.eq(songInfo.songId))
                            .querySingle();
                    if (musicInfo == null) {
                        //如果歌曲信息表中没有则添加
                        musicInfo = new MusicInfo();
                        musicInfo.setTitle(songInfo.title);
                        musicInfo.setAlbum(songInfo.albumTitle);
                        musicInfo.setDuration(songInfo.fileDuration);
                        musicInfo.setArtist(songInfo.author);
                        musicInfo.setSongId(songInfo.songId);
                        musicInfo.save(db);
                    } else {
                        boolean isLocal = SQLite.selectCountOf()
                                .from(LocalMusic.class)
                                .innerJoin(MusicInfo.class)
                                .on(LocalMusic_Table.musicInfo_id.eq(musicInfo.getId()))
                                .count() > 0;
                        playListMembers.setLocal(isLocal);
                    }
                    playListMembers.setMusicInfo(musicInfo);
                    playListMembers.save(db);
                }
                return 1;
            }
        });
    }

    public static <T> Observable<T> createObservable(final Callable<T> callable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                DatabaseWrapper database = FlowManager.getDatabase(YiduDB.class)
                        .getWritableDatabase();
                DBObserver.get().beginTransaction();
                database.beginTransaction();
                try {
                    T result = callable.call(database);
                    database.setTransactionSuccessful();
                    database.endTransaction();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(result);
                    }
                } catch (Exception e) {
                    database.endTransaction();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                } finally {
                    DBObserver.get().endTranscaction();
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public interface Callable<V> {
        V call(DatabaseWrapper db) throws Exception;
    }
}
