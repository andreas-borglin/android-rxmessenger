package com.aevi.android.rxmessenger;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class ObservableActivityHelper<T> {

    private static final String INTENT_ID = "ObservableActivityHelper.ID";

    private static Map<String, ObservableActivityHelper> INSTANCES_MAP = new java.util.HashMap<>();

    private ObservableEmitter<T> emitter;
    private final Context context;
    private final Intent intent;

    private ObservableActivityHelper(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    public static <T> ObservableActivityHelper<T> getInstance(Context context, Intent intent) {
        String uuid = UUID.randomUUID().toString();
        if (intent.hasExtra(INTENT_ID)) {
            uuid = intent.getStringExtra(INTENT_ID);
        }

        intent.putExtra(INTENT_ID, uuid);

        if (!INSTANCES_MAP.containsKey(uuid)) {
            ObservableActivityHelper<T> instance = new ObservableActivityHelper<>(context, intent);
            INSTANCES_MAP.put(uuid, instance);
        }
        return INSTANCES_MAP.get(uuid);
    }

    public void publishResponse(T response) {
        emitter.onNext(response);
        emitter.onComplete();
    }

    public void returnError(MessageException me) {
        emitter.onError(me);
    }

    public Observable<T> startObservableActivity() {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
                Log.d(ObservableActivityHelper.class.getSimpleName(), "subscribe - starting activity: " + intent.toString());
                ObservableActivityHelper.this.emitter = emitter;
                context.startActivity(intent);
            }
        });
    }

}
