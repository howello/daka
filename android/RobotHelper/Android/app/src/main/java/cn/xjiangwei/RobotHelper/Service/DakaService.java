package cn.xjiangwei.RobotHelper.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import cn.xjiangwei.RobotHelper.GamePackage.Main;
import cn.xjiangwei.RobotHelper.MainApplication;

public class DakaService extends Service {
    public DakaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TAG", "onStartCommand: 服务启动启动服务启动");
        MainApplication.startOneTime();
        return super.onStartCommand(intent, flags, startId);
    }
}