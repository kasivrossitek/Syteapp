package com.syte.activities.addsyte;

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

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.OwnedYasPases;
import com.syte.models.Syte;
import com.syte.models.YasPasManager;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 13-02-2016.
 */
public class AddSyteDetailsActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
        private RelativeLayout mRelLayBack,mRelLayForward;
        private EditText mEtName,mEtCategories,mEtMobileNo;
        private YasPasPreferences mYasPasPref;
        private Bundle mBun;
        private Syte mSyte;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        private InputMethodManager mInputManager;
        private String mSyteId;
        //private ArrayList<YasPasTeams> mTeamMembers;
        public static Activity TO_BE_DELETED;
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.acitivity_add_syte_details);
                TO_BE_DELETED=null;
                TO_BE_DELETED=this;
                mInItObjects();
                mInItWidgets();
                mSetValues();
            }// END onCreate()
        private void mInItObjects()
            {
                mBun = getIntent().getExtras();
                mYasPasPref=YasPasPreferences.GET_INSTANCE(AddSyteDetailsActivity.this);
                mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                //mTeamMembers = mBun.getParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST);
                mNetworkStatus=new NetworkStatus(AddSyteDetailsActivity.this);
                mPrgDia = new ProgressDialog(AddSyteDetailsActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
                try
                    {
                        if(mSyte.getMobileNo()!=null)
                            mEtMobileNo.setText(mSyte.getMobileNo());
                        else
                            mEtMobileNo.setText(mYasPasPref.sGetRegisteredNum());
                    }
                catch (Exception e)
                    {
                        mEtMobileNo.setText(mYasPasPref.sGetRegisteredNum());
                    }
            }// END mSetValues()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xRelLayBack:
                            {
                                mFun786();
                                break;
                            }
                        case R.id.xRelLayForward:
                            {
                                if(mValidateData())
                                    {
                                        if(mNetworkStatus.isNetworkAvailable())
                                            {
                                                if(mSyteId.toString().trim().length()<=0)
                                                    {
                                                        mPrgDia.show();
                                                        mAddSyte();
                                                    }
                                                else
                                                    {
                                                        mPrgDia.show();
                                                        mUpdateSyte();
                                                    }
                                        }
                                        else
                                            {
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteDetailsActivity.this,this);
                                                customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                            }
                                    }
                                break;
                            }
                        default:break;
                    }
            }// END onClick()
        private void mAddSyte()
            {
                // Making logged in user as Syte's owner
                mSyte.setOwner(mYasPasPref.sGetRegisteredNum());
                // Making isDeleted flag as false
                mSyte.setIsDeleted(0);
                // Making openForBiz flag as true
                mSyte.setOpenForBiz(1);
                // Making messagingEnabled flag as true
                mSyte.setMessagingEnabled(1);
                // Setting latest bulletin as empty, as this is gonna be a new Syte and it is not having any bulletin
                mSyte.setLatestBulletin("");
                // Setting dateCreated and dateModified as current server time and both of them are equal
                mSyte.setDateCreated(ServerValue.TIMESTAMP);
                mSyte.setDateModified(ServerValue.TIMESTAMP);
                // Setting dateDeleted as 0, because this Syte is not deleted and it will be live.
                mSyte.setDateDeleted(0);
                mSyte.setIsActive(0); // Making it in-active, as it is until, it is not published

                mSyte.setName(mEtName.getText().toString().trim());
                if(mEtCategories.getText().toString().trim().length()>0)
                    mSyte.setCategory(mEtCategories.getText().toString().trim());
                else
                    mSyte.setCategory("");
                if(mEtMobileNo.getText().toString().trim().length()>0)
                    mSyte.setMobileNo(mEtMobileNo.getText().toString().trim());
                else
                    mSyte.setMobileNo("");

                /*Adding Syte's general data
                * Latitude
                * Longitude
                * Address1
                * Address2
                * City
                * State
                * Country
                * Zipcode
                * Name
                * Category
                * Mobile Number
                * Owner
                * Dates - created, modified, deleted
                * Flags - isDeleted, openForBiz, isMsgEnabled
                * Latest Bulletin*/

                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL);
                mFireBsYasPasObj.push().setValue(mSyte, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                            {
                                if (firebaseError == null && firebase.getKey() != null)
                                    {
                                        mSyteId = firebase.getKey();
                                        mAddManager();
                                    }
                                else
                                    {
                                        mPrgDia.dismiss();
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteDetailsActivity.this, AddSyteDetailsActivity.this);
                                        customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                    }
                            }
                    });
            } // END mAddSyte()
        private void mAddManager()
            {
                // Setting logged in or owner as Syte's manager
                YasPasManager mYasPasManager = new YasPasManager();
                mYasPasManager.setManagerId(mYasPasPref.sGetRegisteredNum());
                final Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.YASPAS_MANAGERS).child(mYasPasPref.sGetRegisteredNum());
                mFireBsYasPasObj.setValue(mYasPasManager, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            mAddYasPas_To_YasPasee();
                        } else {
                            mPrgDia.dismiss();
                            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteDetailsActivity.this, AddSyteDetailsActivity.this);
                            customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                        }
                    }
                });
            } // END mAddManager()
        private void mAddYasPas_To_YasPasee()
            {
                // Setting this newly added Syte as owned syte to YasPasee .i.e.; logged in user
                final Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                OwnedYasPases mOwnedYasPases = new OwnedYasPases();
                mOwnedYasPases.setYasPasId(mSyteId);
                mOwnedYasPases.setName(mSyte.getName());
                mOwnedYasPases.setImageUrl(mSyte.getImageUrl());
                mOwnedYasPases.setCity(mSyte.getCity());
                mOwnedYasPases.setCountry(mSyte.getCountry());
                mOwnedYasPases.setIsActive(mSyte.getIsActive());
                mFireBsYasPasObj.setValue(mOwnedYasPases, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            Intent mInt_AddSyteOtionalDetails = new Intent(AddSyteDetailsActivity.this, AddSyteOptinalDetailsActivity.class);
                            Bundle mBdl = new Bundle();
                            mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                            mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                            //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                            mInt_AddSyteOtionalDetails.putExtras(mBdl);
                            mPrgDia.dismiss();
                            startActivityForResult(mInt_AddSyteOtionalDetails, 786);
                        } else {
                            mPrgDia.dismiss();
                            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteDetailsActivity.this, AddSyteDetailsActivity.this);
                            customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                        }
                    }
                });
            }// END mAddYasPas_To_YasPasee()
        private void mUpdateSyte()
            {
                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                mUpdateMap.put("name",mSyte.getName());
                mUpdateMap.put("category", mSyte.getCategory());
                mUpdateMap.put("mobileNo", mSyte.getMobileNo());
                mUpdateMap.put("dateModified",ServerValue.TIMESTAMP);
                mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                            {
                                if(firebaseError==null)
                                    {
                                        mAddManager();
                                    }
                                else
                                    {
                                        mPrgDia.dismiss();
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteDetailsActivity.this, AddSyteDetailsActivity.this);
                                        customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                    }
                            }
                });
            }// END mUpdateSyte()
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
            }
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
    @Override
    public void onBackPressed()
    {
        mFun786();
        super.onBackPressed();
    }// END onBackPressed()
    private void mFun786()
        {
            Intent intent = new Intent();
            Bundle mBun1 = new Bundle();
            mBun1.putParcelable(StaticUtils.IPC_SYTE,mSyte);
            mBun1.putString(StaticUtils.IPC_SYTE_ID,mSyteId);
            //mBun1.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
            intent.putExtras(mBun1);
            setResult(786, intent);
            finish();
        }// END mFun786()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 786)
            {
               /* mTeamMembers.removeAll(mTeamMembers);
                ArrayList<YasPasTeams> yasPasTeams = data.getExtras().getParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST);
                for(int i=0;i<yasPasTeams.size();i++)
                {
                    YasPasTeams yasPasTeam = yasPasTeams.get(i);
                    mTeamMembers.add(yasPasTeam);
                }*/
                mSyte=data.getExtras().getParcelable(StaticUtils.IPC_SYTE);
                mSyteId=data.getExtras().getString(StaticUtils.IPC_SYTE_ID);
            }
    } // END onActivityResult();
    }//END AddSyteDetailsActivity
