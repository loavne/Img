package com.botu.img.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * 单种ItemViewType 的 RecyclerView通用Adapter
 *
 * @author: swolf
 * @date : 2016-11-23 10:39
 */
public abstract class HAdapter<T> extends RecyclerView.Adapter<HViewHolder> {

    public static final String TAG = "hlh";

    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    public HAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mLayoutId = layoutId;
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    /** 说白了就是获取布局及其控件 */
    @Override
    public HViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HViewHolder holder = HViewHolder.get(mContext, parent, mLayoutId);
        return holder;
    }

    /** 说白了就是给控件绑定数据，或者添加监听事件 */
    @Override
    public void onBindViewHolder(HViewHolder holder, int position) {
        convert(holder, mDatas.get(position));
    }

    //用户自己继承操作
    protected abstract void convert(HViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
