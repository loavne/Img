package com.botu.img.ui;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.botu.img.R;
import com.botu.img.ui.activity.BaseActivity;
import com.botu.img.ui.activity.GuideActivity;
import com.botu.img.ui.activity.MainActivity;
import com.botu.img.utils.SpUtils;

/**
 * Splash页面
 * 1、展示logo,提高公司形象
 * 2、初始化数据 (拷贝数据到SD)
 * 3、提高用户体验
 * 4、连接服务器是否有新的版本等。
 * @author: swolf
 * @date : 2016-11-01 17:40
 */
public class SplashActivity extends BaseActivity{


    private RelativeLayout rlbg;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        //设置状态栏颜色
        addStatusBarView(R.color.colorSplash);

        rlbg = (RelativeLayout) findViewById(R.id.rl_bg);

        //判断是否有更新
        //判断是否是第一次进入APP

        ScaleAnimation alphaAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        alphaAnimation.setDuration(1000);

        rlbg.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                Log.e("hlh", "onAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Log.e("hlh", "onAnimationEnd: " );
                boolean isFirstEnter = SpUtils.getBoolean(SplashActivity.this, "isFirstEnter", true);
                Intent intent;
                if (isFirstEnter) {
                    //进入引导页
                    intent = new Intent(getApplicationContext(), GuideActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
