package com.botu.img.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author: swolf
 * @date : 2016-11-02 11:52
 */
public class SystemUtils {
    /**
     * 获取系统版本号
     * @return
     */
    public static String getAppVersion(Context context) {
        PackageManager pm = context.getPackageManager(); //获取包管理类
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return  packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
