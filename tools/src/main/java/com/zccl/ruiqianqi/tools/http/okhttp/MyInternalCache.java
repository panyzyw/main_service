package com.zccl.ruiqianqi.tools.http.okhttp;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.InternalCache;

/**
 * Created by ruiqianqi on 2016/8/11 0011.
 */
public class MyInternalCache implements InternalCache {
    @Override
    public Response get(Request request) throws IOException {
        return null;
    }

    @Override
    public CacheRequest put(Response response) throws IOException {
        return null;
    }

    @Override
    public void remove(Request request) throws IOException {

    }

    @Override
    public void update(Response cached, Response network) {

    }

    @Override
    public void trackConditionalCacheHit() {

    }

    @Override
    public void trackResponse(CacheStrategy cacheStrategy) {

    }
}
