package com.sanron.yidumusic;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.sanron.yidumusic.data.db.HttpCache;
import com.sanron.yidumusic.data.net.YiduRetrofit;
import com.sanron.yidumusic.data.net.repository.DataRepository;
import com.sanron.yidumusic.data.net.repository.LocalDataResource;
import com.sanron.yidumusic.data.net.repository.RemoteDataResource;
import com.sanron.yidumusic.util.ToastUtil;
import com.squareup.leakcanary.LeakCanary;

/**************************************
 * FileName : com.sanron.yidumusic
 * Author : Administrator
 * Time : 2016/7/15 15:50
 * Description :
 **************************************/
public class YiduApp extends Application {

    private static YiduApp sInstance;
    private DataRepository mDataRepository;

    public DataRepository getDataRepository() {
        return mDataRepository;
    }

    public static YiduApp get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ToastUtil.init(this);
        LeakCanary.install(this);
        FlowConfig flowConfig = new FlowConfig.Builder(this)
                .build();
        FlowManager.init(flowConfig);
        mDataRepository = new DataRepository(
                new LocalDataResource(new HttpCache()),
                new RemoteDataResource(YiduRetrofit.get().getApiService()));
    }

}
