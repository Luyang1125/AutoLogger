package com.winlab.selfdrivingloggingtool.speed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.winlab.selfdrivingloggingtool.SteeringWheelAngle.Global;
import com.winlab.selfdrivingloggingtool.utils.ApplicationHelper;

/**
 * Created by Luyang on 10/6/2015.
 */
public class GoogleLocationService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private String tag = "GoogleLocationService";

    private Context mContext = ApplicationHelper.getAppContext();

    public GoogleLocationService() {
        Log.i(tag, "Create!");
        buildGoogleApiClient();
    }

    public void start() {

        Log.i(tag, "Connected!");
        mGoogleApiClient.connect();
    }

    public void cancel() {
        if (mGoogleApiClient.isConnected()) {
            Log.v(tag, "Destroyed!");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            return;
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        Long tsLong = System.currentTimeMillis();
        Log.i(tag, "Speed is " + location.getSpeed() + "(Provider:"+location.getProvider()+")");

        Intent intent = new Intent(Global.BROADCAST_LOCATION)
                .putExtra(Global.EXTENDED_DATA_LAT, location.getLatitude())
                .putExtra(Global.EXTENDED_DATA_LNG, location.getLongitude())
                .putExtra(Global.EXTENDED_DATA_SPEED, location.getSpeed())
                .putExtra(Global.EXTENDED_DATA_TIMETAG, tsLong);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
