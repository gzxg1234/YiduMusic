package com.sanron.yidumusic.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.data.net.bean.Artist;
import com.sanron.yidumusic.data.net.bean.response.SingerListData;
import com.sanron.yidumusic.rx.ToastSubscriber;
import com.sanron.yidumusic.ui.base.BaseFragment;
import com.sanron.yidumusic.ui.base.PullAdapter;
import com.sanron.yidumusic.ui.dialog.ScrimPopupWindow;
import com.sanron.yidumusic.util.StatusBarUtil;
import com.sanron.yidumusic.util.UITool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sanron on 16-8-9.
 */
public class SingerListFragment extends BaseFragment implements PullAdapter.OnLoadMoreListener {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_wrap) View mWrap;
    @BindView(R.id.tool_bar) Toolbar mToolbar;

    private int mArea;
    private int mSex;
    private int mAbcIndex;
    private String mTitle;
    private int mPage;
    private SingerAdapter mAdapter;

    public static final String ARG_TITLE = "title";
    public static final String ARG_AREA = "area";
    public static final String ARG_SEX = "sex";

    public static final int LOAD_LIMIT = 30;

    public static final String[] ABC = new String[28];

    static {
        for (int i = 1; i < 27; i++) {
            ABC[i] = String.valueOf((char) ('a' + i - 1));
        }
        ABC[27] = "other";
    }

    public static SingerListFragment newInstance(String title, int area, int sex) {
        SingerListFragment fragment = new SingerListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_AREA, area);
        args.putInt(ARG_SEX, sex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString(ARG_TITLE);
            mArea = args.getInt(ARG_AREA);
            mSex = args.getInt(ARG_SEX);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.toolbar_with_recycler_view;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        StatusBarUtil.applyInsertTop(getActivity(), mWrap);
        initToolbar();
        mAdapter = new SingerAdapter();
        mAdapter.setOnLoadMoreListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        load(false);
    }

    private void initToolbar() {
        mToolbar.setTitle(mTitle);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(SingerListFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        mToolbar.getMenu().add(0, 0, 0, "选择排序")
                .setIcon(R.mipmap.ic_sort_by_alpha_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new SelectLetterWindow(getActivity()).showAtLocation(getView(),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                return true;
            }
        });
    }

    @Override
    public void onLoad() {
        load(false);
    }

    private void load(final boolean refresh) {
        if (refresh) {
            mAdapter.setItems(null);
            mAdapter.setHasMore(true);
            mPage = 0;
        }
        addSub(YiduApp.get()
                .getDataRepository()
                .getSingerList(mPage * LOAD_LIMIT, LOAD_LIMIT, mArea, mSex, 1, ABC[mAbcIndex])
                .subscribe(new ToastSubscriber<SingerListData>(getContext()) {
                    @Override
                    public void onNext(SingerListData singerListData) {
                        super.onNext(singerListData);
                        if (refresh) {
                            mAdapter.setItems(singerListData.artists);
                        } else {
                            mAdapter.addItems(singerListData.artists);
                        }
                        mPage++;
                        mAdapter.setHasMore(singerListData.havemore == 1);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mAdapter.onLoadComplete();
                    }
                })
        );
    }

    class SingerAdapter extends PullAdapter<SingerAdapter.ItemHolder> {

        private List<Artist> mItems;

        public void setItems(List<Artist> items) {
            mItems = items;
            notifyDataSetChanged();
        }

        public void addItems(List<Artist> artists) {
            if (mItems == null) {
                mItems = artists;
            } else {
                mItems.addAll(artists);
            }

            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateItemView(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_singer_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindItemView(ItemHolder holder, int position) {
            Artist artist = mItems.get(position);
            holder.tvName.setText(artist.name);
            Glide.with(getContext())
                    .load(artist.avatarBig)
                    .into(holder.ivImg);
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateFooterView(ViewGroup parent) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.loading_footer_layout, parent, false);
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


        class FooterHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_text) TextView text;

            public FooterHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_img) ImageView ivImg;
            @BindView(R.id.tv_name) TextView tvName;

            public ItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public class SelectLetterWindow extends ScrimPopupWindow {

        private Context mContext;

        public SelectLetterWindow(Activity activity) {
            super(activity);
            mContext = activity;
            View root = LayoutInflater.from(mContext)
                    .inflate(R.layout.window_select_singer_letter, null);
            setContentView(root);
            setOutsideTouchable(true);
            setFocusable(true);
            setAnimationStyle(R.style.MyWindowAnim);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            GridView gv = ButterKnife.findById(root, R.id.grid_view);
            Button btnCancel = ButterKnife.findById(root, R.id.btn_cancel);
            gv.setAdapter(new LetterAdapter());
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        private class LetterAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return 28;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) convertView;
                if (textView == null) {
                    textView = new TextView(mContext);
                    final int height = UITool.dpToPx(mContext, 40);
                    textView.setLayoutParams(
                            new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                }

                if (position == mAbcIndex) {
                    textView.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                if (position == 0) {
                    textView.setText("热门");
                } else if (position == getCount() - 1) {
                    textView.setText("其他");
                } else {
                    char ch = (char) (position - 1 + 'A');
                    textView.setText(String.valueOf(ch));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAbcIndex == position) {
                            return;
                        }
                        mAbcIndex = position;
                        load(true);
                        dismiss();
                    }
                });
                return textView;
            }
        }
    }
}
