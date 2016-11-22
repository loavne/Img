package com.botu.img.ui.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.botu.img.R;
import com.botu.img.bean.Newsbean;
import com.botu.img.ui.adapter.ImagePreviewAdapter;
import com.botu.img.ui.view.HackyViewPager;
import com.botu.img.utils.UIUtils;

import java.util.List;

/**
 * 预览界面
 * @author: swolf
 * @date : 2016-11-21 11:31
 */
public class ImagePreviewActivity extends BaseActivity{

    public static final String IMAGE_INFO = "IMAGE_INFO";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";
    public static final int ANIMATE_DURATION = 200;

    private TextView mTitle;
    private HackyViewPager mViewPager;
    private RelativeLayout rootView;

    private List<Newsbean> mImageInfo;

    private ImagePreviewAdapter mAdapter;
    private int currentItem;
    private int screenWidth;    //屏幕宽度
    private int screenHeight;   //屏幕高度

    @Override
    public int getLayoutId() {
        return R.layout.activity_preview;
    }

    @Override
    protected void initView() {
        mViewPager = (HackyViewPager) findViewById(R.id.viewPager);
        mTitle = (TextView) findViewById(R.id.tv_pager_title);
        rootView = (RelativeLayout) findViewById(R.id.rootView);

        screenWidth = UIUtils.getScreenWidth(this);
        screenHeight = UIUtils.getScreenHeight(this);

        //数据源
        Intent intent = getIntent();
        mImageInfo = (List<Newsbean>) intent.getSerializableExtra(IMAGE_INFO);
        currentItem = intent.getIntExtra(CURRENT_ITEM, 0);

        mAdapter = new ImagePreviewAdapter(mImageInfo, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                mTitle.setText(mImageInfo.get(position).getTilte());
            }
        });
        mTitle.setText(mImageInfo.get(currentItem).getTilte());
    }
}
