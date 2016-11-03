package com.botu.img.ui.fragment;

import java.util.HashMap;

/**
 * 生产Fragment的工厂
 * @author: swolf
 * @date : 2016-11-03 10:17
 */
public class FragmentFactory {

    //检查是否会内存泄漏
    private static HashMap<Integer, BaseFragment> sFragmentHashMap = new HashMap<>();

    public static BaseFragment createFragment(int position) {
        //从集合中获取，集合中没有时在创建
        BaseFragment fragment = sFragmentHashMap.get(position);
        if (fragment == null) {
            //创建并显示
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new DynamicFragment();
                    break;
                case 2:
                    fragment = new SearchFragment();
                    break;
                case 3:
                    fragment = new PersonFragment();
                    break;
                default:
                    break;
            }
            sFragmentHashMap.put(position, fragment);//存入集合中
        }
        return fragment;
    }
}
