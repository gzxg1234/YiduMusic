package com.sanron.yidumusic.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.playback.PlayTrack;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.playback.Player;
import com.sanron.yidumusic.ui.vo.RemotePlayTrack;
import com.sanron.yidumusic.util.UITool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GedanItemAdapter extends RecyclerView.Adapter<GedanItemAdapter.ItemHolder> implements Player.OnPlayStateChangeListener, PlayUtil.OnPlayerReadyListener {

    private Context mContext;
    private List<RemotePlayTrack> mItems;
    private PlayTrack mCurrentPlayTrack;

    public GedanItemAdapter(Context context, List<RemotePlayTrack> items) {
        mContext = context;
        mItems = items;
    }

    public void setData(List<RemotePlayTrack> data) {
        mItems = data;
        mCurrentPlayTrack = PlayUtil.getCurrentMusic();
        notifyDataSetChanged();
    }

    public RemotePlayTrack getData(int position) {
        return mItems.get(position);
    }

    public List<RemotePlayTrack> getData() {
        return mItems;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        PlayUtil.addOnPlayerBindListener(this);
    }

    @Override
    public void onReady(Player player) {
        PlayUtil.removeOnPlayerBindListener(this);
        PlayUtil.addPlayStateChangeListener(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        PlayUtil.removePlayStateChangeListener(this);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_gedan_song_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        final RemotePlayTrack remotePlayTrack = getData(position);
        final SongInfo songInfo = remotePlayTrack.getSongInfo();
        holder.tvPos.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(songInfo.title);
        SpannableStringBuilder ssb = new SpannableStringBuilder(songInfo.author + " - " + songInfo.albumTitle);
        if (remotePlayTrack.getMatchLocalMusic() != null) {
            //本地歌曲有记录
            ssb.insert(0, " ");
            Drawable drawable = UITool.getTintDrawable(mContext,
                    R.mipmap.ic_ok,
                    mContext.getResources().getColor(R.color.colorPrimary));
            //设置图标大小和文字高度一样
            Paint.FontMetricsInt fontMetricsInt = holder.tvArtist.getPaint().getFontMetricsInt();
            int height = fontMetricsInt.descent - fontMetricsInt.ascent;
            drawable.setBounds(0, 0, height, height);
            ssb.setSpan(new ImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.tvArtist.setText(ssb);

        if (remotePlayTrack.equals(mCurrentPlayTrack)) {
            holder.tvPos.setVisibility(View.GONE);
            holder.icPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.tvPos.setVisibility(View.VISIBLE);
            holder.icPlaying.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public void onPlayStateChange(int state) {
        switch (state) {
            case Player.STATE_IDLE:
            case Player.STATE_PREPARING: {
                mCurrentPlayTrack = PlayUtil.getCurrentMusic();
                notifyDataSetChanged();
            }
            break;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ic_playing) public ImageView icPlaying;
        @BindView(R.id.tv_pos) public TextView tvPos;
        @BindView(R.id.tv_title) public TextView tvTitle;
        @BindView(R.id.tv_artist) public TextView tvArtist;
        @BindView(R.id.iv_action) public ImageView ivAction;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}