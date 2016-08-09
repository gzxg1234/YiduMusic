package com.sanron.yidumusic.rx;

import android.content.Context;

import com.sanron.yidumusic.util.ToastUtil;

/**
 * Created by sanron on 16-7-22.
 */
public class ToastSubscriber<T> extends SubscriberAdapter<T> {

    private Context mContext;

    protected ToastSubscriber(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onError(Throwable e) {
//        e.printStackTrace();
        ToastUtil.$("获取数据失败");
    }
}
