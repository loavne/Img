package com.botu.img.db;

import android.content.Context;

import com.botu.img.dao.DaoMaster;
import com.botu.img.dao.NewsbeanDao;

import org.greenrobot.greendao.database.Database;

/**
 * @author: swolf
 * @date : 2016-11-25 09:43
 */
public class MyOpenHelper extends DaoMaster.OpenHelper{
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        switch (oldVersion) {
            case 1:
                NewsbeanDao.createTable(db, true);
                //假如字段

                break;
        }
    }
}
