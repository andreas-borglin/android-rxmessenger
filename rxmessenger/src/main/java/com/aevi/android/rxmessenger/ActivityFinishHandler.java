package com.aevi.android.rxmessenger;


import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Helper class to encapsulate the logic required to finish an activity upon request and manage the lifecycle accordingly to avoid memory leaks
 * or old {@link ObservableActivityHelper} instances around.
 */
public class ActivityFinishHandler implements LifecycleObserver {

    private static final String TAG = ActivityFinishHandler.class.getSimpleName();

    private final ObservableActivityHelper<?> observableActivityHelper;
    private final WeakReference<Activity> activityRef;
    private final String activityClassName;
    private Disposable disposable;

    public ActivityFinishHandler(Activity activity, ObservableActivityHelper observableActivityHelper) {
        this.activityRef = new WeakReference<>(activity);
        this.observableActivityHelper = observableActivityHelper;
        this.activityClassName = activity.getClass().getName();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onActivityCreated() {
        Log.d(TAG, "Activity created (" + activityClassName + ")");
        disposable = observableActivityHelper.onFinishActivity().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (activityRef.get() != null) {
                    Activity activity = activityRef.get();
                    if (!activity.isDestroyed() && !activity.isFinishing()) {
                        Log.d(TAG, "Finishing activity (" + activityClassName + ")");
                        activity.finish();
                    }
                    activityRef.clear();
                }
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onActivityDestroyed() {
        Log.d(TAG, "Activity destroyed (" + activityClassName + ")");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (activityRef != null) {
            activityRef.clear();
        }
        observableActivityHelper.removeFromMap();
    }
}