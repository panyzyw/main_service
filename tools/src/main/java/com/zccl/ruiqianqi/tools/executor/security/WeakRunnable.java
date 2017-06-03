package com.zccl.ruiqianqi.tools.executor.security;

import java.lang.ref.WeakReference;

/**
 * Created by zc on 2016/4/21.
 */
public abstract class WeakRunnable<T> implements Runnable {
    protected WeakReference<T> weak;

    public WeakRunnable(T reference) {
        weak = new WeakReference<T>(reference);
    }

    @Override
    public void run() {
        if (weak.get()==null) {
            over();
        }else {
            run(weak.get());
        }
    }

    protected abstract void run(T reference);
    protected void over(){}
}