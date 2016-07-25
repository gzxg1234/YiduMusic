package com.sanron.yidumusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.config.LocalMusicConfig;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.net.bean.response.LrcpicData;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.widget.Indexable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by sanron on 16-7-20.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ItemHolder> implements Indexable {

    private Context mContext;
    private List<LocalMusic> mData;
    private DataRepository mDataRepository;
    private SparseArray<Boolean> mCheckStates;
    private RecyclerView mRecyclerView;
    private boolean mIsMultiMode;
    private int mSortType;

    private OnItemActionClickListener mOnItemActionClickListener;
    private OnItemClickListener mOnItemClickListener;
    private MultiModeCallback mMultiModeCallback;

    public LocalMusicAdapter(Context context, DataRepository dataRepository) {
        mContext = context;
        mDataRepository = dataRepository;
    }

    public void setData(List<LocalMusic> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public LocalMusic getItem(int position) {
        return mData.get(position);
    }

    public List<LocalMusic> getData() {
        return mData;
    }


    public boolean isMultiMode() {
        return mIsMultiMode;
    }

    public void setMultiMode(boolean multiMode) {
        if (mIsMultiMode == multiMode) {
            return;
        }
        mIsMultiMode = multiMode;
        if (mIsMultiMode) {
            if (mCheckStates == null) {
                mCheckStates = new SparseArray<>();
            }
            if (mMultiModeCallback != null) {
                mMultiModeCallback.onStart();
            }
            notifyItemCheckViewChange(true);
        } else {
            mCheckStates.clear();
            notifyItemCheckViewChange(false);
        }
    }

    public void setMultiModeCallback(MultiModeCallback multiModeCallback) {
        mMultiModeCallback = multiModeCallback;
    }

    public void setItemChecked(int position, boolean checked) {
        if (mIsMultiMode && mCheckStates.get(position, false) != checked) {
            mCheckStates.put(position, checked);
            if (mMultiModeCallback != null) {
                mMultiModeCallback.onItemChecked(position, checked);
            }

            ItemHolder holder = (ItemHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.cbCheck.setChecked(checked);
            }
        }
    }

    public boolean isItemChecked(int position) {
        return mCheckStates.get(position, false);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_local_music_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
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
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        MusicInfo musicInfo = mData.get(position).getMusicInfo();
        String artist = musicInfo.getArtist();
        String album = musicInfo.getAlbum();
        String sArtist = "";
        StringBuilder albuminfo = new StringBuilder();
        if (TextUtils.isEmpty(artist) || MusicInfo.UNKNOWN.equals(artist)) {
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
                .subscribe(new SubscriberAdapter<LrcpicData>() {
                    @Override
                    public void onNext(LrcpicData lrcpicData) {
                        Glide.with(mContext)
                                .load(lrcpicData.songinfo.picRadio)
                                .placeholder(R.mipmap.default_song_pic)
                                .error(R.mipmap.default_song_pic)
                                .dontAnimate()
                                .into(holder.ivImg);
                    }
                });

        //多选模式下
        if (mIsMultiMode) {
            holder.cbCheck.setVisibility(View.VISIBLE);
            holder.ivAction.setVisibility(View.GONE);

            holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCheckStates.put(position, isChecked);
                    if (mMultiModeCallback != null) {
                        mMultiModeCallback.onItemChecked(position, isChecked);
                    }
                }
            });
            holder.cbCheck.setChecked(mCheckStates.get(position, false));
        } else {
            holder.cbCheck.setVisibility(View.GONE);
            holder.ivAction.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMultiMode) {
                    setItemChecked(position, !isItemChecked(position));
                } else if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setMultiMode(true);
                setItemChecked(position, true);
                return true;
            }
        });
        holder.ivAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemActionClickListener != null) {
                    mOnItemActionClickListener.onItemActionClick(v, position);
                }
            }
        });
    }


    private void notifyItemCheckViewChange(boolean inMultiChoice) {
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            View child = mRecyclerView.getChildAt(i);
            ItemHolder holder = (ItemHolder) mRecyclerView.getChildViewHolder(child);
            if (holder == null) {
                continue;
            }
            holder.cbCheck.setChecked(mCheckStates.get(holder.getAdapterPosition(), false));
            holder.cbCheck.setVisibility(inMultiChoice ? View.VISIBLE : View.GONE);
            holder.ivAction.setVisibility(!inMultiChoice ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public int getCheckedItemCount() {
        if (mCheckStates == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < mCheckStates.size(); i++) {
            if (mCheckStates.valueAt(i)) {
                count++;
            }
        }
        return count;
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

    public void setSortType(int sortType) {
        mSortType = sortType;
    }

    @Override
    public int getCount() {
        return getItemCount();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemActionClickListener(OnItemActionClickListener onItemActionClickListener) {
        mOnItemActionClickListener = onItemActionClickListener;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        public ImageView ivImg;
        @BindView(R.id.tv_title)
        public TextView tvTitle;
        @BindView(R.id.tv_artist)
        public TextView tvArtist;
        @BindView(R.id.iv_action)
        public ImageView ivAction;
        @BindView(R.id.cb_check)
        public CheckBox cbCheck;
        Subscription subscription;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemActionClickListener {
        void onItemActionClick(View view, int position);
    }

    public interface MultiModeCallback {
        void onItemChecked(int position, boolean checked);

        void onStart();
    }
}