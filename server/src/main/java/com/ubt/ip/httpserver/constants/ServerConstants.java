package com.ubt.ip.httpserver.constants;

/**
 * Created by afunx on 17/09/2017.
 */

public interface ServerConstants {
    /**
     * 回读模式
     */
    interface ReadMode {
        /**
         * 所有舵机都在回读模式
         */
        int ALL = 1;
        /**
         * 部分舵机在回读模式，部分舵机不在回读模式
         */
        int PARTIAL = 0;
        /**
         * 所有舵机都不在回读模式
         */
        int NONE = -1;
    }
}
