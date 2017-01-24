package com.winlab.selfdrivingloggingtool.context;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.Global;

public class ActivityDetectService extends IntentService {

    String tag = "GoogleActivityService";



    private Location lastLocation = null;


    public ActivityDetectService() {
        super("not important");
    }

    protected void onHandleIntent(Intent intent)
    {
        Log.i(tag,"ActivityDetectService");
        //  Toast.makeText(this, "onHandleintent", Toast.LENGTH_SHORT).activityRecognitionIntentServiceshow();
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent); // Put your application specific logic here (i.e. result.getMostProbableActivity())
            DetectedActivity detActivity = result.getMostProbableActivity();
            String msg = "";
            switch (detActivity.getType()) {
                case DetectedActivity.ON_BICYCLE:
                    msg = "ON BICYCLE";
                    break;
                case DetectedActivity.ON_FOOT:
                    msg = "ON FOOT";
                    break;
                case DetectedActivity.IN_VEHICLE:
                    msg = "DRIVING";
                    break;
                case DetectedActivity.RUNNING:
                    msg = "RUNNING";
                    break;
                case DetectedActivity.STILL:
                    msg = "STILL";
                    break;
                case DetectedActivity.TILTING:
                    msg = "TILTING";
                    break;
                case DetectedActivity.WALKING:
                    msg = "WALKING";
                    break;
                case DetectedActivity.UNKNOWN:
                    msg = "UNKNOWN";
                    break;
            }

            Long tsLong = System.currentTimeMillis();
            Intent localIntent = new Intent(Global.BROADCAST_GOOGLE_ACTIVITY)
                    .putExtra(Global.EXTENDED_DATA_GOOGLE_ACTIVITY_MESSAGE, msg)
                    .putExtra(Global.EXTENDED_DATA_GOOGLE_ACTIVITY_CODE, detActivity.getType())
                    .putExtra(Global.EXTENDED_DATA_GOOGLE_ACTIVITY_CONFIDENCE, detActivity.getConfidence())
                    .putExtra(Global.EXTENDED_DATA_TIMETAG,tsLong);

            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            Log.i(tag,msg);

        }
    }


}
