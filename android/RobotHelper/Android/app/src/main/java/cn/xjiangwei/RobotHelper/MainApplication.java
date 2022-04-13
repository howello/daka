package cn.xjiangwei.RobotHelper;

import android.app.Application;
import android.os.Environment;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.xjiangwei.RobotHelper.Accessibility.HttpServer;
import cn.xjiangwei.RobotHelper.DTO.SettingDTO;
import cn.xjiangwei.RobotHelper.Service.Accessibility;
import cn.xjiangwei.RobotHelper.Service.RunTime;
import cn.xjiangwei.RobotHelper.Service.TimerWorker;
import cn.xjiangwei.RobotHelper.Tools.FileUtils;
import cn.xjiangwei.RobotHelper.Tools.MLog;


public class MainApplication extends Application {
    private String TAG = "MainApplication";
    public static int sceenWidth = 0;
    public static int sceenHeight = 0;
    public static int dpi;
    private static MainApplication instance;
    public static String wxPackageName = "com.tencent.mm";


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

    public static SettingDTO getSettingDTO(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/daka/conf/conf.json");  //打开要读的文件
        if (!file.exists()) {

            SettingDTO.PicName picName = new SettingDTO.PicName();
            picName.setPic1("daka.png");
            picName.setPic2("daka2.png");
            picName.setPic3("daka3.png");

            Map<Integer, SettingDTO.OcrEntity> ocrEntityMap = new HashMap<>();
            SettingDTO.OcrEntity ocr1 = new SettingDTO.OcrEntity(0, 200, 400, 400, "考勤上报");
            SettingDTO.OcrEntity ocr2 = new SettingDTO.OcrEntity(0, 1000, 1000, 1700, "获 取 有 效 考 勤 点 成 功");
            SettingDTO.OcrEntity ocr3 = new SettingDTO.OcrEntity(200, 600, 1000, 1100, "");
            ocrEntityMap.put(1,ocr1);
            ocrEntityMap.put(2,ocr2);
            ocrEntityMap.put(3,ocr3);

            SettingDTO settingDTO = new SettingDTO();
            settingDTO.setSuccessUrl("http://192.168.137.1:12809/dakaSuccess");
            settingDTO.setPicName(picName);
            settingDTO.setOcrEntityMap(ocrEntityMap);
            MainApplication.setSettingDTO(settingDTO);
            return settingDTO;
        }
        String sdCardInfo = FileUtils.getSDCardInfo(file);
        SettingDTO settingDTO = JSONObject.parseObject(sdCardInfo, SettingDTO.class);
        return settingDTO;
    }

    public static void setSettingDTO(SettingDTO settingDTO){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/daka/conf/conf.json");  //打开要读的文件
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileUtils.writeSDCardInfo(JSONObject.toJSONString(settingDTO),file);
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
