package cn.xjiangwei.RobotHelper;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import cn.xjiangwei.RobotHelper.Tools.*;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1050;
    private String TAG = "MainActivity";
    //    private MainApplication mainApplication;
//    private int mResultCode;
//    private Intent mResultData;
//    private MediaProjection mMediaProjection;
    private Thread thread;

    private EditText tv_url;

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
        tv_url = findViewById(R.id.tv_url);
        tv_url.setText(MainApplication.successUrl);

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
        MainApplication.successUrl = tv_url.getText().toString();
        Log.i(TAG, "save: "+ MainApplication.successUrl);
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
