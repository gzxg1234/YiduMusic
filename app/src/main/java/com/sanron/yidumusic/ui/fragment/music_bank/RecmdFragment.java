package com.sanron.yidumusic.ui.fragment.music_bank;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.net.bean.Album;
import com.sanron.yidumusic.data.net.bean.FocusPic;
import com.sanron.yidumusic.data.net.bean.Gedan;
import com.sanron.yidumusic.data.net.bean.SongInfo;
import com.sanron.yidumusic.data.net.bean.response.HomeData;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.rx.ToastSubscriber;
import com.sanron.yidumusic.ui.activity.MainActivity;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.util.UITool;
import com.sanron.yidumusic.widget.OffsetDecoration;
import com.viewpagerindicator.PageIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/3/10.
 */
public class RecmdFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private RecmdAdapter mRecmdAdapter;
    private DataRepository mDataRepository;
    private static final int FOCUS_NUM = 10;
    private static final int HOTSONGLIST_NUM = 6;
    private static final int RECMD_ALBUM_NUM = 6;
    private static final int RECMD_SONG_NUM = 5;

    @Override
    protected void onLazyLoad() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                loadData();
            }
        });
    }

    public void loadData() {
        addSub(mDataRepository.getHomeData(FOCUS_NUM, HOTSONGLIST_NUM, RECMD_ALBUM_NUM, RECMD_SONG_NUM)
                .subscribe(new ToastSubscriber<HomeData>(getContext()) {
                    @Override
                    public void onCompleted() {
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setState(STATE_FAILED);
                        setFirstLoaded(true);
                    }

                    @Override
                    public void onNext(HomeData homeData) {
                        mRecmdAdapter.setData(homeData);
                        setState(STATE_SUCCESS);
                        setFirstLoaded(true);
                    }
                })
        );
    }

    @Override
    protected void onRetry() {
        onRefresh();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecmdAdapter = new RecmdAdapter();
        mDataRepository = YiduApp.get().getDataRepository();
    }

    @Override
    protected int getLayout() {
        return R.layout.refresh_with_recycler;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecmdAdapter);
        mRecyclerView.addItemDecoration(new OffsetDecoration(0, UITool.dpToPx(getContext(), 16)));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * 推荐页面适配器
     */
    class RecmdAdapter extends RecyclerView.Adapter {

        private HomeData mHomeData;
        private static final int TYPE_FOCUS = 1;
        private static final int TYPE_ITEM_2 = 2;
        private static final int TYPE_GRID = 3;
        private static final int TYPE_LIST = 4;

        private static final int POS_FOCUS = 0;
        private static final int POS_ITEM2 = 1;
        private static final int POS_HOT_SONGLIST = 2;
        private static final int POS_RECMD_ALBUM = 3;
        private static final int POS_RECMD_SONG = 4;

        public void setData(HomeData data) {
            mHomeData = data;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_FOCUS: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_focus_item, parent, false);
                    return new FocusHolder(view);
                }
                case TYPE_ITEM_2: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_fenlei_singer_scene, parent, false);
                    return new Item2Holder(view);
                }
                case TYPE_GRID: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_grid, parent, false);
                    return new GridHolder(view);
                }
                case TYPE_LIST: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_list, parent, false);
                    return new ListHolder(view);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (position) {
                case POS_FOCUS: {
                    if (mHomeData == null) {
                        return;
                    }
                    ((FocusHolder) holder).setData(mHomeData.mFocusPicDatas);
                }
                break;
                case POS_ITEM2: {
                    Item2Holder item2Holder = (Item2Holder) holder;
                    item2Holder.tag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MainActivity) getActivity()).showAllTag();
                        }
                    });
                    item2Holder.singer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MainActivity) getActivity()).showSingerCategory();
                        }
                    });
                }
                break;
                case POS_HOT_SONGLIST: {
                    GridHolder gridHolder = (GridHolder) holder;
                    gridHolder.tvTitle.setText("热门歌单");
                    gridHolder.ivIcon.setImageDrawable(tintDrawable(R.mipmap.ic_hot_songlist));
                    gridHolder.gridView.setAdapter(new HotSongListAdapter(getContext(),
                            mHomeData == null ? null : mHomeData.hotGedans));
                }
                break;
                case POS_RECMD_ALBUM: {
                    GridHolder gridHolder = (GridHolder) holder;
                    gridHolder.tvTitle.setText("专辑推荐");
                    gridHolder.ivIcon.setImageDrawable(tintDrawable(R.mipmap.ic_recmd_album));
                    gridHolder.gridView.setAdapter(new RecmdAlbumAdapter(getContext(),
                            mHomeData == null ? null : mHomeData.recmdAlbums));
                }
                break;
                case POS_RECMD_SONG: {
                    ListHolder listHolder = (ListHolder) holder;
                    listHolder.tvTitle.setText("推荐歌曲");
                    listHolder.ivIcon.setImageDrawable(tintDrawable(R.mipmap.ic_recmd_song));
                    listHolder.listView.setAdapter(new RecmdSongAdapter(getContext(),
                            mHomeData == null ? null : mHomeData.mRecmdSongInfos));
                }
                break;
            }
        }

        private Drawable tintDrawable(int res) {
            return UITool.getTintDrawable(getContext(), res, getResources().getColor(R.color.colorAccent));
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case POS_FOCUS:
                    return TYPE_FOCUS;
                case POS_ITEM2:
                    return TYPE_ITEM_2;
                case POS_RECMD_ALBUM:
                case POS_HOT_SONGLIST:
                    return TYPE_GRID;
                case POS_RECMD_SONG: {
                    return TYPE_LIST;
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        class FocusHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.page_indicator)
            PageIndicator pageIndicator;
            @BindView(R.id.pager_focus_pic)
            ViewPager viewPager;

            public FocusHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                viewPager.setAdapter(new FocusPicPagerAdapter(getContext()));
                pageIndicator.setViewPager(viewPager);
            }

            public void setData(List<FocusPic> focusPics) {
                ((FocusPicPagerAdapter) viewPager.getAdapter()).setData(focusPics);
            }
        }

        class Item2Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.view_scene)
            View scene;
            @BindView(R.id.view_singer)
            View singer;
            @BindView(R.id.view_tag)
            View tag;

            public Item2Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class GridHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_category_icon) ImageView ivIcon;
            @BindView(R.id.tv_title) TextView tvTitle;
            @BindView(R.id.tv_more) TextView tvMore;
            @BindView(R.id.grid_view) GridView gridView;

            public GridHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class ListHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_category_icon) ImageView ivIcon;
            @BindView(R.id.tv_title) TextView tvTitle;
            @BindView(R.id.tv_more) TextView tvMore;
            @BindView(R.id.list_view) ListView listView;

            public ListHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    abstract class CategoryAdapter<T> extends BaseAdapter {

        private Context mContext;
        private List<T> mItems;

        public CategoryAdapter(Context context, List<T> items) {
            mItems = items;
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.common_card, parent, false);
            if (mItems != null
                    && mItems.size() > position) {
                final T t = mItems.get(position);
                Holder holder = new Holder();
                holder.itemView = convertView;
                holder.imageView = ButterKnife.findById(convertView, R.id.iv_img);
                holder.text1 = ButterKnife.findById(convertView, R.id.tv_text1);
                holder.text2 = ButterKnife.findById(convertView, R.id.tv_text2);
                onBindItem(t, holder);
            }
            return convertView;
        }

        class Holder {
            View itemView;
            ImageView imageView;
            TextView text1;
            TextView text2;
        }

        protected abstract void onBindItem(T t, Holder holder);
    }


    /**
     * 推荐专辑
     */
    class RecmdAlbumAdapter extends CategoryAdapter<Album> {

        public RecmdAlbumAdapter(Context context, List<Album> items) {
            super(context, items);
        }

        @Override
        protected void onBindItem(final Album album, Holder holder) {
            Glide.with(getContext())
                    .load(album.picRadio)
                    .into(holder.imageView);
            holder.text1.setLines(1);
            holder.text1.setText(album.title);
            holder.text2.setText(album.author);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).showAlbumInfo(album.albumId);
                }
            });
        }

        @Override
        public int getCount() {
            return RECMD_ALBUM_NUM;
        }
    }

    /**
     * 热门歌单
     */
    class HotSongListAdapter extends CategoryAdapter<Gedan> {

        public HotSongListAdapter(Context context, List<Gedan> mItems) {
            super(context, mItems);
        }

        @Override
        protected void onBindItem(final Gedan songList, Holder holder) {
            Glide.with(getContext())
                    .load(songList.pic)
                    .into(holder.imageView);
            holder.text1.setText(songList.title);
            holder.text2.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).showGedanDetail(songList.listid);
                }
            });
        }

        @Override
        public int getCount() {
            return HOTSONGLIST_NUM;
        }
    }


    /**
     * 推荐歌曲
     */
    class RecmdSongAdapter extends BaseAdapter {

        private Context mContext;
        private List<SongInfo> mItems;

        public RecmdSongAdapter(Context context, List<SongInfo> songInfos) {
            mContext = context;
            mItems = songInfos;
        }

        @Override
        public int getCount() {
            return RECMD_SONG_NUM;
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_recmd_song, parent, false);
            if (mItems != null
                    && mItems.size() > position) {
                ImageView ivImg = ButterKnife.findById(view, R.id.iv_img);
                TextView tvTitle = ButterKnife.findById(view, R.id.tv_title);
                TextView tvArtist = ButterKnife.findById(view, R.id.tv_artist);
                SongInfo songInfo = mItems.get(position);
                Glide.with(mContext)
                        .load(songInfo.picBig)
                        .into(ivImg);
                tvTitle.setText(songInfo.title);
                tvArtist.setText(songInfo.author);
            }
            return view;
        }
    }

    /**
     * 顶部循环推荐适配器
     */
    class FocusPicPagerAdapter extends PagerAdapter {
        private List<FocusPic> mFocusPics;
        private boolean mNotify;
        private Context mContext;
        private SparseArray<ImageView> mImgViews = new SparseArray<>();

        public FocusPicPagerAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<FocusPic> focusPics) {
            mFocusPics = focusPics;
            mImgViews.clear();
            notifyDataSetChanged();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImgViews.get(position);
            final FocusPic focusPic = mFocusPics.get(position);
            if (imageView == null) {
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mImgViews.put(position, imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (focusPic.type == FocusPic.TYPE_ALBUM) {
                            ((MainActivity) getActivity()).showAlbumInfo(Long.valueOf(focusPic.code));
                        } else if (focusPic.type == FocusPic.TYPE_SONGLIST) {
                            ((MainActivity) getActivity()).showGedanDetail(Long.valueOf(focusPic.code));
                        }
                    }
                });
                Glide.with(mContext)
                        .load(mFocusPics.get(position).randpic)
                        .into(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImgViews.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return mNotify ? POSITION_NONE : POSITION_UNCHANGED;
        }

        @Override
        public void notifyDataSetChanged() {
            mNotify = true;
            super.notifyDataSetChanged();
            mNotify = false;
        }

        @Override
        public int getCount() {
            return mFocusPics == null ? 0 : mFocusPics.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
