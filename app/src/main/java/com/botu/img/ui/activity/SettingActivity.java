package com.botu.img.ui.activity;

import android.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.botu.img.R;
import com.botu.img.ui.fragment.SettingFragment;

/**
 * @author: swolf
 * @date : 2016-11-04 11:08
 */
public class SettingActivity extends BaseActivity{

    private SettingFragment mSettingFragment;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //添加一个PreferenceFragment
        mSettingFragment = new SettingFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fl_setting_content, mSettingFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return  true;
    }
}
