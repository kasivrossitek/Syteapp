package com.syte.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syte.R;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.AuthDb;
import com.syte.models.YasPasee;
import com.syte.services.RegistrationIntentService;
import com.syte.utils.LoginStatus;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;


/**
 * Created by atulanand on 26-10-2015.
 * Modified by Khalid on 29-12-2015
 */
public class MobileNumberEntryActivity extends Activity implements View.OnClickListener, ValueEventListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack;
    private TextView mTvNext, mTvCountryCode;
    private EditText mEtPhNo;
    private YasPasPreferences mYasPasPref;
    private int mCountryCode;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private Firebase mChildFirebaseAuthDbUrl;
    private InputMethodManager mInputManager;
    private RegistrationReceiver mRegistrationReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRegistrationReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationReceiver);
        }
    }// END onDestroy()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_no_entry);
        mPrgDia = new ProgressDialog(MobileNumberEntryActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mInitWidgets();
        mYasPasPref = YasPasPreferences.GET_INSTANCE(MobileNumberEntryActivity.this);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInitCountry();
        mNetworkStatus = new NetworkStatus(MobileNumberEntryActivity.this);
    }

    private void mInitCountry() {
        int maxLength = 0;
        InputFilter[] FilterArray = new InputFilter[1];
        try {
            if (getIntent().getExtras().getString("keyCC").equalsIgnoreCase("IN")) {
                mTvCountryCode.setText("+91");
                maxLength = 10;
                mCountryCode = 1;
            } else if (getIntent().getExtras().getString("keyCC").equalsIgnoreCase("US")) {
                mTvCountryCode.setText("+1");
                maxLength = 12;
                mCountryCode = 2;
                mEtPhNo.addTextChangedListener(MyTextWatcher);
            }
        } catch (Exception e) {
            mTvCountryCode.setText("+91");
            maxLength = 10;
            mCountryCode = 1;
        }
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        mEtPhNo.setFilters(FilterArray);
    }// END mInitCountry()

    private void mInitWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mTvNext = (TextView) findViewById(R.id.xTvNext);
        mTvNext.setOnClickListener(this);
        mEtPhNo = (EditText) findViewById(R.id.xEtPhNo);
        mTvCountryCode = (TextView) findViewById(R.id.xTvCountryCode);
        mTvCountryCode.setOnClickListener(this);
    }// END mInitWidgets()

    protected TextWatcher MyTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEtPhNo.getText().length() == 4 || mEtPhNo.getText().length() == 8) {
                String tempString = mEtPhNo.getText().toString() + "-";
                char c = tempString.charAt(tempString.length() - 2);
                if (c != '-') {
                    char[] stringArray = tempString.toCharArray();
                    stringArray[tempString.length() - 2] = stringArray[tempString.length() - 1];
                    stringArray[tempString.length() - 1] = c;
                    tempString = new String(stringArray);
                    mEtPhNo.setText(tempString);
                    mEtPhNo.setSelection(tempString.length());

                }
            }
        }
    }; // END MyTextWatcher

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xTvNext: {
                if (mValidateMobileNumber()) {
                    if (mNetworkStatus.isNetworkAvailable()) {
                        if (mYasPasPref.sGetGCMToken().equalsIgnoreCase("")) {
                            // GCM Token is not generated
                            mPrgDia.show();
                            mRegistrationReceiver = new RegistrationReceiver();
                            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationReceiver, new IntentFilter("GCM_REGISTRATION_COMPLETED"));
                            // Start IntentService to register this application with GCM.
                            Intent intent = new Intent(this, RegistrationIntentService.class);
                            startService(intent);
                        } else {
                            // GCM Token is already generated
                            mPrgDia.show();
                            mRequestOTP();
                        }
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(MobileNumberEntryActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
                }

                break;
            }
            case R.id.xTvCountryCode: {
                mShowCountryCodeDialog();
                break;
            }
            default: {
                break;
            }
        }
    }// END onClick()

    private boolean mValidateMobileNumber() {
        if (mEtPhNo.getText().toString().trim().length() <= 0) {
            mEtPhNo.setError(getString(R.string.err_msg_mob_num_empty));
            StaticUtils.REQUEST_FOCUS(mEtPhNo, mInputManager);
            return false;
        }
        if (mCountryCode == 1 && mEtPhNo.getText().toString().trim().length() > 0 && mEtPhNo.getText().toString().trim().length() < 10) {
            mEtPhNo.setError(getString(R.string.err_msg_mob_num_invalid));
            StaticUtils.REQUEST_FOCUS(mEtPhNo, mInputManager);
            return false;
        }
        if (mCountryCode == 2 && mEtPhNo.getText().toString().trim().length() > 0 && mEtPhNo.getText().toString().trim().length() < 12) {
            mEtPhNo.setError(getString(R.string.err_msg_mob_num_invalid));
            StaticUtils.REQUEST_FOCUS(mEtPhNo, mInputManager);
            return false;
        }

        mYasPasPref.sSetRegisteredNum(mEtPhNo.getText().toString().trim().replace("-", ""));
        return true;
    }

    private void mShowCountryCodeDialog() {
        final Dialog mDialog = new Dialog(MobileNumberEntryActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_country_code_dialog);
        mDialog.show();
        Window window = mDialog.getWindow();
        final TextView mImg_indiaflage = (TextView) window.findViewById(R.id.xImg_indiaflage);
        mImg_indiaflage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvCountryCode.setText("+91");
                mEtPhNo.setText("");
                mCountryCode = 1;
                mEtPhNo.removeTextChangedListener(MyTextWatcher);
                int maxLength = 10;
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                mEtPhNo.setFilters(FilterArray);
                mDialog.dismiss();
            }
        });
        final TextView mImg_Usa = (TextView) window.findViewById(R.id.xImg_Usa);
        mImg_Usa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvCountryCode.setText("+1");
                mCountryCode = 2;
                mEtPhNo.setText("");
                mEtPhNo.removeTextChangedListener(MyTextWatcher);
                mEtPhNo.addTextChangedListener(MyTextWatcher);
                int maxLength = 12;
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                mEtPhNo.setFilters(FilterArray);
                mDialog.dismiss();

            }
        });
    }

    private void mRequestOTP() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mDeviceId = telephonyManager.getDeviceId();
        if (mDeviceId == null) {
            mDeviceId = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        AuthDb mAuthDb = new AuthDb();
        if (mCountryCode == 2) {
            mAuthDb.setRegisteredNum(mEtPhNo.getText().toString().replace("-", "").trim());
        } else {
            mAuthDb.setRegisteredNum(mEtPhNo.getText().toString().trim());
        }
        mAuthDb.setDeviceId(mDeviceId.trim());
        mAuthDb.setCountryCode((mCountryCode == 1) ? "+91" : "+1");
        mAuthDb.setDeviceToken(mYasPasPref.sGetGCMToken());

        Firebase mFirebaseAuthDbUrl = new Firebase(StaticUtils.AUTH_DB_URL);
        mChildFirebaseAuthDbUrl = mFirebaseAuthDbUrl.child(mAuthDb.getRegisteredNum());
        mChildFirebaseAuthDbUrl.removeValue();
        mChildFirebaseAuthDbUrl.setValue(mAuthDb, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mChildFirebaseAuthDbUrl.addValueEventListener(MobileNumberEntryActivity.this);

            }
        });
    } // END mRequestOTP()

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("verificationStatus").exists()) {
            mChildFirebaseAuthDbUrl.removeEventListener(MobileNumberEntryActivity.this);
            final AuthDb serverAuthDB = dataSnapshot.getValue(AuthDb.class);
            if (serverAuthDB.getVerificationStatus().equalsIgnoreCase("VERIFIED")) {
                Firebase firebaseYasPasee = new Firebase(StaticUtils.YASPASEE_URL).child(serverAuthDB.getRegisteredNum());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("deviceToken", mYasPasPref.sGetGCMToken());
                firebaseYasPasee.updateChildren(hashMap, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        mSetUserPreferences(serverAuthDB.getRegisteredNum());
                    }
                });
            } else {
                if (mPrgDia != null) {
                    mPrgDia.dismiss();
                }
                Intent i = new Intent(MobileNumberEntryActivity.this, OTPVerificationActivity.class);
                Bundle mBun = new Bundle();
                mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                mBun.putString("keyCC", mCountryCode == 1 ? "IN" : "US");
                i.putExtras(mBun);
                startActivity(i);
                finish();
            }
        } else {
            mPrgDia.dismiss();
            Toast.makeText(MobileNumberEntryActivity.this, YasPasMessages.SERVER_NOT_REACHABLE, Toast.LENGTH_LONG).show();
        }
    }// END onDataChange()

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            finish();
        }
    }// END onDialogLeftBtnClicked()

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }// END onDialogRightBtnClicked()

    private void mSetUserPreferences(final String paramRegisteredNum) {
        Firebase mYasPaseeUrl = new Firebase(StaticUtils.YASPASEE_URL).child(paramRegisteredNum);
        mYasPaseeUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                YasPasee objYasPasee = dataSnapshot.getValue(YasPasee.class);
                try {
                    if (objYasPasee.getUserName().equalsIgnoreCase("")) {
                        mYasPasPref.sSetLoginStatus(LoginStatus.SIGNUP);
                        mYasPasPref.sSetRegisteredNum(paramRegisteredNum);
                        Intent i = new Intent(MobileNumberEntryActivity.this, SignUpActivity.class);
                        Bundle mBun = new Bundle();
                        mBun.putString("keyCC", mCountryCode == 1 ? "IN" : "US");
                        mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                        mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                        i.putExtras(mBun);
                        if (mPrgDia != null) {
                            mPrgDia.dismiss();
                        }
                        startActivity(i);
                        finish();
                    } else {
                        mYasPasPref.sSetLoginStatus(LoginStatus.COMPLETED);
                        mYasPasPref.sSetUserName(objYasPasee.getUserName());
                        mYasPasPref.sSetUserGender(objYasPasee.getUserGender());
                        mYasPasPref.sSetUserProfilePic(objYasPasee.getUserProfilePic());
                        mYasPasPref.sSetRegisteredNum(paramRegisteredNum);
                        Intent mInt_Map = new Intent(MobileNumberEntryActivity.this, HomeActivity.class);
                        Bundle mBun = new Bundle();
                        mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                        mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                        mInt_Map.putExtras(mBun);
                        if (mPrgDia != null) {
                            mPrgDia.dismiss();
                        }
                        startActivity(mInt_Map);
                        finish();
                    }
                } catch (Exception e) {
                    mYasPasPref.sSetLoginStatus(LoginStatus.SIGNUP);
                    mYasPasPref.sSetRegisteredNum(paramRegisteredNum);
                    Intent i = new Intent(MobileNumberEntryActivity.this, SignUpActivity.class);
                    Bundle mBun = new Bundle();
                    mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                    mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                    i.putExtras(mBun);
                    if (mPrgDia != null) {
                        mPrgDia.dismiss();
                    }
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (mPrgDia != null) {
                    mPrgDia.dismiss();
                }
            }
        });
    } // END mSetUserPreferences()

    public class RegistrationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mYasPasPref.sGetGCMToken().equalsIgnoreCase("")) {
                mRequestOTP();
            } else {
                mPrgDia.dismiss();
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(MobileNumberEntryActivity.this, MobileNumberEntryActivity.this);
                customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "NoGCMToken", false, false);
            }
        }
    }// END RegistrationReceiver
}
