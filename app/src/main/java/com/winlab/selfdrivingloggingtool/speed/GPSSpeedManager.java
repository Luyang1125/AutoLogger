package com.winlab.selfdrivingloggingtool.speed;

import android.content.Context;

import com.winlab.selfdrivingloggingtool.context.GoogleActivityService;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;


/**
 * Created by Luyang on 9/7/2016.
 */
public class GPSSpeedManager {

    private GoogleLocationService mGoogleLocationService;
    private GoogleActivityService mGoogleActivityService;
    private Context mContext;

    public GPSSpeedManager(){
        mContext = ApplicationHelper.getAppContext();
        mGoogleLocationService = new GoogleLocationService();
        mGoogleActivityService = new GoogleActivityService();

    }

    public void start(){
        mGoogleLocationService.start();
        mGoogleActivityService.start();
    }

    public void stop(){
        mGoogleLocationService.cancel();
        mGoogleActivityService.cancel();
    }

}
