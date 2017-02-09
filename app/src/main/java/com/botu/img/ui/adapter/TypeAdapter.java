package com.botu.img.ui.adapter;

import android.content.Context;

import com.botu.img.base.HAdapter;
import com.botu.img.base.HViewHolder;

import java.util.List;

/**
 * @author: swolf
 * @date : 2016-12-26 10:27
 */
public class TypeAdapter<T> extends HAdapter<T>{
    private List<T> mDatas;
    public TypeAdapter(Context context, int layoutId, List<T> datas) {
        super(context, layoutId, datas);
        mDatas = datas;
    }

    @Override
    protected void convert(HViewHolder holder, T t) {

    }

    public T getData(int position) {
        return mDatas.get(position);
    }

    public void addData(List<T> list) {
        if (!mDatas.containsAll(list)) {
            mDatas.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
    }

    public List<T> getDatas() {
        return mDatas;
    }
}
