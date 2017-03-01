package com.winlab.selfdrivingloggingtool.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.Global;
import com.winlab.selfdrivingloggingtool.camera.Recorder;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static java.sql.Types.FLOAT;

/**
 * Created by luyangliu on 1/30/17.
 */

public class DataManager {

    private static final String TAG = "DataManager";
    private Context mContext = ApplicationHelper.getAppContext();
    private DataReceiver mDataReceiver = new DataReceiver();


    private static final boolean Multiple_Files = true;
    private static final boolean Single_Files = false;

    private boolean DataRecordingMode = Single_Files;

    private File f_p_accel;
    private File f_p_gyro;
    private File f_p_magn;
    private File f_gps;
    private File f_s_accel;
    private File f_s_gyro;

    private File f_single;


    private BufferedOutputStream bos_p_accel;
    private BufferedOutputStream bos_p_gyro;
    private BufferedOutputStream bos_p_magn;
    private BufferedOutputStream bos_gps;
    private BufferedOutputStream bos_s_accel;
    private BufferedOutputStream bos_s_gyro;

    private BufferedOutputStream bos_single;

    private File directory;

    private boolean running_sign = false;

    private Recorder mRecorder;

    public DataManager(){

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
        folder_main = "AutoLogger/";
        folder = new File(Environment.getExternalStorageDirectory(),
                folder_main);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        directory = Environment.getExternalStoragePublicDirectory(folder_main);

        if(DataRecordingMode == Multiple_Files) {
            f_p_accel = new File(directory, "p_accel.csv");
            f_p_gyro = new File(directory, "p_gyro.csv");
            f_p_magn = new File(directory, "p_magn.csv");
            f_gps = new File(directory, "gps.csv");
            f_s_accel = new File(directory, "s_accel.csv");
            f_s_gyro = new File(directory, "s_gyro.csv");
        }else{
            f_single = new File(directory, time);
        }

    }

    public void startLogging(){

        createFolderAndFiles();

        running_sign = true;
        mRecorder.setFoler(directory);
        try {
            if(DataRecordingMode == Multiple_Files) {
                bos_p_accel = new BufferedOutputStream(new FileOutputStream(f_p_accel));
                bos_p_gyro = new BufferedOutputStream(new FileOutputStream(f_p_gyro));
                bos_p_magn = new BufferedOutputStream(new FileOutputStream(f_p_magn));
                bos_gps = new BufferedOutputStream(new FileOutputStream(f_gps));
                bos_s_accel = new BufferedOutputStream(new FileOutputStream(f_s_accel));
                bos_s_gyro = new BufferedOutputStream(new FileOutputStream(f_s_gyro));
            }else{
                bos_single = new BufferedOutputStream(new FileOutputStream(f_single));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_PHONE_SENSOR));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_IMU_SENSOR));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_LOCATION));

        mRecorder.resume();

    }

    public void stopLogging(){
        running_sign = false;
        mRecorder.pause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mDataReceiver);
        try {
            if(DataRecordingMode == Multiple_Files) {
                bos_p_accel.flush();
                bos_p_accel.close();
                bos_p_gyro.flush();
                bos_p_gyro.close();
                bos_p_magn.flush();
                bos_p_magn.close();
                bos_gps.flush();
                bos_gps.close();
                bos_s_accel.flush();
                bos_s_accel.close();
                bos_s_gyro.flush();
                bos_s_gyro.close();
            }else{
                bos_single.flush();
                bos_single.close();
            }
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

                Writer myWriter;
                String dataPacket;
                if(DataRecordingMode == Multiple_Files) {
                    dataPacket = new StringBuilder()
                            .append(time).append(",")
                            .append(value[0]).append(",")
                            .append(value[1]).append(",")
                            .append(value[2]).append(";\n")
                            .toString();
                    switch (type) {
                        case "ACCEL":
                            myWriter = new Writer(bos_p_accel, dataPacket);
                            new Thread(myWriter).start();
                            break;
                        case "GYRO":
                            myWriter = new Writer(bos_p_gyro, dataPacket);
                            new Thread(myWriter).start();
                            break;
                    }
                }else{
                    dataPacket = new StringBuilder()
                            .append(time).append(",")
                            .append("p_").append(type).append(",")
                            .append(value[0]).append(",")
                            .append(value[1]).append(",")
                            .append(value[2]).append(";\n")
                            .toString();
                    myWriter = new Writer(bos_single, dataPacket);
                    new Thread(myWriter).start();
                }
                Log.i(TAG,type+": "+dataPacket);
            }else if(act == Global.BROADCAST_IMU_SENSOR){
                //Log.i(TAG,"IMU Received!");
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                float[] value = intent.getFloatArrayExtra(Global.EXTENDED_IMU_SENSOR_VALUE);
                String type = intent.getStringExtra(Global.EXTENDED_IMU_SENSOR_TYPE);
                String log = "IMU: "+ type + ", " + time;
                for(float v : value){
                    log = log + ", " + v;
                }
                //Log.i(TAG,log);
                Writer myWriter;
                String dataPacket;
                if(DataRecordingMode == Multiple_Files) {
                    dataPacket = new StringBuilder()
                            .append(time).append(",")
                            .append(value[0]).append(",")
                            .append(value[1]).append(",")
                            .append(value[2]).append(";\n")
                            .toString();
                    switch (type) {
                        case "ACCEL":
                            myWriter = new Writer(bos_p_accel, dataPacket);
                            new Thread(myWriter).start();
                            break;
                        case "GYRO":
                            myWriter = new Writer(bos_p_gyro, dataPacket);
                            new Thread(myWriter).start();
                            break;
                    }
                }else{
                    dataPacket = new StringBuilder()
                            .append(time).append(",")
                            .append("s_").append(type).append(",")
                            .append(value[0]).append(",")
                            .append(value[1]).append(",")
                            .append(value[2]).append(";\n")
                            .toString();
                    myWriter = new Writer(bos_single, dataPacket);
                    new Thread(myWriter).start();
                }
                Log.i(TAG,type+": "+dataPacket);
            }else if(act == Global.BROADCAST_LOCATION){
                //Log.i(TAG,"IMU Received!");
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                String lat = Double.toString(intent.getDoubleExtra(Global.EXTENDED_DATA_LAT, 0));
                String lng = Double.toString(intent.getDoubleExtra(Global.EXTENDED_DATA_LNG, 0));
                String speed = Float.toString(intent.getFloatExtra(Global.EXTENDED_DATA_SPEED, 0));
                //Log.i(TAG,log);

                Writer myWriter;
                String dataPacket;
                if(DataRecordingMode == Multiple_Files) {
                    dataPacket = new StringBuilder()
                            .append(time).append(",")
                            .append(lat).append(",")
                            .append(lng).append(",")
                            .append(speed).append(";\n")
                            .toString();
                    myWriter = new Writer(bos_gps, dataPacket);
                    new Thread(myWriter).start();
                }else{
                    dataPacket = new StringBuilder()
                            .append(time).append(",")
                            .append("gps").append(",")
                            .append(lat).append(",")
                            .append(lng).append(",")
                            .append(speed).append(";\n")
                            .toString();
                    myWriter = new Writer(bos_single, dataPacket);
                    new Thread(myWriter).start();
                }
                Log.i(TAG,"GPS: "+dataPacket);
            }
        }

    }

    public boolean getRunningSign(){
        return running_sign;
    }

}
