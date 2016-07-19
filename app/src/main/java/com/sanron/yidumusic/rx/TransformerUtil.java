package com.sanron.yidumusic.rx;

import com.sanron.yidumusic.data.net.ApiException;
import com.sanron.yidumusic.data.net.model.response.BaseData;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sanron on 16-7-16.
 */
public class TransformerUtil {

    public static <T> Observable.Transformer<T, T> io() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 检查errorcode
     */
    public static <T extends BaseData> Observable.Transformer<T, T> checkError() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .flatMap(new Func1<T, Observable<T>>() {
                            @Override
                            public Observable<T> call(T t) {
                                if (t.errorCode != BaseData.CODE_SUCCES) {
                                    return Observable.error(new ApiException(t.errorCode));
                                } else {
                                    return Observable.just(t);
                                }
                            }
                        });
            }
        };
    }

    public static <T extends BaseData> Observable.Transformer<T, T> net() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.compose(TransformerUtil.<T>checkError())
                        .compose(TransformerUtil.<T>io());
            }
        };
    }
}
