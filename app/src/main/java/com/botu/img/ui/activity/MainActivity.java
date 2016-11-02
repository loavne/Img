package com.botu.img.ui.activity;

import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.botu.img.R;

/**
 * @author: swolf
 * @date : 2016-11-02 14:21
 */
public class MainActivity extends BaseActivity{

    private RadioGroup radioGroup;
    private FrameLayout flContent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab_bottom);
        flContent = (FrameLayout) findViewById(R.id.fl_content);


    }
}
