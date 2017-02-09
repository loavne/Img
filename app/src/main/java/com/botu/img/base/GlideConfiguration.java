package com.botu.img.base;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * @author: swolf
 * @date : 2017-01-09 11:36
 */
public class GlideConfiguration implements GlideModule {
    private static final int MEMORY_MAX = (int) (Runtime.getRuntime().maxMemory() / 8);

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //设置图片加载的样式
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setMemoryCache(new LruResourceCache(MEMORY_MAX));
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, getDiskFileString(context, "GlideCache"), 20 * 1024 * 1024));
    }

    private String getDiskFileString(Context mContext,String str) {
        File dirFile=new File(mContext.getCacheDir().getAbsolutePath().toString()+str);
        File tempFile=new File(dirFile,"img_bitmaps");
        if (! tempFile.getParentFile().exists()){
            tempFile.getParentFile().mkdirs();
        }
        return tempFile.getAbsolutePath().toString();
    }



    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
