package com.syte.activities.editsyte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.syte.R;
import com.syte.activities.addsyte.AddSyteDetailsActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.Syte;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 02-03-2016.
 */
public class EditSyteAddressActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack, mRelLayForward;
    private TextView mTopLbl;
    private EditText mEtAddress1, mEtAddress2, mEtCity, mEtState, mEtCoutry, mEtZipCode;
    private Bundle mBun;
    private Syte mSyte;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private InputMethodManager mInputManager;
    private String mSyteId;
    private YasPasPreferences mYasPasPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_syte_address);
        mInItObjects();
        mInItWidgets();
    }// END onCreate()

    private void mInItObjects() {
        mBun = getIntent().getExtras();
        mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
        mSyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mNetworkStatus = new NetworkStatus(EditSyteAddressActivity.this);
        mPrgDia = new ProgressDialog(EditSyteAddressActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mYasPasPref = YasPasPreferences.GET_INSTANCE(EditSyteAddressActivity.this);
    }// END mInItObjects

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mRelLayForward = (RelativeLayout) findViewById(R.id.xRelLayForward);
        mRelLayForward.setOnClickListener(this);
        mTopLbl = (TextView) findViewById(R.id.xTopLbl);
        if (mSyte.getStreetAddress1().length() <= 0 &&
                mSyte.getCity().length() <= 0 &&
                mSyte.getState().length() <= 0 &&
                mSyte.getCountry().length() <= 0 &&
                mSyte.getZipCode().length() <= 0) {
            mTopLbl.setText(getString(R.string.err_syte_address_page_empty_address));
        }
        mEtAddress1 = (EditText) findViewById(R.id.xEtAddress1);
        mEtAddress2 = (EditText) findViewById(R.id.xEtAddress2);
        mEtCity = (EditText) findViewById(R.id.xEtCity);
        mEtState = (EditText) findViewById(R.id.xEtState);
        mEtCoutry = (EditText) findViewById(R.id.xEtCoutry);
        mEtZipCode = (EditText) findViewById(R.id.xEtZipCode);
        mEtAddress1.setText(mSyte.getStreetAddress1());
        mEtAddress2.setText(mSyte.getStreetAddress2());
        mEtCity.setText(mSyte.getCity());
        mEtState.setText(mSyte.getState());
        mEtCoutry.setText(mSyte.getCountry());
        mEtZipCode.setText(mSyte.getZipCode());
    }// END mInItObjects

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xRelLayForward: {
                if (mValidateData()) {
                    if (mNetworkStatus.isNetworkAvailable()) {
                        mPrgDia.show();
                        mUpdateSyteLocationNAddress();
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteAddressActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
                }
                break;
            }
            default:
                break;
        }// END onClick()
    }// END EditSyteAddressActivity

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

    private boolean mValidateData() {
        if (mEtAddress1.getText().toString().trim().length() <= 0) {
            mEtAddress1.setError(getString(R.string.err_syte_address_page_empty_add1));
            StaticUtils.REQUEST_FOCUS(mEtAddress1, mInputManager);
            return false;
        }
        if (mEtCity.getText().toString().trim().length() <= 0) {
            mEtCity.setError(getString(R.string.err_syte_address_page_empty_city));
            StaticUtils.REQUEST_FOCUS(mEtCity, mInputManager);
            return false;
        }
        if (mEtState.getText().toString().trim().length() <= 0) {
            mEtState.setError(getString(R.string.err_syte_address_page_empty_state));
            StaticUtils.REQUEST_FOCUS(mEtState, mInputManager);
            return false;
        }
        if (mEtCoutry.getText().toString().trim().length() <= 0) {
            mEtCoutry.setError(getString(R.string.err_syte_address_page_empty_country));
            StaticUtils.REQUEST_FOCUS(mEtCoutry, mInputManager);
            return false;
        }
        if (mEtZipCode.getText().toString().trim().length() <= 0) {
            mEtZipCode.setError(getString(R.string.err_syte_address_page_empty_zip_code));
            StaticUtils.REQUEST_FOCUS(mEtZipCode, mInputManager);
            return false;
        }
        mSyte.setStreetAddress1(mEtAddress1.getText().toString().trim());
        mSyte.setStreetAddress2(mEtAddress2.getText().toString().trim());
        mSyte.setCity(mEtCity.getText().toString().trim());
        mSyte.setState(mEtState.getText().toString().trim());
        mSyte.setCountry(mEtCoutry.getText().toString().trim());
        mSyte.setZipCode(mEtZipCode.getText().toString().trim());
        return true;
    }// END mValidateData()

    private void mUpdateSyteLocationNAddress() {
        Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("latitude", mSyte.getLatitude());
        mUpdateMap.put("longitude", mSyte.getLongitude());
        mUpdateMap.put("streetAddress1", mEtAddress1.getText().toString().trim());
        mUpdateMap.put("streetAddress2", mEtAddress2.getText().toString().trim());
        mUpdateMap.put("city", mEtCity.getText().toString().trim());
        mUpdateMap.put("state", mEtState.getText().toString().trim());
        mUpdateMap.put("country", mEtCoutry.getText().toString().trim());
        mUpdateMap.put("zipCode", mEtZipCode.getText().toString().trim());
        mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
        mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    final Firebase mFireBsYasPasGeoObj = new Firebase(StaticUtils.YASPAS_GEO_LOC_URL);
                    GeoFire mObjGeoFire = new GeoFire(mFireBsYasPasGeoObj);
                    mObjGeoFire.setLocation(mSyteId, new GeoLocation(mSyte.getLatitude(), mSyte.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, FirebaseError error) {
                            if (error == null) {
                                Intent mInt_AddSyteDetails = new Intent(EditSyteAddressActivity.this, EditSyteDetailsActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                                mInt_AddSyteDetails.putExtras(mBdl);
                                mPrgDia.dismiss();
                                startActivityForResult(mInt_AddSyteDetails, 786);

                            } else {
                                mPrgDia.dismiss();
                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteAddressActivity.this, EditSyteAddressActivity.this);
                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                            }
                        }
                    });
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteAddressActivity.this, EditSyteAddressActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });
    }// END mUpdateSyte()

    private void mPrivate_To_Yaspasee() {
        final Firebase firebaseOwnedSyte = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("city", mEtCity.getText().toString().trim());
        mUpdateMap.put("country", mEtCoutry.getText().toString().trim());
        firebaseOwnedSyte.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    mPrgDia.dismiss();
                    EditSyteLocationActivity.TO_BE_DELETED.finish();
                    mFun786();
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteAddressActivity.this, EditSyteAddressActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 786) {

            mSyte = data.getExtras().getParcelable(StaticUtils.IPC_SYTE);
            mSyteId = data.getExtras().getString(StaticUtils.IPC_SYTE_ID);
        }
    } // END onActivityResult();

    private void mFun786() {
        Intent intent = new Intent();
        Bundle mBun1 = new Bundle();
        mBun1.putParcelable(StaticUtils.IPC_SYTE, mSyte);
        intent.putExtras(mBun1);
        setResult(786, intent);
        mPrgDia.dismiss();
        finish();
    }// END mFun786()
} // END EditSyteAddressActivity()
