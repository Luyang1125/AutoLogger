package com.winlab.selfdrivingloggingtool.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.winlab.selfdrivingloggingtool.R;
import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.Global;
import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.SteeringWheelAngleManager;
import com.winlab.selfdrivingloggingtool.camera.Recorder;
import com.winlab.selfdrivingloggingtool.speed.GPSSpeedManager;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;
import com.winlab.selfdrivingloggingtool.data.DataManager;

import java.text.DecimalFormat;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private static final String SENSOR_CONNECT = "Sensor connected!";
    private static final String SENSOR_NOT_CONNECT = "Sensor not connected!";
    private static final String SENSOR_CONNECTING = "Connecting...";

    private Button mButton;
    private TextView mDrivingMode;
    private TextView mSteeringWheelAngle;
    private TextView mGPSFusedSpeed;
    private TextView mVehicleAcceleration;
    private ImageView mSteeringWheelImage;
    private Recorder mRecorder;
    private SurfaceView mSurfaceView;

    private Context mContext = ApplicationHelper.getAppContext();

    private DataReceiver mDataReceiver;

    private SteeringWheelAngleManager mSteeringWheelAngleManager;
    private DataManager mDataManager;
    private GPSSpeedManager mGPSSpeedManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mButton = (Button) view.findViewById(R.id.button);
        mButton.setText(SENSOR_NOT_CONNECT);
        mButton.setBackgroundColor(Color.RED);

        mDrivingMode = (TextView) view.findViewById(R.id.drivingStatusTextView);
        mDrivingMode.setText("Not Available");
        //mDrivingMode.setText("Driving");
        mDrivingMode.setTextColor(Color.BLACK);

        mSteeringWheelAngle = (TextView) view.findViewById(R.id.textView4);
        mSteeringWheelAngle.setText("");
        mSteeringWheelAngle.setTextColor(Color.WHITE);
        mSteeringWheelAngle.setTextSize(20);

        mSteeringWheelImage = (ImageView) view.findViewById(R.id.imageView);
        mSteeringWheelImage.setImageResource(R.mipmap.steering_wheel);

        mGPSFusedSpeed = (TextView) view.findViewById(R.id.textView5);

        mGPSFusedSpeed.setText("Speed Not Available");
        //mGPSFusedSpeed.setText("Speed: " + new DecimalFormat("##.##").format(5.34) + " m/s");

        mGPSFusedSpeed.setTextColor(Color.BLACK);

        mVehicleAcceleration = (TextView) view.findViewById(R.id.textView6);
        mVehicleAcceleration.setText("Accel Not Available");
        //mVehicleAcceleration.setText("Accel: " + new DecimalFormat("##.##").format(0.12) + "m/s^2");
        mVehicleAcceleration.setTextColor(Color.BLUE);

        mSurfaceView = (SurfaceView) view.findViewById(R.id.surface);

        mDataReceiver = new DataReceiver();
        LocalBroadcastManager.getInstance(ApplicationHelper.getAppContext()).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_PHONE_SENSOR));
        LocalBroadcastManager.getInstance(ApplicationHelper.getAppContext()).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_IMU_SENSOR));
        LocalBroadcastManager.getInstance(ApplicationHelper.getAppContext()).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_IMU_CONNECTION));
        LocalBroadcastManager.getInstance(ApplicationHelper.getAppContext()).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_STEERING_WHEEL_ANGLE));

        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onResume() {
        mSteeringWheelAngleManager = new SteeringWheelAngleManager();
        mSteeringWheelAngleManager.start();

        mGPSSpeedManager = new GPSSpeedManager();
        mGPSSpeedManager.start();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                if (btn.getText().equals(SENSOR_CONNECT)) {
                    //mSteeringWheelAngleManager.stop();
                } else if (btn.getText().equals(SENSOR_NOT_CONNECT)) {
                    //mSteeringWheelAngleManager.start();
                }
            }
        });
        //LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_PHONE_SENSOR));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_IMU_CONNECTION));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_STEERING_WHEEL_ANGLE));
        /*LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_LOCATION));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mDataReceiver, new IntentFilter(Global.BROADCAST_GOOGLE_ACTIVITY));*/


        mRecorder = new Recorder(mSurfaceView);
        super.onResume();
    }

    public void setSensorLoggingStatus(){
        if(Global.SensorLogStatus == 1) {
            mButton.setText(SENSOR_CONNECT);
            mButton.setBackgroundColor(Color.GREEN);
        }else{
            mButton.setText(SENSOR_NOT_CONNECT);
            mButton.setBackgroundColor(Color.RED);
        }
    }

    public void onDestroy() {
        Log.i(TAG, "Destroy");
        mSteeringWheelAngleManager.stop();
        mGPSSpeedManager.stop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mDataReceiver);
        super.onDestroy();
    }

    public void startLogging(){
        mDataManager = new DataManager();
        mDataManager.setRecorder(mRecorder);
        mDataManager.startLogging();
    }

    public void stopLogging(){
        mDataManager.stopLogging();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mDataManager.getRunningSign()) {
            mDataManager.stopLogging();
        }
    }

    public class DataReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            //Log.i("DataReceiverService","Received!");
            String act = intent.getAction();
            if(act == Global.BROADCAST_IMU_CONNECTION){
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                int sign = intent.getIntExtra(Global.EXTENDED_IMU_CONNECTION, 0);
                if(sign==1){
                    mButton.setText(SENSOR_CONNECT);
                    mButton.setBackgroundColor(Color.GREEN);
                    mButton.setVisibility(View.INVISIBLE);
                }else if(sign==0){
                    mButton.setText(SENSOR_NOT_CONNECT);
                    mButton.setBackgroundColor(Color.RED);
                    mSteeringWheelAngle.setText("");
                    mSteeringWheelImage.setRotation(0);
                }else if(sign==2){
                    mButton.setText(SENSOR_CONNECTING);
                    mButton.setBackgroundColor(Color.YELLOW);
                }
            }else if(act == Global.BROADCAST_STEERING_WHEEL_ANGLE){
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                double angle = intent.getDoubleExtra(Global.EXTENDED_STEERING_WHEEL_ANGLE_VALUE, 0);
                String log = (int)angle + "°";
                mSteeringWheelImage.setRotation((float)angle);
                Log.i(TAG, log);
                mSteeringWheelAngle.setText(log);
                mButton.setText(SENSOR_CONNECT);
                mButton.setBackgroundColor(Color.GREEN);
            }else if (act == Global.BROADCAST_LOCATION) {
                Long time = intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0);
                double lat = intent.getDoubleExtra(Global.EXTENDED_DATA_LAT, 0);
                double lng = intent.getDoubleExtra(Global.EXTENDED_DATA_LNG, 0);
                float speed = intent.getFloatExtra(Global.EXTENDED_DATA_SPEED, 0);
                String log = "Speed: " + new DecimalFormat("##.##").format(speed*2.23694) + " mph";
                mGPSFusedSpeed.setText(log);
            }else if (act == Global.BROADCAST_GOOGLE_ACTIVITY) {
                Long time = intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0);
                String msg = intent.getStringExtra(Global.EXTENDED_DATA_GOOGLE_ACTIVITY_MESSAGE);
                mDrivingMode.setText(msg);
            }else if (act == Global.BROADCAST_PHONE_SENSOR) {
                String time = Long.toString(intent.getLongExtra(Global.EXTENDED_DATA_TIMETAG, 0));
                String type = intent.getStringExtra(Global.EXTENDED_PHONE_SENSOR_TYPE);
                if(type == "ACCEL") {
                    float[] value = intent.getFloatArrayExtra(Global.EXTENDED_PHONE_SENSOR_VALUE);
                    String log = "Phone: "+ type + ", " + time;
                    for(float v : value){
                        log = log + ", " + v;
                    }
                    if(mDrivingMode.getText().equals("DRIVING")) {
                        mVehicleAcceleration.setText("Accel: " + new DecimalFormat("##.##").format(-value[2]) + "m/s^2");
                        if(value[2]<-1){
                            mVehicleAcceleration.setTextColor(Color.BLUE);
                        }else if(value[2]>1){
                            mVehicleAcceleration.setTextColor(Color.RED);
                        }else{
                            mVehicleAcceleration.setTextColor(Color.BLACK);
                        }
                    }
                }
            }

        }

    }




}
