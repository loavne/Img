package com.botu.img.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.botu.img.MyApp;

import static com.botu.img.base.HAdapter.TAG;

/**
 * 网络状态监听服务
 * @author: swolf
 * @date : 2016-11-24 11:32
 */
public class NetworkStateService extends Service {

    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mInfo;
    public static final String NETWORK_STATE = "com.text.android.network.state"; //Action

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册网络状态广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                mInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mInfo != null && mInfo.isAvailable()) {
                    //有网
                    MyApp.isNetworkConn = true;
                } else {
                    //无网
                    MyApp.isNetworkConn = false;
                }
            }
            Log.i(TAG, "接收网络监听到了广播:  MyApp.isNetworkConn = " + MyApp.isNetworkConn);
        }
    };

}
