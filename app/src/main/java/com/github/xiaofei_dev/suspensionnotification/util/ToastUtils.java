package com.github.xiaofei_dev.suspensionnotification.util;

import android.widget.Toast;
/**
 *author Administrator
 *time 2017/5/13 15:01
 *desc：便捷显示 Toast 的工具类
 */
public final class ToastUtils {


    public static void showShort(int stringId) {
        Toast.makeText(App.getContext(), stringId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int stringId) {
        Toast.makeText(App.getContext(), stringId, Toast.LENGTH_LONG).show();
    }

//    public static void showShort(String toast) {
//        Toast.makeText(MyApplication.getContext(), toast, Toast.LENGTH_SHORT).show();
//    }
//
//    public static void showLong(String toast) {
//        Toast.makeText(MyApplication.getContext(), toast, Toast.LENGTH_LONG).show();
//    }
}
