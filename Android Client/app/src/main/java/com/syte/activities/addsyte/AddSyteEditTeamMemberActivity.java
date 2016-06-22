package com.syte.activities.addsyte;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid.p on 19-02-2016.
 */
public class AddSyteEditTeamMemberActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener
    {
        private EditText mEtTmName,mEtTmDesignation,mEtTmProfile;
        private RelativeLayout mRelLayBack,mRelLaySave;
        private ImageView mIvTeamMemberImage;
        private FloatingActionButton mIvAddTmProfilePic;
        private TextView mTvDelete,mTvHide;
        private Cloudinary mCloudinary;
        private Transformation cTransformation;
        private DisplayImageOptions mDisImgOpt;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        private InputMethodManager mInputManager;
        private YasPasTeams yasPasTeams;
        private YasPasTeam yasPasTeam;
        private String yasPasTeamId;
        private Bundle mBun;
        private String mSyteId;
        private String mImageLocalPath="";
        private ProgressBar mPbLoader;
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
                setContentView(R.layout.acitivity_edit_syte_team_member);
                mInItObjects();
                mInItWidgets();
                mSetValues();
            }// END onCreate()
        private void mInItObjects()
            {
                mBun=getIntent().getExtras();
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                yasPasTeams=mBun.getParcelable(StaticUtils.IPC_TEAM_MEMBER);
                yasPasTeam=yasPasTeams.getYasPasTeam();
                yasPasTeamId=yasPasTeams.getYasPasTeamId();
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                cTransformation=new Transformation().width((int) (getResources().getDimension(R.dimen.profile_image_wd) / getResources().getDisplayMetrics().density))
                        .height((int) (getResources().getDimension(R.dimen.profile_image_ht) / getResources().getDisplayMetrics().density)).crop("crop").gravity("face");
                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageForEmptyUri(R.drawable.img_syte_user_no_image)
                        .showImageOnFail(R.drawable.img_syte_user_no_image)
                        .showImageOnLoading(R.drawable.img_syte_user_no_image)
                        .considerExifParams(true).build();
                mNetworkStatus=new NetworkStatus(AddSyteEditTeamMemberActivity.this);
                mPrgDia=new ProgressDialog(AddSyteEditTeamMemberActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            }// END mInItObjects()
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mRelLaySave=(RelativeLayout)findViewById(R.id.xRelLaySave);
                mRelLaySave.setOnClickListener(this);
                mEtTmName=(EditText)findViewById(R.id.xEtTmName);
                mEtTmDesignation=(EditText)findViewById(R.id.xEtTmDesignation);
                mEtTmProfile=(EditText)findViewById(R.id.xEtTmProfile);
                mIvAddTmProfilePic=(FloatingActionButton)findViewById(R.id.xIvAddTmProfilePic);
                mIvAddTmProfilePic.setOnClickListener(this);
                mIvTeamMemberImage=(ImageView)findViewById(R.id.xIvTeamMemberImage);
                mTvDelete=(TextView)findViewById(R.id.xTvDelete);
                mTvDelete.setOnClickListener(this);
                mTvHide=(TextView)findViewById(R.id.xTvHide);
                mTvHide.setOnClickListener(this);
                mPbLoader=(ProgressBar)findViewById(R.id.xPbLoader);
            }// END mInItWidgets()
        private void mSetValues()
            {
                if(yasPasTeam.getProfilePic().toString().trim().length()>0 && !yasPasTeam.getProfilePic().toString().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                    {
                        String cURL = mCloudinary.url().transformation(cTransformation).generate(yasPasTeam.getProfilePic());
                        ImageLoader.getInstance().displayImage(cURL, mIvTeamMemberImage, mDisImgOpt, new ImageLoadingListener()
                            {
                                @Override
                                public void onLoadingStarted(String imageUri, View view)
                                    {
                                        mPbLoader.setVisibility(View.VISIBLE);
                                    }
                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                                    {
                                        mPbLoader.setVisibility(View.GONE);
                                    }
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                                    {
                                        mPbLoader.setVisibility(View.GONE);
                                    }
                                @Override
                                public void onLoadingCancelled(String imageUri, View view)
                                    {
                                        mPbLoader.setVisibility(View.GONE);
                                    }
                            });
                    }
                mEtTmName.setText(yasPasTeam.getName().toString().trim());
                mEtTmDesignation.setText(yasPasTeam.getDesignation().toString().trim());
                mEtTmProfile.setText(yasPasTeam.getAbout().toString().trim());
                if(yasPasTeam.getIsHide()==0)
                    {
                        mTvHide.setText(getString(R.string.add_syte_edit_tm_mem_page_btn_hide));
                    }
                else
                    {
                        mTvHide.setText(getString(R.string.add_syte_edit_tm_mem_page_btn_unhide));
                    }
            }
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
                        case R.id.xRelLaySave:
                            {
                                if(mValidateData())
                                    {
                                        if(mNetworkStatus.isNetworkAvailable())
                                            {
                                                mEditTeamMember();
                                            }
                                        else
                                            {
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteEditTeamMemberActivity.this,this);
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
                        case R.id.xTvDelete:
                            {
                                if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        mDeleteTeamMemberProfilePic();
                                    }
                                else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteEditTeamMemberActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }
                                break;
                            }
                        case R.id.xTvHide:
                            {
                                if(yasPasTeam.getIsHide()==0)
                                    {
                                        yasPasTeam.setIsHide(1);
                                        mTvHide.setText(getString(R.string.add_syte_edit_tm_mem_page_btn_unhide));
                                    }
                                else
                                    {
                                        yasPasTeam.setIsHide(0);
                                        mTvHide.setText(getString(R.string.add_syte_edit_tm_mem_page_btn_hide));
                                    }
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
        private void mEditTeamMember()
            {
                mPrgDia.show();
                yasPasTeam.setName(mEtTmName.getText().toString().trim());
                yasPasTeam.setDesignation(mEtTmDesignation.getText().toString().trim());
                yasPasTeam.setAbout(mEtTmProfile.getText().toString().trim());
                /*Checking if We are editing profile pic for this member or not
                        * If YES the setting profile pic as MediaUploadService.DUMMY_URL until profile pic is not updated to cloudinary and return cloudinary id for the profile pic
                        * If NO then doing nothing, hence profile pic will be as it is previously*/
                if(mImageLocalPath.trim().length()>0)
                    {

                        yasPasTeam.setProfilePic(MediaUploadService.DUMMY_URL);
                    }
                Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.YASPAS_TEAM).child(yasPasTeamId);
                mFireBsYasPasObj.setValue(yasPasTeam, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
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
                                        String old_cloudinary_id = "";
                                        if (yasPasTeam.getProfilePic().toString().trim().length() > 0 && !yasPasTeam.getProfilePic().toString().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                                            old_cloudinary_id = yasPasTeam.getProfilePic().toString().trim();
                                        }
                                        SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(AddSyteEditTeamMemberActivity.this);
                                        syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                                                SyteDataBaseConstant.C_MEDIA_TAR_TEAM_MEM_PROFILE_PIC,
                                                yasPasTeamId,
                                                mSyteId,
                                                MediaUploadService.DUMMY_URL,
                                                mImageLocalPath,
                                                old_cloudinary_id);
                                        Intent i = new Intent(AddSyteEditTeamMemberActivity.this, MediaUploadService.class);
                                        startService(i);
                                    }
                                    Intent intent = new Intent();
                                    Bundle mBun1 = new Bundle();
                                    yasPasTeams.setYasPasTeam(yasPasTeam);
                                    mBun1.putParcelable(StaticUtils.IPC_TEAM_MEMBER, yasPasTeams);
                                    mBun1.putInt(StaticUtils.IPC_TEAM_MEMBER_INDEX, mBun.getInt(StaticUtils.IPC_TEAM_MEMBER_INDEX));
                                    mBun1.putString(StaticUtils.IPC_ACTION, StaticUtils.IPC_ACTION_EDIT);
                                    intent.putExtras(mBun1);
                                    setResult(2, intent);
                                    mPrgDia.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            mPrgDia.dismiss();
                            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteEditTeamMemberActivity.this, AddSyteEditTeamMemberActivity.this);
                            customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                        }
                    }
                });
                } // END mEditTeamMember()
        private void mDeleteTeamMemberProfilePic()
            {
                new AsyncTask<String,Void,String>()
                    {
                        @Override
                        protected String doInBackground(String... params)
                            {
                                /*Checking if there is any image for this team member
                                * if YES then deleting the image from cloudinary*/
                                if(yasPasTeam.getProfilePic().toString().trim().length()>0 && !yasPasTeam.getProfilePic().toString().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                                    {
                                        Map result = new HashMap();
                                        try
                                            {
                                                mCloudinary.uploader().destroy(yasPasTeam.getProfilePic().toString().trim(), result);
                                            }
                                        catch (IOException e)
                                            {

                                            }
                                        return null;
                                    }
                                else
                                    {
                                        return null;
                                    }
                            }
                        @Override
                        protected void onPreExecute()
                            {
                                mPrgDia.show();
                            }
                        @Override
                        protected void onPostExecute(String re)
                            {
                                mDeleteTeamMember();
                            }
                    }.execute();
            } // END mDeleteTeamMember()
        private void mDeleteTeamMember()
            {
                // Deleting team member from Firebase
                Firebase mFireBaseSpecificTeamMember = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.YASPAS_TEAM).child(yasPasTeamId);
                mFireBaseSpecificTeamMember.removeValue();
                mFireBaseSpecificTeamMember.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (!dataSnapshot.hasChild(yasPasTeamId))
                        {
                            Intent intent = new Intent();
                            Bundle mBun1 = new Bundle();
                            mBun1.putInt(StaticUtils.IPC_TEAM_MEMBER_INDEX, mBun.getInt(StaticUtils.IPC_TEAM_MEMBER_INDEX));
                            mBun1.putString(StaticUtils.IPC_ACTION, StaticUtils.IPC_ACTION_DELETE);
                            intent.putExtras(mBun1);
                            setResult(2, intent);
                            mPrgDia.dismiss();
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError)
                    {

                    }
                });
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
                if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
                    {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
            }// END onDialogRightBtnClicked()
        private void showPicOptionDialog()
            {
                final Dialog mDialog = new Dialog(AddSyteEditTeamMemberActivity.this);
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
                        /*Intent intent = new Intent();
                        intent.setType("image*//**//*");
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);*/
                        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), StaticUtils.ACTION_GALLERY_PICK);
                    }
                });
                mLinLayCamera.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
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
                                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                                imageMap= GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, AddSyteEditTeamMemberActivity.this);
                                mImageLocalPath=imageMap.get("keyImagePath").toString();
                                ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvTeamMemberImage, mDisImgOpt);
                            }
                    }
            } // END onActivityResult();
    }// END AddSyteEditTeamMemberActivity
