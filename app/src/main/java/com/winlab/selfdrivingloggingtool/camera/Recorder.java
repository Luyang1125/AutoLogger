package com.winlab.selfdrivingloggingtool.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cagda on 9/5/2016.
 */
public class Recorder  implements SurfaceHolder.Callback
{
    SurfaceHolder  mHolder;
    private RecorderThread mRecorderThread;
    private boolean mHasSurface;
    private File folder;


    public Recorder( SurfaceView sf){

        mHolder = sf.getHolder();
        mHolder.addCallback(this);
        mHasSurface = true;
        mHolder.setSizeFromLayout();


    }

    public void setFoler(File folder_main){
        this.folder = folder_main;
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mHasSurface = false;
        pause();
    }



    public void resume() {
        // We do the actual acquisition in a separate thread. Create it now.
        Log.i("Recorder","Thread null ?" + (mRecorderThread == null));

        if (mRecorderThread == null) {
            mRecorderThread = new RecorderThread();
            // If we already have a surface, just start the thread now too.
            if (mHasSurface == true) {
                mRecorderThread.start();
            }
        }
    }

    public void pause() {
        // Stop Preview.
        if (mRecorderThread != null) {
            mRecorderThread.requestExitAndWait();
            mRecorderThread = null;
        }
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, start our main acquisition thread.
        mHasSurface = true;
        if (mRecorderThread != null) {
            mRecorderThread.start();
        }
    }



    class RecorderThread extends Thread {
        private boolean mDone;
        MediaRecorder m;
        Camera camera;

        RecorderThread () {
            super();
            mDone = false;
        }

        @Override
        public void run() {

            // We first open the CameraDevice and configure it.
            camera = Camera.open();
            if (camera != null) {
                Camera.Parameters param = camera.getParameters();
                param.setPreviewSize(1280,960);
                camera.setParameters(param);
                camera.setDisplayOrientation(0);
                try {
                    camera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
                camera.unlock();
                m = new MediaRecorder();
                m.setCamera(camera);
                m.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                m.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                m.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));


                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File mediaFile = new File(folder.getPath() + File.separator +
                        "VID_"+ timeStamp +"_"+System.currentTimeMillis()+ ".mp4");

                m.setOutputFile(String.valueOf(mediaFile));

                try {
                    m.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                m.start();


            }
            // This is our main acquisition thread's loop, we go until
            // asked to quit.
            SurfaceHolder holder = mHolder;
            while (!mDone) {
                ;
            }
            // Make sure to release the CameraDevice
            if (camera != null) {
                Log.i("Recorder","exiting because mDone");
                mDone = true;
                m.stop();
                m.reset();
                m.release();
                camera.lock();
                camera.stopPreview();
                camera.release();
            }
        }

        public void requestExitAndWait() {
            // don't call this from PreviewThread thread or it a guaranteed
            // deadlock!

            Log.i("Recorder","requestExitAndWait()");
            mDone = true;

            try {
                join();
            } catch (InterruptedException ex) { }
        }
    }


}
