package com.botu.img.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import com.botu.img.bean.ImgType;
import com.botu.img.ui.adapter.ImagePreviewAdapter;
import com.botu.img.ui.view.HackyViewPager;
import com.botu.img.utils.L;
import com.botu.img.utils.SpUtils;
import com.botu.img.utils.WxUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
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
import com.tencent.connect.share.QzonePublish;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 预览界面
 *
 * @author: swolf
 * @date : 2016-11-21 11:31
 */
public class ImagePreviewActivity extends BaseActivity implements View.OnClickListener, IWeiboHandler.Response {

    public static final String IMAGE_INFO = "IMAGE_INFO";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";

    private TextView mTitle;
    //    private ImageView mBack;
    private HackyViewPager mViewPager;
    private RelativeLayout rootView;

    private List<ImgType> mImageInfo;

    private ImagePreviewAdapter mAdapter;
    private int currentItem;

    private TextView mDownload;
    private LinearLayout mFavorite;
    private TextView mShare;
    private ImageView mFavoriteImg;

    private PopupWindow mPopupWindow;
    private IWeiboShareAPI mWeiboShareAPI;
    private QIUiListener mQIUiListener;
    private SsoHandler mSsoHandler;
    private int mIsFav;
    private File mFile;
    private String mImage_url;
    public static final int IMAGE_SIZE = 32768;//微信分享图片大小限制

    @Override
    public int getLayoutId() {
        return R.layout.activity_preview;
    }

    @Override
    protected void initView() {
        mViewPager = (HackyViewPager) findViewById(R.id.viewPager);
        mTitle = (TextView) findViewById(R.id.tv_per_title);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        mDownload = (TextView) findViewById(R.id.tv_download);
        mFavorite = (LinearLayout) findViewById(R.id.tv_collect);
        mFavoriteImg = (ImageView) findViewById(R.id.iv_favorite);
        mShare = (TextView) findViewById(R.id.tv_share);

        mDownload.setOnClickListener(this);
        mFavorite.setOnClickListener(this);
        mShare.setOnClickListener(this);

        //数据源
        Intent intent = getIntent();
        mImageInfo = (List<ImgType>) intent.getSerializableExtra(IMAGE_INFO);
        currentItem = intent.getIntExtra(CURRENT_ITEM, 0);

        setToolBar(mImageInfo.get(currentItem).getTitle(), R.color.toolbar_bg, true);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundResource(R.color.all_tran);
        final boolean hasFav = intent.getBooleanExtra("isFav", true);
        if (hasFav) {
            mFavoriteImg.setImageResource(R.drawable.icon_favorite_focused);
        } else {
            mIsFav = mImageInfo.get(currentItem).getIsFav();
            if (mIsFav == 1) {
                mFavoriteImg.setImageResource(R.drawable.icon_favorite_focused);
            } else {
                mFavoriteImg.setImageResource(R.drawable.icon_favorite_normal);
            }
        }

        mAdapter = new ImagePreviewAdapter(mImageInfo, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                mTitle.setText(mImageInfo.get(currentItem).getTitle());
                mIsFav = mImageInfo.get(currentItem).getIsFav();
                if (hasFav) {
                    mFavoriteImg.setImageResource(R.drawable.icon_favorite_focused);
                } else {
                    if (mIsFav == 1) {
                        mFavoriteImg.setImageResource(R.drawable.icon_favorite_focused);
                    } else {
                        mFavoriteImg.setImageResource(R.drawable.icon_favorite_normal);
                    }
                }
                //发送数据，添加足迹
                foot();

            }
        });
        mTitle.setText(mImageInfo.get(currentItem).getTitle());
        foot();


    }

    private void foot() {
        OkGo.post(IConstants.FOOT)
                .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                .params(IConstants.picture_id, mImageInfo.get(currentItem).getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        L.i("足迹记录添加成功");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            sendShare();
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
        mImage_url = IConstants.BASE_URL + mImageInfo.get(currentItem).getFilepath() + mImageInfo.get(currentItem).getFilename();
        switch (view.getId()) {
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
        textObject.text = mImageInfo.get(currentItem).getTitle();
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
        getBitmapAndSave(type);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mQIUiListener = new QIUiListener();
            Bundle bundle = new Bundle();
            if (msg.what == 0) {
                //QQ  分享类型 （图片类型）
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mFile.getAbsolutePath());
                MyApp.mTencent.shareToQQ(ImagePreviewActivity.this, bundle, mQIUiListener);
            } else if (msg.what == 1) {
                //发表至QQ空间
                bundle.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD); //发表类型 （纯图片）
                ArrayList<String> imgList = new ArrayList<String>();
                imgList.add(mFile.getAbsolutePath());
                bundle.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, imgList);
                MyApp.mTencent.publishToQzone(ImagePreviewActivity.this, bundle, mQIUiListener);
            }
        }
    };

    /**
     * 微信朋友圈分享 （图片分享）
     */
    private void shareWechat(final int type) {
        OkGo.get(mImage_url).execute(new BitmapCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, Call call, Response response) {
                L.i("微信分享对头");
                //初始化WxImageObject和WxMediaMessage对象
                WXImageObject imgObj = new WXImageObject(bitmap);
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;
                msg.thumbData = WxUtils.bitmap2Bytes(bitmap, IMAGE_SIZE);
                //设置请求
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = type;
                MyApp.wxApi.sendReq(req);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                L.i("微信分享出错 " + e.toString());
            }
        });
    }

    /**
     * 分享成功后发送给后台
     */
    public void sendShare() {
        OkGo.post(IConstants.SHARE)
                .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                .params(IConstants.picture_id, mImageInfo.get(currentItem).getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        L.i("分享记录添加成功");
                    }
                });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 下载和保存图片
     */
    private void getBitmapAndSave(final int type) {
        String title = mImageInfo.get(currentItem).getTitle();
        //存在ＳＤ卡
        mFile = new File("/sdcard/" + title + ".png");
        OkGo.get(mImage_url).tag(this).execute(new BitmapCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, Call call, Response response) {
                //保存图片
                try {
                    mFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(mFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    mHandler.obtainMessage();
                    Message msg = new Message();
                    msg.what = type;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        //请求收藏,判断是否登录
        L.i("图片Id  " + mImageInfo.get(currentItem).getId());
        L.i("用户Id  " + SpUtils.getInt(this, IConstants.userId, 0));

        if (mIsFav == 1) {
            //删除收藏
            mFavoriteImg.setImageResource(R.drawable.icon_favorite_normal);
            OkGo.post(IConstants.DEL_COLLECT)
                    .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                    .params(IConstants.picture_id, mImageInfo.get(currentItem).getId())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            L.i(s.toString());
                        }
                    });
            sendBroadcast2Ipa(0);
            mImageInfo.get(currentItem).setIsFav(0);
            mIsFav = 0;
        } else {
            //添加收藏
            mFavoriteImg.setImageResource(R.drawable.icon_favorite_focused);
            OkGo.post(IConstants.COLLECT)
                    .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                    .params(IConstants.picture_id, mImageInfo.get(currentItem).getId())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            L.i(s.toString());
                        }
                    });
            //发送广播至ImageActivity中，更新Adapter的数据源
            sendBroadcast2Ipa(1);
            //当前页面的也要修改
            mImageInfo.get(currentItem).setIsFav(1);
            mIsFav = 1;
        }
    }

    //发送广播至ImageActivity中，更新Adapter的数据源
    private void sendBroadcast2Ipa(int type) {
        Intent intent = new Intent("com.botu.img.update");
        intent.putExtra("picId", mImageInfo.get(currentItem).getId());
        intent.putExtra("current", currentItem);
        intent.putExtra("isfav", type);
        sendBroadcast(intent);
    }


    /**
     * 下载
     */
    private void imgDownload() {
        String img_url = IConstants.BASE_URL + mImageInfo.get(currentItem).getFilepath() +
                mImageInfo.get(currentItem).getFilename();
        OkGo.get(img_url)
                .tag(this)
                .execute(new FileCallback(mImageInfo.get(currentItem).getTitle() + ".png") {
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
                sendShare();
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
            sendShare();
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
