package com.fanao.daoshuri.ndkprotect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 守护 记助  app的 小插件的服务
 *  create by yw  qq:1073629321
 */
public class LittleWidgetService extends Service {
    public LittleWidgetService() {
        Log.i("widget", "开启service" + Process.myUid());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LittleWidgetWatcher watcher = new LittleWidgetWatcher();
        Log.i("widget", "Process.myUid()=" + Process.myUid());
        watcher.openLittleWidgetWatcher(Process.myUid());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
