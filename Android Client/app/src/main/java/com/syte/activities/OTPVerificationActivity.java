package com.syte.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.AuthDb;
import com.syte.models.YasPasee;
import com.syte.utils.LoginStatus;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;
import com.syte.widgets.PinEntryView;

import java.util.HashMap;

/**
 * Created by Khalid on 26-10-2015.
 */
public class OTPVerificationActivity extends Activity implements View.OnClickListener, ValueEventListener, OnCustomDialogsListener {
    private TextView mTvVerify, mTvOTPResend;
    private RelativeLayout mRelLayBack;
    private PinEntryView mPEVOtp;
    private ProgressDialog mPrgDia;
    private Firebase mChildFirebaseAuthDbUrl;
    private NetworkStatus mNetworkStatus;
    private YasPasPreferences mYasPasPref;
    private OTPGeneratorListener otpGeneratorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otpGeneratorListener = new OTPGeneratorListener();
        mYasPasPref = YasPasPreferences.GET_INSTANCE(OTPVerificationActivity.this);
        mTvVerify = (TextView) findViewById(R.id.xTvVerify);
        mTvVerify.setOnClickListener(this);

        mTvOTPResend = (TextView) findViewById(R.id.xTvOTPResend);
        mTvOTPResend.setPaintFlags(mTvOTPResend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTvOTPResend.setOnClickListener(this);

        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mPEVOtp = (PinEntryView) findViewById(R.id.xPEVOtp);
        mNetworkStatus = new NetworkStatus(OTPVerificationActivity.this);
        mPrgDia = new ProgressDialog(OTPVerificationActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (v == mRelLayBack) {
            finish();
        } else if (v == mTvVerify) {
            if (mPEVOtp.getText().toString().trim().length() == 6) {
                if (mNetworkStatus.isNetworkAvailable()) {
                    mPrgDia.show();
                    mVerifyOTP();
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(OTPVerificationActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            } else {
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(OTPVerificationActivity.this, this);
                customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_veification_code_invalid), null, null, "OK", "Invalid Code", false, false);
            }
        } else if (v == mTvOTPResend) {
            if (mNetworkStatus.isNetworkAvailable()) {
                mRequestOTP();
            } else {
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(OTPVerificationActivity.this, this);
                customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
            }
        }
    } // END onClick()

    private void mVerifyOTP() {
        Firebase mFirebaseAuthDbUrl = new Firebase(StaticUtils.AUTH_DB_URL);
        mChildFirebaseAuthDbUrl = mFirebaseAuthDbUrl.child(mYasPasPref.sGetRegisteredNum());
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("recievedOtp", mPEVOtp.getText().toString().trim());
        mUpdateMap.put("modified", ServerValue.TIMESTAMP);
        mChildFirebaseAuthDbUrl.updateChildren(mUpdateMap);
        mChildFirebaseAuthDbUrl.addValueEventListener(OTPVerificationActivity.this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final AuthDb serverAuthDB = dataSnapshot.getValue(AuthDb.class);
        if (dataSnapshot.child("verificationStatus").exists()) {
            mChildFirebaseAuthDbUrl.removeEventListener(OTPVerificationActivity.this);
            Log.e("Status", "" + serverAuthDB.getVerificationStatus());
            if (serverAuthDB.getVerificationStatus().equalsIgnoreCase("PENDING")) {
                if (mPEVOtp.getText().toString().trim().length() == 6) {
                    if (mNetworkStatus.isNetworkAvailable())
                        mVerifyOTP();
                    else {
                        mPrgDia.dismiss();
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(OTPVerificationActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
                }
            } else if (serverAuthDB.getVerificationStatus().equalsIgnoreCase("EXPIRED")) {
                mPrgDia.dismiss();
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(OTPVerificationActivity.this, this);
                customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_veification_code_expired), null, "NO", "YES", "Exipred Code", true, true);
            } else if (serverAuthDB.getVerificationStatus().equalsIgnoreCase("WRONG")) {
                mPrgDia.dismiss();
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(OTPVerificationActivity.this, this);
                customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_veification_code_wrong), null, null, "OK", "Wrong Code", false, false);
            } else if (serverAuthDB.getVerificationStatus().equalsIgnoreCase("VERIFIED")) {
                Firebase mYasPaseeUrl = new Firebase(StaticUtils.YASPASEE_URL);
                Firebase mObj = mYasPaseeUrl.child(mYasPasPref.sGetRegisteredNum());
                mObj.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        YasPasee objYasPasee = dataSnapshot1.getValue(YasPasee.class);
                        mPrgDia.dismiss();
                        Toast.makeText(OTPVerificationActivity.this, "VERIFIED", Toast.LENGTH_LONG).show();
                        try {
                            if (objYasPasee.getUserName().equalsIgnoreCase("")) {
                                Intent i = new Intent(OTPVerificationActivity.this, SignUpActivity.class);
                                Bundle mBun = new Bundle();
                                mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                                mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                                i.putExtras(mBun);
                                startActivity(i);
                                finish();
                            } else {
                                mYasPasPref.sSetLoginStatus(LoginStatus.COMPLETED);
                                mYasPasPref.sSetUserName(objYasPasee.getUserName());
                                mYasPasPref.sSetUserGender(objYasPasee.getUserGender());
                                mYasPasPref.sSetUserProfilePic(objYasPasee.getUserProfilePic());

                                Bundle mBun = new Bundle();
                                mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                                mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                                Intent mIntMapActivity = new Intent(OTPVerificationActivity.this, HomeActivity.class);
                                mIntMapActivity.putExtras(mBun);
                                startActivity(mIntMapActivity);
                                finish();
                            }
                        } catch (Exception e) {
                            Intent i = new Intent(OTPVerificationActivity.this, SignUpActivity.class);
                            Bundle mBun = new Bundle();
                            mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                            mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                            i.putExtras(mBun);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            finish();
        }
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("Exipred Code") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            finish();
        }
    }// END onDialogLeftBtnClicked()

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && (paramCallingMethod.equalsIgnoreCase("Invalid Code") || (paramCallingMethod.equalsIgnoreCase("Wrong Code")))) {
            // Do nothing
        }
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("Exipred Code")) {
            Intent mIntMobNoEntryScreen = new Intent(OTPVerificationActivity.this, MobileNumberEntryActivity.class);
            mIntMobNoEntryScreen.putExtra("keyCC", getIntent().getExtras().getString("keyCC"));
            startActivity(mIntMobNoEntryScreen);
            finish();
        }
    }// END onDialogRightBtnClicked()

    private void mRequestOTP() {
        mPrgDia.show();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mDeviceId = telephonyManager.getDeviceId();
        if (mDeviceId == null) {
            mDeviceId = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        AuthDb mAuthDb = new AuthDb();
        mAuthDb.setRegisteredNum(mYasPasPref.sGetRegisteredNum());
        mAuthDb.setDeviceId(mDeviceId.trim());
        mAuthDb.setCountryCode((getIntent().getExtras().getString("keyCC").toString().equalsIgnoreCase("IN")) ? "+91" : "+1");
        mAuthDb.setDeviceToken(mYasPasPref.sGetGCMToken());
        Firebase mFirebaseAuthDbUrl = new Firebase(StaticUtils.AUTH_DB_URL);
        mChildFirebaseAuthDbUrl = mFirebaseAuthDbUrl.child(mAuthDb.getRegisteredNum());
        mChildFirebaseAuthDbUrl.removeValue();
        mChildFirebaseAuthDbUrl.setValue(mAuthDb, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                try {
                    mChildFirebaseAuthDbUrl.removeEventListener(otpGeneratorListener); // if any
                } catch (Exception e) {
                }
                mChildFirebaseAuthDbUrl.addValueEventListener(otpGeneratorListener);
                mPrgDia.dismiss();
            }
        });
    } // END mRequestOTP()

    private class OTPGeneratorListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.child("verificationStatus").exists()) {
                mChildFirebaseAuthDbUrl.removeEventListener(otpGeneratorListener);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }// END OTPListener
}