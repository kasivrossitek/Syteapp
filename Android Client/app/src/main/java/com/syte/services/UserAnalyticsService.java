package com.syte.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;


import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.syte.models.UserAnalytics;
import com.syte.utils.StaticUtils;

import java.lang.reflect.Field;

/**
 * */
public class UserAnalyticsService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param  Used to name the worker thread, important only for debugging.
     */
    public UserAnalyticsService() {
        super("UserAnalyticsService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("android : ").append(Build.VERSION.RELEASE);
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String fieldName="";
        for (Field field : fields) {
            fieldName = field.getName();}
        UserAnalytics userAnalytics =new UserAnalytics();
        userAnalytics.setOsName("Android");
        userAnalytics.setOsVersion(Build.VERSION.RELEASE);
        userAnalytics.setOsVersionName(fieldName);
        userAnalytics.setUserLatitude(intent.getExtras().getDouble("lat"));
        userAnalytics.setUserLongitude(intent.getExtras().getDouble("long"));
        userAnalytics.setActivityTimeStamp(ServerValue.TIMESTAMP);
        Firebase firebaseUserAnalytics = new Firebase(StaticUtils.USER_ANALYTICS_URL);
        firebaseUserAnalytics.push().setValue(userAnalytics);


    }
}
