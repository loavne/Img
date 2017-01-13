/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.botu.img.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.botu.img.MyApp;
import com.botu.img.base.IConstants;
import com.botu.img.bean.WxAccessToken;
import com.botu.img.bean.WxUserInfo;
import com.botu.img.callback.JsonCallback;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    //微信返回的消息类型，
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private String token_url = "";
    private String user_info_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这句不添加则调用不起
        MyApp.wxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result = null;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //拿到了微信返回的code,立马再去请求access_token
                String code = ((SendAuth.Resp) baseResp).code; //授权code
                //发送请求获取access_token
                getAccessToken(code);
                WXEntryActivity.this.finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }
    }

    /**
     * 获取微信access_token
     *
     * @param code
     */
    private void getAccessToken(String code) {
        //接口
        token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + IConstants.WX_APP_ID
                + "&secret="
                + IConstants.WX_APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
        OkGo.get(token_url)
                .execute(new JsonCallback<WxAccessToken>(WxAccessToken.class) {
                    @Override
                    public void onSuccess(WxAccessToken wxAccessToken, Call call, Response response) {
                        super.onSuccess(wxAccessToken, call, response);
                        //获取到access_token和OpenId
                        getUserInfo(wxAccessToken.getAccess_token(), wxAccessToken.getOpenid());
                    }

                    @Override
                    public WxAccessToken convertSuccess(Response response) throws Exception {
                        String result = response.body().string();
                        if (TextUtils.isEmpty(result)) return null;
                        return new Gson().fromJson(result, WxAccessToken.class);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo(String token, final String openid) {
        //接口
        user_info_url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + token
                + "&openid="
                + openid;
        OkGo.get(user_info_url)
                .execute(new JsonCallback<WxUserInfo>(WxUserInfo.class) {
                    @Override
                    public void onSuccess(WxUserInfo wxUserInfo, Call call, Response response) {
                        Log.e("hlh", "onSuccess: " + wxUserInfo.toString());
                        //发送广播告知PersonFrament，成功获取信息
                        Intent intent = new Intent(IConstants.loginReceiver);
                        intent.putExtra(IConstants.header, wxUserInfo.getHeadimgurl());
                        intent.putExtra(IConstants.username, wxUserInfo.getNickname());
                        intent.putExtra(IConstants.openId, openid);
                        sendBroadcast(intent);
                        WXEntryActivity.this.finish();
                    }

                    @Override
                    public WxUserInfo convertSuccess(Response response) throws Exception {
                        String result = response.body().string();
                        if (TextUtils.isEmpty(result)) return null;
                        return new Gson().fromJson(result, WxUserInfo.class);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
