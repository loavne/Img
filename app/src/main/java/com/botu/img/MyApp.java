package com.botu.img;

import android.app.Application;

import com.botu.img.base.IConstants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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
    }
}
