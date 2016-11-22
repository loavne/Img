package com.botu.img.base;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.botu.img.animation.BaseAnimation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * @author: swolf
 * @date : 2016-11-15 16:47
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // 1.集成动画
    public static final int ALPHA_IN = 0x00000001;
    public static final int SCALE_IN = 0x00000002;

    //打开动画
    private boolean mOpenAnimatioEnable = false;
    //持续时间
    private int mDuration = 200;

    @AnimationType
    private BaseAnimation mBaseAnimation;

    @IntDef({ALPHA_IN, SCALE_IN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType{}

    protected Context mContext;
    //布局文件资源Id
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    //数据源
    private List<T> mData;

    /**
     * 设置动画持续时间
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    public CommonAdapter(int layoutResId, List<T> data) {
        mLayoutResId = layoutResId;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
