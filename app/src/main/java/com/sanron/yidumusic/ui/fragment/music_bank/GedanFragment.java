package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.net.model.Gedan;
import com.sanron.yidumusic.data.net.model.OfficicalGedan;
import com.sanron.yidumusic.data.net.model.response.GedanCategoryData;
import com.sanron.yidumusic.data.net.model.response.GedanListData;
import com.sanron.yidumusic.data.net.model.response.OfficialGedanData;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.ui.dialog.SelectGedanCategoryDialog;
import com.sanron.yidumusic.util.ToastUtil;
import com.sanron.yidumusic.util.UITool;
import com.sanron.yidumusic.widget.OffsetDecoration;
import com.sanron.yidumusic.widget.PullAdapter;
import com.sanron.yidumusic.widget.RecyclerViewFloatHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by sanron on 16-7-16.
 */
public class GedanFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener, PullAdapter.OnLoadMoreListener {

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
        return R.layout.fragment_gedan;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGedanAdapter = new GedanAdapter();
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
        mGedanAdapter.setFirstLoadPic(true);
    }

    @OnClick(R.id.sticky_header)
    public void onHeaderClick() {
        addSub(mDataRepository
                .getGedanCategory()
                .compose(TransformerUtil.<GedanCategoryData>net())
                .subscribe(new Action1<GedanCategoryData>() {
                    @Override
                    public void call(GedanCategoryData gedanCategoryData) {
                        showGedanCategoryDlg(gedanCategoryData);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.$("获取数据失败，请重试");
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
                    .compose(TransformerUtil.<OfficialGedanData>io())
                    .map(new Func1<OfficialGedanData, GedanData>() {
                        @Override
                        public GedanData call(OfficialGedanData officialGedanData) {
                            GedanData gedanData = new GedanData();
//                            数据错误
//                            gedanData.haveMore = officialGedanData.havemore == 1;
                            gedanData.haveMore = mGedanAdapter.getRealItemCount()
                                    < officialGedanData.total;
                            gedanData.data = tranformOfficialGedan(officialGedanData.gedanList);
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
                .subscribe(new Action1<GedanData>() {
                    @Override
                    public void call(GedanData gedanData) {
                        mRefreshLayout.setRefreshing(false);
                        mGedanAdapter.onLoadComplete();
                        mGedanAdapter.setHasMore(gedanData.haveMore);
                        if (refresh) {
                            mGedanAdapter.setData(gedanData.data);
                        } else {
                            mGedanAdapter.addAll(gedanData.data);
                        }
                        mPage = page;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        if (refresh) {
                            mGedanAdapter.setData(null);
                        }
                        ToastUtil.$("获取数据失败");
                        mGedanAdapter.onLoadComplete();
                        mRefreshLayout.setRefreshing(false);
                    }
                })
        );
    }

    private List<GedanModel> tranformGedan(List<Gedan> gedenList) {
        List<GedanModel> models = new ArrayList<>();
        for (Gedan gedan : gedenList) {
            GedanModel model = new GedanModel();
            model.type = GedanModel.TYPE_GEDAN;
            model.pic = gedan.pic300;
            model.code = gedan.listid;
            model.text1 = gedan.title;
            model.text2 = gedan.tag;
            model.num = gedan.listenum;
            models.add(model);
        }
        return models;
    }

    private List<GedanModel> tranformOfficialGedan(List<OfficicalGedan> gedenList) {
        List<GedanModel> models = new ArrayList<>();
        for (OfficicalGedan gedan : gedenList) {
            GedanModel model = new GedanModel();
            model.type = GedanModel.TYPE_OFFICIAL;
            model.pic = gedan.pic;
            model.code = gedan.code;
            model.text1 = gedan.name;
            model.text2 = gedan.desc;
            models.add(model);
        }
        return models;
    }

    static class GedanData {
        boolean haveMore;
        List<GedanModel> data;
    }

    static class GedanModel {
        String pic;
        String text1;
        String text2;
        int num;
        String code;
        int type;
        static final int TYPE_GEDAN = 1;
        static final int TYPE_OFFICIAL = 2;
    }

    class GedanAdapter extends PullAdapter<GedanAdapter.Holder> {

        private List<GedanModel> mGedanList;
        private boolean mFirstLoadPic = true;

        public void setData(List<GedanModel> data) {
            mGedanList = data;
            mFirstLoadPic = true;
            notifyDataSetChanged();
        }

        public void setFirstLoadPic(boolean firstLoadPic) {
            mFirstLoadPic = firstLoadPic;
        }

        public void addAll(List<? extends GedanModel> models) {
            if (models != null) {
                if (mGedanList == null) {
                    mGedanList = new ArrayList<>();
                }
                mGedanList.addAll(models);
                mFirstLoadPic = true;
                notifyDataSetChanged();
            }
        }

        @Override
        public Holder onCreateRealViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_gedan_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            //图片停止滚动时再加载
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mFirstLoadPic = false;
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(
                                    recyclerView.getChildAt(i));
                            if (holder instanceof Holder) {
                                GedanModel model = ((Holder) holder).data;
                                if (model != null) {
                                    Glide.with(getContext())
                                            .load(model.pic)
                                            .into(((Holder) holder).ivImg);
                                }
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onBindRealViewHolder(Holder holder, int position) {
            holder.setData(mGedanList.get(position));
        }

        @Override
        public int getRealItemCount() {
            return mGedanList == null ? 0 : mGedanList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateFooterView(ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_footer_layout,
                    parent, false);
            return new FooterHolder(view);
        }

        @Override
        public void onBindFooterViewHolder(RecyclerView.ViewHolder viewHolder, boolean hasMore) {
            String label = hasMore ? "加载中" : "没有更多";
            ((FooterHolder) viewHolder).tvLabel.setText(label);
        }

        class FooterHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_text)
            TextView tvLabel;

            public FooterHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_img)
            ImageView ivImg;
            @BindView(R.id.tv_text1)
            TextView tvText1;
            @BindView(R.id.tv_text2)
            TextView tvText2;
            @BindView(R.id.tv_listen_num)
            TextView tvListenNum;
            GedanModel data;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(GedanModel data) {
                this.data = data;
                Glide.clear(ivImg);
                ivImg.setImageBitmap(null);
                if (mFirstLoadPic) {
                    Glide.with(getContext())
                            .load(data.pic)
                            .into(ivImg);
                }
                tvText1.setText(data.text1);
                tvText2.setText(data.text2);
                if (data.type == GedanModel.TYPE_OFFICIAL) {
                    tvListenNum.setVisibility(View.INVISIBLE);
                } else {
                    tvListenNum.setVisibility(View.VISIBLE);
                    if (data.num > 100000) {
                        tvListenNum.setText(data.num / 10000 + "万");
                    } else {
                        tvListenNum.setText(String.valueOf(data.num));
                    }
                }
            }
        }
    }


}
