package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;


/**
 * This class is responsible for handling communication between the wearable
 * sensor and the Android application. Data is read here and stored in a Global
 * variable for other classes to access.
 * 
 * @author Invensense
 * 
 */
public final class IMUBluetoothDataReader extends AsyncTask {

    String tag = "IMUBluetoothDataReader";

    public final static int PACKET_DEBUG = 1;
    public final static int PACKET_QUAT = 2;
    public final static int PACKET_DATA = 3;
    public final static int PACKET_OTHER_SENSORS = 4;
    public final static int PACKET_ROT_MAT = 5;
    public final static int PACKET_EULER = 6;
    public final static int PACKET_HEADING = 7;

    public final static int PACKET_DATA_ACCEL = 0;
    public final static int PACKET_DATA_GYRO = 1;
    public final static int PACKET_DATA_COMPASS = 2;
    public final static int PACKET_DATA_QUAT = 3;
    public final static int PACKET_DATA_EULER = 4;
    public final static int PACKET_DATA_ROT = 5;
    public final static int PACKET_DATA_HEADING = 6;
    public final static int PACKET_DATA_OTHER_SENSORS = 7;
    public final static int PACKET_DATA_LOG = 8;

    Semaphore mutexLock = new Semaphore(1);

    /** true if the bluetooth connection is on */
    public boolean running = false;

    private Context mContext;

    public IMUBluetoothDataReader(){
        mContext = ApplicationHelper.getAppContext();
    }


    /**
     * Synchronizes the first read operation between the wearable device and the
     * application to ensure the synchronization for rest of the packets.
     *
     * @param inStream
     *            input stream to read from
     * @return true if successful false otherwise
     */
    public boolean synRead(InputStream inStream) {
        byte[] pData = new byte[23];
        int curRead = 0;
        Log.i("IMUBluetoothDataReader", "sync read reached");
        pData[0] = 0;
        while (pData[0] != '$') {
            Log.i("IMUBluetoothDataReader", " NOT $ " + pData[0]);
            try {
                curRead = inStream.read(pData, 0, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pData[0] != '$')
                Log.i("IMUBluetoothDataReader", "Wrong Ping");
            if (curRead < 0) {
                return false;
            }
        }
        Log.i("IMUBluetoothDataReader", "Out of here");
        int m = 1;
        int shiftIndex = m;
        int pLength = 23;
        while (m != pLength) {
            try {
                curRead = inStream
                        .read(pData, shiftIndex, pLength - shiftIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (curRead < 0) {
                return false;
            }
            m += curRead;
            shiftIndex = m;
        }

        return true;
    }

    /**
     * Reads data from input stream and stores them to a buffer
     *
     * @param inStream
     *            input stream to read from
     * @param pData
     *            buffer where the read data will be stored
     * @param pLength
     *            maximum bytes to store into pData
     * @return true if successful false otherwise
     */
    public boolean readData(InputStream inStream, byte[] pData, int pLength) {

        try {
            int m = 0;
            int shiftIndex = m;
            while (m != pLength) {

                int curRead = inStream.read(pData, shiftIndex, pLength- shiftIndex);
                if (curRead < 0) {
                    return false;
                }
                m += curRead;
                shiftIndex = m;
            }

            return true;

        } catch (IOException e) {

            running = false;
            // e.printStackTrace();
            return false;
        }
    }

    protected void onPreExecute() {
        Log.i("IMUBluetoothDataReader", "PreExecute!");
        sendConnectBroadcast();
        super.onPreExecute();
        running = true;
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.i("IMUBluetoothDataReader", "PostExecute!");
        sendDisconnectBroadcast();
        super.onPostExecute(o);
    }

    private void sendConnectBroadcast(){
        Long tsLong = System.currentTimeMillis();
        Intent intent = new Intent(Global.BROADCAST_IMU_CONNECTION)
                .putExtra(Global.EXTENDED_IMU_CONNECTION, 1)
                .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void sendConnectingBroadcast(){
        Long tsLong = System.currentTimeMillis();
        Intent intent = new Intent(Global.BROADCAST_IMU_CONNECTION)
                .putExtra(Global.EXTENDED_IMU_CONNECTION, 2)
                .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void sendDisconnectBroadcast(){
        Long tsLong = System.currentTimeMillis();
        Intent intent = new Intent(Global.BROADCAST_IMU_CONNECTION)
                .putExtra(Global.EXTENDED_IMU_CONNECTION, 0)
                .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Combine two individual bytes into a float without overflow check.
     *
     * @param d1
     *            First Byte
     * @param d2
     *            Second Byte
     * @return Combined float value.
     */
    float twoBytes_nocheck(byte d1, byte d2) {
        // """ unmarshal two bytes into int16 """
        float d = ord(d1) * 256 + ord(d2);
        return d;
    }

    /**
     * Combine two individual bytes into a float with overflow check.
     *
     * @param d1
     *            First Byte
     * @param d2
     *            Second Byte
     * @return Combined float value.
     */

    float twoBytes(byte d1, byte d2) {
        // """ unmarshal two bytes into int16 """
        float d = ord(d1) * 256 + ord(d2);
        if (d > 32767)
            d -= 65536;
        return d;
    }

    int ord(byte d) {
        return d & 0xff;
    }

    /**
     * Combine four individual bytes into a float with overflow check.
     *
     * @param d1
     *            First Byte
     * @param d2
     *            Second Byte
     * @param d3
     *            Third Byte
     * @param d4
     *            Fourth Byte
     * @return Combined Float value.
     */

    // For 32-bit signed integers.
    float four_bytes(byte d1, byte d2, byte d3, byte d4) {
        float d = ord(d1) * (1 << 24) + ord(d2) * (1 << 16) + ord(d3)
                * (1 << 8) + ord(d4);
        if (d > 2147483648l)
            d -= 4294967296l;
        return d;
    }

    private void onPacketDataAccel(byte[] l, String recvTime) {
        // Record Accelerometer data
        float[] accel = new float[3];

        accel[0] = four_bytes(l[3], l[4], l[5], l[6]) * 1.0f / (1 << 16);
        accel[1] = four_bytes(l[7], l[8], l[9], l[10]) * 1.0f / (1 << 16);
        accel[2] = four_bytes(l[11], l[12], l[13], l[14]) * 1.0f / (1 << 16);
        sendBroadCast(accel, "ACCEL");
    }

    private void onPacketDataGyro(byte[] l, String recvTime) {
        // Record Gyroscope data
        float[] gyro = new float[3];

        gyro[0] = four_bytes(l[3], l[4], l[5], l[6]) * 1.0f / (1 << 16);
        gyro[1] = four_bytes(l[7], l[8], l[9], l[10]) * 1.0f / (1 << 16);
        gyro[2] = four_bytes(l[11], l[12], l[13], l[14]) * 1.0f / (1 << 16);
        sendBroadCast(gyro, "GYRO");
    }

    private void onPacketDataCompass(byte[] l, String recvTime) {
        // Record Compass data
        float[] comp = new float[3];

        comp[0] = four_bytes(l[3], l[4], l[5], l[6]) * 1.0f / (1 << 16);
        comp[1] = four_bytes(l[7], l[8], l[9], l[10]) * 1.0f / (1 << 16);
        comp[2] = four_bytes(l[11], l[12], l[13], l[14]) * 1.0f / (1 << 16);
        sendBroadCast(comp, "MAG");
    }

    private void onPacketDataQuat(byte[] l, String recvTime) {
        // Record Quaternion data
        float[] quat = new float[4];

        quat[0] = four_bytes(l[3], l[4], l[5], l[6]) * 1.0f / (1 << 30);
        quat[1] = four_bytes(l[7], l[8], l[9], l[10]) * 1.0f / (1 << 30);
        quat[2] = four_bytes(l[11], l[12], l[13], l[14]) * 1.0f / (1 << 30);
        quat[3] = four_bytes(l[15], l[16], l[17], l[18]) * 1.0f / (1 << 30);
        sendBroadCast(quat, "QUAT");
    }

    private void sendBroadCast(float[] value, String type) {
        Log.i("IMUBluetoothDataReader", type + "Sensor Changed!");
        Long tsLong = System.currentTimeMillis();
        Intent intent = new Intent(Global.BROADCAST_IMU_SENSOR)
                .putExtra(Global.EXTENDED_IMU_SENSOR_VALUE, value)
                .putExtra(Global.EXTENDED_IMU_SENSOR_TYPE, type)
                .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }


    /**
     * this function runs in the background and continuously updates the Sensors
     * values by reading the Bluetooth input stream from wearable SDK. There are
     * optional filters can be applied in the code
     */

    protected Integer doInBackground(Object... param) {

        InputStream params = (InputStream) param[0];
        synRead(params);

        while (running) {
            try {
                byte buffer[] = new byte[23];

                if (readData(params, buffer, 23)) {
                    //	Log.e("NEW", String.valueOf(buffer[1]));
                    Log.i(tag,""+buffer[1]);
                    if (buffer[1] == PACKET_DATA) {
                        String recvTime = Long.toString(System.currentTimeMillis());
                        //Log.i("here",""+buffer[2]);
                        switch (buffer[2]) {


                            case PACKET_DATA_ACCEL:
                                onPacketDataAccel(buffer, recvTime);
                                break;

                            case PACKET_DATA_GYRO:
                                onPacketDataGyro(buffer, recvTime);
                                break;

                            case PACKET_DATA_COMPASS:
                                onPacketDataCompass(buffer, recvTime);
                                break;

                            case PACKET_DATA_QUAT:
                                onPacketDataQuat(buffer, recvTime);
                                break;

                        }

                    } else if (buffer[1] == PACKET_QUAT) {

                        if (buffer[2] == PACKET_DATA_QUAT)
                            Log.e("NEW", "Quat");

                    }
                }
            } catch (Exception e) {
                Log.i("IMUBluetoothDataReader", e.toString());
            } finally {
            }
        }
        return 1;
    }


}
