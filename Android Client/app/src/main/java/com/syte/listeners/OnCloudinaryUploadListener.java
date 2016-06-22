package com.syte.listeners;

import java.util.ArrayList;

/**
 * Created by khalid.p on 25-01-2016.
 */
public interface OnCloudinaryUploadListener
    {
        public void onUploadStarted();
        public void onUploadCompleted(ArrayList<String> paramCloudinaryIDs, String paramCallingMethod);
    }
