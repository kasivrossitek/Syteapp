package com.syte.activities.editsyte;

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
import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.AboutUs;
import com.syte.models.Syte;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 02-03-2016.
 */
public class EditSyteAboutUsActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
        private RelativeLayout mRelLayBack;
        private TextView mTvSave,mTvWrdLft;
        private EditText mEtAboutUsTitle,mEtAboutUsBody;
        private InputMethodManager mInputManager;
        private Bundle mBun;
        private Syte mSyte;
        private AboutUs aboutUs;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        private String mSyteId;
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_edit_syte_about_us);
                mInItObjects();
                mInItWidgets();
                mSetValues();
            }// END onCreate()
        private void mInItObjects()
            {
                mBun = getIntent().getExtras();
                mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                aboutUs = mSyte.getAboutUs();
                mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mNetworkStatus=new NetworkStatus(EditSyteAboutUsActivity.this);
                mPrgDia = new ProgressDialog(EditSyteAboutUsActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
            }// END mInItObjects
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mTvSave=(TextView)findViewById(R.id.xTvSave);
                mTvSave.setOnClickListener(this);
                mTvWrdLft=(TextView)findViewById(R.id.xTvWrdLft);
                mEtAboutUsTitle=(EditText)findViewById(R.id.xEtAboutUsTitle);
                mEtAboutUsBody=(EditText)findViewById(R.id.xEtAboutUsBody);
                mEtAboutUsBody.addTextChangedListener(mTextWatcherWordCount);
            }// END mInItWidgets()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xRelLayBack:
                            {
                                finish();
                                break;
                            }
                        case R.id.xTvSave:
                            {
                                if(mValidateData())
                                    {
                                        if(mNetworkStatus.isNetworkAvailable())
                                            {
                                                mPrgDia.show();
                                                aboutUs.setTitle(mEtAboutUsTitle.getText().toString().trim());
                                                aboutUs.setDescription(mEtAboutUsBody.getText().toString().trim());
                                                Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.YASPAS_ABOUT_US);
                                                mFireBsYasPasObj.setValue(aboutUs, new Firebase.CompletionListener()
                                                    {
                                                        @Override
                                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                            {
                                                                if (firebaseError == null)
                                                                    {
                                                                        Firebase mFireBsYasPasObj1=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                                                                        HashMap<String, Object> mUpdateMap1 = new HashMap<String, Object>();
                                                                        mUpdateMap1.put("dateModified", ServerValue.TIMESTAMP);
                                                                        mFireBsYasPasObj1.updateChildren(mUpdateMap1, new Firebase.CompletionListener()
                                                                            {
                                                                                @Override
                                                                                public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                                                    {
                                                                                        mSyte.setAboutUs(aboutUs);
                                                                                        mFun786();
                                                                                    }
                                                                            });
                                                                    }
                                                                else
                                                                    {
                                                                        mPrgDia.dismiss();
                                                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteAboutUsActivity.this, EditSyteAboutUsActivity.this);
                                                                        customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                                                    }
                                                            }
                                                    });
                                            }
                                        else
                                            {
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteAboutUsActivity.this,this);
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
                if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
                    {
                        finish();
                    }
            }// END onDialogLeftBtnClicked()
        @Override
        public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
            {
                if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
                    {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
            }// END onDialogRightBtnClicked()
        private TextWatcher mTextWatcherWordCount = new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                    {}
                @Override
                public void afterTextChanged(Editable s)
                {
                    mTvWrdLft.setText(500 - (s.toString().length())+ " characters left");
                }
            }; // END mTextWatcherWordCount
        private boolean mValidateData()
            {
                if(mEtAboutUsTitle.getText().toString().trim().length()<=0)
                {
                    mEtAboutUsTitle.setError(getString(R.string.err_syte_about_us_page_empty_title));
                    StaticUtils.REQUEST_FOCUS(mEtAboutUsTitle, mInputManager);
                    return false;
                }
                if(mEtAboutUsBody.getText().toString().trim().length()<=0)
                {
                    mEtAboutUsTitle.setError(getString(R.string.err_syte_about_us_page_empty_description));
                    StaticUtils.REQUEST_FOCUS(mEtAboutUsBody, mInputManager);
                    return false;
                }
                return true;
            }// END mValidateData()
        private void mFun786()
            {
                Intent intent = new Intent();
                Bundle mBun1 = new Bundle();
                mBun1.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                intent.putExtras(mBun1);
                setResult(786, intent);
                mPrgDia.dismiss();
                finish();
            }// END mFun786()
        private void mSetValues()
            {
                if(aboutUs!=null)
                    {
                        mEtAboutUsTitle.setText(aboutUs.getTitle());
                        mEtAboutUsBody.setText(aboutUs.getDescription());
                        mTvWrdLft.setText(500 - (aboutUs.getDescription().toString().trim().length()) + " characters left");
                    }
                else
                    {
                        aboutUs=new AboutUs();
                    }
            }// END mSetValues()
    }// END EditSyteAboutUsActivity()
