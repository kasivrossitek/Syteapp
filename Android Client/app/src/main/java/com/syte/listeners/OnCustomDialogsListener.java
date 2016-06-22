package com.syte.listeners;

/**
 * Created by khalid.p on 05-02-2016.
 */
public interface OnCustomDialogsListener
    {
        public void onDialogLeftBtnClicked(int paramDialogType,String paramCallingMethod,boolean paramIsFinish);
        public void onDialogRightBtnClicked(int paramDialogType,String paramCallingMethod,boolean paramIsFinish);
    }
