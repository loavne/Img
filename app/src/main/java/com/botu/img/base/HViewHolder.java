package com.botu.img.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * RecyclerView通用ViewHolder
 * @author: swolf
 * @date : 2016-11-23 10:17
 */
public class HViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;// 成员变量View
    private View mCovertView;
    private Context mContext;

    public HViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mCovertView = itemView;
        mViews = new SparseArray<View>();
    }

    /** 通过布局Id获取HViewHolder */
    public static HViewHolder get(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        HViewHolder holder = new HViewHolder(context, itemView, parent);
        return  holder;
    }

    /** 通过viewId获取控件 */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mCovertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public HViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public HViewHolder setText(int viewId, @StringRes int strId) {
        TextView tv = getView(viewId);
        tv.setText(strId);
        return this;
    }

    public HViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public HViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public HViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

}
