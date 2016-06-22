package com.syte.activities.editsyte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.Syte;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 01-03-2016.
 */
public class EditSyteDetailsActivity extends Activity implements OnClickListener, OnCustomDialogsListener
    {
        private RelativeLayout mRelLayBack,mRelLayForward;
        private EditText mEtName,mEtCategories,mEtMobileNo;
        private Bundle mBun;
        private Syte mSyte;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        private InputMethodManager mInputManager;
        private String mSyteId;
        private YasPasPreferences mYasPasPref;
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xRelLayBack:
                            {
                                //mFun786();
                                finish();
                                break;
                            }
                        case R.id.xRelLayForward:
                            {
                                if(mValidateData())
                                    {
                                        if(mNetworkStatus.isNetworkAvailable())
                                            {
                                                mPrgDia.show();
                                                mUpdateSyte();
                                            }
                                        else
                                        {
                                            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this,this);
                                            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                        }
                                    }
                                break;
                            }

                        default:break;
                    }
            }// END onClick()
        @Override
        public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
            {

            }// END onDialogLeftBtnClicked()
        @Override
        public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
            {

            }// END onDialogRightBtnClicked()
        private void mInItObjects()
            {
                mBun = getIntent().getExtras();
                mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                mNetworkStatus=new NetworkStatus(EditSyteDetailsActivity.this);
                mPrgDia = new ProgressDialog(EditSyteDetailsActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mYasPasPref = YasPasPreferences.GET_INSTANCE(EditSyteDetailsActivity.this);
            }// END mInItObjects
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mRelLayForward=(RelativeLayout)findViewById(R.id.xRelLayForward);
                mRelLayForward.setOnClickListener(this);
                mEtName=(EditText)findViewById(R.id.xEtName);
                mEtCategories=(EditText)findViewById(R.id.xEtCategories);
                mEtMobileNo=(EditText)findViewById(R.id.xEtMobileNo);
            }// END mInItObjects
        private void mSetValues()
            {
                mEtName.setText(mSyte.getName());
                mEtCategories.setText(mSyte.getCategory());
                mEtMobileNo.setText(mSyte.getMobileNo());
            }// END mSetValues()
        private boolean mValidateData()
            {
                if(mEtName.getText().toString().length()<=0)
                    {
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
                if(mEtCategories.getText().toString().trim().length()>0)
                    mSyte.setCategory(mEtCategories.getText().toString().trim());
                else
                    mSyte.setCategory("");
                if(mEtMobileNo.getText().toString().trim().length()>0)
                    mSyte.setMobileNo(mEtMobileNo.getText().toString().trim());
                else
                    mSyte.setMobileNo("");
                return true;
            }// END mValidateData()
        private void mUpdateSyte()
            {
                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                mUpdateMap.put("name",mSyte.getName());
                mUpdateMap.put("category", mSyte.getCategory());
                mUpdateMap.put("mobileNo", mSyte.getMobileNo());
                mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
                mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null)
                            {
                                final Firebase firebaseOwnedSyte = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                                HashMap<String,Object> mUpdateMap = new HashMap<String, Object>();
                                mUpdateMap.put("name", mSyte.getName());
                                firebaseOwnedSyte.updateChildren(mUpdateMap, new Firebase.CompletionListener()
                                    {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                            {
                                                if (firebaseError == null)
                                                    {
                                                        mPrgDia.dismiss();
                                                        mFun786();
                                                    }
                                                else
                                                    {
                                                        mPrgDia.dismiss();
                                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                                                        customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                                    }
                                    }
                                });



                            }
                        else
                            {
                                mPrgDia.dismiss();
                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteDetailsActivity.this, EditSyteDetailsActivity.this);
                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                            }
                    }
                });
            }// END mUpdateSyte()
        private void mFun786()
            {
                Intent intent = new Intent();
                Bundle mBun1 = new Bundle();
                mBun1.putParcelable(StaticUtils.IPC_SYTE,mSyte);
                intent.putExtras(mBun1);
                mPrgDia.dismiss();
                setResult(786, intent);
                finish();
            }// END mFun786()
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_edit_syte_details);
                mInItObjects();
                mInItWidgets();
                mSetValues();
            }// END onCreate()
    }// END EditSyteDetailsActivity()
