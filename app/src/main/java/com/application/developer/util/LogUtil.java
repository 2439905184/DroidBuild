package com.application.developer.util;

import android.util.Log;

public class LogUtil {
   static final String TAG = "NBLog";
    public static void e(String tag,String msg) {
        Log.e(tag,msg);
    }

    public static void e(String msg) {
        Log.e(TAG,msg);
    }

    public static void d(String tag,String msg) {
        Log.d(tag,msg);
    }

    public static void d(String msg) {
        Log.d(TAG,msg);
    }
}
