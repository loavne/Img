package com.botu.img.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.botu.img.R;
import com.botu.img.base.HViewHolder;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ListType;
import com.botu.img.utils.UIUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * @author: swolf
 * @date : 2017-01-09 15:45
 */
public class ListAdapter extends TypeAdapter<ListType>{
    private Context mContext;
    public ListAdapter(Context context, int layoutId, List<ListType> datas) {
        super(context, layoutId, datas);
        mContext = context;
    }

    @Override
    protected void convert(HViewHolder holder, ListType type) {
        //设置标题
        holder.setText(R.id.tv_sub_title, type.getName());
        //图片缩略图路径
        String thumbUrl = IConstants.BASE_URL + type.getFilepath() + "thumb/" + type.getFilename();
        ImageView iv_img = holder.getView(R.id.iv_sub_img);
        //item宽度
        int width = UIUtils.getScreenOneThird(mContext);
        iv_img.setLayoutParams(new RelativeLayout.LayoutParams(width, width));

        Glide.with(mContext)
                .load(thumbUrl)
                .asBitmap()
//                .thumbnail(0.1f)
                .placeholder(R.drawable.icon_loading)
                .override(width, width)
                .centerCrop()
                .into(iv_img);


    }
}
