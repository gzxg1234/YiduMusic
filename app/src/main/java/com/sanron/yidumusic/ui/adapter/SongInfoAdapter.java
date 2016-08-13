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
import com.sanron.yidumusic.ui.base.PullAdapter;
import com.sanron.yidumusic.ui.vo.SongInfoVO;
import com.sanron.yidumusic.util.UITool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongInfoAdapter extends PullAdapter<SongInfoAdapter.ItemHolder> implements Player.OnPlayStateChangeListener, PlayUtil.OnPlayerBindListener {

    private Context mContext;
    private List<SongInfoVO> mItems;
    private PlayTrack mCurrentPlayTrack;
    private int mPlayingPosition = -1;

    public SongInfoAdapter(Context context, List<SongInfoVO> items) {
        mContext = context;
        mItems = items;
    }

    public void addItems(List<SongInfoVO> items) {
        if (mItems == null) {
            mItems = items;
        } else {
            mItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setItems(List<SongInfoVO> data) {
        mItems = data;
        mCurrentPlayTrack = PlayUtil.getCurrentMusic();
        notifyDataSetChanged();
    }

    public SongInfoVO getItems(int position) {
        return mItems.get(position);
    }

    public List<SongInfoVO> getItems() {
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
    public ItemHolder onCreateItemView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_gedan_song_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindItemView(final ItemHolder holder, final int position) {
        final SongInfoVO songInfoVO = getItems(position);
        final SongInfo songInfo = songInfoVO.getSongInfo();
        holder.tvPos.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(songInfo.title);
        SpannableStringBuilder ssb = new SpannableStringBuilder(songInfo.author + " - " + songInfo.albumTitle);
        if (songInfoVO.getMatchLocalMusic() != null) {
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

        if (mCurrentPlayTrack != null
                && songInfoVO.getSongInfo().songId == mCurrentPlayTrack.getSongId()) {
            mPlayingPosition = position;
            holder.tvPos.setVisibility(View.GONE);
            holder.icPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.tvPos.setVisibility(View.VISIBLE);
            holder.icPlaying.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == mPlayingPosition) {
                    PlayUtil.togglePlayPause();
                    return;
                }

                PlayUtil.clearQueue();
                PlayUtil.enqueueSongInfoVOs(mItems);
                PlayUtil.play(position);
            }
        });
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterView(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading_footer_layout, parent, false);
        return new FooterHolder(view);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder viewHolder, boolean hasMore) {
        if (hasMore) {
            ((FooterHolder) viewHolder).text.setText("加载中");
        } else {
            ((FooterHolder) viewHolder).text.setText("没有更多");
        }
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
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

    static class ItemHolder extends RecyclerView.ViewHolder {
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

    static class FooterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text) TextView text;

        public FooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}