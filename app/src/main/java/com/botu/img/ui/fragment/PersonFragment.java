package com.botu.img.ui.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.cache.ACache;
import com.botu.img.ui.activity.FavouriteActivity;
import com.botu.img.ui.activity.FootActivity;
import com.botu.img.ui.activity.ShareActivity;
import com.botu.img.ui.view.CircleImageView;
import com.botu.img.utils.L;
import com.botu.img.utils.SpUtils;
import com.botu.img.utils.SystemUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
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

import okhttp3.Call;
import okhttp3.Response;

import static com.botu.img.MyApp.mAuthInfo;
import static com.botu.img.MyApp.sTencent;
import static com.botu.img.base.IConstants.SCOPE;


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
    private LoginIUiListener loginListener;
    private UserInfoIUiListener userInfoIUiListener;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;

    private int loginPlatform = 0;
    private ProgressDialog mDialog;
    private String mOpenID;
    private ACache mCache;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void initView() {
        mCache = ACache.get(mActivity);
        llLogin = (RelativeLayout) mActivity.findViewById(R.id.ll_login_from);
        mLlContent = (LinearLayout) mActivity.findViewById(R.id.ll_userinfo_content);
        mWx = (ImageView) mActivity.findViewById(R.id.iv_wx);
        mQq = (ImageView) mActivity.findViewById(R.id.iv_qq);
        mSina = (ImageView) mActivity.findViewById(R.id.iv_sina);

        mFavourite = (LinearLayout) mActivity.findViewById(R.id.ll_favourite);
        mShare = (LinearLayout) mActivity.findViewById(R.id.ll_share);
        mRecent = (LinearLayout) mActivity.findViewById(R.id.ll_recent);
        mIvHead = (CircleImageView) mActivity.findViewById(R.id.iv_head);
        mTvName = (TextView) mActivity.findViewById(R.id.tv_name);

        mWx.setOnClickListener(this);
        mQq.setOnClickListener(this);
        mSina.setOnClickListener(this);

        mFavourite.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mRecent.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //注册登录广播
        mReceiver = new LoginBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IConstants.loginReceiver);
        mActivity.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SpUtils.getBoolean(mActivity, IConstants.isLogin, false)) {
            //登录则显示个人中心界面
            llLogin.setVisibility(View.GONE);
            mLlContent.setVisibility(View.VISIBLE);
            Glide.with(mActivity).load(SpUtils.getString(mActivity, IConstants.header, "")).fitCenter().into(mIvHead); //头像
            mTvName.setText(SpUtils.getString(mActivity, IConstants.username, ""));
        } else {
            //未登录则显示登录界面
            llLogin.setVisibility(View.VISIBLE);
            mLlContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        //注销登录,以便下次登录
        if (sTencent != null) {
            sTencent.logout(mActivity);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mActivity.unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
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
                intent = new Intent(mActivity, FavouriteActivity.class);
                mActivity.startActivity(intent);
                break;
            case R.id.ll_share:
                intent = new Intent(mActivity, ShareActivity.class);
                mActivity.startActivity(intent);
                break;
            case R.id.ll_recent:
                intent = new Intent(mActivity, FootActivity.class);
                mActivity.startActivity(intent);
                break;
        }
    }

    private void loginSina() {
        showDialog();
        loginPlatform = 2;
        mSsoHandler = new SsoHandler(mActivity, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    private void loginWx() {
        showDialog();
        if (!MyApp.wxApi.isWXAppInstalled()) {
            Toast.makeText(mActivity, "微信未安装", Toast.LENGTH_LONG);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com.botu.img";
        MyApp.wxApi.sendReq(req);
//        dismissDialog();
    }

    private void loginQQ() {
        showDialog();
        loginPlatform = 1;
        loginListener = new LoginIUiListener();
        userInfoIUiListener = new UserInfoIUiListener();

        if (sTencent == null) {
            sTencent = Tencent.createInstance(IConstants.QQ_APP_ID, mActivity);
        }

        if (!sTencent.isSessionValid()) {
            sTencent.login(mActivity, SCOPE, loginListener);
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
                    //获取用户信息
                    UserInfo info = new UserInfo(mActivity, MyApp.sTencent.getQQToken());
                    info.getUserInfo(userInfoIUiListener);
                }
            }
        } else if (loginPlatform == 2) {
            // SSO 授权回调+
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }

    }

    private void showDialog() {
        mDialog = new ProgressDialog(mActivity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("正在登录中...");
        mDialog.show();
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    //发送登录信息到后台，注册，接收该用户的个人资料
    private void setCache(String img, String name, String type) {
        dismissDialog();
        String deviceId = SystemUtils.getDeviceID(mActivity);
        OkGo.post(IConstants.LOGIN_URL)
                .params("deviceid", deviceId)
                .params("openid", mOpenID)
                .params("opentype", type)
                .params("username", name)
                .params("headpic", img)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        L.i(s);
                        try {
                            JSONObject js = new JSONObject(s);
                            if (1 == js.getInt("code")) { //登录成功
                                JSONObject data = (JSONObject) js.get("data");
                                //接收到个人信息存储到数据库
                                SpUtils.setBoolean(mActivity, IConstants.isLogin, true);
                                SpUtils.setString(mActivity, IConstants.header, data.getString("headpic"));
                                SpUtils.setString(mActivity, IConstants.username, data.getString("username"));
                                SpUtils.setInt(mActivity, IConstants.userId, data.getInt("id"));
                                //刷新界面
                                onStart();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        L.i(e.toString());
                    }
                });
    }

    //QQ 登录回调接口
    class LoginIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject jo = (JSONObject) o;
                mOpenID = jo.getString("openid");
                String accessToken = jo.getString("access_token");
                String expires = jo.getString("expires_in");
                sTencent.setOpenId(mOpenID);
                sTencent.setAccessToken(accessToken, expires);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            dismissDialog();
        }

        @Override
        public void onCancel() {
            dismissDialog();
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
                //回调
                setCache(img, nickName, "qq");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            dismissDialog();
        }

        @Override
        public void onCancel() {
            dismissDialog();
        }
    }

    //微博登录回调
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle bundle) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);// 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
                //获取用户信息
                UsersAPI usersAPI = new UsersAPI(mActivity, IConstants.SINA_APP_KEY, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                usersAPI.show(uid, mRequestListener);
                mOpenID = mAccessToken.getUid();
            } else {
                String code = bundle.getString("code");
                String message = mActivity.getString(R.string.auth_failure);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\ncode: " + code;
                }
                L.i("微博授权 " + message);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            L.e("WeiboException: " + e.getMessage());
            dismissDialog();
        }

        @Override
        public void onCancel() {
            dismissDialog();
        }
    }

    //新浪微博获取用户信息接口
    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                setCache(user.profile_image_url, user.screen_name, "weibo");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            dismissDialog();
        }
    };

    //WX登录广播
    class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String img = intent.getStringExtra(IConstants.header);
            String name = intent.getStringExtra(IConstants.username);
            mOpenID = intent.getStringExtra(IConstants.openId);
            setCache(img, name, "wechat");
        }
    }

}
