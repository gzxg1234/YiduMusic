package com.sanron.yidumusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.activity.MainActivity;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.util.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanron on 16-8-9.
 */
public class SingerCategoryFragment extends BaseFragment {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_wrap) View mWrap;
    @BindView(R.id.tool_bar) Toolbar mToolbar;

    public static final String[] CLASSES = new String[]{
            "华语男歌手", "华语女歌手", "华语组合",
            "欧美男歌手", "欧美女歌手", "欧美组合",
            "日本男歌手", "日本女歌手", "日本组合",
            "韩国男歌手", "韩国女歌手", "韩国组合",
            "其他"
    };

    public static final int[] AREAS = new int[]{
            6, 6, 6,
            3, 3, 3,
            60, 60, 60,
            7, 7, 7,
            5
    };

    public static final int[] SEXS = new int[]{
            1, 2, 3,
            1, 2, 3,
            1, 2, 3,
            1, 2, 3,
            0
    };

    @Override
    protected int getLayout() {
        return R.layout.toolbar_with_recycler_view;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        StatusBarUtil.applyInsertTop(getActivity(), mWrap);
        mToolbar.setTitle("歌手分类");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(SingerCategoryFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new SingerClassAdapter());
    }

    public static Fragment newInstance() {
        return new SingerCategoryFragment();
    }

    class SingerClassAdapter extends RecyclerView.Adapter<SingerClassAdapter.ItemHolder> {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_singger_category_item,
                    parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            holder.text.setText(CLASSES[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = position;
                    int area = AREAS[i];
                    int sex = SEXS[i];
                    String title = CLASSES[i];
                    ((MainActivity)getActivity()).showSingerList(title, area, sex);
                }
            });
        }


        @Override
        public int getItemCount() {
            return CLASSES.length;
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_text) TextView text;

            public ItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
