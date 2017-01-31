package com.winlab.selfdrivingloggingtool.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.Global;
import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.SteeringWheelAngleEst;
import com.winlab.selfdrivingloggingtool.camera.Recorder;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by luyangliu on 1/30/17.
 */

public class DataManager {

    private static final String TAG = "DataManager";
    private Context mContext = ApplicationHelper.getAppContext();
    private DataReceiver mDataReceiver = new DataReceiver();

    private File f_accel;
    private File f_gyro;
    private File f_magn;
    private File f_gps;

    private BufferedOutputStream bos_accel;
    private BufferedOutputStream bos_gyro;
    private BufferedOutputStream bos_magn;
    private BufferedOutputStream bos_gps;

    private File directory;

    private boolean running_sign = false;

    private Recorder mRecorder;

    public DataManager(){
        createFolderAndFiles();
    }

    public void setRecorder(Recorder mRecorder_in){
        this.mRecorder = mRecorder_in;
    }

    private void createFolderAndFiles(){

        String folder_main = "AutoLogger";

        File folder = new File(Environment.getExternalStorageDirectory(),
                folder_main);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //long time= System.currentTimeMillis();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String name_raw = currentDateTimeString + ".csv";
        String time = name_raw.replaceAll("\\s", "_");
        folder_main = "AutoLogger/"+time;
        folder = new File(Environment.getExternalStorageDirectory(),
                folder_main);


        if (!folder.exists()) {
            folder.mkdirs();
        }

        directory = Environment.getExternalStoragePublicDirectory(folder_main);

        f_accel = new File(directory,"accel.csv");
        f_gyro = new File(directory,"gyro.csv");
        f_magn = new File(directory,"magn.csv");
        f_gps = new File(directory,"gps.csv");

    }

    public void startLogging(){
        running_sign = true;
        mRecorder.setFoler(directory);
        try {
            bos_accel = new BufferedOutputStream(new FileOutputStream(f_accel));
            bos_gyro = new BufferedOutputStream(new FileOutputStream(f_gyro));
            bos_magn = new BufferedOutputStream(new FileOutputStream(f_magn));
            bos_gps = new BufferedOutputStream(new FileOutputStream(f_gps));
        } catch (IOException e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_PHONE_SENSOR));

        mRecorder.resume();

    }

    public void stopLogging(){
        running_sign = false;
        mRecorder.pause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mDataReceiver);
        try {
            bos_accel.flush();
            bos_accel.close();
            bos_gyro.flush();
            bos_gyro.close();
            bos_magn.flush();
            bos_magn.close();
            bos_gps.flush();
            bos_gps.close();
            Log.d("File Close", "Closed!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
                //Log.i(TAG, log);
                String dataPacket = "";
                Writer myWriter;
                switch (type){
                    case "ACCEL":
                        dataPacket = new StringBuilder()
                                .append(time).append(",")
                                .append(value[0]).append(",")
                                .append(value[1]).append(",")
                                .append(value[2]).append(";\n")
                                .toString();

                        myWriter = new Writer(bos_accel,dataPacket);
                        new Thread(myWriter).start();
                        break;
                    case "GYRO":
                        dataPacket = new StringBuilder()
                                .append(time).append(",")
                                .append(value[0]).append(",")
                                .append(value[1]).append(",")
                                .append(value[2]).append(";\n")
                                .toString();
                        myWriter = new Writer(bos_gyro,dataPacket);
                        new Thread(myWriter).start();
                        break;
                }
                Log.i(TAG,type+": "+dataPacket);
            }
        }

    }

    public boolean getRunningSign(){
        return running_sign;
    }

}
