package com.botu.img.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 请求网络工具类
 *
 * @author: swolf
 * @date : 2016-11-08 15:45
 */
public class HttpUtils {
    public Context mContext;

    public static String put(Context context, String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                //请求成功
                InputStream inputStream = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                byte[] bytes = new byte[1024];
                int len = 0;
                inputStream.read(bytes, 0, len);
                String result = buffer.toString();
                return  result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
