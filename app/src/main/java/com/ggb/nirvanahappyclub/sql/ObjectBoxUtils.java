package com.ggb.nirvanahappyclub.sql;

import android.content.Context;
import android.util.Log;

import com.ggb.nirvanahappyclub.BuildConfig;
import com.ggb.nirvanahappyclub.sql.entity.MyObjectBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.reactive.DataSubscription;
import io.reactivex.Observable;

public class ObjectBoxUtils {

    private static BoxStore boxStore;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder().androidContext(context).build();
        if (BuildConfig.DEBUG) {
            boolean started = new AndroidObjectBrowser(boxStore).start(context);
            Log.i("ObjectBrowser", "Started: " + started);
        }
    }

    public static <T> Box<T> boxFor(Class<T> clazz) {
        return boxStore.boxFor(clazz);
    }

    public static <T> Observable<Box<T>> boxForRx(Class<T> t) {
        return Observable.just(t)
                .map(tClass -> boxStore.boxFor(tClass));
    }

    /**
     * 监听表的增删改变化
     *
     * @param boxClass BoxClass
     * @param <T>      BoxType
     * @return Box
     */
    public static <T> Observable<Class<T>> listenBoxFor(Class<T> boxClass) {
        return Observable.create(emitter -> {
            final DataSubscription dataSubscription = boxStore.subscribe(boxClass).observer(data -> {
                if (!emitter.isDisposed()) {
                    emitter.onNext(data);
                }
            });
            emitter.setCancellable(dataSubscription::cancel);
        });
    }

    public static void deleteAll() {
        Collection<Class<?>> allEntity = boxStore.getAllEntityClasses();
        for (Class<?> c : allEntity) {
            boxFor(c).removeAll();
        }
    }

}
