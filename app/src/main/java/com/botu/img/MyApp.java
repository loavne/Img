package com.botu.img;

import android.app.Application;
import android.os.Environment;

import com.botu.img.base.IConstants;
import com.botu.img.utils.DateUtils;
import com.botu.img.utils.L;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author: swolf
 * @date : 2016-11-09 17:36
 */
public class MyApp extends Application{
    public static boolean isNetworkConn;

    public static IWXAPI wxApi;
    public static Tencent sTencent;
    public static AuthInfo mAuthInfo;

    //奔溃日志路径
    private static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pic/log/";
    //奔溃日志名称
    private static final String LOG_NAME = DateUtils.getCurrentDateString() + ".txt";
    //奔溃线程
    Thread.UncaughtExceptionHandler mHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            writeErrorLog(throwable);
            System.exit(0);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        L.isDebug = true;

        //注册微信
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), IConstants.WX_APP_ID);
        wxApi.registerApp(IConstants.WX_APP_ID);

        //注册QQ
        sTencent = Tencent.createInstance(IConstants.QQ_APP_ID, getApplicationContext());


        //注册sinaWeibo
        mAuthInfo = new AuthInfo(getApplicationContext(), IConstants.SINA_APP_KEY, IConstants.REDIRECT_URL, IConstants.SCOPE);

        //注册腾讯X5内核
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//            @Override
//            public void onCoreInitFinished() {
//            }
//
//            @Override
//            public void onViewInitFinished(boolean b) {
//                Log.e("pp", " onViewInitFinished is " + b);
//            }
//        };
//        QbSdk.initX5Environment(getApplicationContext(), cb);

        //系统奔溃日志注册
        Thread.setDefaultUncaughtExceptionHandler(mHandler);
    }

    public void writeErrorLog(Throwable ex) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //写入文件
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
