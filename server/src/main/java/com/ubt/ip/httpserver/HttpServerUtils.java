package com.ubt.ip.httpserver;

import android.content.Context;
import android.content.Intent;

/**
 * Created by afunx on 15/09/2017.
 */

public class HttpServerUtils {

    public static void startService(Context context) {
        Intent intent = new Intent(context, HttpServerService.class);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, HttpServerService.class);
        context.stopService(intent);
    }
}