package com.sanron.yidumusic.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.ui.base.BaseActivity;
import com.sanron.yidumusic.util.ToastUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sanron on 16-3-22.
 */
public class ScanDiyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.lv_file_explore)
    ListView mLvFileExplore;
    @BindView(R.id.tv_cur_directory)
    TextView mTvCurDir;
    @BindView(R.id.ll_directory_back)
    View mViewBack;
    @BindView(R.id.btn_ok)
    Button mBtnOk;
    @BindView(R.id.cb_select_all)
    CheckBox mCbSelectAll;

    private DirAdapter mDirAdapter;
    private File mCurDir;
    private File mStorageDir;

    public static final String EXTRA_SELECT_PATHS = "select_paths";


    @Override
    protected int getLayout() {
        return R.layout.activity_scan_diy;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager sbm = new SystemBarTintManager(this);
            sbm.setStatusBarTintEnabled(true);
            sbm.setStatusBarTintResource(R.color.colorPrimary);
        }

        mDirAdapter = new DirAdapter(this);
        mLvFileExplore.setAdapter(mDirAdapter);

        mViewBack.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mLvFileExplore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = (File) mDirAdapter.getItem(i);
                setCurDir(file);
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mCbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mDirAdapter.setAllChecked(checked);
            }
        });

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            mStorageDir = Environment.getExternalStorageDirectory();
            setCurDir(mStorageDir);
        }
    }

    private void setCurDir(File dir) {
        if (dir == null) {
            return;
        }

        mCurDir = dir;
        mTvCurDir.setText(mCurDir.getAbsolutePath());
        mDirAdapter.setData(mCurDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory()
                        && !file.getName().startsWith(".");
            }
        }));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_directory_back: {
                backDir();
            }
            break;

            case R.id.btn_ok: {
                List<File> selects = mDirAdapter.getSelectFiles();
                if (selects.size() == 0) {
                    ToastUtil.$("您还未选择文件夹");
                    return;
                }

                String[] paths = new String[selects.size()];
                for (int i = 0; i < selects.size(); i++) {
                    paths[i] = selects.get(i).getAbsolutePath();
                }

                Intent data = new Intent();
                data.putExtra(EXTRA_SELECT_PATHS, paths);
                setResult(RESULT_OK, data);
                finish();
            }
            break;
        }
    }

    /**
     * 返回上层目录
     */
    private boolean backDir() {
        if (!mCurDir.equals(mStorageDir)) {
            setCurDir(mCurDir.getParentFile());
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!backDir()) {
            super.onBackPressed();
        }
    }

    public static class DirAdapter extends BaseAdapter {

        private Context context;
        /**
         * 记录选中状态
         */
        private SparseBooleanArray checkedState;
        private File[] data;

        public DirAdapter(Context context) {
            this.context = context;
        }

        public void setAllChecked(boolean checked) {
            if (data != null) {
                for (int i = 0; i < checkedState.size(); i++) {
                    checkedState.put(i, checked);
                }
                notifyDataSetChanged();
            }
        }

        public File[] getData() {
            return data;
        }

        public void setData(File[] data) {
            this.data = data;
            if (data != null) {
                //排列文件夹
                Arrays.sort(data, new Comparator<File>() {
                    @Override
                    public int compare(File file, File t1) {
                        return file.getName().compareTo(t1.getName());
                    }
                });
                checkedState = new SparseBooleanArray(data.length);
                for (int i = 0; i < data.length; i++) {
                    checkedState.put(i, false);
                }
                notifyDataSetChanged();
            }
        }

        public List<File> getSelectFiles() {
            List<File> selectFiles = new LinkedList<>();
            if (data != null) {
                for (int i = 0; i < checkedState.size(); i++) {
                    if (checkedState.get(i)) {
                        selectFiles.add(data[i]);
                    }
                }
            }
            return selectFiles;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_file_item, viewGroup, false);
            }
            TextView tvDirName = (TextView) view.findViewById(R.id.tv_dir_name);
            CheckBox cbSelect = (CheckBox) view.findViewById(R.id.cb_select);
            tvDirName.setText(data[position].getName());
            cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    checkedState.put(position, checked);
                }
            });
            cbSelect.setChecked(checkedState.get(position));
            return view;
        }
    }

}
