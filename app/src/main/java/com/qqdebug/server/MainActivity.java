package com.qqdebug.server;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ubt.ip.ctrl_motor.adapter.LedAdapter;
import com.ubt.ip.ctrl_motor.util.LedUtil;
import com.ubt.ip.httpserver.HttpServerUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        HttpServerUtils.startService(getApplicationContext());
    }
}