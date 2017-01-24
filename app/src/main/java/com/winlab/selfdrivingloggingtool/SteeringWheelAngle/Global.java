package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

import android.os.Handler;

import java.io.BufferedOutputStream;

/**
 *
 * This is a Global class that is primarily intended to provide a bridge between the sensor values
 * that are shared between different classes and activities. A better alternative of class could be
 * Listener pattern.
 * 
 */
public class Global {

    public static final int PRINT_ACCEL     = (0x01);
    public static final int PRINT_GYRO      = (0x02);
    public static final int PRINT_QUAT      = (0x04);
    public static final int PRINT_COMPASS   = (0x08);
    public static final int PRINT_EULER     = (0x10);
    public static final int PRINT_ROT_MAT   = (0x20);
    public static final int PRINT_HEADING   = (0x40);
    public static final int PRINT_PEDO      = (0x80);
    public static final int PRINT_OTHER_SENSORS	= (0x100);

    /**
     * Six-axis quaternion values that are helpful in displaying a rolling dice.
     */
    public static float[] q = new float[4];
    static {
        q[0] = (float) 1.0;
        q[1] = (float) 0.0;
        q[2] = (float) 0.0;
        q[3] = (float) 0.0;
    }

    /**
     * Euler angle values
     */
    public static float[] eular = new float[3];
    static{
        eular[0]= 0f;
        eular[1]= 0f;
        eular[2]= 0f;
    }
    /**
     * Current activity information
     */
    public static String currActivity="Starting...";
    /**
     * Confidence level of current calculated activity in percentage.
     */
    public static int confidenceLevel = 0;
    /**
     * Gyro data
     */
    public static float[] Gyro= new float[3];
    /**
     * Accelerometer data
     */
    public static float[] Accel= new float[3];
    /**
     * Compass data if available
     */
    public static float[] Compass= new float[3];
    /**
     * Quaternion data if available
     */
    public static float[] Quat= new float[4];

    /**
     * A start and stop flag for data logging.
     */
    public static boolean startLogging=false;

    public static float Heading = 0f;

    public static float[] RM = new float[9];

    public static Handler CASDKUtilityActivityHandler;

    public static Handler MainActivityHandler;

    public static int SensorLogStatus = 0;
    public static double status= 20.0;
    public static BufferedOutputStream file;

    public static final String BROADCAST_IMU_SENSOR ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_IMU_SENSOR";
    public static final String BROADCAST_PHONE_SENSOR ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_PHONE_SENSOR";

    public static final String BROADCAST_IMU_CONNECTION ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_IMU_CONNECTION";
    public static final String BROADCAST_STEERING_WHEEL_ANGLE ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_STEERING_WHEEL_ANGLE";

    public static final String EXTENDED_PHONE_SENSOR_VALUE = "com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_PHONE_SENSOR_VALUE";
    public static final String EXTENDED_PHONE_SENSOR_TYPE = "com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_PHONE_SENSOR_TYPE";
    public static final String EXTENDED_IMU_SENSOR_VALUE = "com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_IMU_SENSOR_VALUE";
    public static final String EXTENDED_IMU_SENSOR_TYPE = "com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_IMU_SENSOR_TYPE";
    public static final String EXTENDED_DATA_TIMETAG = "com.winlab.selfdrivinglogger.steeringwheelangle.TIMETAG";
    public static final String EXTENDED_IMU_CONNECTION ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_IMU_CONNECTION";
    public static final String EXTENDED_STEERING_WHEEL_ANGLE_VALUE ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_STEERING_WHEEL_ANGLE_VALUE";


    public static final String BROADCAST_LOCATION ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_LOCATION";
    public static final String EXTENDED_DATA_LAT ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_DATA_LAT";
    public static final String EXTENDED_DATA_LNG ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_DATA_LNG";
    public static final String EXTENDED_DATA_SPEED ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_DATA_SPEED";

    public static final String BROADCAST_GOOGLE_ACTIVITY ="com.winlab.selfdrivinglogger.steeringwheelangle.BROADCAST_GOOGLE_ACTIVITY";
    public static final String EXTENDED_DATA_GOOGLE_ACTIVITY_MESSAGE ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_DATA_GOOGLE_ACTIVITY_MESSAGE";
    public static final String EXTENDED_DATA_GOOGLE_ACTIVITY_CODE ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_DATA_GOOGLE_ACTIVITY_CODE";
    public static final String EXTENDED_DATA_GOOGLE_ACTIVITY_CONFIDENCE ="com.winlab.selfdrivinglogger.steeringwheelangle.EXTENDED_DATA_GOOGLE_ACTIVITY_CONFIDENCE";

}