package com.botu.img.base;

/**
 * @author: swolf
 * @date : 2016-11-23 11:34
 */
public interface MultiItemType<T> {
    //通过ItemType获取当前Item的布局文件id
    int getLayoutId(int itemType);
    //通过实体Bean(T)返回当前Item的类型
    int getItemViewType(int postion, T t);
}
