package com.sanron.yidumusic.data.db;

import android.content.Context;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;
import com.sanron.yidumusic.data.db.model.LocalMusic;
import com.sanron.yidumusic.data.db.model.MusicInfo;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayListMembers;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by sanron on 16-7-21.
 */
public class DBObserver implements FlowContentObserver.OnTableChangedListener {
    private static DBObserver sInstance;

    PublishSubject<Class<? extends Model>> mSubject = PublishSubject.create();

    public static DBObserver get() {
        if (sInstance == null) {
            synchronized (DBObserver.class) {
                if (sInstance == null) {
                    sInstance = new DBObserver();
                }
            }
        }
        return sInstance;
    }

    private FlowContentObserver mFlowContentObserver;

    public DBObserver() {
        mFlowContentObserver = new FlowContentObserver();
        mFlowContentObserver.addOnTableChangedListener(this);
    }

    public void init(Context context) {
        mFlowContentObserver.registerForContentChanges(context, MusicInfo.class);
        mFlowContentObserver.registerForContentChanges(context, LocalMusic.class);
        mFlowContentObserver.registerForContentChanges(context, PlayList.class);
        mFlowContentObserver.registerForContentChanges(context, PlayListMembers.class);
    }

    @Override
    public void onTableChanged(@Nullable Class<? extends Model> tableChanged, BaseModel.Action action) {
        mSubject.onNext(tableChanged);
    }

    public Observable<Class<? extends Model>> toObservable(final Class<? extends Model>... tables) {
        return mSubject.filter(new Func1<Class<? extends Model>, Boolean>() {
            @Override
            public Boolean call(Class<? extends Model> aClass) {
                for (Class table : tables) {
                    if (table == aClass) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void beginTransaction() {
        mFlowContentObserver.beginTransaction();
    }

    public void endTranscaction() {
        mFlowContentObserver.endTransactionAndNotify();
    }
}

