package com.sanron.yidumusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.config.LocalMusicConfig;
import com.sanron.yidumusic.data.db.bean.LocalMusic;
import com.sanron.yidumusic.data.db.bean.MusicInfo;
import com.sanron.yidumusic.data.net.model.response.LrcpicData;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.widget.Indexable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sanron on 16-7-20.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.MusicInfoHolder> implements Indexable {

    private Context mContext;
    private List<LocalMusic> mData;
    private DataRepository mDataRepository;

    public LocalMusicAdapter(Context context, DataRepository dataRepository) {
        mContext = context;
        mDataRepository = dataRepository;
    }

    public void setData(List<LocalMusic> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public MusicInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_local_music_item, parent, false);
        return new MusicInfoHolder(view);
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
    public void onBindViewHolder(final MusicInfoHolder holder, int position) {
        MusicInfo musicInfo = mData.get(position).getMusicInfo();
        String artist = musicInfo.getArtist();
        String album = musicInfo.getAlbum();
        String sArtist = "";
        StringBuilder albuminfo = new StringBuilder();
        if (TextUtils.isEmpty(artist) || "<unknown>".equals(artist)) {
            albuminfo.append("未知");
        } else {
            albuminfo.append(artist);
            sArtist = artist;
        }
        albuminfo.append(" - ");
        if (TextUtils.isEmpty(album)) {
            albuminfo.append("未知");
        } else {
            albuminfo.append(album);
        }
        holder.tvTitle.setText(musicInfo.getTitle());
        holder.tvArtist.setText(albuminfo.toString());

        holder.ivImg.setImageResource(R.mipmap.default_song_pic);
        if (holder.subscription != null) {
            holder.subscription.unsubscribe();
        }
        holder.subscription = mDataRepository.getLrcpic(musicInfo.getTitle(), sArtist)
                .subscribe(new Action1<LrcpicData>() {
                    @Override
                    public void call(LrcpicData lrcpicData) {
                        Glide.with(mContext)
                                .load(lrcpicData.songinfo.picRadio)
                                .placeholder(R.mipmap.default_song_pic)
                                .error(R.mipmap.default_song_pic)
                                .dontAnimate()
                                .into(holder.ivImg);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public char getIndexForPosition(int position) {
        String str;
        MusicInfo musicInfo = mData.get(position).getMusicInfo();
        switch (mSortType) {
            case LocalMusicConfig.SORT_BY_ALBUM: {
                str = musicInfo.getAlbum();
            }
            break;
            case LocalMusicConfig.SORT_BY_ARTIST: {
                str = musicInfo.getArtist();
            }
            break;
            case LocalMusicConfig.SORT_BY_TITLE: {
                str = musicInfo.getTitle();
            }
            break;
            default:
                return (char) -1;
        }
        if (TextUtils.isEmpty(str)) {
            return '#';
        } else {
            char letter = PinyinHelper.convertToPinyinString(str.substring(0, 1), "", PinyinFormat.WITHOUT_TONE)
                    .toUpperCase().charAt(0);
            if (letter < 'A' || letter > 'Z') {
                return '#';
            }
            return letter;
        }
    }

    private int mSortType;

    public void setSortType(int sortType) {
        mSortType = sortType;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    static class MusicInfoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_artist)
        TextView tvArtist;
        @BindView(R.id.iv_operator)
        ImageView ivOperator;
        Subscription subscription;

        public MusicInfoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}