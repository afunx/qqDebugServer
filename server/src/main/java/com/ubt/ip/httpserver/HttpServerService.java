package com.ubt.ip.httpserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by afunx on 15/09/2017.
 */

public class HttpServerService extends Service {

    private static final String TAG = "HttpServerService";
    private HttpServer mHttpServer = null;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate() starting");
        mHttpServer = new HttpServer(8266);
        try {
            mHttpServer.start();
            Log.e(TAG, "onCreate() started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        if (mHttpServer != null) {
            mHttpServer.stop();
            mHttpServer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}