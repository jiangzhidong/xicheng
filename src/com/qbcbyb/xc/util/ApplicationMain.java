package com.qbcbyb.xc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.qbcbyb.libandroid.ActivityBase;
import com.qbcbyb.libandroid.StringUtils;
import com.qbcbyb.libandroid.dbhelp.DbHelper;
import com.qbcbyb.libandroid.msg.Msg;

public class ApplicationMain extends Application implements UncaughtExceptionHandler {
    private static ApplicationMain instance;

    public BMapManager mBMapManager = null;

    private String appPath;
    private Config config;
    private DbHelper dbHelper;

    public Config getConfig() {
        return config;
    }

    public DbHelper getDbHelper() {
        return dbHelper;
    }

    public String getAppPath() {
        if (StringUtils.isEmpty(appPath)) {
            File appPathFold = new File(Environment.getExternalStorageDirectory(), "XC");
            if (!appPathFold.exists()) {
                appPathFold.mkdirs();
            }
            appPath = appPathFold.getAbsolutePath();
        }
        return appPath;
    }

    public File getAppPathFile(String fileName) {
        return new File(getAppPath(), fileName);
    }

    public String getImagePath(String relativePath) {

        return getAppPathFile("img").getAbsolutePath() + relativePath;
    }

    public static ApplicationMain getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(this);

        config = new Config();

        String dbName = "xc_all.db";
        // File dbFile = getAppPathFile(dbName);
        // dbHelper = new DbHelper(dbFile.getAbsolutePath());
        if (copyAssetsToFilesystem(dbName, getAppPathFile(dbName).getAbsolutePath())) {
            dbHelper = new DbHelper(getAppPathFile(dbName).getAbsolutePath());
        } else {

        }

        if (mBMapManager == null) {
            mBMapManager = new BMapManager(this);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
//            Toast.makeText(this, "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 打包数据库要用到
     * 
     * @param assetsSrc
     * @param des
     * @return 拷贝路径
     */
    private boolean copyAssetsToFilesystem(String assetsSrc, String des) {

        InputStream istream = null;
        OutputStream ostream = null;
        try {
            AssetManager am = this.getAssets();
            istream = am.open(assetsSrc);
            ostream = new FileOutputStream(des);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = istream.read(buffer)) > 0) {
                ostream.write(buffer, 0, length);
            }
            istream.close();
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (istream != null)
                    istream.close();
                if (ostream != null)
                    ostream.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Msg.showInfo(this, "程序意外终止退出");
        Log.e("ApplicationExit", "程序意外终止退出", ex);
        for (Activity activity : ActivityBase.activedActivity) {
            activity.finish();
        }
        // 结束进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取指定资源ID的VIEW
     * 
     * @param resource
     * @return
     */
    public View inflateView(int resource) {
        return inflateView(resource, null);
    }

    public View inflateView(int resource, ViewGroup root) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return vi.inflate(resource, root);
    }

    public void hideSoftInputFromWindow(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int iError) {
            // 非零值表示key验证未通过
            if (iError != 0) {
                // 授权Key错误：
//                Toast.makeText(ApplicationMain.getInstance().getApplicationContext(),
//                    "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(ApplicationMain.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_LONG)
//                    .show();
            }
        }
    }
}
