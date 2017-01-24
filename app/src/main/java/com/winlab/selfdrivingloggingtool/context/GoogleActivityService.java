package com.winlab.selfdrivingloggingtool.context;

/**
 * Created by Luyang on 10/6/2015.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

/**
 * Created by Luyang on 4/15/2015.
 */
public class GoogleActivityService implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    String tag = "GoogleActivityService";
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private PendingIntent callbackIntent;
    private boolean isConnected = false;

    public GoogleActivityService() {
        Log.i(tag, "Start!");
        buildGoogleApiClient();

    }

    public void start(){
        mGoogleApiClient.connect();
    }

    public void cancel(){
        Log.v(tag, "Destroyed!");
        if(mGoogleApiClient.isConnected()) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,callbackIntent);
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ApplicationHelper.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(tag, "Successfully connected to play services!");


        Intent intent = new Intent(ApplicationHelper.getAppContext(), ActivityDetectService.class);

        callbackIntent = PendingIntent.getService(ApplicationHelper.getAppContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 5000, callbackIntent);
        isConnected = true;

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(tag, "play services connection SUSPENDED!");
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(tag, "connection to play services FAILED!");
        if (mResolvingError)
            return;
        else if (connectionResult.hasResolution()) {
            Log.i(tag, "and it has resolution!");
            mResolvingError = true;
//            try {
//                connectionResult.startResolutionForResult(holder.context_, Global.REQUEST_RESOLVE_ERROR);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }


        } else {
            //Toast.makeText(ApplicationHelper.getAppContext(), "This program is fucked up!", Toast.LENGTH_LONG).show();
            mResolvingError = true;
            Log.i(tag, "no!");
        }
    }
}
