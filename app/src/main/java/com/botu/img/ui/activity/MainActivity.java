package com.botu.img.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.botu.img.R;
import com.botu.img.ui.fragment.BaseFragment;
import com.botu.img.ui.fragment.FragmentFactory;
import com.botu.img.ui.view.StatusBarUtil;


/**
 * @author: swolf
 * @date : 2016-11-02 14:21
 */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, Toolbar.OnMenuItemClickListener {

    private RadioGroup radioGroup;
    private FrameLayout flContent;
    private Toolbar mToolbar;
    private TextView mTitle;

    public Menu mMenu;

    private boolean isSetting = false;
    private long exitTime = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        setToolBar(getString(R.string.tab_home), false);
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab_bottom);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.tv_per_title);

        radioGroup.setOnCheckedChangeListener(this);
        mToolbar.setOnMenuItemClickListener(this);

        fragmentCommit(0);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int position) {
        int index = 0;
        switch (position) {
            case R.id.rb_tab_home:
                index = 0;
                mTitle.setText(getResources().getString(R.string.tab_home));
                mMenu.setGroupVisible(0, false);
                break;
            case R.id.rb_tab_search:
                index = 1;
                mTitle.setText(getResources().getString(R.string.tab_search));
                mMenu.setGroupVisible(0, false);
                break;
            case R.id.rb_tab_person:
                index = 2;
                mTitle.setText(getResources().getString(R.string.tab_user));
                mMenu.setGroupVisible(0, true);
                break;
        }
        fragmentCommit(index);
    }

    private void fragmentCommit(int index) {
        // 从FragmentManager中查找Fragment，找不到就使用getItem获取
        BaseFragment fragment = (BaseFragment) fragments.instantiateItem(flContent, index);
        //设置显示第一个fragment
        fragments.setPrimaryItem(flContent, 0, fragment);
        //提交事务
        fragments.finishUpdate(flContent);
    }

    FragmentStatePagerAdapter fragments = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            BaseFragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FragmentFactory.createFragment(0);
                    break;
                case 1:
                    fragment = FragmentFactory.createFragment(1);
                    break;
                case 2:
                    fragment = FragmentFactory.createFragment(2);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    //为toolbar设置设置按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        //在我的页面显示
        mMenu.setGroupVisible(0, false);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //
        switch (item.getItemId()) {
            case R.id.action_settings:
                //进入设置界面
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentFactory.createFragment(2).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
