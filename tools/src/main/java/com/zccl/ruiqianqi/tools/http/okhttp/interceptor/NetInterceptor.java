package com.zccl.ruiqianqi.tools.http.okhttp.interceptor;

import android.content.Context;

import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ruiqianqi on 2016/8/11 0011.
 */
public class NetInterceptor implements Interceptor {

    /** 全局上下文 */
    private Context mContext;

    public NetInterceptor(Context context){
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 看看本地有没有存TOKEN
        String token = ShareUtils.getP(mContext).getString(MyAuthenticator.TOKEN_KEY, null);
        if (token == null || noAuthorizationHeader(originalRequest)) {
            return chain.proceed(originalRequest);
        }
        // 有就带上
        Request authorised = originalRequest.newBuilder()
                .header(MyAuthenticator.TOKEN_KEY, token)
                .build();
        return chain.proceed(authorised);
    }

    /**
     * 验证请求中是否有带TOKEN
     * @param request
     * @return
     */
    private boolean noAuthorizationHeader(Request request){
        String token = request.header(MyAuthenticator.TOKEN_KEY);
        if(StringUtils.isEmpty(token)){
            return true;
        }
        return false;
    }
}
