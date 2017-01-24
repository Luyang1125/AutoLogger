package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luyang on 8/13/15.
 */
public class PhoneSensorLogging {

    private SensorManager sensorManager;
    private Map<Integer, String> sensorTypes = new HashMap<Integer, String>();
    private Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();

    private Context mContext;

    public PhoneSensorLogging(){
        mContext = ApplicationHelper.getAppContext();
    }


    public void start() {

        Log.i("PhoneSensorLogging", "Start");

        // Get sensors to be captured
        sensorTypes.put(Sensor.TYPE_ACCELEROMETER, "ACCEL");
        sensorTypes.put(Sensor.TYPE_GYROSCOPE, "GYRO");
        /*sensorTypes.put(Sensor.TYPE_ROTATION_VECTOR, "ROT");
        sensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD, "MAG");
		sensorTypes.put(Sensor.TYPE_GRAVITY, "GRAV");
		sensorTypes.put(Sensor.TYPE_LINEAR_ACCELERATION, "LINEAR");*/

        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        for (Integer type : sensorTypes.keySet()) {
            sensors.put(type, sensorManager.getDefaultSensor(type));
        }

        for (Sensor sensor : sensors.values()) {

            //	sensorManager.registerListener(sensorListener, sensor, 50000); // 20 Hz
            //	sensorManager.registerListener(sensorListener, sensor, 10000); //100 Hz
            sensorManager.registerListener(sensorListener, sensor, 20000); //50 Hz

            //	sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);  //200Hz

        }

    }


    public void stop() {
        sensorManager.unregisterListener(sensorListener);
        Log.i("PhoneSensorLogging", "Stop");
    }

    private SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            Log.i("PhoneSensorLogging", "Sensor Changed!");

            // Broadcasting sensor data
            Long tsLong = System.currentTimeMillis();
            Intent intent = new Intent(Global.BROADCAST_PHONE_SENSOR)
                    .putExtra(Global.EXTENDED_PHONE_SENSOR_VALUE, event.values)
                    .putExtra(Global.EXTENDED_PHONE_SENSOR_TYPE, sensorTypes.get(event.sensor.getType()))
                    .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            /*if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                Intent intent = new Intent(Constants.BROADCAST_ACCELERATION)
                        .putExtra(Constants.EXTENDED_DATA_ACCELX, event.values[0])
                        .putExtra(Constants.EXTENDED_DATA_ACCELY, event.values[1])
                        .putExtra(Constants.EXTENDED_DATA_ACCELZ, event.values[2])
                        .putExtra(Constants.EXTENDED_DATA_TIMETAG, tsLong);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                Intent intent = new Intent(Constants.BROADCAST_GYROSCOPE)
                        .putExtra(Constants.EXTENDED_DATA_GYROX, event.values[0])
                        .putExtra(Constants.EXTENDED_DATA_GYROY, event.values[1])
                        .putExtra(Constants.EXTENDED_DATA_GYROZ, event.values[2])
                        .putExtra(Constants.EXTENDED_DATA_TIMETAG, tsLong);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                Intent intent = new Intent(Constants.BROADCAST_ROTATION_VECTOR)
                        .putExtra(Constants.EXTENDED_DATA_ROTX, event.values[0])
                        .putExtra(Constants.EXTENDED_DATA_ROTY, event.values[1])
                        .putExtra(Constants.EXTENDED_DATA_ROTZ, event.values[2])
                        .putExtra(Constants.EXTENDED_DATA_TIMETAG, tsLong);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                // get Quaternion from Rotation Vector and broadcast it
                float[] quaternion = new float[4];
                sensorManager.getQuaternionFromVector(quaternion, event.values);
                Intent intent1 = new Intent(Constants.BROADCAST_QUATERNION)
                        .putExtra(Constants.EXTENDED_DATA_QUATW, quaternion[0])
                        .putExtra(Constants.EXTENDED_DATA_QUATX, quaternion[1])
                        .putExtra(Constants.EXTENDED_DATA_QUATY, quaternion[2])
                        .putExtra(Constants.EXTENDED_DATA_QUATZ, quaternion[3])
                        .putExtra(Constants.EXTENDED_DATA_TIMETAG, tsLong);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
            }
            else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                Intent intent = new Intent(Constants.BROADCAST_MAGNETO)
                        .putExtra(Constants.EXTENDED_DATA_MAGNETOX, event.values[0])
                        .putExtra(Constants.EXTENDED_DATA_MAGNETOY, event.values[1])
                        .putExtra(Constants.EXTENDED_DATA_MAGNETOZ, event.values[2])
                        .putExtra(Constants.EXTENDED_DATA_TIMETAG, tsLong);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            // Other sensors ...*/
        }



    };


}
