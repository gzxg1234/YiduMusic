package com.sanron.yidumusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.widget.PullAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GedanAdapter extends PullAdapter<GedanAdapter.ItemHolder> {

    private List<GedanModel> mItems;
    private Context mContext;

    public GedanAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<GedanModel> data) {
        mItems = data;
        notifyDataSetChanged();
    }


    public void addAll(List<? extends GedanModel> models) {
        if (models != null) {
            if (mItems == null) {
                mItems = new ArrayList<>();
            }
            mItems.addAll(models);
            notifyDataSetChanged();
        }
    }

    @Override
    public ItemHolder onCreateRealViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_gedan_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        //图片停止滚动时再加载
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(mContext).resumeRequests();
                } else {
                    Glide.with(mContext).pauseRequests();
                }
            }
        });
    }

    @Override
    public void onBindRealViewHolder(ItemHolder holder, int position) {
        GedanModel data = mItems.get(position);
        holder.tvText1.setText(data.text1);
        holder.tvText2.setText(data.text2);
        if (data.type == GedanModel.TYPE_OFFICIAL) {
            holder.tvListenNum.setVisibility(View.INVISIBLE);
        } else {
            holder.tvListenNum.setVisibility(View.VISIBLE);
            if (data.num > 100000) {
                holder.tvListenNum.setText(data.num / 10000 + "万");
            } else {
                holder.tvListenNum.setText(String.valueOf(data.num));
            }
        }
        holder.ivImg.setImageBitmap(null);
        Glide.with(mContext)
                .load(data.pic)
                .into(holder.ivImg);
    }

    @Override
    public int getRealItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterView(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading_footer_layout,
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

    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        public ImageView ivImg;
        @BindView(R.id.tv_text1)
        public TextView tvText1;
        @BindView(R.id.tv_text2)
        public TextView tvText2;
        @BindView(R.id.tv_listen_num)
        public TextView tvListenNum;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class GedanModel {
        public String pic;
        public String text1;
        public String text2;
        public int num;
        public String code;
        public int type;
        public static final int TYPE_GEDAN = 1;
        public static final int TYPE_OFFICIAL = 2;
    }
}

