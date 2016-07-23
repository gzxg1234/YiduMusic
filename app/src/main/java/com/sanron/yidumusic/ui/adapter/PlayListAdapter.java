package com.sanron.yidumusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.ui.vo.PlayListVO;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanron on 16-7-23.
 */
public class PlayListAdapter extends SectionAdapter<PlayListAdapter.SectionHolder, PlayListAdapter.ItemHolder> {

    private Context mContext;
    private List<PlayListVO> mItems;

    public static final int SECTION_SELF = 1;
    public static final int SECTION_WEB = 2;

    public PlayListAdapter(Context context, List<PlayListVO> items) {
        mContext = context;
        mItems = items;
    }

    public void setData(List<PlayListVO> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public SectionHolder onCreateSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_playlist_section_item, parent, false);
        return new SectionHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SectionHolder holder, int section) {
        if (section == SECTION_SELF) {
            holder.tv.setText("自建歌单");
        } else {
            holder.tv.setText("网络歌单");
        }
    }

    @Override
    public ItemHolder onCreateRealItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_playlist_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindRealItemViewHoler(ItemHolder holder, int position) {
        PlayListVO playListVO = mItems.get(position);
        if (playListVO.getType() == PlayList.TYPE_FAVORITE) {
            holder.ivImg.setImageResource(R.mipmap.ic_favorite_list);
        } else if (playListVO.getType() == PlayList.TYPE_USER) {
            holder.ivImg.setImageResource(R.mipmap.icon_normal_list);
        } else {

        }
        holder.tvName.setText(playListVO.getName());
        holder.tvCount.setText(playListVO.getMusicCount() + "首");
    }

    @Override
    public int getRealItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getSectionForPosition(int position) {
        int type = mItems.get(position).getType();
        if (type == PlayListVO.TYPE_FAVORITE || type == PlayListVO.TYPE_USER) {
            return SECTION_SELF;
        } else {
            return SECTION_WEB;
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_music_count)
        TextView tvCount;
        @BindView(R.id.iv_action)
        ImageView ivAction;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class SectionHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public SectionHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }
}
