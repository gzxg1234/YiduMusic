package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.net.bean.Gedan;
import com.sanron.yidumusic.data.net.bean.OfficicalGedan;
import com.sanron.yidumusic.data.net.bean.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.bean.response.GedanListData;
import com.sanron.yidumusic.data.net.bean.response.OfficialGedanListData;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.rx.ToastSubscriber;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.activity.MainActivity;
import com.sanron.yidumusic.ui.adapter.GedanAdapter;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.ui.base.PullAdapter;
import com.sanron.yidumusic.ui.dialog.SelectGedanCategoryDialog;
import com.sanron.yidumusic.util.UITool;
import com.sanron.yidumusic.widget.OffsetDecoration;
import com.sanron.yidumusic.widget.RecyclerViewFloatHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by sanron on 16-7-16.
 */
public class GedanListFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener, PullAdapter.OnLoadMoreListener, GedanAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.sticky_header)
    RecyclerViewFloatHeader mStickyHeader;
    @BindView(R.id.tv_current_tag)
    TextView mTvCurTag;

    private DataRepository mDataRepository;
    private int mPage;
    private static final int PAGE_SIZE = 20;
    private GedanAdapter mGedanAdapter;
    private String mCurrentTag = "全部";

    @Override
    protected void onLazyLoad() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_gedan_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGedanAdapter = new GedanAdapter(getContext());
        mDataRepository = YiduApp.get().getDataRepository();
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        final int space = UITool.dpToPx(getContext(), 8);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mGedanAdapter);
        mRecyclerView.addItemDecoration(new OffsetDecoration(space, space));
        mStickyHeader.attachRecyclerView(mRecyclerView);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(this);
        mGedanAdapter.setOnLoadMoreListener(this);
        mGedanAdapter.setOnItemClickListener(this);
        mTvCurTag.setText(mCurrentTag);
    }

    @OnClick(R.id.sticky_header)
    public void onHeaderClick() {
        addSub(mDataRepository
                .getGedanCategory()
                .subscribe(new ToastSubscriber<GedanCategoryData>(getContext()) {
                    @Override
                    public void onNext(GedanCategoryData gedanCategoryData) {
                        showGedanCategoryDlg(gedanCategoryData);
                    }
                })
        );
    }


    private void showGedanCategoryDlg(GedanCategoryData gedanCategoryData) {
        SelectGedanCategoryDialog dlg = new SelectGedanCategoryDialog(getContext(),
                gedanCategoryData);
        dlg.setCallback(new SelectGedanCategoryDialog.Callback() {
            @Override
            public void onSelect(String tag) {
                if (!mCurrentTag.equals(tag)) {
                    setCurrentTag(tag);
                }
            }
        });
        dlg.show();
    }

    private void setCurrentTag(String tag) {
        mCurrentTag = tag;
        mTvCurTag.setText(tag);
        mRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        load(true);
    }

    @Override
    public void onLoad() {
        load(false);
    }

    private void load(final boolean refresh) {
        if (refresh) {
            mGedanAdapter.setHasMore(true);
            mGedanAdapter.setLoadEnable(true);
            mPage = 0;
        }
        final int page = refresh ? 1 : mPage + 1;
        Observable<GedanData> observable;
        if ("全部".equals(mCurrentTag)) {
            observable = mDataRepository
                    .getGedanList(page, PAGE_SIZE)
                    .compose(TransformerUtil.<GedanListData>net())
                    .map(new Func1<GedanListData, GedanData>() {
                        @Override
                        public GedanData call(GedanListData gedanListData) {
                            GedanData gedanData = new GedanData();
                            gedanData.haveMore = gedanListData.havemore == 1;
                            gedanData.data = tranformGedan(gedanListData.gedans);
                            return gedanData;
                        }
                    });
        } else if ("音乐专题".equals(mCurrentTag)) {
            observable = mDataRepository
                    .getOfficialGedan((page - 1) * PAGE_SIZE, PAGE_SIZE)
                    .compose(TransformerUtil.<OfficialGedanListData>io())
                    .map(new Func1<OfficialGedanListData, GedanData>() {
                        @Override
                        public GedanData call(OfficialGedanListData officialGedanListData) {
                            GedanData gedanData = new GedanData();
//                            数据错误
//                            gedanData.haveMore = officialGedanListData.havemore == 1;
                            gedanData.haveMore = mGedanAdapter.getCount()
                                    < officialGedanListData.total;
                            gedanData.data = tranformOfficialGedan(officialGedanListData.gedanList);
                            return gedanData;
                        }
                    });
        } else {
            observable = mDataRepository
                    .getGedanListByTag(mCurrentTag, page, PAGE_SIZE)
                    .compose(TransformerUtil.<GedanListData>net())
                    .map(new Func1<GedanListData, GedanData>() {
                        @Override
                        public GedanData call(GedanListData gedanListData) {
                            GedanData gedanData = new GedanData();
                            gedanData.haveMore = gedanListData.havemore == 1;
                            gedanData.data = tranformGedan(gedanListData.gedans);
                            return gedanData;
                        }
                    });

        }

        addSub(observable
                .subscribe(new ToastSubscriber<GedanData>(getContext()) {
                    @Override
                    public void onNext(GedanData gedanData) {
                        mGedanAdapter.setHasMore(gedanData.haveMore);
                        if (refresh) {
                            mGedanAdapter.setItems(gedanData.data);
                            setState(STATE_SUCCESS);
                            setFirstLoaded(true);
                        } else {
                            mGedanAdapter.addItems(gedanData.data);
                        }
                        mPage = page;
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mGedanAdapter.onLoadComplete();
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (refresh) {
                            mGedanAdapter.setItems(null);
                            setState(STATE_FAILED);
                            setFirstLoaded(true);
                        }
                    }
                })
        );
    }


    @Override
    protected void onRetry() {
        onRefresh();
    }

    private List<GedanAdapter.GedanModel> tranformGedan(List<Gedan> gedenList) {
        List<GedanAdapter.GedanModel> models = new ArrayList<>();
        for (Gedan gedan : gedenList) {
            GedanAdapter.GedanModel model = new GedanAdapter.GedanModel();
            model.type = GedanAdapter.GedanModel.TYPE_GEDAN;
            model.pic = gedan.pic300;
            model.code = gedan.listid + "";
            model.text1 = gedan.title;
            model.text2 = gedan.tag;
            model.num = gedan.listenum;
            models.add(model);
        }
        return models;
    }

    private List<GedanAdapter.GedanModel> tranformOfficialGedan(List<OfficicalGedan> gedenList) {
        List<GedanAdapter.GedanModel> models = new ArrayList<>();
        for (OfficicalGedan gedan : gedenList) {
            GedanAdapter.GedanModel model = new GedanAdapter.GedanModel();
            model.type = GedanAdapter.GedanModel.TYPE_OFFICIAL;
            model.pic = gedan.pic;
            model.code = gedan.code;
            model.text1 = gedan.name;
            model.text2 = gedan.desc;
            models.add(model);
        }
        return models;
    }

    @Override
    public void onItemClick(View view, int position) {
        GedanAdapter.GedanModel model = mGedanAdapter.getItem(position);
        if (model.type == GedanAdapter.GedanModel.TYPE_GEDAN) {
            ((MainActivity) getActivity()).showGedanDetail(Long.valueOf(model.code));
        } else if (model.type == GedanAdapter.GedanModel.TYPE_OFFICIAL) {
            ((MainActivity) getActivity()).showOfficialGedanDetail(model.code);
        }
    }

    static class GedanData {
        boolean haveMore;
        List<GedanAdapter.GedanModel> data;
    }

}
