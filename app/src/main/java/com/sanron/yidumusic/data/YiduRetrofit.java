package com.sanron.yidumusic.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanron.yidumusic.YiduApp;
import com.sanron.yidumusic.util.baidu.DeviceTool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by sanron on 16-7-13.
 */
public class YiduRetrofit {

    private static class HOLDER {
        private static YiduRetrofit INSTANCE = new YiduRetrofit();
    }

    public static YiduRetrofit get() {
        return HOLDER.INSTANCE;
    }

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private BaiduApiService mApiService;

    private final Interceptor INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //统一参数
            HttpUrl httpUrl = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("from", "android")
                    .addQueryParameter("version", "5.6.5.6")
                    .addQueryParameter("format", "json")
                    .build();
            //设置UserAgent
            Request request = chain.request()
                    .newBuilder()
                    .url(httpUrl)
                    .addHeader("cuid", DeviceTool.getDeviceId(YiduApp.get()))
                    .addHeader("User-Agent", "android_5.8.0.1;baiduyinyue")
                    .build();
            return chain.proceed(request);
        }
    };

    private YiduRetrofit() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(INTERCEPTOR)
                .addNetworkInterceptor(INTERCEPTOR)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BaiduApiService.BASE)
                .client(mOkHttpClient)
                .build();

        mApiService = mRetrofit.create(BaiduApiService.class);
    }

    public BaiduApiService getApiService() {
        return mApiService;
    }
}
