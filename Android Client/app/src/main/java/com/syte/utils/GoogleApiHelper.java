package com.syte.utils;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.syte.listeners.OnLocationListener;

/**
 * Created by khalid.p on 24-12-2015.
 */
public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener
    {
        private static final String TAG = GoogleApiHelper.class.getSimpleName();
        private OnLocationListener mLocHnd;
        private GoogleApiClient mGoogleApiClient;
        public GoogleApiHelper(Context mContext, OnLocationListener paramLocHand)
            {
                this.mLocHnd=paramLocHand;
                mBuildGoogleApiClient(mContext);
            }
        public GoogleApiClient sGetGoogleApiClient()
            {
                return this.mGoogleApiClient;
            }
        public void sGoogleApiConnect()
            {
                if (mGoogleApiClient != null)
                    {
                        mGoogleApiClient.connect();
                    }
            }
        public void sGoogleApiDisconnect()
            {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    {
                        mGoogleApiClient.disconnect();
                    }
            }
        public boolean sIsConnected()
            {
                if (mGoogleApiClient != null)
                    {
                        return mGoogleApiClient.isConnected();
                    }
                else
                    {
                        return false;
                    }
            }
        private void mBuildGoogleApiClient(Context mContext)
            {
                mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
            }
        @Override
        public void onConnected(Bundle bundle)
            {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLocHnd.onLocationManagerConnected();
                mLocHnd.onLastLocationKnown(location);


            }
        public void sStartLocationUpdate(int paramUpdateInterval,int paramFastestInterval,int paramDisplacement)
            {
                //int UPDATE_INTERVAL = 1000;
                //int FATEST_INTERVAL = 1000;
                //int DISPLACEMENT = 1;

                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(paramUpdateInterval);
                mLocationRequest.setFastestInterval(paramFastestInterval);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setSmallestDisplacement(paramDisplacement);

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        @Override
        public void onConnectionSuspended(int i)
            {
                Log.d(TAG, "onConnectionSuspended: googleApiClient.connect()");
                mGoogleApiClient.connect();
            }
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult)
            {
                Log.d(TAG, "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());
            }
        @Override
        public void onLocationChanged(Location location)
            {
                if(location!=null)
                    mLocHnd.onLocationUpdate(location);
            }
    }
