package com.botu.img.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.botu.img.R;
import com.botu.img.ui.fragment.BaseFragment;
import com.botu.img.ui.fragment.FragmentFactory;

/**
 * @author: swolf
 * @date : 2016-11-02 14:21
 */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

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
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentCommit(0);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int position) {
        int index = 0;

        switch (position) {
            case R.id.rb_tab_home:
                index = 0;
                break;
            case R.id.rb_tab_dynamic:
                index=1;
                break;
            case R.id.rb_tab_search:
                index =2;
                break;
            case R.id.rb_tab_person:
                index =3;
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
                case 3:
                    fragment = FragmentFactory.createFragment(3);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
}
