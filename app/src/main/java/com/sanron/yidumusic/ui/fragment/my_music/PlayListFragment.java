package com.sanron.yidumusic.ui.fragment.my_music;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.DBObserver;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayList_Table;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.adapter.PlayListAdapter;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.ui.vo.PlayListVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by sanron on 16-7-20.
 */
public class PlayListFragment extends LazyLoadFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PlayListAdapter mPlayListAdapter;
    private Subscription mTableSubscriber;

    @Override
    protected int getLayout() {
        return R.layout.recycler_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPlayListAdapter = new PlayListAdapter(getContext(), null);
        mTableSubscriber = DBObserver.get().toObservable(PlayList.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Class<? extends Model>>() {
                    @Override
                    public void call(Class<? extends Model> aClass) {
                        loadData();
                    }
                });
    }

    @Override
    public void onDestroy() {
        mTableSubscriber.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, "新建")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: {
                new NewListDialog(getContext()).show();
            }
            break;
        }
        return true;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mPlayListAdapter);
    }

    @Override
    protected void onLazyLoad() {
        loadData();
    }

    private void loadData() {
        addSub(YiduDB.getPlayList()
                .map(new Func1<List<PlayList>, List<PlayListVO>>() {
                    @Override
                    public List<PlayListVO> call(List<PlayList> playLists) {
                        List<PlayListVO> list = new ArrayList<>();
                        for (PlayList playList : playLists) {
                            list.add(PlayListVO.from(playList));
                        }
                        return list;
                    }
                })
                .compose(TransformerUtil.<List<PlayListVO>>io())
                .subscribe(new Action1<List<PlayListVO>>() {
                    @Override
                    public void call(List<PlayListVO> playListVOs) {
                        mPlayListAdapter.setData(playListVOs);
                    }
                })
        );
    }

    static class NewListDialog extends Dialog {

        @BindView(R.id.et_input)
        EditText mEtInput;

        public NewListDialog(Context context) {
            super(context);
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dlg_input_listname, null);
            ButterKnife.bind(this, view);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(view);
            setCanceledOnTouchOutside(false);
        }

        @OnClick(R.id.btn_ok)
        void onOK() {
            String name = mEtInput.getText().toString();
            if (TextUtils.isEmpty(name)) {
                mEtInput.setError("请输入名称");
            } else {
                if (SQLite.selectCountOf()
                        .from(PlayList.class)
                        .where(PlayList_Table.name.eq(name))
                        .and(PlayList_Table.type.eq(PlayList.TYPE_USER))
                        .count() == 0) {
                    PlayList playList = new PlayList();
                    playList.setName(name);
                    playList.setAddTime(System.currentTimeMillis());
                    playList.setType(PlayList.TYPE_USER);
                    playList.async()
                            .success(new Transaction.Success() {
                                @Override
                                public void onSuccess(Transaction transaction) {
                                    cancel();
                                }
                            }).save();
                } else {
                    mEtInput.setError("歌单名已存在");
                }
            }
        }

        @OnClick(R.id.btn_cancel)
        void onCancel() {
            cancel();
        }
    }
}
