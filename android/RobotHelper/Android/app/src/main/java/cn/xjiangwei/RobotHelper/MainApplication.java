package cn.xjiangwei.RobotHelper;

import android.app.Application;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.xjiangwei.RobotHelper.Accessibility.HttpServer;
import cn.xjiangwei.RobotHelper.Service.Accessibility;
import cn.xjiangwei.RobotHelper.Service.RunTime;
import cn.xjiangwei.RobotHelper.Service.TimerWorker;
import cn.xjiangwei.RobotHelper.Tools.MLog;


public class MainApplication extends Application {
    private String TAG = "MainApplication";
    public static int sceenWidth = 0;
    public static int sceenHeight = 0;
    public static int dpi;
    private static MainApplication instance;
    public static String wxPackageName = "com.tencent.mm";
    public static String successUrl = "http://192.168.101.23:12809/dakaSuccess";


    public static MainApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        setTimer();
        MLog.i(TAG, "onCreate: 启动成功！");
    }

    public void setTimer() {
        PeriodicWorkRequest build = new PeriodicWorkRequest.Builder(TimerWorker.class,
                30, TimeUnit.MINUTES,
                15, TimeUnit.MINUTES
        )
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("daka", ExistingPeriodicWorkPolicy.REPLACE, build);
    }

    public static void shutTimer() {
        WorkManager.getInstance(MainApplication.getInstance()).cancelAllWork();
    }

    public static void startOneTime() {
        if (RunTime.getInstance().isRunning()) {
            RunTime.getInstance().stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RunTime.getInstance().start();
        } else {
            RunTime.getInstance().start();
        }
    }

    public static int sceenSize() {
        if (MainApplication.sceenWidth == 3120 && MainApplication.sceenHeight == 1440) {
            return 1;
        }
        return 2;
    }


    public boolean checkXposedHook() {
        return false;
    }

    public boolean checkAccessibilityService() {
        return Accessibility.DOM != null;
    }

    public boolean checkHttpServer() {
        return HttpServer.getInstance().isRuning();
    }
}
