package com.botu.img.ui.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.botu.img.R;
import com.botu.img.utils.SpUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * 我的
 * @author: swolf
 * @date : 2016-11-03 09:58
 */
public class PersonFragment extends BaseFragment implements View.OnClickListener, PlatformActionListener {

    public static final String TAG = PersonFragment.class.getSimpleName();

    private ImageView mSina;
    private ImageView mQq;
    private ImageView mWx;
    private RelativeLayout llLogin;
    private LinearLayout mLlContent;
    private LinearLayout mFavourite;
    private LinearLayout mShare;
    private LinearLayout mRecent;
    private Platform mPlatform;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void initView() {
        ShareSDK.initSDK(mActivity);

        //先判断是否登录过
        if (SpUtils.getBoolean(mActivity, "isLogin", false)) {
            //登录则显示个人衷心界面

        }else{
            //未登录则显示登录界面
        }

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

//        mLlContent.setOnClickListener(this);
        mFavourite.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mRecent.setOnClickListener(this);

        llLogin.setVisibility(View.VISIBLE);
        mLlContent.setVisibility(View.GONE);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wx:
                authorize("WeChat");
                break;
            case R.id.iv_sina:
                authorize("SinaWeibo");
                break;
            case R.id.iv_qq:
                authorize("QQ");
                break;

            case R.id.ll_favourite:
                break;
            case R.id.ll_share:
                break;
            case R.id.ll_recent:
                break;
        }
    }

    public void authorize(String platformName) {

        mPlatform = ShareSDK.getPlatform(platformName);
        if (mPlatform == null) {
            return;
        }
        mPlatform.setPlatformActionListener(this);
        mPlatform.SSOSetting(false);//此处设置为false，则在优先采用客户端授权的方法，设置true会采用网页方式
        mPlatform.showUser(null);//获得用户数据
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.e(TAG, "onComplete:  " + platform.getName());
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.e(TAG, "onError: " );
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
