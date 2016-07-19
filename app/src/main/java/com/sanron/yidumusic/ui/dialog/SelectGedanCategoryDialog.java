package com.sanron.yidumusic.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.net.model.response.GedanCategoryData;
import com.sanron.yidumusic.util.UITool;
import com.sanron.yidumusic.widget.OffsetDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 歌单分类选择对话框
 */
public class SelectGedanCategoryDialog extends BottomSheetDialog {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    GedanCategoryData mCategoryData;
    Callback mCallback;

    /**
     * 选择分类回调
     */
    public interface Callback {
        void onSelect(String tag);
    }

    public SelectGedanCategoryDialog(@NonNull Context context, GedanCategoryData categoryData) {
        super(context);
        mCategoryData = categoryData;

        int screenHeight = UITool.getScreenSize(context)[1];
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_select_gedan_category, null);
        ButterKnife.bind(this, view);

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                screenHeight * 2 / 3));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new OffsetDecoration(0, UITool.dpToPx(context, 16)));
        setContentView(view);
    }


    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @OnClick(R.id.tv_text)
    public void onAllClick() {
        if (mCallback != null) {
            mCallback.onSelect("全部");
        }
        dismiss();
    }

    @Override
    public void show() {
        mRecyclerView.setAdapter(new CategoryAdapter(mCategoryData.content));
        super.show();
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

        List<GedanCategoryData.Content> mCategories;

        public CategoryAdapter(List<GedanCategoryData.Content> categories) {
            mCategories = categories;
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_gedan_category, parent, false);
            return new CategoryHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            holder.setData(mCategories.get(position));
        }

        @Override
        public int getItemCount() {
            return mCategories == null ? 0 : mCategories.size();
        }

        class CategoryHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_category)
            TextView tvCategory;
            @BindView(R.id.grid_view)
            GridView gridView;

            public CategoryHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(GedanCategoryData.Content data) {
                tvCategory.setText(data.title);
                gridView.setAdapter(new TagAdapter(data.tags));
            }
        }

        private class TagAdapter extends BaseAdapter {

            private List<GedanCategoryData.Content.Tag> tags;

            public TagAdapter(List<GedanCategoryData.Content.Tag> tags) {
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
                    convertView = LayoutInflater.from(getContext())
                            .inflate(R.layout.text_view_gedan_category, parent, false);
                }
                final String tag = tags.get(position).tag;
                TextView tvTag = (TextView) convertView;
                tvTag.setText(tag);
                tvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onSelect(tag);
                        }
                        dismiss();
                    }
                });
                return tvTag;
            }
        }
    }
}