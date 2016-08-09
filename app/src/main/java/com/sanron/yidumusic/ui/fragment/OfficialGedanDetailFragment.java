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
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanInfoData;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.util.ToastUtil;
import com.sanron.yidumusic.widget.ItemClickHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

/**
 * 歌单详情界面
 * Created by sanron on 16-8-3.
 */
public class OfficialGedanDetailFragment extends SongListFragment implements ItemClickHelper.OnItemClickListener {

    private String mCode;
    private OfficialGedanInfoData mGedan;

    public static final String ARG_CODE = "code";


    public static OfficialGedanDetailFragment newInstance(String mCode) {
        OfficialGedanDetailFragment gedanDetailFragment = new OfficialGedanDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CODE, mCode);
        gedanDetailFragment.setArguments(bundle);
        return gedanDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCode = getArguments().getString(ARG_CODE);
    }

    private void loadData() {
        addSub(YiduApp.get()
                .getDataRepository()
                .getOfficialGedanInfo(mCode)
                .subscribe(new SubscriberAdapter<OfficialGedanInfoData>() {

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.$("获取数据失败");
                    }

                    @Override
                    public void onNext(OfficialGedanInfoData data) {
                        setupUI(data);
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
        playList.setName(mGedan.name);
        playList.setType(PlayList.TYPE_OFFICIAL_GEDAN);
        playList.setIcon(mGedan.pic);
        playList.setCode(mGedan.code);

        addSub(YiduDB.addWebPlayList(playList, mGedan.list)
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
        mIvIcon.setImageResource(R.mipmap.ic_access_time_white_18dp);
        loadData();
        checkCollected();
    }

    private void checkCollected() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean added = SQLite.selectCountOf()
                        .from(PlayList.class)
                        .where(PlayList_Table.type.eq(PlayList.TYPE_OFFICIAL_GEDAN))
                        .and(PlayList_Table.code.eq(mCode))
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
    private void setupUI(OfficialGedanInfoData gedan) {
        mGedan = gedan;
        mText1.setText(gedan.name);
        mText2.setText(gedan.desc);
        mText3.setText(formatDate(gedan.createTime));
        setData(gedan.list);
        loadPicture(gedan.pic);
    }

    private String formatDate(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }
}
