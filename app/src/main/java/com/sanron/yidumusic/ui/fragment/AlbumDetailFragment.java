package com.sanron.yidumusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayList_Table;
import com.sanron.yidumusic.data.net.bean.response.AlbumDetailData;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by sanron on 16-8-4.
 */
public class AlbumDetailFragment extends SongListFragment {

    private long mAlbumId;
    private AlbumDetailData mAlbumDetailData;

    public static final String ARG_ALBUM_ID = "album_id";

    public static AlbumDetailFragment newInstance(long albumId) {
        AlbumDetailFragment albumDetailFragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ALBUM_ID, albumId);
        albumDetailFragment.setArguments(args);
        return albumDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbumId = getArguments().getLong(ARG_ALBUM_ID);
    }

    @OnClick(R.id.iv_collect)
    public void onClickCollect() {
        if (mAlbumDetailData == null || mIvCollect.getTag() == null) {
            return;
        }
        if (mIvCollect.getTag() == true) {
            ToastUtil.$("已收藏过此专辑");
            return;
        }

        PlayList playList = new PlayList();
        playList.setAddTime(new Date().getTime());
        playList.setName(mAlbumDetailData.album.title);
        playList.setType(PlayList.TYPE_ALBUM);
        playList.setIcon(mAlbumDetailData.album.picRadio);
        playList.setCode(mAlbumDetailData.album.albumId + "");

        addSub(YiduDB.addWebPlayList(playList, mAlbumDetailData.songlist)
                .compose(TransformerUtil.<Integer>io())
                .subscribe(new SubscriberAdapter<Integer>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtil.$("收藏失败");
                    }

                    @Override
                    public void onNext(Integer result) {
                        if (result == 0) {
                            ToastUtil.$("已收藏过此专辑");
                        } else {
                            ToastUtil.$("收藏歌单成功");
                            mIvCollect.setTag(true);
                            mIvCollect.setImageResource(R.mipmap.ic_favorite_pink_500_24dp);
                        }
                    }
                })
        );
    }

    private void loadData() {
        addSub(YiduApp.get()
                .getDataRepository()
                .getAlbumInfo(mAlbumId)
                .subscribe(new SubscriberAdapter<AlbumDetailData>() {
                    @Override
                    public void onNext(AlbumDetailData albumDetailData) {
                        super.onNext(albumDetailData);
                        setupUI(albumDetailData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtil.$("获取数据失败");
                    }
                })
        );
    }

    private void setupUI(AlbumDetailData data) {
        mAlbumDetailData = data;
        mText1.setText(data.album.title);
        mText2.setText("歌手:" + data.album.author + "\n"
                + "发行时间:" + formatDate(data.album.publishtime));
        loadPicture(data.album.picRadio);
        setData(data.songlist);
    }

    private String formatDate(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setTitle("专辑");
        mIvIcon.setVisibility(View.INVISIBLE);
        mText3.setVisibility(View.INVISIBLE);
        //检查是否收藏过此专辑
        checkCollected();
        loadData();
    }

    private void checkCollected() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean added = SQLite.selectCountOf()
                        .from(PlayList.class)
                        .where(PlayList_Table.type.eq(PlayList.TYPE_ALBUM))
                        .and(PlayList_Table.code.eq(mAlbumId + ""))
                        .count() > 0;
                subscriber.onNext(added);
            }
        }).compose(TransformerUtil.<Boolean>io()
        ).subscribe(new SubscriberAdapter<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                mIvCollect.setTag(aBoolean);
                if (aBoolean) {
                    mIvCollect.setImageResource(R.mipmap.ic_favorite_pink_500_24dp);
                }
            }
        });
    }
}
