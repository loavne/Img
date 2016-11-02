package com.botu.img.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.botu.img.R;

/**
 * Activity基类
 * @author: swolf
 * @date : 2016-11-01 17:42
 */
public abstract class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏着色，4.4以上才可以
        setStatusBarColor2Transparent();
        setContentView(getLayoutId());
        initView();
    }

    public abstract int getLayoutId();

    protected abstract void initView();

    /**
     * 设置状态栏颜色－＞透明
     */
    public void setStatusBarColor2Transparent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { //5.0及以上
            //获取窗口DecorView
            View decorView = getWindow().getDecorView();
            //设置显示方式
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            //设置UI
            decorView.setSystemUiVisibility(option);
            //设置状态栏为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ //4.4-5.0
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | layoutParams.flags);
        }
    }

    /**
     * 设置状态栏View,覆盖原有的
     * @param color 设置颜色
     */
    public void addStatusBarView(int color) {
        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(R.color.colorSplash));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        //获取ViewGroup
        ViewGroup decorView = (ViewGroup) findViewById(android.R.id.content);
        //添加到当前布局
        decorView.addView(view, params);
    }

    /**
     * 获取状态栏高度
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resoureId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resoureId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resoureId);
        }
//        Log.e("hlh", "状态栏高度 : " + statusBarHeight);
        return statusBarHeight;
    }

}
