package com.botu.img.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.botu.img.R;
import com.botu.img.base.HAdapter;
import com.botu.img.base.HViewHolder;
import com.botu.img.bean.Newsbean;
import com.botu.img.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

/**
 * @author: swolf
 * @date : 2016-11-17 16:17
 */
public class NewsAdapter extends HAdapter<Newsbean> {
    private Context mContext;
    private List<Newsbean> mDatas;

    public NewsAdapter(Context context, int layoutId, List<Newsbean> datas) {
        super(context, layoutId, datas);
        mContext = context;
        mDatas = datas;
    }

    public void addData(List<Newsbean> data) {
        mDatas.addAll(data);
        this.notifyDataSetChanged();
    }

    public List<Newsbean> getDatas() {
        return mDatas;
    }

    @Override
    protected void convert(final HViewHolder holder, final Newsbean newsbean) {
        holder.getView(R.id.iv_img).setTag(newsbean.getPicurl());
        Glide.with(mContext).load(newsbean.getPicurl()).asBitmap().placeholder(R.drawable.photo_default_icon).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                float width = bitmap.getWidth(); //图片宽度
                float height = bitmap.getHeight();//图片高度
                float ratio = width / height; //图片比例
                int screenWidth = UIUtils.getScreenWidth(mContext);//获取屏幕的宽度
                int itemWidth = (int) ((screenWidth - UIUtils.dip2px(8)) / 2); //一列item的宽度
                int itemHeight = (int) (itemWidth / ratio);//显示在ImageView中图片的高度
                ImageView imageView = holder.getView(R.id.iv_img);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(itemWidth, itemHeight)); //设置item的宽高
                //防止图片错位
                if (newsbean.getPicurl().equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }
}
