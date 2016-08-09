package com.sanron.yidumusic.ui.fragment.music_bank;

import android.content.Context;
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
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.data.net.bean.response.BillCategoryData;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.rx.ToastSubscriber;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by sanron on 16-7-16.
 */
public class BillFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private BillboardAdapter mBillboardAdapter;
    private DataRepository mDataRepository;

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
    protected void onRetry() {
        onRefresh();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillboardAdapter = new BillboardAdapter(getContext(), null);
        mDataRepository = YiduApp.get().getDataRepository();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mBillboardAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        addSub(mDataRepository.getBillCategory()
                .subscribe(new ToastSubscriber<BillCategoryData>(getContext()) {
                    @Override
                    public void onNext(BillCategoryData billCategoryData) {
                        mBillboardAdapter.setData(billCategoryData.content);
                        setState(STATE_SUCCESS);
                        setFirstLoaded(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setState(STATE_FAILED);
                        setFirstLoaded(true);
                    }

                    @Override
                    public void onCompleted() {
                        mRefreshLayout.setRefreshing(false);
                    }
                })
        );
    }

    static class BillboardAdapter extends RecyclerView.Adapter<BillboardAdapter.ItemHolder> {

        private Context mContext;
        private List<BillCategoryData.BillCategory> mItems;

        public BillboardAdapter(Context context, List<BillCategoryData.BillCategory> items) {
            mItems = items;
            mContext = context;
        }

        private final int[] TOP_TEXT_COLORS = new int[]{
                0xFFF50000, 0xFFF77722, 0xFFFFC505
        };

        public void setData(List<BillCategoryData.BillCategory> data) {
            mItems = data;
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_billboard_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int position) {
            BillCategoryData.BillCategory data = mItems.get(position);
            Glide.with(mContext)
                    .load(data.picS192)
                    .into(itemHolder.ivImg);
            itemHolder.tvTitle.setText(data.name);
            for (int i = 0; i < itemHolder.tvTops.size() && i < data.top3.size(); i++) {
                SongInfo songInfo = data.top3.get(i);
                SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(i + 1));
                ssb.setSpan(new ForegroundColorSpan(TOP_TEXT_COLORS[i]), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(" ")
                        .append(songInfo.title)
                        .append(" - ")
                        .append(songInfo.author);
                itemHolder.tvTops.get(i).setText(ssb);
            }
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }

        static class ItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_billcategory)
            TextView tvTitle;
            @BindView(R.id.iv_img)
            ImageView ivImg;
            @BindViews({R.id.tv_top1, R.id.tv_top2, R.id.tv_top3})
            List<TextView> tvTops;

            public ItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
