package com.sanron.yidumusic.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayList_Table;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.util.ToastUtil;
import com.sanron.yidumusic.util.UITool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;


/**
 * 添加歌单选择对话框
 */
public class SelectPlayListDlg extends BottomSheetDialog implements AdapterView.OnItemClickListener {
    @BindView(R.id.lv_play_list) ListView mLvPlayList;
    private List<PlayList> mPlayLists;
    private List<MusicInfo> mMusicInfos;

    private SelectPlayListDlg(@NonNull Context context, List<MusicInfo> musicInfos, List<PlayList> playLists) {
        super(context);
        mMusicInfos = musicInfos;
        mPlayLists = playLists;
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_select_playlist, null);
        int[] screenSize = UITool.getScreenSize(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(screenSize[0], screenSize[1] * 2 / 3));
        setContentView(view);
        ButterKnife.bind(this, view);
        setCanceledOnTouchOutside(true);
        mLvPlayList.setOnItemClickListener(this);
    }

    @Override
    public void show() {
        List<String> names = new ArrayList<>();
        for (PlayList playList : mPlayLists) {
            names.add(playList.getName());
        }
        mLvPlayList.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, names));
        super.show();
    }

    public static void show(final Context context, final List<MusicInfo> musicInfos) {
        Observable
                .create(new Observable.OnSubscribe<List<PlayList>>() {
                    @Override
                    public void call(Subscriber<? super List<PlayList>> subscriber) {
                        List<PlayList> playLists = SQLite.select()
                                .from(PlayList.class)
                                .where(PlayList_Table.type.eq(PlayList.TYPE_USER))
                                .or(PlayList_Table.type.eq(PlayList.TYPE_FAVORITE))
                                .orderBy(PlayList_Table.addTime, false)
                                .queryList();
                        subscriber.onNext(playLists);
                    }
                })
                .compose(TransformerUtil.<List<PlayList>>io())
                .subscribe(new SubscriberAdapter<List<PlayList>>() {
                    @Override
                    public void onNext(List<PlayList> playLists) {
                        super.onNext(playLists);
                        new SelectPlayListDlg(context, musicInfos, playLists).show();
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        YiduDB.addToPlayList(mMusicInfos, mPlayLists.get(position))
                .compose(TransformerUtil.<int[]>io())
                .subscribe(new SubscriberAdapter<int[]>() {
                    @Override
                    public void onNext(int[] result) {
                        StringBuilder msg = new StringBuilder("成功添加" + result[0] + "首歌曲");
                        if (result[1] > 0) {
                            msg.append(",").append(result[1]).append("首已存在");
                        }
                        ToastUtil.$(getContext(), msg.toString());
                    }
                });
        cancel();
    }
}
