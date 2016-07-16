package com.sanron.yidumusic.ui.fragment.music_bank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.YiduRetrofit;
import com.sanron.yidumusic.data.model.Gedan;
import com.sanron.yidumusic.data.model.response.GedanListData;
import com.sanron.yidumusic.data.model.response.SongListCategoryData;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.base.LazyLoadFragment;
import com.sanron.yidumusic.util.UITool;
import com.sanron.yidumusic.widget.OffsetDecoration;
import com.sanron.yidumusic.widget.RecyclerViewFloatHeader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by sanron on 16-7-16.
 */
public class GedanFragment extends LazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.sticky_header)
    RecyclerViewFloatHeader mStickyHeader;

    SongListAdapter mSongListAdapter;

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
        return R.layout.fragment_gedan;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongListAdapter = new SongListAdapter();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mSongListAdapter);
        final int space = UITool.dpToPx(getContext(), 8);
        mRecyclerView.addItemDecoration(new OffsetDecoration(space, space));
        mStickyHeader.attachRecyclerView(mRecyclerView);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        addSub(YiduRetrofit.get()
                .getApiService()
                .getSongListCategory()
                .compose(TransformerUtil.<SongListCategoryData>apply())
                .subscribe(new Action1<SongListCategoryData>() {
                    @Override
                    public void call(SongListCategoryData songListCategoryData) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                })
        );
        addSub(YiduRetrofit.get()
                .getApiService()
                .getGedanList(0, 10)
                .compose(TransformerUtil.<GedanListData>apply())
                .map(new Func1<GedanListData, List<Gedan>>() {
                    @Override
                    public List<Gedan> call(GedanListData gedanListData) {
                        return gedanListData.gedans;
                    }
                })
                .subscribe(new Action1<List<Gedan>>() {
                    @Override
                    public void call(List<Gedan> gedanList) {
                        mSongListAdapter.setData(gedanList);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRefreshLayout.setRefreshing(false);
                    }
                })
        );
    }


    class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.Holder> {

        private List<Gedan> mGedanList;

        public void setData(List<Gedan> data) {
            mGedanList = data;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.common_card, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.setData(mGedanList.get(position));
        }

        @Override
        public int getItemCount() {
            return mGedanList == null ? 0 : mGedanList.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_img)
            ImageView ivImg;
            @BindView(R.id.tv_text1)
            TextView tvText1;
            @BindView(R.id.tv_text2)
            TextView tvText2;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(Gedan data) {
                Glide.with(getContext())
                        .load(data.pic300)
                        .into(ivImg);
                tvText1.setText(data.title);
                tvText2.setText(data.tag);
            }
        }
    }
}
