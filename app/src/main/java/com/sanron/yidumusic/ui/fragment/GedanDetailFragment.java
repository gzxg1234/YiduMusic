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
import com.sanron.yidumusic.data.net.bean.Gedan;
import com.sanron.yidumusic.data.net.bean.response.GedanInfoData;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.util.ToastUtil;
import com.sanron.yidumusic.widget.ItemClickHelper;

import java.util.Date;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * 歌单详情界面
 * Created by sanron on 16-8-3.
 */
public class GedanDetailFragment extends SongListFragment implements ItemClickHelper.OnItemClickListener {

    private long mListId;
    private Gedan mGedan;

    public static final String ARG_LIST_ID = "list_id";


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

    @OnClick(R.id.iv_collect)
    public void onClickCollect() {
        if (mGedan == null || mIvCollect.getTag() == null) {
            return;
        }
        if (mIvCollect.getTag() == true) {
            ToastUtil.$("已收藏过此歌单");
            return;
        }

        PlayList playList = new PlayList();
        playList.setAddTime(new Date().getTime());
        playList.setName(mGedan.title);
        playList.setType(PlayList.TYPE_GEDAN);
        playList.setIcon(mGedan.pic300);
        playList.setCode(String.valueOf(mGedan.listid));

        addSub(YiduDB.addWebPlayList(playList, mGedan.songList)
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
                            ToastUtil.$("已收藏过此歌单");
                        } else {
                            ToastUtil.$("收藏歌单成功");
                            mIvCollect.setTag(true);
                            mIvCollect.setImageResource(R.mipmap.ic_favorite_pink_500_24dp);
                        }
                    }
                })
        );
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mIvIcon.setImageResource(R.mipmap.ic_tag);
        loadData();
        checkCollected();
    }

    private void checkCollected() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean added = SQLite.selectCountOf()
                        .from(PlayList.class)
                        .where(PlayList_Table.type.eq(PlayList.TYPE_GEDAN))
                        .and(PlayList_Table.code.eq(mListId + ""))
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

    //更新UI
    private void setupUI(Gedan gedan) {
        mGedan = gedan;
        mText1.setText(gedan.desc);
        mText2.setText(gedan.title);
        mText3.setText(gedan.tag);
        setData(gedan.songList);
        loadPicture(gedan.pic300);
    }
}
