package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Luyang on 9/4/2016.
 */
public class IMUSensorLogging {

    static String tag = "IMUSensorLogging";

    /**
     * Bluetooth Adapter of the device
     **/
    BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Bluetooth Device that is intended to get the information
     */
    BluetoothDevice mBluetoothDevice = null;
    /**
     * A Bluetooth socket that connects the device to stream the data
     */
    BluetoothSocket mBluetoothSocket = null;
    /**
     * A flag indicating the connectivity of the socket
     */
    Boolean socketConnected = false;
    /**
     * Bluetooth socket output stream
     */
    OutputStream mBluetoothOS = null;

    private InputStream mBluetoothIS = null;
    /**
     * A utility class instance that provides the easy read operation from
     * bluetooth data stream.
     */
    private IMUBluetoothDataReader mBluetoothDataReader;
    protected static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String TAG = "IMUSensorLogging";

    protected static final int REQUEST_CONNECT_DEVICE = 1;
    protected static final int REQUEST_ENABLE_BT = 2;

    private Context mContext = ApplicationHelper.getAppContext();

    public void start() {
        sendConnectingBroadcast();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        socketConnected = false;

        String address = "00:A0:96:3D:93:36";
        //String address = "00:A0:96:3D:90:8C";
//        String address = "EC:FE:7E:11:86:58";


        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (mBluetoothDevice == null) {
            Log.i(TAG, "BLUETOOTH DEVICE IS NULL");

        } else {

            Log.i(TAG, "Setting up connection....");
            // SetupConnection();


            try {
                mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e4) {
                // TODO Auto-generated catch block
                e4.printStackTrace();
            }
            Log.i(TAG, "got socket");
            mBluetoothAdapter.cancelDiscovery();

            try {
                mBluetoothSocket.connect();
                Log.i(TAG, "socket connected");
                Log.i(TAG, "connecting streams");
                mBluetoothOS = mBluetoothSocket.getOutputStream();
                mBluetoothIS = mBluetoothSocket.getInputStream();
                Log.i(TAG, "streams connected!");
                IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                mContext.registerReceiver(mReceiver, filter1);

                Toast notify = Toast.makeText(mContext,
                        "Connected to IMU!", Toast.LENGTH_LONG);
                notify.setGravity(Gravity.CENTER, 0, 0);
                notify.show();
                socketConnected = true;

                Handler MainActivityHandler = myHandler;

                String command = "invainvginv4";

                Message msg = new Message();
                msg.obj = command;
                MainActivityHandler.sendMessage(msg);

                mBluetoothDataReader = new IMUBluetoothDataReader();
                mBluetoothDataReader.execute(mBluetoothIS);
                Global.SensorLogStatus = 1;

            } catch (IOException e) {
                Log.i(TAG, "failed to connect socket");
                Toast notify = Toast.makeText(mContext,
                        "IMU not detected!", Toast.LENGTH_LONG);


                sendDisconnectBroadcast();
                Global.SensorLogStatus = 0;
                try {
                    if (mBluetoothIS != null)
                        mBluetoothIS.close();
                    if (mBluetoothOS != null)
                        mBluetoothOS.close();
                    if (mBluetoothSocket != null)
                        mBluetoothSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }


        }
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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Log.e("BLUETOOTH","Disconnected");
                Toast disconnect = Toast.makeText(context, "Sensor 1 Disconnected!", Toast.LENGTH_LONG);
                disconnect.setGravity(Gravity.CENTER, 0, 0);
                disconnect.show();

            }
        }
    };

    public void stop(){
        Log.i(TAG, "Destroy");
        Toast notify = Toast.makeText(mContext,
                "Disconnected to IMU!", Toast.LENGTH_LONG);
        sendDisconnectBroadcast();
        notify.setGravity(Gravity.CENTER, 0, 0);
        notify.show();
        if (socketConnected) {
            sendCommands("invx");
            if (!(mBluetoothDataReader.isCancelled())
                    && mBluetoothDataReader.running) {
                mBluetoothDataReader.running = false;
                mBluetoothDataReader.cancel(true);
                // mBluetoothDataReader = null;
            }
            try {
                mBluetoothOS.close();
                mBluetoothIS.close();
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.i("MainActivity", "exception thrown on close");
            }
            mBluetoothSocket = null;
            socketConnected = false;
        }
    }


    private void sendCommands(String command) {
        try {
            if (socketConnected) {
                byte[] cmd = command.getBytes();
                for (int ii = 0; ii < cmd.length; ii++) {
                    mBluetoothOS.write(cmd[ii]);
                    long currentTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - currentTime < 50) {
                        ;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler myHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            String command = (String)msg.obj;
            long currentTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - currentTime < 2000) {
                ;
            }
            sendCommands(command);
            Log.i(tag, "in command : " + command);
        };
    };



}
