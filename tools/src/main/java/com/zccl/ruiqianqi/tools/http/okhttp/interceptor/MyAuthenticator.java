package com.zccl.ruiqianqi.tools.http.okhttp.interceptor;

import android.content.Context;

import com.zccl.ruiqianqi.tools.ShareUtils;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by ruiqianqi on 2016/8/11 0011.
 */
public class MyAuthenticator implements Authenticator {

    /** 登录TOKEN的存储KEY */
    public static final String TOKEN_KEY = "Authorization";
    /** 全局上下文 */
    private Context mContext;

    public MyAuthenticator(Context context){
        this.mContext = context;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        String token = ShareUtils.getP(mContext).getString(TOKEN_KEY, null);
        return response.request().newBuilder()
                .addHeader(TOKEN_KEY, token)
                .build();
    }

}
