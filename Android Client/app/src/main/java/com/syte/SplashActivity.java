package com.syte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.syte.activities.HomeActivity;

import com.syte.activities.SignUpActivity;
import com.syte.activities.walkthrough.WalkThroughBeforeLoginActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnLocationListener;
import com.syte.listeners.OnPermissionRequestListener;
import com.syte.services.UserAnalyticsService;

import com.syte.utils.GoogleApiHelper;
import com.syte.utils.LoginStatus;
import com.syte.utils.NetworkStatus;
import com.syte.utils.PermissionManager;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity implements OnClickListener, OnLocationListener, OnCustomDialogsListener, OnPermissionRequestListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private TextView mTvGetStart;
    private GoogleApiHelper mGoogleApiHelper;
    private String mCountryCode;
    private YasPasPreferences mYasPasPref;
    private NetworkStatus mNetworkStatus;
    private Bundle mBun;
    private ProgressDialog mPrgDia;
    private PermissionManager mPermissionManager;
    public static boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Firebase.setAndroidContext(this);
        mGoogleApiHelper = YasPasApp.getGoogleApiHelper(SplashActivity.this, this);
        if (!DEBUG)
            Crittercism.initialize(getApplicationContext(), "e850f97c1b6b4940abd3c1e3e83bd3cb00555300");
        mBun = new Bundle();

        try {
            // if activity is initiated by push notification
            // Checking if it is chat push notification, if yes then putting extra values
            if (getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_CHAT) {
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_ID, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_ID));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME));
            }
            mBun.putInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT, getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT));
        } catch (Exception e) {
            // if activity is initiated not by push notification, but from app drawer
            mBun.putInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT, StaticUtils.HOME_STARTING_FRAG_MAP);
        }


        mYasPasPref = YasPasPreferences.GET_INSTANCE(SplashActivity.this);
        mNetworkStatus = new NetworkStatus(SplashActivity.this);
        mInitializeWidgets();
    }// END onCreate()

    private void mInitializeWidgets() {
        mTvGetStart = (TextView) findViewById(R.id.xTvGetStart);
        mTvGetStart.setOnClickListener(this);
    } // END mInitializeWidgets()

    /***
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }// END checkPlayServices()

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startApp() {
        // Checking if user is already logged in
        if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.COMPLETED)) {
            //User is logged in
            mTvGetStart.setVisibility(View.GONE);
        } else {
            //User is not logged in
            mTvGetStart.setVisibility(View.VISIBLE);
        }
        if (mNetworkStatus.isNetworkAvailable()) {
            if (StaticUtils.IS_GPS_TURNED_ON(SplashActivity.this)) {
                if (checkPlayServices()) {
                    mGoogleApiHelper.sGoogleApiConnect();
                }
            } else {
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SplashActivity.this, this);
                customDialogs.sShowDialog_Common(YasPasMessages.NO_GPS_SUBJECT, YasPasMessages.NO_GPS_HEADING, YasPasMessages.NO_GPS_BODY, "NO", "YES", "GPS", true, false);
            }
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SplashActivity.this, this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionManager = new PermissionManager(this, this);
            if (checkMMPermissions() == 0) {
                startApp();
            }
        } else {
            startApp();
        }

    }// END onResume()

    @Override
    public void onClick(View v) {
        if (v == mTvGetStart) {

            if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.OTP)) {
                Intent mInt_MNES = new Intent(SplashActivity.this, WalkThroughBeforeLoginActivity.class);
                mBun.putString("keyCC", mCountryCode);
                mInt_MNES.putExtras(mBun);
                startActivity(mInt_MNES);
                finish();
            } else if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.SIGNUP)) {
                Intent mInt_SignUp = new Intent(SplashActivity.this, SignUpActivity.class);
                mBun.putString("keyCC", mCountryCode);
                mInt_SignUp.putExtras(mBun);
                startActivity(mInt_SignUp);
                finish();
            } else {

                Intent mInt_Map = new Intent(SplashActivity.this, HomeActivity.class);
                mInt_Map.putExtras(mBun);
                startActivity(mInt_Map);
                finish();


                                /*// Updating user's name and his profile pic, if updated from other phone using same registered number
                                Firebase mYasPaseeUrl = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum());
                                mYasPaseeUrl.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        YasPasee objYasPasee = dataSnapshot.getValue(YasPasee.class);
                                        mYasPasPref.sSetUserName(objYasPasee.getUserName());
                                        mYasPasPref.sSetUserProfilePic(objYasPasee.getUserProfilePic());

                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {


                                    }
                                });*/


            }
        }
    }// END onClick()

    private void mSaveCountryCode(double paramLat, double paramLong) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(paramLat, paramLong, 1);

            mCountryCode = addresses.get(0).getCountryCode();

            Log.e("mCountryCode", "" + mCountryCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// END mSaveCountryCode()

    @Override
    public void onLastLocationKnown(Location lastKnownLoc) {
        if (lastKnownLoc != null) {
            mBun.putDouble("ipcCurrentLat", lastKnownLoc.getLatitude());
            mBun.putDouble("ipcCurrentLong", lastKnownLoc.getLongitude());
            if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.OTP) || mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.SIGNUP))
                mSaveCountryCode(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());
            mGoogleApiHelper.sGoogleApiDisconnect();
            sendUserAnalytics(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());

            if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.COMPLETED)) {
                sendUserAnalytics(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());
                goToHomeActivity();
            }
        } else {
            mPrgDia = new ProgressDialog(SplashActivity.this);
            mPrgDia.setCancelable(false);
            mPrgDia.setMessage(getString(R.string.prg_bar_wait));
            mPrgDia.show();
            mGoogleApiHelper.sStartLocationUpdate(1, 1, 1);
        }
    }// END onLastLocationKnown()

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiHelper.sGoogleApiDisconnect();

    }

    @Override
    public void onLocationUpdate(Location updatedLoc) {
        mBun.putDouble("ipcCurrentLat", updatedLoc.getLatitude());
        mBun.putDouble("ipcCurrentLong", updatedLoc.getLongitude());
        mPrgDia.dismiss();
        if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.OTP) || mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.SIGNUP))
            mSaveCountryCode(updatedLoc.getLatitude(), updatedLoc.getLongitude());
        mGoogleApiHelper.sGoogleApiDisconnect();
        sendUserAnalytics(updatedLoc.getLatitude(), updatedLoc.getLongitude());
        if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.COMPLETED)) {
            sendUserAnalytics(updatedLoc.getLatitude(), updatedLoc.getLongitude());
            goToHomeActivity();
        }
    }// END onLocationUpdate()

    @Override
    public void onLocationManagerConnected() {

    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("GPS") && paramIsFinish) {
            finish();
        } else if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            finish();
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("GPS")) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

    }

    private void goToHomeActivity() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mInt_Map = new Intent(SplashActivity.this, HomeActivity.class);
                mInt_Map.putExtras(mBun);
                startActivity(mInt_Map);
                finish();

            }
        }, 2000);

    }

    @Override
    public void onPermissionRequest(String paramPermission, boolean isPermissionDeniedBefore, int paramRequestCode) {
        if (isPermissionDeniedBefore) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{paramPermission}, paramRequestCode);
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{paramPermission}, paramRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_PHONE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        /*if(checkMMPermissions()==0)
                                            {
                                                startApp();
                                            }*/

                } else {
                    finish();
                }
            }
            break;
            case PermissionManager.PERMISSION_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        /*if(checkMMPermissions()==0)
                                            {
                                                startApp();
                                            }*/

                } else {
                    finish();
                }
            }
            break;
            case PermissionManager.PERMISSION_STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        /*if(checkMMPermissions()==0)
                                            {
                                                startApp();
                                            }*/

                } else {
                    finish();
                }
                break;
            }
            case PermissionManager.PERMISSION_SMS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                    /*if(checkMMPermissions()==0)
                                        {
                                            startApp();
                                        }*/

                } else {
                    finish();
                }
                break;
            }
            default:
                break;
        }
    }

    private int checkMMPermissions() {
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_PHONE))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_PHONE, PermissionManager.PERMISSION_PHONE_REQUEST_CODE);
            return PermissionManager.PERMISSION_PHONE_REQUEST_CODE;
        }
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_LOCATION))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_LOCATION, PermissionManager.PERMISSION_LOCATION_REQUEST_CODE);
            return PermissionManager.PERMISSION_LOCATION_REQUEST_CODE;
        }
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_STORAGE))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_STORAGE, PermissionManager.PERMISSION_STORAGE_REQUEST_CODE);
            return PermissionManager.PERMISSION_STORAGE_REQUEST_CODE;
        }
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_SMS))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_SMS, PermissionManager.PERMISSION_SMS_REQUEST_CODE);
            return PermissionManager.PERMISSION_SMS_REQUEST_CODE;
        }
        return 0;
    }

    private void sendUserAnalytics(double paramLat, double paramLong) {
        Intent userAnalytics = new Intent(SplashActivity.this, UserAnalyticsService.class);
        userAnalytics.putExtra("lat", paramLat);
        userAnalytics.putExtra("long", paramLong);
        startService(userAnalytics);
    }
}

