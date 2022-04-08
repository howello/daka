package cn.xjiangwei.RobotHelper.Service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Service.RunTime;
import cn.xjiangwei.RobotHelper.Tools.MLog;

/**
 * <p>@Author lu
 * <p>@Date 2021/8/30 16:26 星期一
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class TimerWorker extends Worker {
    private String TAG = "TimerWorker";

    public TimerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int ap_pm = calendar.get(Calendar.AM_PM);
        if (ap_pm == Calendar.AM) {
            //上午
            if (hour == 8 && minute <= 30) {
                MLog.i(TAG, "doWork: 当前为上午，到达时间段，开始执行");
//                MainApplication.startOneTime();
                MLog.i(TAG, "doWork: 执行结束");
            } else {
                MLog.i(TAG, "doWork: 当前为上午，不在时间段内！当前时间为：" + sdf.format(calendar.getTime()));
            }
        } else {
            if (hour == 18 && minute >= 30) {
                MLog.i(TAG, "doWork: 当前为下午，到达时间段，开始执行");
//                MainApplication.startOneTime();
                MLog.i(TAG, "doWork: 执行结束");
            } else {
                MLog.i(TAG, "doWork: 不在时间段内！当前时间为：" + sdf.format(calendar.getTime()));
            }
        }
        return Result.success();
    }
}
