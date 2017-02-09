package com.botu.img.ui.adapter;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.botu.img.R;
import com.botu.img.base.HViewHolder;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ImgType;
import com.botu.img.utils.UIUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * @author: swolf
 * @date : 2017-01-06 09:36
 */
public class ImageAdapter extends TypeAdapter<ImgType> {

    private Context mContext;

    public ImageAdapter(Context context, int layoutId, List<ImgType> datas) {
        super(context, layoutId, datas);
        mContext = context;
    }

    @Override
    protected void convert(HViewHolder holder, ImgType type) {
        //图片缩略图路径
        String thumb = IConstants.BASE_URL + type.getFilepath() + "thumb/" + type.getFilename();
//        L.i(thumb);
        ImageView iv_img = holder.getView(R.id.iv_img);
        //图片的宽高比,一定是浮点或者double型，否则宽高比一直是0或1
        float ratio = (float)type.getWidth()/type.getHeight();
        int iWidth = (int) (((UIUtils.getScreenWidth(mContext) - UIUtils.dip2px(8)) /(float) 2) - iv_img.getPaddingLeft()
                        - iv_img.getPaddingRight()); //一列item的宽度
        int iHeight = (int) (iWidth / ratio);
        iv_img.setLayoutParams(new FrameLayout.LayoutParams(iWidth, iHeight));
        Glide.with(mContext)
                .load(thumb)
                .asBitmap()
//                .thumbnail(0.1f)
                .placeholder(R.drawable.icon_loading)
                .override(iWidth, iHeight)
                .centerCrop()
                .into(iv_img);
//        DrawableRequestBuilder<String> thumbnailRequest = Glide.with(mContext).load(thumb);
//        Glide.with(mContext)
//                .load(thumb)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .thumbnail(thumbnailRequest)
//                .into(iv_img);

    }

}