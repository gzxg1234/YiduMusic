package com.sanron.yidumusic.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.playback.PlayTrack;
import com.sanron.yidumusic.playback.PlayUtil;
import com.sanron.yidumusic.playback.Player;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 播放队列窗口
 */
public class ShowPlayQueueWindow extends ScrimPopupWindow implements Player.OnPlayStateChangeListener, View.OnClickListener {

    View mContentView;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.view_clear)
    TextView mViewClear;
    @BindView(R.id.lv_queue_music)
    RecyclerView mLvQueue;

    private List<PlayTrack> mQueue;
    private Context mContext;
    private QueueItemAdapter mAdapter;

    public ShowPlayQueueWindow(Activity activity) {
        super(activity);
        mContext = activity;
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(screenHeight * 2 / 3);
        setAnimationStyle(R.style.MyWindowAnim);
        mContentView = LayoutInflater.from(activity).inflate(R.layout.window_queue_music, null);
        setContentView(mContentView);
        ButterKnife.bind(this, mContentView);

        mQueue = PlayUtil.getQueue();
        mTvTitle.setText("播放队列(" + mQueue.size() + ")");
        mAdapter = new QueueItemAdapter();
        mLvQueue.setLayoutManager(new LinearLayoutManager(mContext));
        mLvQueue.setAdapter(mAdapter);
        mLvQueue.post(new Runnable() {
            @Override
            public void run() {
                mLvQueue.scrollToPosition(PlayUtil.getCurrentIndex());
            }
        });

        mViewClear.setOnClickListener(this);
        PlayUtil.addPlayStateChangeListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        PlayUtil.removePlayStateChangeListener(this);
    }


    @Override
    public void onPlayStateChange(int state) {
        if (state == Player.STATE_PREPARING) {
            mAdapter.notifyDataSetChanged();
            mLvQueue.scrollToPosition(PlayUtil.getCurrentIndex());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_clear: {
                PlayUtil.clearQueue();
                mQueue.clear();
                mTvTitle.setText("播放队列(0)");
                mAdapter.notifyDataSetChanged();
            }
            break;
        }
    }


    public class QueueItemAdapter extends RecyclerView.Adapter<QueueItemAdapter.QueueItemHolder> {


        final int DEFAULT_TITLE_COLOR = mContext.getResources().getColor(R.color.textColorPrimary);
        final int DEFAULT_ARTIST_COLOR = mContext.getResources().getColor(R.color.textColorSecondary);
        final int PLAY_TEXT_COLOR = mContext.getResources().getColor(R.color.colorAccent);

        @Override
        public QueueItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_queue_item, parent, false);
            return new QueueItemHolder(view);
        }

        public PlayTrack getItem(int position) {
            return mQueue.get(position);
        }

        @Override
        public void onBindViewHolder(final QueueItemHolder holder, final int position) {
            PlayTrack music = getItem(position);

            if (position == PlayUtil.getCurrentIndex()) {
                holder.tvArtist.setTextColor(PLAY_TEXT_COLOR);
                holder.tvTitle.setTextColor(PLAY_TEXT_COLOR);
                holder.sign.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitle.setTextColor(DEFAULT_TITLE_COLOR);
                holder.tvArtist.setTextColor(DEFAULT_ARTIST_COLOR);
                holder.sign.setVisibility(View.INVISIBLE);
            }

            holder.tvTitle.setText(music.getTitle());
            String artist = music.getArtist();
            artist = TextUtils.isEmpty(artist) || MusicInfo.UNKNOWN.equals(artist) ? "未知歌手" : artist;
            holder.tvArtist.setText(artist);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPos = holder.getAdapterPosition();
                    if (PlayUtil.getCurrentIndex() == adapterPos) {
                        PlayUtil.togglePlayPause();
                        return;
                    }
                    PlayUtil.play(adapterPos);
                }
            });
            holder.ibtnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayUtil.dequeue(position);
                    mQueue.remove(position);
                    mTvTitle.setText("播放队列(" + mQueue.size() + ")");
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mQueue == null ? 0 : mQueue.size();
        }

        class QueueItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.playing_sign)
            View sign;
            @BindView(R.id.tv_title)
            TextView tvTitle;
            @BindView(R.id.tv_artist)
            TextView tvArtist;
            @BindView(R.id.ibtn_remove)
            ImageButton ibtnRemove;

            public QueueItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
