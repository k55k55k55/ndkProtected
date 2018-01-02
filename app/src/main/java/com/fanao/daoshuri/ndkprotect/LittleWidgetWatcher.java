package com.fanao.daoshuri.ndkprotect;

/**
 * 监视 记助  app的 小插件的服务是否崩溃 崩溃就守护
 * Created by yw
 */

public class LittleWidgetWatcher {
    static {
        System.loadLibrary("daoshuriwidget");
    }


    public native void openLittleWidgetWatcher(int uId);
}
