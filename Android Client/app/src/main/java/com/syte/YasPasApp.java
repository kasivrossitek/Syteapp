package com.syte;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.firebase.client.Firebase;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.syte.listeners.OnLocationListener;
import com.syte.utils.GoogleApiHelper;

//import android.support.multidex.MultiDex;

/**
 * Created by kumar.m on 16-07-2015.
 * Modified by khalid.p on 26-12-2015.
 */
public class YasPasApp extends Application
    {
        private GoogleApiHelper googleApiHelper;
        private static YasPasApp YASPASAPP_INSTANCE;
        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
           MultiDex.install(this);
        }
        @Override
        public void onCreate()
            {
                super.onCreate();
                Firebase.setAndroidContext(this);
                initImageLoader(getApplicationContext());
                YASPASAPP_INSTANCE = this;
            }
        public static void initImageLoader(Context context)
            {
                //https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info
                ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
                config.threadPriority(Thread.NORM_PRIORITY - 2);
                config.denyCacheImageMultipleSizesInMemory();
                config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
                config.diskCacheSize(50 * 1024 * 1024);
                config.tasksProcessingOrder(QueueProcessingType.LIFO);
                config.writeDebugLogs(); // Remove for release app
                config.threadPoolSize(1);

                // Initialize ImageLoader with configuration.
                ImageLoader.getInstance().init(config.build());
            }
        public static synchronized YasPasApp getInstance()
            {
                return YASPASAPP_INSTANCE;
            }
        public GoogleApiHelper getGoogleApiHelperInstance(Context paramCon,OnLocationListener paramLocHand)
            {
                googleApiHelper = new GoogleApiHelper(paramCon, paramLocHand);
                return this.googleApiHelper;
            }
        public static GoogleApiHelper getGoogleApiHelper(Context paramCon, OnLocationListener paramLocHand)
            {
                return getInstance().getGoogleApiHelperInstance(paramCon, paramLocHand);
            }

    }
