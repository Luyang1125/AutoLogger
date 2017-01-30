package com.winlab.selfdrivingloggingtool.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.Global;
import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.SteeringWheelAngleEst;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by luyangliu on 1/30/17.
 */

public class DataManager {

    private static final String TAG = "DataManager";
    private Context mContext = ApplicationHelper.getAppContext();
    private SteeringWheelAngleEst.DataReceiver mDataReceiver;

    private File f_accel;
    private File f_gyro;
    private File f_magn;
    private File f_gps;

    private BufferedOutputStream bos_accel;
    private BufferedOutputStream bos_gyro;
    private BufferedOutputStream bos_magn;
    private BufferedOutputStream bos_gps;

    public DataManager(){

    }

    private void createFolderAndFiles(){

        String folder_main = "AutoLogger";

        File folder = new File(Environment.getExternalStorageDirectory(),
                folder_main);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        long time= System.currentTimeMillis();
        folder_main = "AutoLogger/"+time;
        folder = new File(Environment.getExternalStorageDirectory(),
                folder_main);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        f_accel = new File(folder_main,"Accel");
        f_gyro = new File(folder_main,"Gyro");
        f_magn = new File(folder_main,"Magn");
        f_gps = new File(folder_main,"GPS");


    }

    public void startLogging(){
        try {
            bos_accel = new BufferedOutputStream(new FileOutputStream(f_accel));
            bos_gyro = new BufferedOutputStream(new FileOutputStream(f_gyro));
            bos_magn = new BufferedOutputStream(new FileOutputStream(f_magn));
            bos_gps = new BufferedOutputStream(new FileOutputStream(f_gps));
        } catch (IOException e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_PHONE_SENSOR));


    }

    public class DataReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            //Log.i(TAG,"Received!");
            String act = intent.getAction();
            if (act == Global.BROADCAST_PHONE_SENSOR) {
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                float[] value = intent.getFloatArrayExtra(Global.EXTENDED_PHONE_SENSOR_VALUE);
                String type = intent.getStringExtra(Global.EXTENDED_PHONE_SENSOR_TYPE);
                String log = "Phone: "+ type + ", " + time;
                for(float v : value){
                    log = log + ", " + v;
                }
                Log.i(TAG, log);
                switch (type){
                    case "ACCEL":
                        phone_accel[0] = value;
                        break;
                    case "GYRO":
                        phone_gyro[0] = value;
                        break;
                }
            }
        }

    }

}
