package com.botu.img;

import android.app.Application;
import android.util.Log;

import com.botu.img.base.IConstants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author: swolf
 * @date : 2016-11-09 17:36
 */
public class MyApp extends Application{
    public static IWXAPI wxApi;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册微信
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), IConstants.WX_APP_ID);
        wxApi.registerApp(IConstants.WX_APP_ID);

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
