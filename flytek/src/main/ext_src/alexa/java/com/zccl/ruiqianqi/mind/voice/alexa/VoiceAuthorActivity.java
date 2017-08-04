package com.zccl.ruiqianqi.mind.voice.alexa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.ScopeFactory;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.PhoneUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.http.http2.MyHttp2Client;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

import static com.zccl.ruiqianqi.mind.voice.alexa.Configuration.DIRECTIVE_URL;
import static com.zccl.ruiqianqi.mind.voice.alexa.Configuration.PRODUCT_ID;


/**
 * Created by ruiqianqi on 2017/2/10 0010.
 */

public class VoiceAuthorActivity extends AppCompatActivity {

    // 类标志
    private static String TAG = VoiceAuthorActivity.class.getSimpleName();
    // 获取ID
    private static final String KEY_ID = "key_robot_id";
    // 获取SID
    private static final String KEY_SID = "key_robot_sid";

    private static final Scope ALEXA_ALL_SCOPE = ScopeFactory.scopeNamed("alexa:all");

    // 唯一序列号
    // INSERT UNIQUE DSN FOR YOUR DEVICE
    private String PRODUCT_DSN;

    // 授权验证方法
    // LWA when used with AVS currently supports the alexa:all scope.
    private enum AUTHORIZE_WAY {
        ALL_WAY,
        PROFILE,
    }

    // 授权验证的方法
    private final AUTHORIZE_WAY mAuthorizeWay = AUTHORIZE_WAY.ALL_WAY;

    // 验证请求类
    private RequestContext mRequestContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = ShareUtils.getP(this).getString(KEY_ID, "");
        String sid = ShareUtils.getP(this).getString(KEY_SID, "");
        PRODUCT_DSN = id + sid;
        if(StringUtils.isEmpty(PRODUCT_DSN)){
            PRODUCT_DSN = PhoneUtils.getUniqueNO(this);
        }

        mRequestContext = RequestContext.create(this);
        mRequestContext.registerListener(new AuthorizeListenerImpl());

    }

    @Override
    protected void onStart() {
        super.onStart();
        getToken(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRequestContext.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取TOKEN, 先看TOKEN是不是在有效期内
     * @param reLogin
     */
    private void getToken(boolean reLogin) {
        if (mAuthorizeWay == AUTHORIZE_WAY.ALL_WAY) {
            Scope[] scopes_all = { ALEXA_ALL_SCOPE };
            AuthorizationManager.getToken(VoiceAuthorActivity.this, scopes_all,
                    new TokenListener(reLogin));
        }
        else {
            Scope[] scopes_profile = { ProfileScope.profile(), ProfileScope.postalCode() };
            AuthorizationManager.getToken(VoiceAuthorActivity.this, scopes_profile,
                    new TokenListener(reLogin));
        }
    }

    /**
     * 登录授权
     */
    private void login() {
        JSONObject scopeData = new JSONObject();
        JSONObject productInstanceAttributes = new JSONObject();
        try {

            if (mAuthorizeWay == AUTHORIZE_WAY.ALL_WAY) {
                productInstanceAttributes.put("deviceSerialNumber", PRODUCT_DSN);
                scopeData.put("productInstanceAttributes", productInstanceAttributes);
                scopeData.put("productID", PRODUCT_ID);
                Scope scope_all = ScopeFactory.scopeNamed("alexa:all", scopeData);

                LogUtils.e(TAG, "authorize alexa:all \nPRODUCT_ID = " + PRODUCT_ID + " \nPRODUCT_DSN = " + PRODUCT_DSN);
                // 开始授权
                AuthorizationManager.authorize(new AuthorizeRequest.Builder(mRequestContext)
                        .addScope(scope_all)
                        .forGrantType(AuthorizeRequest.GrantType.ACCESS_TOKEN)
                        .shouldReturnUserData(false)
                        .build());
            }
            else {
                // 开始授权
                AuthorizationManager.authorize(new AuthorizeRequest.Builder(mRequestContext)
                        .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                        .build());
            }

        } catch (JSONException e) {

        }
    }

    /**
     * 退出登录
     */
    private void loginOut() {
        AuthorizationManager.signOut(getApplicationContext(), new Listener<Void, AuthError>() {
            @Override
            public void onSuccess(Void response) {
                // Set logged out state in UI
            }

            @Override
            public void onError(AuthError authError) {
                // Log the error
            }
        });
    }

    /**
     * 取得用户信息
     * Profile request not valid for authorized scopes【alexa:all 报的错】
     * User.getUserPostalCode is only returned if you request the ProfileScope.postalCode() scope.
     */
    private void fetchUserProfile() {
        if (mAuthorizeWay == AUTHORIZE_WAY.PROFILE) {
            User.fetch(this, new Listener<User, AuthError>() {

                /**
                 * fetch completed successfully.
                 */
                @Override
                public void onSuccess(User user) {
                    String name = user.getUserName();
                    String email = user.getUserEmail();
                    String account = user.getUserId();
                    String zipCode = user.getUserPostalCode();

                    StringBuilder profileBuilder = new StringBuilder();
                    profileBuilder.append(String.format("Welcome, %s!\n", name));
                    profileBuilder.append(String.format("Your email is %s\n", email));
                    profileBuilder.append(String.format("Your userId is %s\n", account));
                    profileBuilder.append(String.format("Your zipCode is %s\n", zipCode));

                    String profile = profileBuilder.toString();
                    LogUtils.e(TAG, "Profile Response: " + profile);
                }

                /**
                 * There was an error during the attempt to get the profile.
                 */
                @Override
                public void onError(AuthError ae) {
                    LogUtils.e(TAG, ae.getMessage(), ae);
                }

            });
        }
    }


    /**
     * 用户验证回调
     */
    private class AuthorizeListenerImpl extends AuthorizeListener {
        /*
        * Authorization was completed successfully.
        * 授权成功
        */
        @Override
        public void onSuccess(AuthorizeResult authorizeResult) {

            String authorizationCode = authorizeResult.getAuthorizationCode();
            String redirectUri = authorizeResult.getRedirectURI();
            String clientId = authorizeResult.getClientId();
            String accessToken = authorizeResult.getAccessToken();

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append(String.format("authorizationCode is %s!\n", authorizationCode));
            resultBuilder.append(String.format("redirectUri is %s\n", redirectUri));
            resultBuilder.append(String.format("clientId is %s\n", clientId));
            resultBuilder.append(String.format("accessToken is %s\n", accessToken));
            LogUtils.e(TAG, resultBuilder.toString());

            //fetchUserProfile();

            // call AmazonAuthorizationManager.getToken to retrieve the access token
            getToken(false);
        }

        /*
         * There was an error during the attempt to authorize the application.
         * 尝试授权的时候出错了
         */
        @Override
        public void onError(final AuthError authError) {
            LogUtils.e(TAG, "onError = " + authError.getType() + authError.getCategory(), authError);
        }

        /*
         * Authorization was cancelled before it could be completed.
         * 在授权完成前取消了
         */
        @Override
        public void onCancel(final AuthCancellation authCancellation) {
            LogUtils.e(TAG, "onCancel = " + authCancellation.getDescription());
        }

    }

    /**
     * 获取TOKEN回调
     */
    public class TokenListener implements Listener<AuthorizeResult, AuthError> {

        // 获取TOKEN失败，是否重新登录
        private boolean reLogin;

        public TokenListener(boolean reLogin) {
            this.reLogin = reLogin;
        }

        /**
         * getToken completed successfully.
         */
        @Override
        public void onSuccess(AuthorizeResult authorizeResult) {
            String accessToken = authorizeResult.getAccessToken();

            // TOKEN在有效期内
            if (!StringUtils.isEmpty(accessToken)) {
                // 存储TOKEN
                ShareUtils.getE(VoiceAuthorActivity.this).putString("TOKEN", accessToken).commit();
                LogUtils.e(TAG, "accessToken = " + accessToken);
                VoiceAuthorActivity.this.finish();

                initDownChannel(accessToken);

            }
            // TOKEN已过期
            else {
                // 清除TOKEN
                ShareUtils.getE(VoiceAuthorActivity.this).putString("TOKEN", null).commit();
                LogUtils.e(TAG, "accessToken is null");

                // 重新登录
                if (reLogin) {
                    login();
                }

            }
        }

        /**
         * There was an error during the attempt to get the token.
         */
        @Override
        public void onError(AuthError authError) {
            // 清除TOKEN
            ShareUtils.getE(VoiceAuthorActivity.this).putString("TOKEN", null).commit();
            LogUtils.e(TAG, "get accessToken failed!");

            // 重新登录
            if (reLogin) {
                login();
            }

        }
    }

    /**
     * Establishing the downchannel stream
     */
    private void initDownChannel(String accessToken){
        // AVS特有的头消息
        Request.Builder builder = Configuration.addGetHeaders(accessToken);
        MyHttp2Client.getAsync(builder, DIRECTIVE_URL);
    }
}
