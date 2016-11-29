package com.botu.img;

import android.app.Application;
import android.util.Log;

import com.botu.img.base.IConstants;
import com.botu.img.db.DbCore;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.tauth.Tencent;

/**
 * @author: swolf
 * @date : 2016-11-09 17:36
 */
public class MyApp extends Application{
    public static boolean isNetworkConn;

    public static IWXAPI wxApi;
    public static Tencent mTencent;
    public static AuthInfo mAuthInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册微信
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), IConstants.WX_APP_ID);
        wxApi.registerApp(IConstants.WX_APP_ID);

        //注册QQ
        mTencent = Tencent.createInstance(IConstants.QQ_APP_ID, getApplicationContext());

        //注册sinaWeibo
        mAuthInfo = new AuthInfo(getApplicationContext(), IConstants.SINA_APP_KEY, IConstants.REDIRECT_URL, IConstants.SCOPE);
        //初始化greenDao
        DbCore.init(this);

        //注册腾讯X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e("pp", " onViewInitFinished is " + b);
            }
        };
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

}
