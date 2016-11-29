package com.botu.img.db;

import android.content.Context;

import com.botu.img.dao.DaoMaster;
import com.botu.img.dao.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 核心辅助类，用于获取DaoMaster和DaoSession
 * @author: swolf
 * @date : 2016-11-25 09:46
 */
public class DbCore {
    private static final String DEFAULT_DB_NAME = "botu_img.db";
    private static DaoMaster sDaoMaster;
    private static DaoSession sDaoSession;

    private static Context sContext;
    private static String DB_NAME;

    public static void init(Context context) {
        init(context, DEFAULT_DB_NAME);
    }

    public static void init(Context context, String dbName) {
        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }
        sContext = context.getApplicationContext();
        DB_NAME = dbName;
    }

    public static DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {
            DaoMaster.OpenHelper openHelper = new MyOpenHelper(sContext, DB_NAME);
            sDaoMaster = new DaoMaster(openHelper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    public static DaoSession getDaoSession() {
        if (sDaoSession == null) {
            if(sDaoMaster == null)
                sDaoMaster = getDaoMaster();
            sDaoSession = sDaoMaster.newSession();
        }
        return  sDaoSession;
    }

    /** 是否开启调试 */
    public static void enableQueryBuilderLog() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES  = true;
    }
}
