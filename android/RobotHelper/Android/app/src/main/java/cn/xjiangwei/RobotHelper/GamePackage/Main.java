package cn.xjiangwei.RobotHelper.GamePackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.xjiangwei.RobotHelper.MainActivity;
import cn.xjiangwei.RobotHelper.MainApplication;
import cn.xjiangwei.RobotHelper.Tools.Image;
import cn.xjiangwei.RobotHelper.Tools.MLog;
import cn.xjiangwei.RobotHelper.Tools.Point;
import cn.xjiangwei.RobotHelper.Tools.Robot;
import cn.xjiangwei.RobotHelper.Tools.ScreenCaptureUtil;
import cn.xjiangwei.RobotHelper.Tools.SuUtil;
import cn.xjiangwei.RobotHelper.Tools.TessactOcr;
import cn.xjiangwei.RobotHelper.Tools.Toast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static android.os.SystemClock.sleep;

public class Main {
    private String TAG = "Main";

    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();


    /**
     * 在这个函数里面写你的业务逻辑
     */
    public void start() {
        MLog.i("TAG", "start: 我进来了进来了==============================================================");
        sleep(1000); //点击开始后等待5秒后再执行，因为状态栏收起有动画时间，建议保留这行代码
//        MLog.setDebug(true);
        //Robot.setExecType(Robot.ExecTypeXposed);         //使用xposed权限执行模拟操作，建议优先使用此方式
//        Robot.setExecType(Robot.ExecTypeAccessibillty);  //使用安卓无障碍接口执行模拟操作
        Robot.setExecType(Robot.ExecTypeROOT);           //使用root权限执行模拟操作（实验阶段，仅在oneplus 7pro测试过，欢迎提bug）
//
//        /****************************  模板匹配demo  *******************************/
//        InputStream is = null;
//        try {
//            is = MainApplication.getInstance().getAssets().open("daka.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(is);
//        //在当前屏幕中查找模板图片
//        Point point = Image.matchTemplate(ScreenCaptureUtil.getScreenCap(), bitmap, 0.1);
//        MLog.i("找到模板", point.toString());
//        // 点击找到的这个图
//        Robot.tap(point);


        /**************************** 文字识别demo  **********************************/
//        try {
//            //识别素材文件中的ocrTest.png图片中的文字
//            is = MainApplication.getInstance().getAssets().open("ocrTest.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        bitmap = BitmapFactory.decodeStream(is);
//
//        String res = TessactOcr.img2string(ScreenCaptureUtil.getScreenCap(0, 1000, 1000, 1700), "chi_sim", "", "");
//        MLog.i("文字识别结果：" + res);


        /*****************************  特征点找图  ************************************/
        //当前屏幕中查找chrome图标（特征点是3120X1440分辨率手机制作）
//        point = Image.findPointByMulColor(ScreenCaptureUtil.getScreenCap(), "434FD7,65|0|414DDB,90|55|46CDFF,5|86|5FA119");
//        //点击chrome图标
//        Robot.tap(point);


        /*****************************  双指缩放操作  ************************************/


//        Robot.pinchOpen(100);  // 目前仅在xposed模式中实现了该方法，distance值为0到100
//        Robot.pinchClose(100);  // 目前仅在xposed模式中实现了该方法，
        MLog.i(TAG, "start: 开始运行");
        while (true) {
            if (doDaKa()) {
                break;
            } else {
                MLog.i(TAG, "doDaKa: 等待时间过长，退出开始下一个");
            }
        }
        MLog.i(TAG, "start: 运行结束");
        /***** 提示  *****/
        Toast.show("运行结束！");
        //声音提示
        Toast.notice();

    }

    private boolean doDaKa() {
        int times = 0;
        //先把微信杀了重开
        MLog.i(TAG, "doDaKa: 杀掉微信进程");
        SuUtil.kill(MainApplication.wxPackageName);
        //1.打开桌面，然后找快捷方式，如果找到了。打开之后往下走。找不到一直找

        do {
            MLog.i(TAG, "doDaKa: 打开桌面。Times：" + times);
            openDeskTop();
            sleep(1000);
            if (times++ > 10) {
                return false;
            }
        } while (!doFirstStep());
        MLog.i(TAG, "doDaKa: 开始等待15秒");
        sleep(15 * 1000);
        MLog.i(TAG, "doDaKa: 等待结束");
        OcrEntity ocr = new OcrEntity(0, 200, 400, 400, "考勤上报");
        times = 0;
        while (!doOcrStep(ocr).contains("考勤上报")) {
            MLog.i(TAG, "doDaKa: 考勤上报文字识别中。。。Times：" + times);
            sleep(1000);
            if (times++ > 10) {
                return false;
            }
        }

        times = 0;
        while (!doSencondStep()) {
            sleep(1000);
            MLog.i(TAG, "doDaKa: 考勤上报识别中。。。Times：" + times);
            if (times++ > 10) {
                return false;
            }
        }
        times = 0;

        OcrEntity ocrEntity = new OcrEntity(0, 1000, 1000, 1700, "获 取 有 效 考 勤 点 成 功");
        while (!doOcrStep(ocrEntity).contains("获取有效考勤点成功")) {
            MLog.i(TAG, "doDaKa: 获取有效考勤点成功文字识别中。。。TImes:" + times);
            sleep(1000);
            if (times++ > 10) {
                return false;
            }
        }
        times = 0;
        while (!doThirdStep()) {
            MLog.i(TAG, "doDaKa: 打卡按钮识别中。。。Times：" + times);
            sleep(1000);
            if (times++ > 10) {
                return false;
            }
        }
        times = 0;
        MLog.i(TAG, "doDaKa: 等待10秒");
        sleep(10 * 1000);
        MLog.i(TAG, "doDaKa: 等待结束");
        OcrEntity ocrSuccess = new OcrEntity(200, 600, 1000, 1100, "");
        while (true) {
            MLog.i(TAG, "doDaKa: 打卡结果文字识别中。。。Times:" + times);
            String ret = doOcrStep(ocrSuccess);
            MLog.i(TAG, "doDaKa: 打卡结果文字识别结果：" + ret);
            if (ret.contains("打卡成功")) {
                MLog.i(TAG, "doDaKa: ===========================================================");
                MLog.i(TAG, "doDaKa: =====================打卡成功=============");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                MLog.i(TAG, "doDaKa: ===========当前时间：" + sdf.format(new Date()) + "====================");
                MLog.i(TAG, "doDaKa: =====================打卡成功=============");
                MLog.i(TAG, "doDaKa: ===========================================================");
                MLog.i(TAG, "doDaKa: 打卡成功，杀死微信");
                SuUtil.kill(MainApplication.wxPackageName);
                Intent intent = new Intent(MainApplication.getInstance(), MainActivity.class);
                MainApplication.getInstance().startActivity(intent);
                return true;
            } else if (ret.contains("连接超时") || ret.contains("请重试")) {
                MLog.i(TAG, "doDaKa: 连接超时，退出去，重新来一遍。");
                return false;
            } else {
                sleep(1000);
            }
            if (times++ > 30) {
                return false;
            }
        }
    }

    private Point getPointByPic(String fileName) {
        InputStream is = null;
        try {
            is = MainApplication.getInstance().getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        //在当前屏幕中查找模板图片
        Point point = Image.matchTemplate(ScreenCaptureUtil.getScreenCap(), bitmap, 0.1);
        MLog.i(TAG, "getPointByPic: 屏幕中找到图片模板，位置坐标位：" + point.toString());
        return point;
    }

    private void openDeskTop() {
        //1.回到桌面
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        MainApplication.getInstance().startActivity(i);
    }

    private boolean doFirstStep() {
        //2. 找到打卡的快捷方式的点位
        Point point = getPointByPic("daka.png");
        MLog.i(TAG, "doFirstStep: 获取到快捷方式的坐标：" + point.toString());
        if (point.getX() == -1 || point.getY() == -1) {
            return false;
        } else {
            //3. 点击这个点，打开快捷方式
            Robot.tap(point);
            MLog.i(TAG, "doFirstStep: 点击快捷方式，打开。");
            return true;
        }
    }

    private boolean doSencondStep() {
        //4. 找到考勤上报的点位
        Point point2 = getPointByPic("daka2.png");
        MLog.i(TAG, "doSencondStep: 获取到考勤上报的坐标：" + point2.toString());
        if (point2.getX() == -1 || point2.getY() == -1) {
            return false;
        } else {
            //5. 点击这个点位进去
            Robot.tap(point2);
            MLog.i(TAG, "doSencondStep: 点击考勤上报，进入下一个页面");
            return true;
        }
    }

    private boolean doThirdStep() {
        //6.找到打卡按钮的点位
        Point point3 = getPointByPic("daka3.png");
        MLog.i(TAG, "doThirdStep: 获取到打卡位置坐标：" + point3.toString());
        if (point3.getX() == -1 || point3.getY() == -1) {
            return false;
        } else {
            //7. 点击打卡
            Robot.tap(point3);
            MLog.i(TAG, "doThirdStep: 点击打卡！！！");
            Toast.show("我打卡了！！");
            return true;
        }
    }

    private String doOcrStep(OcrEntity ocrEntity) {
        MLog.i(TAG, "doOcrStep: 开始识别文字。实体类：" + ocrEntity.toString());
        String res = TessactOcr.img2string(ScreenCaptureUtil.getScreenCap(ocrEntity.getLeftX(), ocrEntity.getLeftY(), ocrEntity.getRightX(), ocrEntity.getRightY()), "chi_sim", ocrEntity.getWhiteList(), "").replaceAll(" ", "");
        MLog.i(TAG, "doOcrStep: 文字识别结果：" + res);
        return res;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    class OcrEntity {
        int leftX;
        int leftY;
        int rightX;
        int rightY;
        String whiteList;
    }

}
