package com.botu.img.ui.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.botu.img.R;
import com.botu.img.ui.view.StatusBarUtil;
import com.botu.img.utils.SpUtils;

import java.util.ArrayList;

/**
 * @author: swolf
 * @date : 2016-11-01 17:42
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private Button btEnter;
    private LinearLayout llDot;

    private GuidePagerAdapter mPagerAdapter;
    //背景图片
    private ArrayList<ImageView> mImageViewList;
    //小点数组
    private ImageView[] mDots;
    //背景图片资源Id
    private int[] imageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    //当前显示的位置
    private int currentIndex;

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTranslucent(this, 0);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        btEnter = (Button) findViewById(R.id.bt_enter);
        llDot = (LinearLayout) findViewById(R.id.ll_dot);

        mImageViewList = new ArrayList<>();

        mDots = new ImageView[imageIds.length];

        //添加三个ImageView
        for (int i = 0; i < imageIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setImageResource(imageIds[i]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageViewList.add(view);
        }

        mViewPager.setAdapter(new GuidePagerAdapter());

        //设置小点
        for (int i = 0; i < imageIds.length; i++) {
            mDots[i] = (ImageView) llDot.getChildAt(i);
            mDots[i].setEnabled(false); //设置为不选中状态
            mDots[i].setTag(i); //标记
        }

        //默认第一个为选中状态
        currentIndex = 0;
        mDots[currentIndex].setEnabled(true);

        //设置viewpager监听事件
        mViewPager.addOnPageChangeListener(this);

        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpUtils.setBoolean(GuideActivity.this, "isFirstEnter", false); //设置不是第一次启动
                //进入主界面
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mDots[position].setEnabled(true);//当前小点被选中
        mDots[currentIndex].setEnabled(false); //上一页不被选中
        currentIndex = position;
        if (position == imageIds.length - 1) { //最后一页显示Button按钮
            btEnter.setVisibility(View.VISIBLE);
        }else {
            btEnter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class GuidePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //初始化页面
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
