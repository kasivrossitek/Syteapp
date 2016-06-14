package com.syte.listeners;

/**
 * Created by khalid.p on 21-03-2016.
 */
public interface OnTransferASyteRequest
    {
        public void onTasrStarted();
        public void onTasrErrorOccured();
        public void onTasrClaimerExistance(boolean paramIsClaimerExists);
        public void onTasrFinshed();
    }
