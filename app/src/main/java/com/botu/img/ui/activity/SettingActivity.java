package com.botu.img.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.botu.img.R;
import com.botu.img.utils.SpUtils;

/**
 * @author: swolf
 * @date : 2016-11-04 11:08
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

//    private SettingFragment mSettingFragment;
    private RelativeLayout clearCache;
    private TextView cacheSize;
    private TextView exit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        //添加
        addStatusBarView(R.color.tab_txt_selected);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //添加一个PreferenceFragment
//        mSettingFragment = new SettingFragment();
//        FragmentManager fm = getFragmentManager();
//        fm.beginTransaction().replace(R.id.fl_setting_content, mSettingFragment).commit();
        clearCache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        cacheSize = (TextView) findViewById(R.id.tv_cache_size);
        exit = (TextView) findViewById(R.id.tv_exit);

        if (SpUtils.getString(this, "name", "").equals("")) {
            exit.setVisibility(View.GONE);
        } else {
            exit.setVisibility(View.VISIBLE);
        }

        clearCache.setOnClickListener(this);
        exit.setOnClickListener(this);

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.tv_exit:
//
//                break;
//        }
//        return true;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                //清除缓存
                SpUtils.removeKey(SettingActivity.this, "img");
                SpUtils.removeKey(SettingActivity.this, "name");
                SpUtils.setBoolean(SettingActivity.this, "isLogin", false); //设置为未登录
                SettingActivity.this.finish();
                break;
        }
    }
}
