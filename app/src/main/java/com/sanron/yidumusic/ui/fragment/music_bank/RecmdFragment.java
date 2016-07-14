package com.sanron.yidumusic.ui.fragment.music_bank;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.BaiduApiService;
import com.sanron.yidumusic.data.YiduRetrofit;
import com.sanron.yidumusic.data.model.Album;
import com.sanron.yidumusic.data.model.FocusPic;
import com.sanron.yidumusic.data.model.FocusPicData;
import com.sanron.yidumusic.data.model.HotSongListData;
import com.sanron.yidumusic.data.model.HotTagData;
import com.sanron.yidumusic.data.model.RecmdAlbumData;
import com.sanron.yidumusic.data.model.RecmdData;
import com.sanron.yidumusic.data.model.SongList;
import com.sanron.yidumusic.data.model.Tag;
import com.sanron.yidumusic.rx.SchedulerTransformer;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.viewpagerindicator.PageIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;

/**
 * Created by Administrator on 2016/3/10.
 */
public class RecmdFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private RecmdAdapter mRecmdAdapter;
    private static final int FOCUS_COUNT = 10;
    private static final int HOTTAG_COUNT = 3;
    private static final int HOTSONGLIST_COUNT = 6;

    public static RecmdFragment newInstance() {
        return new RecmdFragment();
    }

    @Override
    protected void onLazyLoad() {
        loadData();
    }

    public void loadData() {
        BaiduApiService apiService = YiduRetrofit.get().getApiService();

        //循环图
        Observable<List<FocusPic>> focus = apiService.getFocusPic(FOCUS_COUNT)
                .map(new Func1<FocusPicData, List<FocusPic>>() {
                    @Override
                    public List<FocusPic> call(FocusPicData focusPicData) {
                        return focusPicData.pics;
                    }
                })
                .flatMap(new Func1<List<FocusPic>, Observable<FocusPic>>() {
                    @Override
                    public Observable<FocusPic> call(List<FocusPic> focusPics) {
                        return Observable.from(focusPics);
                    }
                })
                .filter(new Func1<FocusPic, Boolean>() {
                    @Override
                    public Boolean call(FocusPic focusPic) {
                        return focusPic.type == FocusPic.TYPE_ALBUM || focusPic.type == FocusPic.TYPE_SONG_LIST;
                    }
                }).toList();

        //热门标签
        Observable<List<Tag>> hotTag = apiService.getHotTag(HOTTAG_COUNT)
                .map(new Func1<HotTagData, List<Tag>>() {
                    @Override
                    public List<Tag> call(HotTagData hotTagData) {
                        return hotTagData.tags;
                    }
                });

        //热门歌单
        Observable<List<SongList>> hotSongList = apiService.getHotSongList(HOTSONGLIST_COUNT)
                .map(new Func1<HotSongListData, List<SongList>>() {
                    @Override
                    public List<SongList> call(HotSongListData hotSongListData) {
                        return hotSongListData.content.songLists;
                    }
                });

        //推荐专辑
        final Observable<List<Album>> recmdAlbum = apiService.getRecmdAlbum(0, 6)
                .map(new Func1<RecmdAlbumData, List<Album>>() {
                    @Override
                    public List<Album> call(RecmdAlbumData recmdAlbumData) {
                        return recmdAlbumData.plazeAlbumList.rm.albumList.albums;
                    }
                });

        Observable
                .zip(focus, hotTag, hotSongList, recmdAlbum,
                        new Func4<List<FocusPic>, List<Tag>, List<SongList>, List<Album>, RecmdData>() {
                            @Override
                            public RecmdData call(List<FocusPic> focusPics, List<Tag> tags, List<SongList> songLists, List<Album> albums) {
                                RecmdData recmdData = new RecmdData();
                                recmdData.focusPics = focusPics;
                                recmdData.hotTags = tags;
                                recmdData.hotSongLists = songLists;
                                recmdData.recmdAlbums = albums;
                                return recmdData;
                            }
                        })
                .compose(SchedulerTransformer.<RecmdData>io())
                .subscribe(new Action1<RecmdData>() {
                    @Override
                    public void call(RecmdData recmdData) {
                        mRecmdAdapter.setData(recmdData);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecmdAdapter = new RecmdAdapter();
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecmdAdapter);
    }

    @Override
    protected int getLayout() {
        return R.layout.refresh_with_recycler;
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * 推荐页面适配器
     */
    class RecmdAdapter extends RecyclerView.Adapter {

        private RecmdData mRecmdData;
        private static final int TYPE_FOCUS = 1;
        private static final int TYPE_HOT_TAG = 2;
        private static final int TYPE_GRID = 3;

        public void setData(RecmdData data) {
            mRecmdData = data;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_FOCUS: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_focus_item, parent, false);
                    return new FocusHolder(view);
                }
                case TYPE_HOT_TAG: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_hottag_item, parent, false);
                    return new HotTagHolder(view);
                }
                case TYPE_GRID: {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recmd_grid, parent, false);
                    return new GridHolder(view);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (position) {
                case 0: {
                    if (mRecmdData == null) {
                        return;
                    }
                    ((FocusHolder) holder).setData(mRecmdData.focusPics);
                }
                break;
                case 1: {
                    if (mRecmdData == null) {
                        return;
                    }
                    ((HotTagHolder) holder).setData(mRecmdData.hotTags);
                }
                break;
                case 2: {
                    GridHolder gridHolder = (GridHolder) holder;
                    gridHolder.tvTitle.setText("热门歌单");
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_hot_songlist);
                    DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorAccent));
                    gridHolder.ivIcon.setImageDrawable(drawable);
                    gridHolder.gridView.setAdapter(new HotSongListAdapter(getContext(),
                            mRecmdData == null ? null : mRecmdData.hotSongLists));
                }
                break;
                case 3: {
                    GridHolder gridHolder = (GridHolder) holder;
                    gridHolder.tvTitle.setText("专辑推荐");
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_recmd_album);
                    DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorAccent));
                    gridHolder.ivIcon.setImageDrawable(drawable);
                    gridHolder.gridView.setAdapter(new RecmdAlbumAdapter(getContext(),
                            mRecmdData == null ? null : mRecmdData.recmdAlbums));
                }
                break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return TYPE_FOCUS;
                case 1:
                    return TYPE_HOT_TAG;
                case 2:
                case 3:
                    return TYPE_GRID;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return 4;
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

        class HotTagHolder extends RecyclerView.ViewHolder {
            @BindViews({R.id.tv_tag1, R.id.tv_tag2, R.id.tv_tag3, R.id.tv_tag4})
            List<TextView> tvTags;

            public HotTagHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(List<Tag> tags) {
                for (int i = 0; i < tags.size() && i < 3; i++) {
                    tvTags.get(i).setText(tags.get(i).title);
                }
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
    }

    static abstract class CategoryAdapter<T> extends BaseAdapter {

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
            Holder holder = new Holder();
            holder.imageView = ButterKnife.findById(convertView, R.id.iv_img);
            holder.text1 = ButterKnife.findById(convertView, R.id.tv_text1);
            holder.text2 = ButterKnife.findById(convertView, R.id.tv_text2);
            onViewCreated(holder);
            if (mItems != null
                    && mItems.size() > position) {
                final T t = mItems.get(position);
                onBindItem(t, holder);
            }
            return convertView;
        }

        static class Holder {
            ImageView imageView;
            TextView text1;
            TextView text2;
        }

        protected void onViewCreated(Holder holder) {
        }

        protected abstract void onBindItem(T t, Holder holder);
    }


    /**
     * 推荐专辑
     */
    static class RecmdAlbumAdapter extends CategoryAdapter<Album> {

        public RecmdAlbumAdapter(Context context, List<Album> items) {
            super(context, items);
        }

        @Override
        protected void onBindItem(Album album, Holder holder) {
            Glide.with(getContext())
                    .load(album.picRadio)
                    .into(holder.imageView);
            holder.text1.setText(album.title);
            holder.text2.setText(album.author);
        }

        @Override
        public int getCount() {
            return 6;
        }
    }

    /**
     * 热门歌单
     */
    static class HotSongListAdapter extends CategoryAdapter<SongList> {

        public HotSongListAdapter(Context context, List<SongList> mItems) {
            super(context, mItems);
        }

        @Override
        protected void onViewCreated(Holder holder) {
            holder.text2.setVisibility(View.GONE);
        }

        @Override
        protected void onBindItem(SongList songList, Holder holder) {
            Glide.with(getContext())
                    .load(songList.pic)
                    .into(holder.imageView);
            holder.text1.setText(songList.title);
        }

        @Override
        public int getCount() {
            return 6;
        }
    }


    /**
     * 顶部循环推荐适配器
     */
    static class FocusPicPagerAdapter extends PagerAdapter {
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
            if (imageView == null) {
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mImgViews.put(position, imageView);
            }
            container.addView(imageView);
            Glide.with(mContext)
                    .load(mFocusPics.get(position).picUrl)
                    .into(imageView);
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
