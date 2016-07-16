package com.sanron.yidumusic;

import android.app.Application;

/**************************************
 * FileName : com.sanron.yidumusic
 * Author : Administrator
 * Time : 2016/7/15 15:50
 * Description :
 **************************************/
public class YiduApp extends Application {

    private static YiduApp sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static YiduApp get() {
        return sInstance;
    }
}
