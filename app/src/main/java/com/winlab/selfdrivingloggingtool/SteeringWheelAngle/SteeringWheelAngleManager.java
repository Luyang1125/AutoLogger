package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

/**
 * Created by Luyang on 9/4/2016.
 */
public class SteeringWheelAngleManager {

    private IMUSensorLogging mIMUWheelAngleLogging;
    private PhoneSensorLogging mPhoneSensorLogging;
    private SteeringWheelAngleEst mSteeringWheelAngleEst;
    private DataLogger mDataLogger;

    public SteeringWheelAngleManager(){
        mIMUWheelAngleLogging = new IMUSensorLogging();
        mPhoneSensorLogging = new PhoneSensorLogging();
        mSteeringWheelAngleEst = new SteeringWheelAngleEst();
        //mDataLogger = new DataLogger();
    }

    public void start(){
        mIMUWheelAngleLogging.start();
        mPhoneSensorLogging.start();
        mSteeringWheelAngleEst.start();
        //mDataLogger.start();
    }

    public void configuration(){

    }

    public void connectIMU(){
        mIMUWheelAngleLogging.start();
    }

    public void disconnectIMU(){
        mIMUWheelAngleLogging.stop();
    }

    public void stop(){
        mIMUWheelAngleLogging.stop();
        mPhoneSensorLogging.stop();
        mSteeringWheelAngleEst.stop();
        //mDataLogger.stop();
    }

}
