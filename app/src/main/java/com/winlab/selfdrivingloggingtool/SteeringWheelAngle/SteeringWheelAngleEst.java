package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;
import com.winlab.selfdrivingloggingtool.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Luyang on 9/5/2016.
 */
public class SteeringWheelAngleEst {

    private static final String TAG = "SteeringWheelAngleEst";
    private Context mContext = ApplicationHelper.getAppContext();
    private DataReceiver mDataReceiver;

    private boolean mConfigurationSign = false;

    private float[][] phone_accel= new float[1][3];
    private float[][] phone_gyro= new float[1][3];
    private float[][] imu_accel= new float[1][3];
    private float[][] imu_gyro= new float[1][3];

    private List<float[][]> configList = new ArrayList<>();

    private double angle = 0;

    public SteeringWheelAngleEst(){

    }

    public void start(){
        Log.i(TAG, "Start!");
        mDataReceiver = new DataReceiver();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_PHONE_SENSOR));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_IMU_SENSOR));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_IMU_CONNECTION));

    }

    public void stop(){
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mDataReceiver);
    }

    public void configuration(float[][] gyro_tmp){
        configList.add(gyro_tmp);
        if(configList.size()>20){
            configList.remove(0);
        }

    }

    public void calculateAngle(){
        float[][] r = new float[][] {
                {-1, 0, 0},
                {0,  1, 0},
                {0,  0, -1}
        };
        float[][] rotated_imu_accel = MathUtils.multiplyByMatrix(imu_accel, r);
        float[][] rotated_imu_gyro = MathUtils.multiplyByMatrix(imu_gyro,r);

        float[] v1 = {0,-1};
        float[] v2 = {rotated_imu_accel[0][0],rotated_imu_accel[0][2]};
        double accelAngle = MathUtils.angleBetweenVector(v1, v2, angle);
        float[] v3 = {phone_accel[0][1]/9.8f,-1};
        double angleError = MathUtils.angleBetweenVector(v1, v3, 0);

        angle = MathUtils.complementryFilter(angle, imu_gyro[0][1], accelAngle-angleError, 0.02, 0.9, 0);
        Log.i(TAG, "Angle: "+(int)angle +". Error: "+(int)angleError);
        sendAngleBroadCast(angle);
    }

    private void sendAngleBroadCast(double angle) {
        //
        // Log.i(TAG, "Angle: "+angle);
        Long tsLong = System.currentTimeMillis();
        Intent intent = new Intent(Global.BROADCAST_STEERING_WHEEL_ANGLE)
                .putExtra(Global.EXTENDED_STEERING_WHEEL_ANGLE_VALUE, angle)
                .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public class DataReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            //Log.i(TAG,"Received!");
            String act = intent.getAction();
            if(act == Global.BROADCAST_IMU_CONNECTION){
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                boolean sign = intent.getBooleanExtra(Global.EXTENDED_IMU_CONNECTION, false);
                if(sign){
                    start();
                }else{
                    stop();
                }
            }
            else if (act == Global.BROADCAST_PHONE_SENSOR) {
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                float[] value = intent.getFloatArrayExtra(Global.EXTENDED_PHONE_SENSOR_VALUE);
                String type = intent.getStringExtra(Global.EXTENDED_PHONE_SENSOR_TYPE);
                String log = "Phone: "+ type + ", " + time;
                for(float v : value){
                    log = log + ", " + v;
                }
                //Log.i(TAG, log);
                switch (type){
                    case "ACCEL":
                        phone_accel[0] = value;
                        break;
                    case "GYRO":
                        phone_gyro[0] = value;
                        break;
                }
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
                switch (type){
                    case "ACCEL":
                        imu_accel[0] = value;
                        break;
                    case "GYRO":
                        imu_gyro[0] = value;
                        break;
                }
                calculateAngle();
            }
        }

    }

}
