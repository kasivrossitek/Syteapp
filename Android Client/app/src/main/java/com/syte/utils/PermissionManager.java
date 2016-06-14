package com.syte.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.syte.listeners.OnPermissionRequestListener;

/**
 * Created by kumar.m on 24-03-2016.
 */
public class PermissionManager
{
    private Context mContext;
    private OnPermissionRequestListener mOnPermissionRequestListener;

    //Permissions
    public static final String PERMISSION_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_SMS = Manifest.permission.SEND_SMS;

    //Permission Request codes
    public static final int PERMISSION_PHONE_REQUEST_CODE = 50;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 60;
    public static final int PERMISSION_STORAGE_REQUEST_CODE = 70;
    public static final int PERMISSION_SMS_REQUEST_CODE = 80;


    public PermissionManager(Context paramContext, OnPermissionRequestListener paramOnPermissionRequestListener)
    {
        this.mContext = paramContext;
        this.mOnPermissionRequestListener = paramOnPermissionRequestListener;
    }


    public boolean isPermissionGranted(String paramPermission)
    {
        return ContextCompat.checkSelfPermission(mContext, paramPermission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String paramPermission, int paramRequestCode)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, paramPermission))
        {
            //User already denied the permission.
            mOnPermissionRequestListener.onPermissionRequest(paramPermission, true, paramRequestCode);
        }
        else
        {
            mOnPermissionRequestListener.onPermissionRequest(paramPermission, false, paramRequestCode);
        }
    }

}
