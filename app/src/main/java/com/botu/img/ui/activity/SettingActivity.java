package com.botu.img.ui.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.ui.view.StatusBarUtil;
import com.botu.img.utils.DataCleanManager;
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
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        setToolBar(getString(R.string.setting), true);
        clearCache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        cacheSize = (TextView) findViewById(R.id.tv_cache_size);
        exit = (TextView) findViewById(R.id.tv_exit);

        if (SpUtils.getString(this, IConstants.username, "").equals("")) {
            exit.setVisibility(View.GONE);
        } else {
            exit.setVisibility(View.VISIBLE);
        }

        //设置缓存大小
        try {
            String size = DataCleanManager.getCacheSize(this);
            cacheSize.setText(size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearCache.setOnClickListener(this);
        exit.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                //退出登录
                SpUtils.removeKey(SettingActivity.this, IConstants.header);
                SpUtils.removeKey(SettingActivity.this, IConstants.username);
                SpUtils.setBoolean(SettingActivity.this, IConstants.isLogin, false); //设置为未登录
                SettingActivity.this.finish();
                break;
            case R.id.rl_clear_cache:
                //清除缓存
                DataCleanManager.cleanAllCache(getApplicationContext());
//                DataCleanManager.cleanDatabases(getApplicationContext()); 如果这么干了，再回到首页点击图片会爆出错误，因为数据库不在了，你却添加东西到数据库中
                Toast.makeText(this, "清理成功", Toast.LENGTH_SHORT).show();
                cacheSize.setText("0.0B");
                break;
        }
    }
}
