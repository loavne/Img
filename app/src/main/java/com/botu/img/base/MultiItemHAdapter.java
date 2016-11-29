package com.botu.img.base;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * 多种ItemViewType的Adapter
 * @author: swolf
 * @date : 2016-11-23 11:31
 */
public abstract class MultiItemHAdapter<T> extends HAdapter<T>{

    protected MultiItemType<T> mMultiItemType;

    public MultiItemHAdapter(Context context, int layoutId, List<T> datas) {
        super(context, layoutId, datas);
    }

    /** 返回指定位置item视图的类型 */
    @Override
    public int getItemViewType(int position) {
        return mMultiItemType.getItemViewType(position, mDatas.get(position));
    }

    @Override
    public HViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layutId = mMultiItemType.getLayoutId(viewType); //获取view类型id
        HViewHolder holder = HViewHolder.get(mContext, parent, layutId); //获取view类型holder
        return holder;
    }
}
