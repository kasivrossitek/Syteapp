package com.syte.listeners;

/**
 * Created by Administrator on 05-04-2016.
 */
public interface OnPermissionRequestListener
    {
        public void onPermissionRequest(String paramPermission, boolean isPermissionDeniedBefore, int paramRequestCode);
    }
