package com.syte.listeners;

import android.location.Location;

/**
 * Created by khalid.p on 26-12-2015.
 */
public interface OnLocationListener
    {
        public void onLastLocationKnown(Location lastKnownLoc);
        public void onLocationUpdate(Location updatedLoc);
        public void onLocationManagerConnected();
    }
