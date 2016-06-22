package com.syte.activities.addsyte;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.syte.R;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.YasPasTeam;
import com.syte.models.YasPasTeams;
import com.syte.services.MediaUploadService;
import com.syte.utils.GalleryHelper;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 13-02-2016.
 */
public class AddSyteAddTeamMemberActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener
    {
        private EditText mEtTmName,mEtTmDesignation,mEtTmProfile;
        private TextView mTvSave;
        private RelativeLayout mRelLayBack;
        private ImageView mIvTeamMemberImage;
        private FloatingActionButton mIvAddTmProfilePic;
        private InputMethodManager mInputManager;
        private YasPasTeam yasPasTeam;
        //private CloudinaryContent mCloudinaryContent;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        private DisplayImageOptions mDisImgOpt;
        private String mSyteId;
        private String mImageLocalPath="";
        @Override
        protected void onDestroy()
            {
                super.onDestroy();
                mIvTeamMemberImage.setImageResource(0);
                mIvTeamMemberImage=null;
            }
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.acitivity_add_syte_add_team_member);
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                mSyteId=getIntent().getExtras().getString(StaticUtils.IPC_SYTE_ID);
                yasPasTeam=new YasPasTeam();
                yasPasTeam.setName("");
                yasPasTeam.setDesignation("");
                yasPasTeam.setAbout("");
                yasPasTeam.setIsHide(0); // by default team member is visible
                yasPasTeam.setProfilePic("");
                mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mNetworkStatus=new NetworkStatus(AddSyteAddTeamMemberActivity.this);
                mPrgDia=new ProgressDialog(AddSyteAddTeamMemberActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);

                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageForEmptyUri(R.drawable.img_syte_user_no_image)
                        .showImageOnFail(R.drawable.img_syte_user_no_image)
                        .showImageOnLoading(R.drawable.img_syte_user_no_image)
                        .considerExifParams(true).build();
            }// END mInItObjects
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mTvSave=(TextView)findViewById(R.id.xTvSave);
                mTvSave.setOnClickListener(this);
                mEtTmName=(EditText)findViewById(R.id.xEtTmName);
                mEtTmDesignation=(EditText)findViewById(R.id.xEtTmDesignation);
                mEtTmProfile=(EditText)findViewById(R.id.xEtTmProfile);
                mIvAddTmProfilePic=(FloatingActionButton)findViewById(R.id.xIvAddTmProfilePic);
                mIvAddTmProfilePic.setOnClickListener(this);
                mIvTeamMemberImage=(ImageView)findViewById(R.id.xIvTeamMemberImage);
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
                                                mAddTeamMember();
                                            }
                                        else
                                            {
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteAddTeamMemberActivity.this,this);
                                                customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                            }
                                    }
                                break;
                            }
                        case R.id.xIvAddTmProfilePic:
                            {
                                showPicOptionDialog();
                                break;
                            }
                        default:break;
                    }
            }// END onClick()
        private boolean mValidateData()
            {
                if(mEtTmName.getText().toString().trim().length()<=0)
                    {
                        mEtTmName.setError(getString(R.string.err_syte_add_tm_mem_page_empty_name));
                        StaticUtils.REQUEST_FOCUS(mEtTmName, mInputManager);
                        return false;
                    }
                /*if(mEtTmDesignation.getText().toString().trim().length()<=0)
                    {
                        mEtTmDesignation.setError(getString(R.string.err_syte_add_tm_mem_page_empty_designation));
                        StaticUtils.REQUEST_FOCUS(mEtTmDesignation, mInputManager);
                        return false;
                    }
                if(mEtTmProfile.getText().toString().trim().length()<=0)
                    {
                        mEtTmProfile.setError(getString(R.string.err_syte_add_tm_mem_page_empty_profile));
                        StaticUtils.REQUEST_FOCUS(mEtTmProfile, mInputManager);
                        return false;
                    }*/
                return true;
            }// END mValidateData()
        private void showPicOptionDialog()
            {
                final Dialog mDialog = new Dialog(AddSyteAddTeamMemberActivity.this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mDialog.setContentView(R.layout.layout_image_picker_selector_dialog);
                mDialog.show();
                final Window window = mDialog.getWindow();
                LinearLayout mLinLayCamera = (LinearLayout) window.findViewById(R.id.xLinLayCamera);
                LinearLayout mLinLayGallery = (LinearLayout) window.findViewById(R.id.xLinLayGallery);

                mLinLayGallery.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        /*Intent intent = new Intent();
                        intent.setType("image*//**//*");
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);*/
                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), StaticUtils.ACTION_GALLERY_PICK);
                    }
                });
                mLinLayCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, StaticUtils.ACTION_CAMERA_PICK);
                    }
                });
            }// END showPicOptionDialog()
        public void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                if(resultCode==Activity.RESULT_OK)
                    {
                        if(requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode==StaticUtils.ACTION_GALLERY_PICK)
                            {
                                yasPasTeam.setProfilePic("");
                                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                                imageMap= GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, AddSyteAddTeamMemberActivity.this);
                                mImageLocalPath = imageMap.get("keyImagePath").toString();
                                ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvTeamMemberImage, mDisImgOpt);
                            }
                    }
            } // END onActivityResult();
        @Override
        public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
            {
                if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
                    {
                        finish();
                    }
            }
        @Override
        public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
            {
                if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
                    {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoProfilePic"))
                    {
                        // do nothing
                    }
            }
        private void mAddTeamMember()
            {
                mPrgDia.show();
                yasPasTeam.setName(mEtTmName.getText().toString().trim());
                yasPasTeam.setDesignation(mEtTmDesignation.getText().toString().trim());
                yasPasTeam.setAbout(mEtTmProfile.getText().toString().trim());
                yasPasTeam.setIsHide(0); // by default team member is visible
                if(mImageLocalPath.trim().length()>0)
                    {
                        yasPasTeam.setProfilePic(MediaUploadService.DUMMY_URL);
                    }
                else
                    {
                        yasPasTeam.setProfilePic("");
                    }
                Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.YASPAS_TEAM);
                mFireBsYasPasObj.push().setValue(yasPasTeam, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, final Firebase firebaseTeamMember) {
                        if (firebaseError == null) {
                            Firebase mFireBsYasPasObj1 = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                            HashMap<String, Object> mUpdateMap1 = new HashMap<String, Object>();
                            mUpdateMap1.put("dateModified", ServerValue.TIMESTAMP);
                            mFireBsYasPasObj1.updateChildren(mUpdateMap1, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                        /*Checking if image is been added to this member
                                                        * If YES then adding the this record to local DB & starting the MediaUploadService*/
                                    if (mImageLocalPath.trim().length() > 0) {
                                        String old_cloudinary_id = ""; // Making it as empty string as there is no image to be deleted as we are adding this member for the first time
                                        SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(AddSyteAddTeamMemberActivity.this);
                                        syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                                                SyteDataBaseConstant.C_MEDIA_TAR_TEAM_MEM_PROFILE_PIC,
                                                firebaseTeamMember.getKey(),
                                                mSyteId,
                                                MediaUploadService.DUMMY_URL,
                                                mImageLocalPath,
                                                old_cloudinary_id);
                                        Intent i = new Intent(AddSyteAddTeamMemberActivity.this, MediaUploadService.class);
                                        startService(i);
                                    }
                                    YasPasTeams yasPasTeams = new YasPasTeams();
                                    yasPasTeams.setYasPasTeamId(firebase.getKey());
                                    yasPasTeams.setYasPasTeam(yasPasTeam);
                                    Intent intent = new Intent();
                                    Bundle mBun1 = new Bundle();
                                    mBun1.putParcelable(StaticUtils.IPC_TEAM_MEMBER, yasPasTeams);
                                    intent.putExtras(mBun1);
                                    setResult(1, intent);
                                    mPrgDia.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            mPrgDia.dismiss();
                            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteAddTeamMemberActivity.this, AddSyteAddTeamMemberActivity.this);
                            customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                        }
                    }
                });
            }// END mAddTeamMember()
    }// END AddSyteAddTeamMemberActivity
