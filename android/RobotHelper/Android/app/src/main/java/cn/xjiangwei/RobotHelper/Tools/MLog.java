package cn.xjiangwei.RobotHelper.Tools;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MLog {

    private static String storageDir = Environment.getExternalStorageDirectory().toString();

    public static void setDebug(boolean debug) {
        MLog.debug = debug;
    }

    private static boolean debug = false;

    private static String Tag = "RobotHelper";

    public static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static void error(String msg) {
        Log.e(Tag, MLog.GetDate() + ":" + msg);
        writeLog2file(MLog.GetDate() + ":" + msg + "\n");
    }

    public static void e(int[] msg) {
        Log.e(Tag, MLog.GetDate() + ":" + Arrays.toString(msg));
        writeLog2file("[" + MLog.GetDate() + "]:" + Arrays.toString(msg) + "\n");
    }


    public static void e(String tag, String msg) {
        Log.e(tag, MLog.GetDate() + ":" + msg);
        writeLog2file(MLog.GetDate() + ":" + tag + ":" + msg + "\n");
    }

    public static void i(String msg) {
        Log.i(Tag, MLog.GetDate() + ":" + msg);
        writeLog2file(MLog.GetDate() + ":" + msg + "\n");
    }

    public static void i(String tag, String msg) {
        Log.i(tag, MLog.GetDate() + ":" + msg);
        writeLog2file("[" + MLog.GetDate() + "]:" + tag + ":" + msg + "\n");
    }

    /**
     * 写日志到本地
     *
     * @param msg
     */
    public static void LocalLog(String msg) {
        writeLog2file(MLog.GetDate() + ":" + msg + "\n");
    }

    public static String getTodayLogName(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String path = storageDir + File.separator + "daka" + File.separator + sdf.format(new Date()) + ".txt";
        return path;
    }


    public static void writeLog2file(String content) {

        try {
            File file = new File(getTodayLogName());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(getTodayLogName(), true);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
