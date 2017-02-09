package com.botu.img.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * @author: swolf
 * @date : 2017-01-11 09:59
 */
public class WxUtils {
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于IMAGE_SIZE
     * @param bitmap
     * @param IMAGE_SIZE
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int IMAGE_SIZE) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > IMAGE_SIZE && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }
}
