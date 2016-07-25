package com.sanron.yidumusic.rx;

import android.content.Context;
import android.widget.Toast;

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
        Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
    }
}
