package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.YiduRetrofit;
import com.sanron.yidumusic.data.model.Song;
import com.sanron.yidumusic.data.model.response.BillCategoryData;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by sanron on 16-7-16.
 */
public class RadioFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private BillboardAdapter mBillboardAdapter;

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
        return R.layout.refresh_with_recycler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillboardAdapter = new BillboardAdapter();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mBillboardAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        addSub(YiduRetrofit.get()
                .getApiService()
                .getBillCategory()
                .compose(TransformerUtil.<BillCategoryData>apply())
                .map(new Func1<BillCategoryData, List<BillCategoryData.BillCategory>>() {
                    @Override
                    public List<BillCategoryData.BillCategory> call(BillCategoryData billCategoryData) {
                        return billCategoryData.content;
                    }
                })
                .subscribe(new Action1<List<BillCategoryData.BillCategory>>() {
                    @Override
                    public void call(List<BillCategoryData.BillCategory> billCategories) {
                        mBillboardAdapter.setData(billCategories);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mRefreshLayout.setRefreshing(false);
                    }
                })
        );
    }

    class BillboardAdapter extends RecyclerView.Adapter<BillboardAdapter.Holder> {

        private List<BillCategoryData.BillCategory> mBillCategories;
        private final int[] TOP_TEXT_COLORS = new int[]{
                0xFFF50000, 0xFFF77722, 0xFFFFC505
        };

        public void setData(List<BillCategoryData.BillCategory> data) {
            mBillCategories = data;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_billboard_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            BillCategoryData.BillCategory billCategory = mBillCategories.get(position);
            holder.setData(billCategory);
        }

        @Override
        public int getItemCount() {
            return mBillCategories == null ? 0 : mBillCategories.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_billcategory)
            TextView tvTitle;
            @BindView(R.id.iv_img)
            ImageView ivImg;
            @BindViews({R.id.tv_top1, R.id.tv_top2, R.id.tv_top3})
            List<TextView> tvTops;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(BillCategoryData.BillCategory data) {
                Glide.with(getContext())
                        .load(data.picS192)
                        .into(ivImg);
                tvTitle.setText(data.name);
                for (int i = 0; i < tvTops.size() && i < data.top3.size(); i++) {
                    Song song = data.top3.get(i);
                    SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(i + 1));
                    ssb.setSpan(new ForegroundColorSpan(TOP_TEXT_COLORS[i]), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(" ")
                            .append(song.title)
                            .append(" - ")
                            .append(song.author);
                    tvTops.get(i).setText(ssb);
                }
            }
        }
    }
}
