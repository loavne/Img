package com.botu.img.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.Newsbean;
import com.botu.img.ui.adapter.ImagePreviewAdapter;
import com.botu.img.ui.view.HackyViewPager;
import com.botu.img.utils.UIUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 预览界面
 * @author: swolf
 * @date : 2016-11-21 11:31
 */
public class ImagePreviewActivity extends BaseActivity implements View.OnClickListener, IWeiboHandler.Response {

    public static final String IMAGE_INFO = "IMAGE_INFO";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";
    public static final int ANIMATE_DURATION = 200;

    private TextView mTitle;
    private HackyViewPager mViewPager;
    private RelativeLayout rootView;

    private List<Newsbean> mImageInfo;

    private ImagePreviewAdapter mAdapter;
    private int currentItem;
    private int screenWidth;    //屏幕宽度
    private int screenHeight;   //屏幕高度

    private TextView mDownload;
    private LinearLayout mFavorite;
    private TextView mShare;
    private ImageView mBack;
    private ImageView mFavoriteImg;

    private boolean enable = false;
    private PopupWindow mPopupWindow;
    private IWeiboShareAPI mWeiboShareAPI;
    private QIUiListener mQIUiListener;
    private SsoHandler mSsoHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_preview;
    }

    @Override
    protected void initView() {
//        addStatusBarView(R.);
        mViewPager = (HackyViewPager) findViewById(R.id.viewPager);
        mTitle = (TextView) findViewById(R.id.tv_pager_title);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        mDownload = (TextView) findViewById(R.id.tv_download);
        mFavorite = (LinearLayout) findViewById(R.id.tv_collect);
        mFavoriteImg = (ImageView) findViewById(R.id.iv_favorite);
        mShare = (TextView) findViewById(R.id.tv_share);
        mBack = (ImageView) findViewById(R.id.iv_back);

        mDownload.setOnClickListener(this);
        mFavorite.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mBack.setOnClickListener(this);

        screenWidth = UIUtils.getScreenWidth(this);
        screenHeight = UIUtils.getScreenHeight(this);

        //数据源
        Intent intent = getIntent();
        mImageInfo = intent.getParcelableArrayListExtra(IMAGE_INFO);
        currentItem = intent.getIntExtra(CURRENT_ITEM, 0);

        mAdapter = new ImagePreviewAdapter(mImageInfo, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                mTitle.setText(mImageInfo.get(position).getTilte());
            }
        });
        mTitle.setText(mImageInfo.get(currentItem).getTilte());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        Tencent.onActivityResultData(requestCode, resultCode, data, mQIUiListener); //书写这一句QQ才会调用成功
        mPopupWindow.dismiss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_download:
                imgDownload();
                break;
            case R.id.tv_collect:
                imgFavorite();
                break;
            case R.id.tv_share:
                imgShare();
                break;
            case R.id.tv_wechat:
                shareWechat(SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.tv_wechatmoments:
                shareWechat(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.tv_qq:
                shareQQ(0);
                break;
            case R.id.tv_qzone:
                shareQQ(1);
                break;
            case R.id.tv_sina:
                shareSina();
                break;
            case R.id.bt_share_cancel:
                mPopupWindow.dismiss();
                break;
        }
    }

    /**
     * 新浪分享
     */
    private void shareSina() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, IConstants.SINA_APP_KEY);
        mWeiboShareAPI.registerApp(); //将应用注册到微博客户端
        //授权
        mSsoHandler = new SsoHandler(this, MyApp.mAuthInfo);
        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                //授权成功后才能分享信息
                sendMessage();
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void sendMessage() {
        //分享内容
        TextObject textObject = new TextObject();
        textObject.text = mImageInfo.get(currentItem).getTilte();
        //初始化微博分享消息
        WeiboMultiMessage message = new WeiboMultiMessage();
        message.textObject = textObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = message;
        mWeiboShareAPI.sendRequest(this, request);
    }

    /**
     * QQ、QZone分享
     */
    private void shareQQ(int type) {
        mQIUiListener = new QIUiListener();
        Bundle bundle = new Bundle();
        if (type == 0) {
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT); //分享类型 （图文类型）
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, mImageInfo.get(currentItem).getTilte()); //标题 (必填)
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mImageInfo.get(currentItem).getPicurl()); //跳转的URL(必填)
            MyApp.mTencent.shareToQQ(this, bundle, mQIUiListener);
        } else if (type == 1) {
            bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT); //分享类型 （图文类型）
            bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, mImageInfo.get(currentItem).getTilte());     //必填
            bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mImageInfo.get(currentItem).getPicurl()); //必填
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>()); //不加这个绝对吊用不了（必填）
            MyApp.mTencent.shareToQzone(this, bundle, mQIUiListener);
        }
    }

    /**
     * 微信、朋友圈分享
     */
    private void shareWechat(int type) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = mImageInfo.get(currentItem).getTilte();

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = mImageInfo.get(currentItem).getTilte();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = type;
        MyApp.wxApi.sendReq(req);
        //该条信息添加至分享表
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 分享
     */
    private void imgShare() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_share, null);
        TextView wechat = (TextView) view.findViewById(R.id.tv_wechat);
        TextView wechatmoments = (TextView) view.findViewById(R.id.tv_wechatmoments);
        TextView qq = (TextView) view.findViewById(R.id.tv_qq);
        TextView qzone = (TextView) view.findViewById(R.id.tv_qzone);
        TextView sina = (TextView) view.findViewById(R.id.tv_sina);
        Button cancel = (Button) view.findViewById(R.id.bt_share_cancel);
        wechat.setOnClickListener(this);
        wechatmoments.setOnClickListener(this);
        qq.setOnClickListener(this);
        qzone.setOnClickListener(this);
        sina.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        mPopupWindow.showAtLocation(mFavorite, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 收藏
     */
    private void imgFavorite() {
        if (enable) {
            mFavoriteImg.setImageResource(R.drawable.favorite_normal);
            enable = false;
            //从最爱表中清除
        } else {
            mFavoriteImg.setImageResource(R.drawable.favorite_focused);
            enable = true;
            //将该条数据添加到最爱表中
        }
    }

    /**
     * 下载
     */
    private void imgDownload() {
        OkGo.get(mImageInfo.get(currentItem).getPicurl())
                .tag(this)
                .execute(new FileCallback(mImageInfo.get(currentItem).getTilte() + ".png") {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        Toast.makeText(ImagePreviewActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(ImagePreviewActivity.this, "下载出错", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 新浪微博回调
     */
    @Override
    public void onResponse(BaseResponse baseResponse) {//接收微客户端博请求的数据。
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_CANCEL:

                break;
            case WBConstants.ErrorCode.ERR_OK:
                mPopupWindow.dismiss();
                Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                //该条信息添加至分享表
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                mPopupWindow.dismiss();
                Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * QQ回调接口
     */
    public class QIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            Toast.makeText(ImagePreviewActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            mPopupWindow.dismiss();
            //该条信息添加至分享表

        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(ImagePreviewActivity.this, "分享失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            mPopupWindow.dismiss();
        }
    }
}
