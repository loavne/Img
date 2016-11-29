package com.botu.img.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.botu.img.R;
import com.botu.img.bean.UpdateInfo;
import com.botu.img.callback.InputStreamCallback;
import com.botu.img.utils.SpUtils;
import com.botu.img.utils.XmlParserUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Splash页面
 * 1、展示logo,提高公司形象
 * 2、初始化数据 (拷贝数据到SD)
 * 3、提高用户体验
 * 4、连接服务器是否有新的版本等。
 *
 * @author: swolf
 * @date : 2016-11-01 17:40
 */
public class SplashActivity extends BaseActivity {

    private RelativeLayout rlbg;
    private UpdateInfo mInfo;
    private String updateUrl;
    private String apkUrl;


    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        //设置状态栏颜色
        addStatusBarView(R.color.colorSplash);

        rlbg = (RelativeLayout) findViewById(R.id.rl_bg);

        //启动网络状态服务
        Intent intent = new Intent();
        intent.setAction("com.botu.img.service.NetworkState");
        intent.setPackage(getPackageName());
        startService(intent);

        //判断是否需要更新(获取服务器版本号)
//        isUpdate();

        ScaleAnimation alphaAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        alphaAnimation.setDuration(1000);
        rlbg.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                boolean isFirstEnter = SpUtils.getBoolean(SplashActivity.this, "isFirstEnter", true);
                Intent intent;
                if (isFirstEnter) {
                    intent = new Intent(getApplicationContext(), GuideActivity.class);  //进入引导页
                } else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);   //进入主页
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 是否要更新，解析数据， 获取到版本号，再跟当前应用版本号对比，相同则不做处理，不同则下载APK
     */
    private void isUpdate() {
        String url = "";
        OkGo.get(url)
                .tag(this)
                .execute(new InputStreamCallback() {
                    @Override
                    public void onSuccess(InputStream inputStream, Call call, Response response) {
                        //解析xml文件获取信息
                        mInfo = XmlParserUtils.xmlParser(inputStream, mInfo);
                        if (!mInfo.getVersion().equals(getAppVersion())) {
                            //下载APK
                            downloadApk();
                        }
                    }
                });
    }


    //检查版本
    private String getAppVersion() {
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName;
    }

    public void downloadApk() {
        OkGo.get(apkUrl)
                .execute(new FileCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        //开始下载
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        //下载完成
                        installApk(file);
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        //下载进度
                    }
                });
    }

    /**
     * 安装APK
     * @param file apk文件
     */
    public void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消网络
        OkGo.getInstance().cancelTag(this);

    }
}
