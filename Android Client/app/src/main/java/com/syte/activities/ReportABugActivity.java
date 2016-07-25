package com.syte.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.syte.BuildConfig;
import com.syte.R;
import com.syte.YasPasApp;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.ReportBug;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

/**
 * Created by khalid.p on 18-03-2016.
 */
public class ReportABugActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack;
    private TextView mTvSave, mTvWrdLft, mTvModelName, mTvModelversion, mTvAppVersionName, mTvAppBuildName;
    private EditText mEtAboutUsTitle, mEtAboutUsBody;
    private InputMethodManager mInputManager;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private YasPasPreferences yasPasPreferences;

    private void mPostReport() {
        Firebase mObjFireBaseReportBug = new Firebase(StaticUtils.REPORT_A_BUG);
        ReportBug reportBug = new ReportBug();
        reportBug.setTitle(mEtAboutUsTitle.getText().toString().trim());
        reportBug.setDescription(mEtAboutUsBody.getText().toString().trim());
        reportBug.setmRegisteredNum(yasPasPreferences.sGetRegisteredNum());
        reportBug.setReplied(0);
        reportBug.setUserName(yasPasPreferences.sGetUserName());
        reportBug.setDeviceModel(mTvModelName.getText().toString());
        reportBug.setDeviceModelVersion(mTvModelversion.getText().toString());
        reportBug.setAppVersionName(mTvAppVersionName.getText().toString());
        reportBug.setAppBuildversion(mTvAppBuildName.getText().toString());
        reportBug.setDateTime(ServerValue.TIMESTAMP);
        mObjFireBaseReportBug.push().setValue(reportBug, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mPrgDia.dismiss();
                if (firebaseError == null) {
                    mEtAboutUsTitle.setText("");
                    mEtAboutUsBody.setText("");
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(ReportABugActivity.this, ReportABugActivity.this);
                    customDialogs.sShowDialog_Common("Thankyou!", getString(R.string.err_report_a_bug_page_bug_reported), null, null, "OK", "RB", false, false);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(ReportABugActivity.this, ReportABugActivity.this);
                    customDialogs.sShowDialog_Common(null, getString(R.string.err_report_a_bug_page_bug_not_reported), null, null, "OK", "RB", false, false);
                }
            }
        });
    }

    private boolean mValidateData() {
        if (mEtAboutUsTitle.getText().toString().trim().length() <= 0) {
            mEtAboutUsTitle.setError(getString(R.string.err_report_a_bug_page_empty_title));
            StaticUtils.REQUEST_FOCUS(mEtAboutUsTitle, mInputManager);
            return false;
        }
        if (mEtAboutUsBody.getText().toString().trim().length() <= 0) {
            mEtAboutUsBody.setError(getString(R.string.err_report_a_bug_page_empty_description));
            StaticUtils.REQUEST_FOCUS(mEtAboutUsBody, mInputManager);
            return false;
        }
        return true;
    }// END mValidateData()

    private TextWatcher mTextWatcherWordCount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mTvWrdLft.setText(500 - (s.toString().length()) + " characters left");

        }
    }; // END mTextWatcherWordCount

    @Override
    protected void onResume() {
        super.onResume();
        mTvModelName.setText(YasPasApp.getDeviceName());
        mTvModelversion.setText(YasPasApp.getDeviceVersion());
        mTvAppVersionName.setText(BuildConfig.VERSION_NAME);
        mTvAppBuildName.setText("" + BuildConfig.VERSION_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_a_bug);
        mInItObjects();
        mInItWidgets();
    }// END onCreate()

    private void mInItObjects() {
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mNetworkStatus = new NetworkStatus(ReportABugActivity.this);
        mPrgDia = new ProgressDialog(ReportABugActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        yasPasPreferences = YasPasPreferences.GET_INSTANCE(ReportABugActivity.this);

    }// END mInItObjects

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mTvSave = (TextView) findViewById(R.id.xTvSave);
        mTvSave.setOnClickListener(this);
        mTvWrdLft = (TextView) findViewById(R.id.xTvWrdLft);
        mEtAboutUsTitle = (EditText) findViewById(R.id.xEtAboutUsTitle);
        mEtAboutUsBody = (EditText) findViewById(R.id.xEtAboutUsBody);
        mEtAboutUsBody.addTextChangedListener(mTextWatcherWordCount);
        //Version details 30-6-16 by kasi
        mTvModelName = (TextView) findViewById(R.id.xTvModelName);
        mTvModelversion = (TextView) findViewById(R.id.xTvModelversion);
        mTvAppVersionName = (TextView) findViewById(R.id.xTvAppVersionName);
        mTvAppBuildName = (TextView) findViewById(R.id.xTvAppBuildName);
        //END Version details 30-6-16 by kasi

    }// END mInItWidgets()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xTvSave: {
                if (mValidateData()) {
                    if (mNetworkStatus.isNetworkAvailable()) {
                        mPrgDia.show();
                        mPostReport();
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(ReportABugActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }

                }
                break;
            }
            default:
                break;
        }
    }// END onClick()

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            finish();
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}// END ReportABugActivity
