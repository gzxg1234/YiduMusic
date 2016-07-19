package com.sanron.yidumusic.ui.activity;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicScanner {

    private Context mContext;
    private MediaScannerConnection mScannerConnection;
    private MediaScannerConnection.MediaScannerConnectionClient mScannerClient;

    private AtomicBoolean mIsScanning = new AtomicBoolean(false);

    private TraverseThread mTraverseThread;

    private final Object mLock = new Object();

    private OnScanMediaListener mOnScanMediaListener;

    public static final String TAG = MusicScanner.class.getSimpleName();


    public interface ConnectionCallback {
        void onConnected();
    }

    public interface OnScanMediaListener {
        /**
         * 扫描开始
         */
        void onStart();

        /**
         * 扫描到音乐文件时
         *
         * @param path
         * @param uri
         */
        void onProgress(String path, Uri uri);

        /**
         * 扫描完成时
         *
         * @param fromStop 是否停止导致完成
         */
        void onCompleted(boolean fromStop);
    }

    public MusicScanner(Context context) {
        this.mContext = context;
    }

    public void connect(final ConnectionCallback callback) {
        mScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {

            @Override
            public void onMediaScannerConnected() {
                Log.d(TAG, "MediaScanner conntected");
                if (callback != null) {
                    callback.onConnected();
                }
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "file:" + path + " scan completed");
                if (mOnScanMediaListener != null) {
                    mOnScanMediaListener.onProgress(path, uri);
                }
                synchronized (mLock) {
                    mLock.notify();
                }
            }
        };
        mScannerConnection = new MediaScannerConnection(mContext, mScannerClient);
        mScannerConnection.connect();
    }


    public void scan(final OnScanMediaListener listener, String... paths) {
        if (mIsScanning.get()) {
            throw new IllegalStateException("last scan is not completed");
        }

        mIsScanning.set(true);
        mOnScanMediaListener = listener;
        mTraverseThread = new TraverseThread(paths);
        mTraverseThread.start();
    }

    public boolean isScanning() {
        return mIsScanning.get();
    }

    public boolean isConnected() {
        return mScannerConnection.isConnected();
    }

    public void stopScan() {
        if (mTraverseThread != null) {
            mTraverseThread.stopRun();
        }
    }

    public void disconnect() {
        stopScan();
        mScannerConnection.disconnect();
    }

    /**
     * 遍历文件线程
     */
    private class TraverseThread extends Thread {

        private String[] mPaths;
        private boolean mFlagStop = false;

        public TraverseThread(String... paths) {
            this.mPaths = paths;
        }

        public void stopRun() {
            mFlagStop = true;
            synchronized (mLock) {
                mLock.notify();
            }
        }

        @Override
        public void run() {
            mOnScanMediaListener.onStart();
            traversePaths(mPaths);
            mOnScanMediaListener.onCompleted(mFlagStop);
            mIsScanning.set(false);
            mOnScanMediaListener = null;
            mTraverseThread = null;
            Log.d(TAG, "scan completed");
        }

        public void traversePaths(String[] paths) {
            for (int i = 0; i < paths.length; i++) {
                traverse(paths[i]);
            }
        }

        /**
         * 遍历文件
         *
         * @param path
         */
        private void traverse(String path) {
            if (mFlagStop) {
                return;
            }

            File file = new File(path);
            if (file.isDirectory()) {
                File[] childs = file.listFiles();
                for (File child : childs) {
                    if (mFlagStop) {
                        return;
                    }
                    traverse(child.getAbsolutePath());
                }
            } else {
                if (path.startsWith(".")) {
                    //隐藏文件不扫描
                    return;
                }
                if (judgeExtension(path)) {
                    synchronized (mLock) {
                        mScannerConnection.scanFile(path, "audio/*");
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private boolean judgeExtension(String path) {
            return path.endsWith(".mp3")
                    || path.endsWith(".wav")
                    || path.endsWith(".m4a")
                    || path.endsWith(".aac");
        }
    }
}