package com.sanron.yidumusic.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.net.bean.Tag;
import com.sanron.yidumusic.data.net.bean.response.AllTagData;
import com.sanron.yidumusic.data.net.bean.response.HotTagData;
import com.sanron.yidumusic.data.net.repository.DataSource;
import com.sanron.yidumusic.rx.SubscriberAdapter;
import com.sanron.yidumusic.rx.TransformerUtil;
import com.sanron.yidumusic.ui.base.ToolbarLoadFragment;
import com.sanron.yidumusic.util.UITool;
import com.sanron.yidumusic.widget.NoScrollGridView;
import com.sanron.yidumusic.widget.OffsetDecoration;
import com.sanron.yidumusic.widget.RatioLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func2;

/**
 * Created by sanron on 16-4-14.
 */
public class AllTagFragment extends ToolbarLoadFragment {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    Toolbar mToolbar;

    private CategoryAdapter mAdapter;

    public static Fragment newInstance() {
        return new AllTagFragment();
    }

    static class Data {
        List<Map.Entry<String, List<Tag>>> allTags;
        List<Tag> hotTags;
    }

    @Override
    protected int getLayout() {
        return R.layout.recycler_view;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        mToolbar = getToolbar();
        mToolbar.setTitle("歌曲分类");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(AllTagFragment.this.getClass().getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mAdapter = new CategoryAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        int padding = UITool.dpToPx(getContext(),16);
        mRecyclerView.setPadding(padding,padding,padding,padding);
        mRecyclerView.addItemDecoration(new OffsetDecoration(0,padding));
        mRecyclerView.setClipToPadding(false);
        loadData();
    }

    private void loadData() {
        DataSource source = YiduApp.get().getDataRepository();
        Observable<HotTagData> observable1 = source.getHotTag(8);
        Observable<AllTagData> observable2 = source.getAllTag();
        Observable.zip(observable1, observable2, new Func2<HotTagData, AllTagData, Data>() {
            @Override
            public Data call(HotTagData hotTagData, AllTagData allTagData) {
                Data data = new Data();
                List<Map.Entry<String, List<Tag>>> tagList = new LinkedList<>();
                for (Map.Entry<String, List<Tag>> entry : allTagData.tagList.entrySet()) {
                    tagList.add(entry);
                }
                data.allTags = tagList;
                data.hotTags = hotTagData.tags;
                return data;
            }
        }).compose(TransformerUtil.<Data>io()
        ).subscribe(new SubscriberAdapter<Data>() {
            @Override
            public void onNext(Data data) {
                setState(STATE_SUCCESS);
                mAdapter.setData(data);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                setState(STATE_FAILED);
            }
        });
    }


    class CategoryAdapter extends RecyclerView.Adapter {

        private Data mData;

        public static final int HOT_TAG_HEADER = 0;
        public static final int CATEGORY_ITEM = 1;


        public void setData(Data data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.hotTags.size() + 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case HOT_TAG_HEADER: {
                    return new HotHolder(createHotTagHeaderView());
                }
                case CATEGORY_ITEM: {
                    View view = LayoutInflater.from(getContext())
                            .inflate(R.layout.list_tag_category_item, parent, false);
                    return new ItemHolder(view);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HotHolder) {
                HotTagAdapter hotTagAdapter = (HotTagAdapter) ((HotHolder) holder).gv.getAdapter();
                if (hotTagAdapter != null
                        && mData.hotTags != null) {
                    hotTagAdapter.setData(mData.hotTags);
                }
            } else {
                ItemHolder itemHolder = (ItemHolder) holder;
                String category = mData.allTags.get(position - 1).getKey();
                List<Tag> tags = mData.allTags.get(position - 1).getValue();
                itemHolder.tvCategory.setText(category);
                itemHolder.gvTags.setAdapter(new TagAdapter(tags));
            }
        }

        private GridView createHotTagHeaderView() {
            final int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            NoScrollGridView gv = new NoScrollGridView(getContext());
            gv.setNumColumns(4);
            gv.setHorizontalSpacing(spacing);
            gv.setVerticalSpacing(spacing);
            gv.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            gv.setAdapter(new HotTagAdapter());
            return gv;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HOT_TAG_HEADER;
            } else {
                return CATEGORY_ITEM;
            }
        }

        class HotHolder extends RecyclerView.ViewHolder {
            GridView gv;

            public HotHolder(View itemView) {
                super(itemView);
                gv = (GridView) itemView;
            }
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_category) TextView tvCategory;
            @BindView(R.id.grid_view) GridView gvTags;

            public ItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    private class HotTagAdapter extends BaseAdapter {
        private List<Tag> tags;
        public final int[] ICONS = new int[]{
                R.mipmap.ic_classify_img01,
                R.mipmap.ic_classify_img02,
                R.mipmap.ic_classify_img03,
                R.mipmap.ic_classify_img04,
                R.mipmap.ic_classify_img05,
                R.mipmap.ic_classify_img06,
                R.mipmap.ic_classify_img07,
                R.mipmap.ic_classify_img08,
        };

        @Override
        public int getCount() {
            return 8;
        }

        public void setData(List<Tag> data) {
            this.tags = data;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return tags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView tvTag = null;
            if (convertView == null) {
                RatioLayout ratioLayout = new RatioLayout(getContext(), null);
                ratioLayout.setType(RatioLayout.TYPE_HEIGHT);
                ratioLayout.setRatio(1f);
                ratioLayout.setBackgroundResource(ICONS[position]);

                tvTag = new TextView(getContext());
                tvTag.setTextColor(Color.WHITE);
                tvTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                tvTag.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                tvTag.setGravity(Gravity.CENTER);
                ratioLayout.addView(tvTag);
                convertView = ratioLayout;
            } else {
                tvTag = (TextView) ((ViewGroup) convertView).getChildAt(0);
            }
            if (tags != null
                    && position < tags.size()) {
                String tag = tags.get(position).title;
                tvTag.setText(tag);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getMainActivity().showTagSong(tags.get(position).title);
                }
            });
            return convertView;
        }
    }

    private class TagAdapter extends BaseAdapter {

        private List<Tag> tags;

        public TagAdapter(List<Tag> tags) {
            this.tags = tags;
        }

        @Override
        public int getCount() {
            return tags == null ? 0 : tags.size();
        }

        @Override
        public Object getItem(int position) {
            return tags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(getContext());
                final int padding = UITool.dpToPx(getContext(), 4);
                textView.setPadding(padding, padding, padding, padding);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tv_tag_bg));
                textView.setTextColor(getResources().getColorStateList(R.drawable.tv_tag_bg));
                textView.setGravity(Gravity.CENTER);
                convertView = textView;
            }
            TextView tvTag = (TextView) convertView;
            tvTag.setText(tags.get(position).title);
            tvTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getMainActivity().showTagSong(tags.get(position).title);
                }
            });
            return tvTag;
        }
    }
}
