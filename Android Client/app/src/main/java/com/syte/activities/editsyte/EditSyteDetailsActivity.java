package com.syte.activities.editsyte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.activities.addsyte.AddSyteOptinalDetailsActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.OwnedYasPases;
import com.syte.models.Syte;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khalid.p on 01-03-2016.
 */
public class EditSyteDetailsActivity extends Activity implements OnClickListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack, mRelLayForward;
    private EditText mEtName, mEtCategories, mEtMobileNo, mEtEmailid, mEtWebsite;
    private Bundle mBun;
    private Syte mSyte;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private InputMethodManager mInputManager;
    private String mSyteId;
    private YasPasPreferences mYasPasPref;
    private RadioGroup mRadioGrp;
    private RadioButton mRadio_selected_Id, mRadioprivate, mRadiopublic;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                //mFun786();
                finish();
                break;
            }
            case R.id.xRelLayForward: {
                if (mValidateData()) {

                    if (mNetworkStatus.isNetworkAvailable()) {

                        mPrgDia.show();
                        mUpdateSyte();
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, this);
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

    }// END onDialogLeftBtnClicked()

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {

    }// END onDialogRightBtnClicked()

    private void mInItObjects() {
        mBun = getIntent().getExtras();
        mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
        mSyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mNetworkStatus = new NetworkStatus(EditSyteDetailsActivity.this);
        mPrgDia = new ProgressDialog(EditSyteDetailsActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mYasPasPref = YasPasPreferences.GET_INSTANCE(EditSyteDetailsActivity.this);
    }// END mInItObjects

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mRelLayForward = (RelativeLayout) findViewById(R.id.xRelLayForward);
        mRelLayForward.setOnClickListener(this);
        mEtName = (EditText) findViewById(R.id.xEtName);
        mEtCategories = (EditText) findViewById(R.id.xEtCategories);
        mEtMobileNo = (EditText) findViewById(R.id.xEtMobileNo);
        mEtEmailid = (EditText) findViewById(R.id.xEtEmailid);//Email and Website on 28-6-16 by kasi
        mEtWebsite = (EditText) findViewById(R.id.xEtWebsite);//Email and Website on 28-6-16 by kasi
        mRadioGrp = (RadioGroup) findViewById(R.id.xRadioGrpType);
        mRadioprivate = (RadioButton) findViewById(R.id.xRadioPrivate);
        mRadiopublic = (RadioButton) findViewById(R.id.xRadioPublic);
    }// END mInItObjects

    private void mSetValues() {
        mEtName.setText(mSyte.getName());
        mEtCategories.setText(mSyte.getCategory());
        mEtMobileNo.setText(mSyte.getMobileNo());
        mEtEmailid.setText(mSyte.getEmailid());//Email and Website on 28-6-16 by kasi
        mEtWebsite.setText(mSyte.getWebsite());//Email and Website on 28-6-16 by kasi
        if (mSyte.getSyteType() != null) {
            if (mSyte.getSyteType().toString().trim().equalsIgnoreCase(mRadiopublic.getText().toString().trim())) {
                mRadioGrp.check(mRadiopublic.getId());
            } else if (mSyte.getSyteType().toString().trim().equalsIgnoreCase(mRadioprivate.getText().toString().trim())) {
                mRadioGrp.check(mRadioprivate.getId());
            }
        } else {
            mRadioGrp.check(mRadioprivate.getId());
        }

    }// END mSetValues()

    private boolean mValidateData() {
        if (mEtName.getText().toString().length() <= 0) {
            mEtName.setError(getString(R.string.err_syte_details_page_empty_name));
            StaticUtils.REQUEST_FOCUS(mEtName, mInputManager);
            return false;
        }
                /*if(mEtCategories.getText().toString().length()<=0)
                    {
                        mEtCategories.setError(getString(R.string.err_syte_details_page_empty_category));
                        StaticUtils.REQUEST_FOCUS(mEtCategories, mInputManager);
                        return false;
                    }*/
                /*if(mEtMobileNo.getText().toString().length()<=0)
                    {
                        mEtMobileNo.setError(getString(R.string.err_syte_details_page_empty_mobile_num));
                        StaticUtils.REQUEST_FOCUS(mEtMobileNo, mInputManager);
                        return false;
                    }*/
        mSyte.setName(mEtName.getText().toString().trim());
        if (mEtCategories.getText().toString().trim().length() > 0)
            mSyte.setCategory(mEtCategories.getText().toString().trim());
        else
            mSyte.setCategory("");
        if (mEtMobileNo.getText().toString().trim().length() > 0)
            mSyte.setMobileNo(mEtMobileNo.getText().toString().trim());
        else
            mSyte.setMobileNo("");
        //Email and Website on 28-6-16 by kasi
        if (mEtEmailid.getText().toString().trim().length() > 0) {
            if (emailValidator(mEtEmailid.getText().toString().trim())) {
                mSyte.setEmailid(mEtEmailid.getText().toString().trim());
            } else {

                mEtEmailid.setError("Please enter Valid Email id");
                StaticUtils.REQUEST_FOCUS(mEtEmailid, mInputManager);
                return false;
            }
        } else {
            mSyte.setEmailid("");
        }

        if (mEtWebsite.getText().toString().trim().length() > 0) {
            if (isValidUrl(mEtWebsite.getText().toString().trim())) {
                mSyte.setWebsite(mEtWebsite.getText().toString().trim());
            } else {
                mEtWebsite.setError("Please enter Valid website ");
                StaticUtils.REQUEST_FOCUS(mEtWebsite, mInputManager);
                return false;
            }
        } else {
            mSyte.setWebsite("");
        }
        //END Email and Website on 28-6-16 by kasi
        // get selected radio button from radioGroup
        int selectedId = mRadioGrp.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        mRadio_selected_Id = (RadioButton) findViewById(selectedId);
        Log.d("Radio_Selected_Type", mRadio_selected_Id.getText().toString());
        mSyte.setSyteType(mRadio_selected_Id.getText().toString());
        return true;
    }// END mValidateData()

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        if (m.matches())
            return true;
        else
            return false;
    }

    private void mUpdateSyte() {
        Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("name", mSyte.getName());
        mUpdateMap.put("category", mSyte.getCategory());
        mUpdateMap.put("mobileNo", mSyte.getMobileNo());
        mUpdateMap.put("emailid", mSyte.getEmailid());//Email and Website on 28-6-16 by kasi
        mUpdateMap.put("website", mSyte.getWebsite());//Email and Website on 28-6-16 by kasi
        mUpdateMap.put("syteType", mSyte.getSyteType());//Syte Type on15-7-16
        mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
        mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    if (mSyte.getSyteType().toString().trim().equalsIgnoreCase("Private")) {
                        final Firebase firebasePublicSyte = new Firebase(StaticUtils.PUBLIC_YASPAS);
                        firebasePublicSyte.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    if (dataSnapshot.hasChild(mSyteId)) {
                                        mRemovepublicsyte();
                                        mAddsyte_To_YasPasee_Private();
                                    } else {
                                        mUpdateSyte_Yaspasee_private();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    } else if (mSyte.getSyteType().toString().trim().equalsIgnoreCase("Public")) {
                        final Firebase firebasePublicSyte = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS);
                        firebasePublicSyte.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    if (dataSnapshot.hasChild(mSyteId)) {
                                        mRemoveprivateSyte();
                                        mAddSyte_To_YasPasee_Public();
                                    } else {
                                        mUpdateSyte_Yaspasee_public();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }


                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });
    }// END mUpdateSyte()

    private void mUpdateSyte_Yaspasee_private() {
        final Firebase firebaseOwnedSyte = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("name", mSyte.getName());
        mUpdateMap.put("mobileNo", mSyte.getMobileNo());
        mUpdateMap.put("syteType", mSyte.getSyteType());//Syte Type on15-7-16
        mUpdateMap.put("city", mSyte.getCity());
        mUpdateMap.put("country", mSyte.getCountry());
        firebaseOwnedSyte.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    mPrgDia.dismiss();
                    Intent mIntent_EditSyteDetails = new Intent(EditSyteDetailsActivity.this, EditSyteMainActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                    mIntent_EditSyteDetails.putExtras(mBdl);
                    startActivity(mIntent_EditSyteDetails);
                    EditSyteLocationActivity.TO_BE_DELETED.finish();
                    EditSyteAddressActivity.TO_BE_DELETED.finish();
                    finish();
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });

    }//update owned syte

    private void mUpdateSyte_Yaspasee_public() {
        final Firebase firebaseOwnedSyte = new Firebase(StaticUtils.PUBLIC_YASPAS).child(mSyteId);
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("name", mSyte.getName());
        mUpdateMap.put("mobileNo", mSyte.getMobileNo());
        mUpdateMap.put("syteType", mSyte.getSyteType());//Syte Type on15-7-16
        mUpdateMap.put("city", mSyte.getCity());
        mUpdateMap.put("country", mSyte.getCountry());
        firebaseOwnedSyte.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    mPrgDia.dismiss();
                    Intent mIntent_EditSyteDetails = new Intent(EditSyteDetailsActivity.this, EditSyteMainActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                    mIntent_EditSyteDetails.putExtras(mBdl);
                    startActivity(mIntent_EditSyteDetails);
                    EditSyteLocationActivity.TO_BE_DELETED.finish();
                    EditSyteAddressActivity.TO_BE_DELETED.finish();
                    finish();
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });

    }//update public syte

    private void mAddsyte_To_YasPasee_Private() {
        // Setting this newly added Syte as owned syte to YasPasee .i.e.; logged in user
        final Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
        OwnedYasPases mOwnedYasPases = new OwnedYasPases();
        mOwnedYasPases.setYasPasId(mSyteId);
        mOwnedYasPases.setName(mSyte.getName());
        mOwnedYasPases.setImageUrl(mSyte.getImageUrl());
        mOwnedYasPases.setCity(mSyte.getCity());
        mOwnedYasPases.setCountry(mSyte.getCountry());
        mOwnedYasPases.setIsActive(mSyte.getIsActive());
        mOwnedYasPases.setSyteType(mSyte.getSyteType());
        mOwnedYasPases.setMobileNo(mSyte.getMobileNo());
        mFireBsYasPasObj.setValue(mOwnedYasPases, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {

                    mPrgDia.dismiss();
                    Intent mIntent_EditSyteDetails = new Intent(EditSyteDetailsActivity.this, EditSyteMainActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                    mIntent_EditSyteDetails.putExtras(mBdl);
                    startActivity(mIntent_EditSyteDetails);
                    EditSyteLocationActivity.TO_BE_DELETED.finish();
                    EditSyteAddressActivity.TO_BE_DELETED.finish();
                    finish();
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });
    }// END mAddYasPas_To_YasPasee()

    private void mAddSyte_To_YasPasee_Public() {
        // Setting this newly added Syte as public syte to YasPasee .i.e.; logged in user
        final Firebase mFireBsYasPasObj = new Firebase(StaticUtils.PUBLIC_YASPAS).child(mSyteId);
        OwnedYasPases mOwnedYasPases = new OwnedYasPases();
        mOwnedYasPases.setYasPasId(mSyteId);
        mOwnedYasPases.setName(mSyte.getName());
        mOwnedYasPases.setImageUrl(mSyte.getImageUrl());
        mOwnedYasPases.setCity(mSyte.getCity());
        mOwnedYasPases.setCountry(mSyte.getCountry());
        mOwnedYasPases.setIsActive(mSyte.getIsActive());
        mOwnedYasPases.setSyteType(mSyte.getSyteType().toString().trim());
        mOwnedYasPases.setMobileNo(mSyte.getMobileNo());
        mFireBsYasPasObj.setValue(mOwnedYasPases, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {

                    mPrgDia.dismiss();
                    Intent mIntent_EditSyteDetails = new Intent(EditSyteDetailsActivity.this, EditSyteMainActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                    mIntent_EditSyteDetails.putExtras(mBdl);
                    startActivity(mIntent_EditSyteDetails);
                    EditSyteLocationActivity.TO_BE_DELETED.finish();
                    EditSyteAddressActivity.TO_BE_DELETED.finish();
                    finish();

                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });
    }// END mAddYasPas_To_YasPasee()

    private void mRemoveprivateSyte() {
        final Firebase firebaseremovesyteId = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS);

        firebaseremovesyteId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Iterator<DataSnapshot> iteratorSyteId = dataSnapshot.getChildren().iterator();
                    while (iteratorSyteId.hasNext()) {
                        DataSnapshot dataSnapshotFollower = (DataSnapshot) iteratorSyteId.next();
                        String abc = dataSnapshotFollower.getKey();
                        Log.d("private syte key", abc);
                        if (abc.equalsIgnoreCase(mSyteId)) {
                            Firebase fb = firebaseremovesyteId.child(abc);
                            fb.getRef().removeValue();
                            Log.d("private syte removed", "successfully");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(" firebaseError", firebaseError.getMessage());
            }
        });
    }

    private void mRemovepublicsyte() {
        final Firebase firebaseRemovePublic = new Firebase(StaticUtils.PUBLIC_YASPAS);

        firebaseRemovePublic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Iterator<DataSnapshot> iteratorsyteId = dataSnapshot.getChildren().iterator();
                    while (iteratorsyteId.hasNext()) {
                        DataSnapshot dataSnapshotFollower = (DataSnapshot) iteratorsyteId.next();
                        String abc = dataSnapshotFollower.getKey();
                        Log.d("public syte key", abc);
                        if (abc.equalsIgnoreCase(mSyteId)) {
                            Firebase fb = firebaseRemovePublic.child(abc);
                            fb.getRef().removeValue();
                            Log.d("public syte removed", "successfully");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(" firebaseError", firebaseError.getMessage());
            }
        });
    }


    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }//end Email validation

    private void mFun786() {
        Intent intent = new Intent();
        Bundle mBun1 = new Bundle();
        mBun1.putParcelable(StaticUtils.IPC_SYTE, mSyte);
        intent.putExtras(mBun1);
        mPrgDia.dismiss();
        setResult(786, intent);
        finish();
    }// END mFun786()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_syte_details);
        mInItObjects();
        mInItWidgets();
        mSetValues();
    }// END onCreate()
}// END EditSyteDetailsActivity()
