package com.sanron.yidumusic.rx;

import com.sanron.yidumusic.data.ApiException;
import com.sanron.yidumusic.data.model.response.BaseData;

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
                        .map(new Func1<T, T>() {
                            @Override
                            public T call(T t) {
                                if (t.errorCode != BaseData.CODE_SUCCES) {
                                    throw new ApiException(t.errorCode);
                                }
                                return t;
                            }
                        });
            }
        };
    }

    public static <T extends BaseData> Observable.Transformer<T, T> apply() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.compose(TransformerUtil.<T>checkError())
                        .compose(TransformerUtil.<T>io());
            }
        };
    }
}
