package com.winlab.selfdrivingloggingtool.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Luyang on 2/15/16.
 */
public class ApplicationHelper extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationHelper.context = getApplicationContext();
    }


    public static Context getAppContext() {
        return ApplicationHelper.context;
    }


}