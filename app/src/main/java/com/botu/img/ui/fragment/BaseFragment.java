package com.botu.img.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment基类
 *
 * @author: swolf
 * @date : 2016-11-03 09:23
 */
public abstract class BaseFragment extends Fragment {
    public Activity mActivity; //MainActivity

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity(); //获取当前Fragment所依赖的Activity
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), null);
        return view;
    }

    //Activity的onCreate创建完后执行该方法
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        // 每个Fragment都需要实现`setMenuVisibility`来控制视图，
        // 当Fragment不可见的时候，需要隐藏相应的视图，不然会使界面重叠在一起。
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    //获取布局控件
    protected abstract int getLayoutId();

    //初始化布局
    protected abstract void initView();

    //初始化数据
    public abstract void initData();


}
