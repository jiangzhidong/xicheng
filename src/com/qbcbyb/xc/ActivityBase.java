package com.qbcbyb.xc;

import java.io.File;

import com.qbcbyb.libandroid.dbhelp.DbHelper;
import com.qbcbyb.xc.util.ApplicationMain;
import com.qbcbyb.xc.util.Config;

public abstract class ActivityBase extends com.qbcbyb.libandroid.ActivityBase {
    public static final String KEY_SPOT="KEY_SPOT";

    public Config getConfig() {
        ApplicationMain app = (ApplicationMain) getApplication();
        return app.getConfig();
    }

    public DbHelper getDbHelper() {
        ApplicationMain app = (ApplicationMain) getApplication();
        return app.getDbHelper();
    }

    public File getAppPathFile(String fileName) {
        ApplicationMain app = (ApplicationMain) getApplication();
        return app.getAppPathFile(fileName);
    }

    public String getImagePath(String relativePath) {
        ApplicationMain app = (ApplicationMain) getApplication();
        return app.getImagePath(relativePath);
    }
}
