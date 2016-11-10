package com.botu.img.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.utils.SpUtils;
import com.tencent.mm.sdk.modelmsg.SendAuth;

/**
 * 我的
 * @author: swolf
 * @date : 2016-11-03 09:58
 */
public class PersonFragment extends BaseFragment implements View.OnClickListener{

    public static final String TAG = PersonFragment.class.getSimpleName();

    private ImageView mSina;
    private ImageView mQq;
    private ImageView mWx;
    private RelativeLayout llLogin;
    private LinearLayout mLlContent;
    private LinearLayout mFavourite;
    private LinearLayout mShare;
    private LinearLayout mRecent;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void initView() {

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
                loginWx();
                break;
            case R.id.iv_sina:
                break;
            case R.id.iv_qq:
                break;

            case R.id.ll_favourite:
                break;
            case R.id.ll_share:
                break;
            case R.id.ll_recent:
                break;
        }
    }

    private void loginWx() {
        if (!MyApp.wxApi.isWXAppInstalled()) {
            Toast.makeText(mActivity, "未安装", Toast.LENGTH_LONG);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com.botu.img";
        MyApp.wxApi.sendReq(req);
    }

}
