package com.botu.img.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.utils.SpUtils;
import com.botu.sticklibrary.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的
 *
 * @author: swolf
 * @date : 2016-11-03 09:58
 */
public class PersonFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = PersonFragment.class.getSimpleName();

    private ImageView mSina;
    private ImageView mQq;
    private ImageView mWx;
    private RelativeLayout llLogin;
    private LinearLayout mLlContent;
    private LinearLayout mFavourite;
    private LinearLayout mShare;
    private LinearLayout mRecent;
    private CircleImageView mIvHead;
    private TextView mTvName;

    private LoginBroadcastReceiver mReceiver;
    public Tencent mTencent;
    private String SCOPE = "all";
    private LoginIUiListener loginListener;
    private UserInfoIUiListener userInfoIUiListener;
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;

    private int loginPlatform = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void initView() {
        llLogin = (RelativeLayout) mActivity.findViewById(R.id.ll_login_from);
        mWx = (ImageView) mActivity.findViewById(R.id.iv_wx);
        mQq = (ImageView) mActivity.findViewById(R.id.iv_qq);
        mSina = (ImageView) mActivity.findViewById(R.id.iv_sina);

        mWx.setOnClickListener(this);
        mQq.setOnClickListener(this);
        mSina.setOnClickListener(this);

        mLlContent = (LinearLayout) mActivity.findViewById(R.id.ll_userinfo_content);
        mFavourite = (LinearLayout) mActivity.findViewById(R.id.ll_favourite);
        mShare = (LinearLayout) mActivity.findViewById(R.id.ll_share);
        mRecent = (LinearLayout) mActivity.findViewById(R.id.ll_recent);
        mIvHead = (CircleImageView) mActivity.findViewById(R.id.iv_head);
        mTvName = (TextView) mActivity.findViewById(R.id.tv_name);

        mFavourite.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mRecent.setOnClickListener(this);

        //先判断是否登录过
        if (SpUtils.getBoolean(mActivity, "isLogin", false)) {
            //登录则显示个人衷心界面
            llLogin.setVisibility(View.VISIBLE);
            mLlContent.setVisibility(View.GONE);
            Glide.with(mActivity).load(SpUtils.getString(mActivity, "img", "")).into(mIvHead); //加载图片
            mTvName.setText(SpUtils.getString(mActivity, "name", ""));

        } else {
            //未登录则显示登录界面
            llLogin.setVisibility(View.GONE);
            mLlContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initData() {
        //注册
        mReceiver = new LoginBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.botu.img");
        mActivity.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SpUtils.getBoolean(mActivity, "isLogin", false)) {
            llLogin.setVisibility(View.GONE);
            mLlContent.setVisibility(View.VISIBLE);
            Glide.with(mActivity).load(SpUtils.getString(mActivity, "img", "")).into(mIvHead); //加载图片
            mTvName.setText(SpUtils.getString(mActivity, "name", ""));
        } else {
            llLogin.setVisibility(View.VISIBLE);
            mLlContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        if (mTencent != null) {
            //注销登录
            mTencent.logout(mActivity);
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wx:
                loginWx();
                break;
            case R.id.iv_sina:
                loginSina();
                break;
            case R.id.iv_qq:
                loginQQ();
                break;

            case R.id.ll_favourite:
                break;
            case R.id.ll_share:
                break;
            case R.id.ll_recent:
                break;
        }
    }

    private void loginSina() {
        loginPlatform = 2;
        mAuthInfo = new AuthInfo(mActivity, IConstants.SINA_APP_KEY, IConstants.REDIRECT_URL, SCOPE);
        mSsoHandler = new SsoHandler(mActivity, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    private void loginWx() {
        if (!MyApp.wxApi.isWXAppInstalled()) {
            Toast.makeText(mActivity, "微信未安装", Toast.LENGTH_LONG);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com.botu.img";
        MyApp.wxApi.sendReq(req);
    }

    private void loginQQ() {
        loginPlatform = 1;
        if (mTencent == null) {
            mTencent = Tencent.createInstance(IConstants.QQ_APP_ID, mActivity);
        }

        loginListener = new LoginIUiListener();
        userInfoIUiListener = new UserInfoIUiListener();

        if (!mTencent.isSessionValid()) {
            mTencent.login(mActivity, SCOPE, loginListener);
        }
    }

    //在上层Activity中调用了该方法，不然不执行fragment的onActivityResult()方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginPlatform == 1) {
            //必须要有这句，不然不回调
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
            if (requestCode == Constants.REQUEST_LOGIN) {
                if (resultCode == -1) {
                    Tencent.handleResultData(data, loginListener);
                    UserInfo info = new UserInfo(mActivity, mTencent.getQQToken());
                    info.getUserInfo(userInfoIUiListener);
                }
            }
        } else if (loginPlatform == 2) {
            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }

    }

    //设置sp
    private void setSp(String img, String name) {
        SpUtils.setBoolean(mActivity, "isLogin", true);
        SpUtils.setString(mActivity, "img", img);
        SpUtils.setString(mActivity, "name", name);
        onStart();
    }

    //QQ 登录回调接口
    class LoginIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject jo = (JSONObject) o;
                String openID = jo.getString("openid");
                String accessToken = jo.getString("access_token");
                String expires = jo.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
        }

        @Override
        public void onCancel() {
        }
    }

    //QQ 用户信息回调接口
    class UserInfoIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject jo = (JSONObject) o;
                String nickName = jo.getString("nickname");
                String img = jo.getString("figureurl_qq_1");
                setSp(img, nickName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
        }

        @Override
        public void onCancel() {

        }
    }

    //微博登录回调
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle bundle) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);// 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
//                String UID = mAccessToken.getUid();
//                String accessToken = mAccessToken.getToken();
                //获取用户信息
                UsersAPI usersAPI = new UsersAPI(mActivity, IConstants.SINA_APP_KEY, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                usersAPI.show(uid, mRequestListener);
                Toast.makeText(mActivity, R.string.auth_success, Toast.LENGTH_SHORT).show();
            } else {
                String code = bundle.getString("code");
                String message = mActivity.getString(R.string.auth_failure);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\ncode: " + code;
                }
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.e("hlh", "WeiboException: " + e.getMessage());
        }

        @Override
        public void onCancel() {
            Toast.makeText(mActivity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
        }
    }

    //新浪微博获取用户信息接口
    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            Log.e("hlh", "RequestListener-------onComplete: ");
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                setSp(user.profile_image_url, user.screen_name);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    };

    //WX登录广播
    class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String img = intent.getStringExtra("img");
            String name = intent.getStringExtra("name");
            setSp(img, name);
        }
    }

}
