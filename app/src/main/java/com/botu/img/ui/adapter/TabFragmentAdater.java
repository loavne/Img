package com.botu.img.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author: swolf
 * @date : 2016-12-22 11:10
 */
public class TabFragmentAdater extends FragmentPagerAdapter {

    private String[] mTitles;
    private Context mContext;
    private List<Fragment> mFragments;

    public TabFragmentAdater(Context context, FragmentManager fm, String[] titles, List<Fragment> fragments) {
        super(fm);
        mContext = context;
        mTitles = titles;
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
