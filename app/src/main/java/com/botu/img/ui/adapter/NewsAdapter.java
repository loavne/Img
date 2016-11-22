package com.botu.img.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.botu.img.R;
import com.botu.img.bean.Newsbean;
import com.botu.img.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author: swolf
 * @date : 2016-11-17 16:17
 */
public class NewsAdapter extends BaseQuickAdapter<Newsbean> {
    private Context mContext;

    public NewsAdapter(Context context, int layoutResId, List<Newsbean> data) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, Newsbean newsbean) {
        Glide.with(mContext).load(newsbean.getPicurl()).asBitmap().placeholder(R.drawable.photo_default_icon).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                float width = bitmap.getWidth(); //图片宽度
                float height = bitmap.getHeight();//图片高度
                float ratio = width / height; //图片比例
                int screenWidth = UIUtils.getScreenWidth(mContext);//获取屏幕的宽度
                int itemWidth = (int) ((screenWidth - UIUtils.dip2px(8)) / 2); //一列item的宽度
                int itemHeight = (int) (itemWidth / ratio);//显示在ImageView中图片的高度
                ImageView imageView = baseViewHolder.getView(R.id.iv_img);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(itemWidth, itemHeight)); //设置item的宽高
                imageView.setImageBitmap(bitmap);
            }
        });
    }

}
