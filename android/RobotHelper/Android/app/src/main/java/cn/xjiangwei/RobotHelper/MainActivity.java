package cn.xjiangwei.RobotHelper;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import cn.xjiangwei.RobotHelper.DTO.SettingDTO;
import cn.xjiangwei.RobotHelper.Tools.*;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1050;
    private String TAG = "MainActivity";
    //    private MainApplication mainApplication;
//    private int mResultCode;
//    private Intent mResultData;
//    private MediaProjection mMediaProjection;
    private Thread thread;

    private EditText tv_url;
    private ImageView iv_1;
    private ImageView iv_2;
    private ImageView iv_3;
    private EditText edt_picName1;
    private EditText edt_picName2;
    private EditText edt_picName3;

    private EditText edt_ocr1_leftX;
    private EditText edt_ocr2_leftX;
    private EditText edt_ocr3_leftX;
    private EditText edt_ocr1_leftY;
    private EditText edt_ocr2_leftY;
    private EditText edt_ocr3_leftY;
    private EditText edt_ocr1_rightX;
    private EditText edt_ocr2_rightX;
    private EditText edt_ocr3_rightX;
    private EditText edt_ocr1_rightY;
    private EditText edt_ocr2_rightY;
    private EditText edt_ocr3_rightY;
    private EditText edt_ocr1_whiteList;
    private EditText edt_ocr2_whiteList;
    private EditText edt_ocr3_whiteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        mainApplication = (MainApplication) getApplication();

        if (MainApplication.sceenWidth == 0 || MainApplication.sceenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            MainApplication.sceenHeight = Math.min(dm.heightPixels, dm.widthPixels);
            MainApplication.sceenWidth = Math.max(dm.heightPixels, dm.widthPixels);
            MainApplication.dpi = dm.densityDpi;
        }

        initView();

        ScreenCaptureUtilByMediaPro.mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        // init
        startActivityForResult(ScreenCaptureUtilByMediaPro.mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                //  获得sdcard写入权限后 初始化tessactocr
                if (!TessactOcr.checkInit()) {
                    TessactOcr.Init();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);


        // 初始化opencv
        if (!OpenCVLoader.initDebug()) {
            MLog.i("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            MLog.i("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SuUtil.requestIgnoreBatteryOptimizations(this);
        }
        if (SuUtil.upgradeRootPermission(getPackageCodePath())) {
            Toast.show("获取权限成功");
        } else {
            Toast.show("root失败");
        }
    }

    private void  initView(){
        tv_url = findViewById(R.id.tv_url);
        iv_1 = findViewById(R.id.iv_1);
        iv_2 = findViewById(R.id.iv_2);
        iv_3 = findViewById(R.id.iv_3);
        edt_picName1 = findViewById(R.id.edt_picName1);
        edt_picName2 = findViewById(R.id.edt_picName2);
        edt_picName3 = findViewById(R.id.edt_picName3);

        edt_ocr1_leftX = findViewById(R.id.edt_ocr1_leftX);
        edt_ocr2_leftX = findViewById(R.id.edt_ocr2_leftX);
        edt_ocr3_leftX = findViewById(R.id.edt_ocr3_leftX);
        edt_ocr1_leftY = findViewById(R.id.edt_ocr1_leftY);
        edt_ocr2_leftY = findViewById(R.id.edt_ocr2_leftY);
        edt_ocr3_leftY = findViewById(R.id.edt_ocr3_leftY);
        edt_ocr1_rightX = findViewById(R.id.edt_ocr1_rightX);
        edt_ocr2_rightX = findViewById(R.id.edt_ocr2_rightX);
        edt_ocr3_rightX = findViewById(R.id.edt_ocr3_rightX);
        edt_ocr1_rightY = findViewById(R.id.edt_ocr1_rightY);
        edt_ocr2_rightY = findViewById(R.id.edt_ocr2_rightY);
        edt_ocr3_rightY = findViewById(R.id.edt_ocr3_rightY);
        edt_ocr1_whiteList = findViewById(R.id.edt_ocr1_whiteList);
        edt_ocr2_whiteList = findViewById(R.id.edt_ocr2_whiteList);
        edt_ocr3_whiteList = findViewById(R.id.edt_ocr3_whiteList);


        SettingDTO settingDTO = MainApplication.getSettingDTO();
        tv_url.setText(settingDTO.getSuccessUrl());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/daka/pic/";
        String picName1 = settingDTO.getPicName().getPic1();
        String picName2 = settingDTO.getPicName().getPic2();
        String picName3 = settingDTO.getPicName().getPic3();
        Bitmap pic1 = BitmapFactory.decodeFile(path + picName1);
        Bitmap pic2 = BitmapFactory.decodeFile(path + picName2);
        Bitmap pic3 = BitmapFactory.decodeFile(path + picName3);
        iv_1.setImageBitmap(pic1);
        iv_2.setImageBitmap(pic2);
        iv_3.setImageBitmap(pic3);

        edt_picName1.setText(picName1);
        edt_picName2.setText(picName2);
        edt_picName3.setText(picName3);


        Map<Integer, SettingDTO.OcrEntity> map = settingDTO.getOcrEntityMap();
        edt_ocr1_leftX.setText(map.get(1).getLeftX()+"");
        edt_ocr2_leftX.setText(map.get(2).getLeftX()+"");
        edt_ocr3_leftX.setText(map.get(3).getLeftX()+"");
        edt_ocr1_leftY.setText(map.get(1).getLeftY()+"");
        edt_ocr2_leftY.setText(map.get(2).getLeftY()+"");
        edt_ocr3_leftY.setText(map.get(3).getLeftY()+"");
        edt_ocr1_rightX.setText(map.get(1).getRightX()+"");
        edt_ocr2_rightX.setText(map.get(2).getRightX()+"");
        edt_ocr3_rightX.setText(map.get(3).getRightX()+"");
        edt_ocr1_rightY.setText(map.get(1).getRightY()+"");
        edt_ocr2_rightY.setText(map.get(2).getRightY()+"");
        edt_ocr3_rightY.setText(map.get(3).getRightY()+"");
        edt_ocr1_whiteList.setText(map.get(1).getWhiteList());
        edt_ocr2_whiteList.setText(map.get(2).getWhiteList());
        edt_ocr3_whiteList.setText(map.get(3).getWhiteList());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && REQUEST_CODE == requestCode) {
            ScreenCaptureUtilByMediaPro.data = data;
            ScreenCaptureUtilByMediaPro.resultCode = resultCode;
        }

        // 启动屏幕监控
        ScreenCaptureUtilByMediaPro.init();

    }

    public void killAppProcess(View view) {
        System.exit(0);
    }

    public void save(View view) {
        SettingDTO.PicName picName = new SettingDTO.PicName();
        picName.setPic1(edt_picName1.getText().toString());
        picName.setPic2(edt_picName2.getText().toString());
        picName.setPic3(edt_picName3.getText().toString());

        Map<Integer, SettingDTO.OcrEntity> ocrEntityMap = new HashMap<>();
        SettingDTO.OcrEntity ocr1 = new SettingDTO.OcrEntity(
                Integer.parseInt(edt_ocr1_leftX.getText().toString()),
                Integer.parseInt(edt_ocr1_leftY.getText().toString()),
                Integer.parseInt( edt_ocr1_rightX.getText().toString()),
                Integer.parseInt(edt_ocr1_rightY.getText().toString()),
                edt_ocr1_whiteList.getText().toString()
        );
        SettingDTO.OcrEntity ocr2 = new SettingDTO.OcrEntity(
                Integer.parseInt(edt_ocr2_leftX.getText().toString()),
                Integer.parseInt(edt_ocr2_leftY.getText().toString()),
                Integer.parseInt( edt_ocr2_rightX.getText().toString()),
                Integer.parseInt(edt_ocr2_rightY.getText().toString()),
                edt_ocr2_whiteList.getText().toString()
        );
        SettingDTO.OcrEntity ocr3 = new SettingDTO.OcrEntity(
                Integer.parseInt(edt_ocr3_leftX.getText().toString()),
                Integer.parseInt(edt_ocr3_leftY.getText().toString()),
                Integer.parseInt( edt_ocr3_rightX.getText().toString()),
                Integer.parseInt(edt_ocr3_rightY.getText().toString()),
                edt_ocr3_whiteList.getText().toString()
        );
        ocrEntityMap.put(1,ocr1);
        ocrEntityMap.put(2,ocr2);
        ocrEntityMap.put(3,ocr3);

        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setSuccessUrl(tv_url.getText().toString());
        settingDTO.setPicName(picName);
        settingDTO.setOcrEntityMap(ocrEntityMap);
        MainApplication.setSettingDTO(settingDTO);

        Toast.show("保存成功！！！");
    }

    public void start(View view) {
                MainApplication.startOneTime();

    }

    public void stop(View view) {
//        MainApplication.shutTimer();
        Toast.show("关闭全部！");
        MLog.i(TAG, "stop: 关闭全部！！！");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateStatus();
    }


    public void openLog(View view) {
        String filePath = MLog.getTodayLogName();
        File file = new File(filePath);
        Uri fileURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(fileURI, "text/plain");
            startActivity(intent);
            Intent.createChooser(intent, "请选择对应的软件打开该附件！");
        } catch (ActivityNotFoundException e) {

        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    MLog.i("OpenCV", "OpenCV loaded successfully");
                    break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    private void updateStatus() {
//        TextView xpStatus = (TextView) findViewById(R.id.xposed_status);
//        TextView asStatus = (TextView) findViewById(R.id.accessibility_status);
//        TextView hsStatus = (TextView) findViewById(R.id.httpserver_status);
//
//        xpStatus.setText(mainApplication.checkXposedHook() ? "Xposed状态：已加载" : "Xposed状态：未加载");
//        asStatus.setText(mainApplication.checkAccessibilityService() ? "Accessibility状态：已加载" : "Accessibility状态：未加载");
//        hsStatus.setText(mainApplication.checkHttpServer() ? "HttpServer状态：已开启" : "HttpServer状态：未开启");
    }


}
