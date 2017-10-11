package com.qqdebug.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubt.ip.ctrl_motor.adapter.LedAdapter;
import com.ubt.ip.ctrl_motor.util.LedUtil;
import com.ubt.ip.httpserver.HttpServerUtils;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive() receive boot broadcast");
        startHttpServer(context);
    }

    private void startHttpServer(Context context) {
        /**
         * 设置Led的Adapter
         */
        LedUtil.setLedAdapter(new LedAdapter() {
            @Override
            public void setLed(int i, int i1, int i2) {
                Log.e(TAG, "led id: " + i + ", led color: " + i1 + ", led frequency: " + i2);
            }
        });

        /**
         * 启动HttpServer
         */
        HttpServerUtils.startService(context.getApplicationContext());
    }
}
