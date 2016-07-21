package com.sanron.yidumusic.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sanron.yidumusic.R;
import com.sanron.yidumusic.data.db.YiduDB;
import com.sanron.yidumusic.data.db.bean.MusicInfo;
import com.sanron.yidumusic.ui.base.BaseActivity;
import com.sanron.yidumusic.util.AudioTool;
import com.sanron.yidumusic.util.ToastUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by sanron on 16-3-22.
 */
public class ScanMusicActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btn_start_scan)
    Button mBtnStart;
    @BindView(R.id.linear1)
    LinearLayout mLayoutFindNumInfo;
    @BindView(R.id.tv_find_song_num)
    TextView mTvFindNum;
    @BindView(R.id.tv_filename)
    TextView mTvFileName;
    @BindView(R.id.cb_ignore_60)
    CheckBox mCbIgnore;

    private MusicScanner mMusicScanner;
    private List<MusicInfo> mScanResult;
    private boolean mIsFullScan;

    public static final String[] PROJECTIONS = new String[]{
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DISPLAY_NAME
    };
    public static final String DURATION_SELECTION = MediaStore.Audio.Media.DURATION + ">=60000";

    public static final int MENU_DIY_SCAN = 1;
    public static final int REQUEST_CODE_DIY = 1;
    public static final String TEXT_START_SCAN = "全盘扫描";
    public static final String TEXT_STOP_SCAN = "停止扫描";
    public static final String TEXT_FINISH = "完成";
    public static final String TAG = ScanMusicActivity.class.getSimpleName();

    private MusicScanner.OnScanMediaListener mListener = new MusicScanner.OnScanMediaListener() {
        @Override
        public void onStart() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mScanResult.clear();
                    mTvFindNum.setText("0");
                    mTvFileName.setVisibility(View.VISIBLE);
                    mLayoutFindNumInfo.setVisibility(View.VISIBLE);
                    mCbIgnore.setVisibility(View.INVISIBLE);
                    mBtnStart.setText(TEXT_STOP_SCAN);
                    mTvFileName.setText("正在扫描...");
                }
            });
        }

        @Override
        public void onProgress(final String filePath, Uri uri) {
            Cursor cursor = getContentResolver().query(uri,
                    PROJECTIONS,
                    mCbIgnore.isChecked() ? DURATION_SELECTION : null,
                    null,
                    MediaStore.Audio.Media.TITLE_KEY);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    int bitrate = AudioTool.readBitrate(path);
                    long modifiedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                    MusicInfo music = new MusicInfo();
                    music.setName(displayName);
                    music.setTitle(title);
                    music.setAlbum(album);
                    music.setArtist(artist);
                    music.setPath(path);
                    music.setLastModifyTime(modifiedDate);
                    music.setBitrate(bitrate);
                    music.setDuration(duration);
                    mScanResult.add(music);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvFileName.setText(filePath);
                            mTvFindNum.setText(String.valueOf(mScanResult.size()));
                        }
                    });
                }
                cursor.close();
            }
        }

        @Override
        public void onCompleted(final boolean fromUser) {
            Log.d(TAG, "停止扫描");
            Log.d(TAG, "扫描到" + mScanResult.size() + "首歌曲");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvFileName.setVisibility(View.INVISIBLE);
                    if (fromUser) {
                        mBtnStart.setText(TEXT_START_SCAN);
                        mCbIgnore.setVisibility(View.VISIBLE);
                        mLayoutFindNumInfo.setVisibility(View.INVISIBLE);
                    } else {
                        mBtnStart.setText(TEXT_FINISH);
                    }
                }
            });
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_scan_music;
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
        initView();
    }

    private void initView() {
        mScanResult = new LinkedList<>();
        mMusicScanner = new MusicScanner(this);
        mMusicScanner.connect(null);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnStart.setTag("start");
        mBtnStart.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_DIY_SCAN, Menu.NONE, "自定义扫描")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DIY_SCAN: {
                startActivityForResult(new Intent(this, ScanDiyActivity.class), REQUEST_CODE_DIY);
            }
            break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_DIY: {
                switch (resultCode) {
                    case RESULT_OK: {
                        String[] paths = data.getStringArrayExtra(ScanDiyActivity.EXTRA_SELECT_PATHS);
                        if (paths != null) {
                            //自定义扫描开始
                            scan(paths);
                            mIsFullScan = false;
                        }
                    }
                    break;
                }
            }
            break;
        }
    }

    private void scan(String... paths) {
        if (!mMusicScanner.isConnected()) {
            ToastUtil.$("服务未连接，请稍等");
        } else if (mMusicScanner.isScanning()) {
            ToastUtil.$("请等待上次扫描完成在操作");
        } else {
            mMusicScanner.scan(mListener, paths);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_scan: {
                String text = ((TextView) view).getText().toString();
                if (TEXT_START_SCAN.equals(text)) {
                    Log.d(TAG, "开始扫描");
                    scan(Environment.getExternalStorageDirectory().getAbsolutePath());
                    mIsFullScan = true;
                } else if (TEXT_STOP_SCAN.equals(text)) {
                    mMusicScanner.stopScan();
                } else if (TEXT_FINISH.equals(text)) {
                    //完成扫描，更新数据
                    updateLocalMusic(mScanResult);
                }
            }
            break;
        }
    }

    private void updateLocalMusic(final List<MusicInfo> musicInfos) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在更新数据，请稍等");
        progressDialog.show();
        YiduDB.updateLocalMusic(musicInfos)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            ToastUtil.$("更新完成");
                        } else {
                            ToastUtil.$("更新失败");
                        }
                        progressDialog.cancel();
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        mMusicScanner.stopScan();
        mMusicScanner.disconnect();
        super.onDestroy();
    }
}
