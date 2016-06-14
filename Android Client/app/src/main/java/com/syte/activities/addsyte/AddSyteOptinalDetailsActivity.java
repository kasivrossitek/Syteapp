package com.syte.activities.addsyte;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.activities.editsyte.EditSyteTeamMembersActivity;
import com.syte.activities.sytedetailsponsor.SytePreviewActivity;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.CloudinaryContent;
import com.syte.models.Syte;
import com.syte.services.MediaUploadService;
import com.syte.utils.GalleryHelper;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 13-02-2016.
 */
public class AddSyteOptinalDetailsActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
        private RelativeLayout mRelLayBack,mRelLayAboutUs,mRelLayOurTeam,mRelLayWhtWeDo;
        private TextView mTvPreview,mTvPublish;
        private YasPasPreferences mYasPasPref;
        private ImageView mIvSyteImage;
        FloatingActionButton mIvAddSyteImg;
        private String mSyteId;
        private Bundle mBun;
        private Syte mSyte;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        //private ArrayList<YasPasTeams> mTeamMembers;
        private DisplayImageOptions mDisImgOpt;
        private CloudinaryContent mCloudinaryContent;
        //private Transformation cTransformation;
        private Cloudinary mCloudinary;
        private Firebase mFireBsYasPas;
        private ValueEventListenerYasPas valueEventListenerYasPas;
        private ProgressBar mPbUpLoader,mPbLoader;
        private class ValueEventListenerYasPas implements ValueEventListener
            {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot!=null && dataSnapshot.getValue()!=null)
                            {
                                Syte syte = dataSnapshot.getValue(Syte.class);
                                mSyte.setImageUrl(syte.getImageUrl());
                                mSetValues();
                            }
                    }
                @Override
                public void onCancelled(FirebaseError firebaseError)
                    {

                    }
            }
        @Override
        protected void onResume()
            {
                super.onResume();
                mFireBsYasPas.addValueEventListener(valueEventListenerYasPas);
            }
        @Override
        protected void onPause()
            {
                super.onPause();
                mFireBsYasPas.removeEventListener(valueEventListenerYasPas);
            }
        @Override
        protected void onDestroy()
            {
                super.onDestroy();
                mIvSyteImage.setImageResource(0);
                mIvSyteImage=null;
            }
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.acitivity_add_syte_optional_details);
                mInItObjects();
                mInItWidgets();
                mSetValues();
            }// END onCreate()
        private void mInItObjects()
            {
                mYasPasPref = YasPasPreferences.GET_INSTANCE(AddSyteOptinalDetailsActivity.this);
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                mBun = getIntent().getExtras();
                mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                //mTeamMembers= mBun.getParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST);
                //cTransformation=new Transformation().width(1440).height(480).crop("scale").quality(100);
                mNetworkStatus=new NetworkStatus(AddSyteOptinalDetailsActivity.this);
                mPrgDia = new ProgressDialog(AddSyteOptinalDetailsActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);

                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .showImageForEmptyUri(R.drawable.img_syte_no_image)
                        .showImageOnFail(R.drawable.img_syte_no_image)
                        .showImageOnLoading(R.drawable.img_syte_no_image).build();
                mCloudinaryContent=new CloudinaryContent();
                mFireBsYasPas=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                valueEventListenerYasPas=new ValueEventListenerYasPas();
            }// END mInItObjects
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mRelLayAboutUs=(RelativeLayout)findViewById(R.id.xRelLayAboutUs);
                mRelLayAboutUs.setOnClickListener(this);
                mRelLayOurTeam=(RelativeLayout)findViewById(R.id.xRelLayOurTeam);
                mRelLayOurTeam.setOnClickListener(this);
                mRelLayWhtWeDo=(RelativeLayout)findViewById(R.id.xRelLayWhtWeDo);
                mRelLayWhtWeDo.setOnClickListener(this);
                mTvPreview=(TextView)findViewById(R.id.xTvPreview);
                mTvPreview.setOnClickListener(this);
                mTvPublish=(TextView)findViewById(R.id.xTvPublish);
                mTvPublish.setOnClickListener(this);
                mIvAddSyteImg=(FloatingActionButton)findViewById(R.id.xIvAddSyteImg);
                mIvAddSyteImg.setOnClickListener(this);
                mIvSyteImage=(ImageView)findViewById(R.id.xIvSyteImage);
                mPbUpLoader=(ProgressBar)findViewById(R.id.xPbUpLoader);
                mPbLoader=(ProgressBar)findViewById(R.id.xPbLoader);
            }// END mInItObjects
        private void mSetValues()
            {
                if(mSyte.getImageUrl().length()>0 )
                    {
                        String cURL = mCloudinary.url().generate(mSyte.getImageUrl());
                        ImageLoader.getInstance().displayImage(cURL, mIvSyteImage, mDisImgOpt, new ImageLoadingListener()
                            {
                                @Override
                                public void onLoadingStarted(String imageUri, View view)
                                    {
                                        if(mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                                            {
                                                mPbUpLoader.setVisibility(View.VISIBLE);
                                                mPbLoader.setVisibility(View.GONE);
                                            }
                                        else
                                            {
                                                mPbUpLoader.setVisibility(View.GONE);
                                                mPbLoader.setVisibility(View.VISIBLE);
                                            }
                                    }
                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                                    {
                                        if(!mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                                            {
                                                mPbLoader.setVisibility(View.GONE);
                                            }
                                    }
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                                    {
                                        if(!mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                                            {
                                                mPbLoader.setVisibility(View.GONE);
                                            }
                                    }
                                @Override
                                public void onLoadingCancelled(String imageUri, View view)
                                    {

                                    }
                            });
                    }
            } // END mSetValues()
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
                        case R.id.xRelLayAboutUs:
                            {
                                Intent mInt_AddSyteAbout = new Intent(AddSyteOptinalDetailsActivity.this,AddSyteAboutUsActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                                mBdl.putString(StaticUtils.IPC_SYTE_ID,mSyteId);
                                mInt_AddSyteAbout.putExtras(mBdl);
                                startActivityForResult(mInt_AddSyteAbout, 100);
                                break;
                            }
                        case R.id.xRelLayOurTeam:
                            {
                                /*Intent mInt_AddSyteTeamMembers = new Intent(AddSyteOptinalDetailsActivity.this,AddSyteTeamMembersActivity.class);
                                Bundle mBdl = new Bundle();
                                //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                                mBdl.putString(StaticUtils.IPC_SYTE_ID,mSyteId);
                                mInt_AddSyteTeamMembers.putExtras(mBdl);
                                startActivityForResult(mInt_AddSyteTeamMembers, 2);
                                break;*/
                                Intent mInt_EditTeamMembers = new Intent(AddSyteOptinalDetailsActivity.this, EditSyteTeamMembersActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                mInt_EditTeamMembers.putExtras(mBdl);
                                startActivity(mInt_EditTeamMembers);
                                break;
                            }
                        case R.id.xRelLayWhtWeDo:
                            {
                                Intent mInt_AddSyteWWD = new Intent(AddSyteOptinalDetailsActivity.this,AddSyteWhatWeDoActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                mInt_AddSyteWWD.putExtras(mBdl);
                                startActivityForResult(mInt_AddSyteWWD, 300);
                                break;
                            }
                        case R.id.xTvPublish:
                            {
                                if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        mPublishSyte();
                                    }
                                else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }
                                break;
                            }
                        case R.id.xIvAddSyteImg:
                            {
                                if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        showPicOptionDialog();
                                    }
                                else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }
                                break;
                            }
                        case R.id.xTvPreview:
                            {
                                if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        Intent mIntSytePreview = new Intent(AddSyteOptinalDetailsActivity.this, SytePreviewActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                        mIntSytePreview.putExtras(bundle);
                                        startActivity(mIntSytePreview);
                                    }
                                else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }
                                break;
                            }
                        default:break;
                    }
            }// END onClick()
    private void mPublishSyte()
            {
                mPrgDia.show();
                //Adding this newly added syte to Geofire
                final Firebase mFireBsYasPasGeoObj = new Firebase(StaticUtils.YASPAS_GEO_LOC_URL);
                GeoFire mObjGeoFire = new GeoFire(mFireBsYasPasGeoObj);

                mObjGeoFire.setLocation(mSyteId, new GeoLocation(mSyte.getLatitude(), mSyte.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, FirebaseError error) {
                        if (error == null)
                            {
                                // making isActive =1 in yaspas
                                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                                HashMap<String, Object> mUpdateMap1 = new HashMap<String, Object>();
                                mUpdateMap1.put("isActive",1);
                                mUpdateMap1.put("dateModified",ServerValue.TIMESTAMP);

                                mFireBsYasPasObj.updateChildren(mUpdateMap1, new Firebase.CompletionListener()
                                    {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                            {
                                                if(firebaseError == null)
                                                    {
                                                        // making isActive =1 in user's owned yaspases
                                                        final Firebase mFireBsYasPaseeObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                                                        HashMap<String,Object> mUpdateMap = new HashMap<String, Object>();
                                                        mUpdateMap.put("isActive",1);
                                                        mFireBsYasPaseeObj.updateChildren(mUpdateMap, new Firebase.CompletionListener()
                                                        {
                                                            @Override
                                                            public void onComplete(FirebaseError firebaseError1, Firebase firebase)
                                                            {
                                                                if(firebaseError1==null)
                                                                {
                                                                    AddSyteLocationActivity.TO_BE_DELETED.finish();
                                                                    AddSyteAddressActivity.TO_BE_DELETED.finish();
                                                                    AddSyteDetailsActivity.TO_BE_DELETED.finish();
                                                                    mPrgDia.dismiss();
                                                                    finish();
                                                                }
                                                                else
                                                                {
                                                                    mPrgDia.dismiss();
                                                                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this, AddSyteOptinalDetailsActivity.this);
                                                                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                                                }
                                                            }
                                                        });
                                                    }
                                                else
                                                    {
                                                        mPrgDia.dismiss();
                                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this, AddSyteOptinalDetailsActivity.this);
                                                        customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                                    }
                                            }
                                    });


                            }
                        else
                            {
                                mPrgDia.dismiss();
                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this, AddSyteOptinalDetailsActivity.this);
                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                            }
                    }
                });
            }// END mAddGeoLocation()
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
                if (paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("ErrorOccurred"))
                    {
                        // Do nothing
                    }
                if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoSyteBanner"))
                    {
                        // do nothing
                    }
            }// END onDialogRightBtnClicked()
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                if((requestCode==100 || requestCode==300) && data!=null)
                    {
                        mSyte = data.getExtras().getParcelable(StaticUtils.IPC_SYTE);
                    }
                if(resultCode==Activity.RESULT_OK && (requestCode!=100 || requestCode!=300))
                {

                if(requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode==StaticUtils.ACTION_GALLERY_PICK)
                    {
                        if(mNetworkStatus.isNetworkAvailable())
                            {
                                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                                imageMap= GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, AddSyteOptinalDetailsActivity.this);
                                mCloudinaryContent.setsIndex(0);
                                mCloudinaryContent.setsImageToBeUploaded(imageMap.get("keyImagePath").toString());

                                if(mSyte.getImageUrl().length()>0 && !mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                                    {
                                        mCloudinaryContent.setsDeleteFlag(true);
                                        mCloudinaryContent.setsImageToBeDeleted(mSyte.getImageUrl());
                                    }
                                else
                                    {
                                        mCloudinaryContent.setsDeleteFlag(false);
                                        mCloudinaryContent.setsImageToBeDeleted("");
                                    }
                                mCloudinaryContent.setsUploadFlag(true);
                                /*ArrayList<CloudinaryContent> mCloudinaryCnts = new ArrayList<>();
                                mCloudinaryCnts.add(mCloudinaryContent);*/
                                /*CloudinaryUpload mObjUpload = new CloudinaryUpload(mCloudinaryCnts,this,new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG()),"");
                                mObjUpload.sManipulateData();*/
                                mAddBannerImage();
                            }
                        else
                            {
                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this,this);
                                customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                            }
                    }}
            }
        private void mAddBannerImage()
            {
                mPrgDia.show();
                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                mUpdateMap.put("imageUrl",MediaUploadService.DUMMY_URL);
                mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
                mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                            {
                                mSyte.setImageUrl(MediaUploadService.DUMMY_URL);
                                mSetValues();
                                //Checking if image to be uploaded
                                if(mCloudinaryContent.sImageToBeUploaded.length()>0)
                                    {
                                        SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(AddSyteOptinalDetailsActivity.this);
                                        syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                                                SyteDataBaseConstant.C_MEDIA_TAR_SYTE_BANNER,
                                                mYasPasPref.sGetRegisteredNum(),
                                                mSyteId,
                                                MediaUploadService.DUMMY_URL,
                                                mCloudinaryContent.sImageToBeUploaded,
                                                mCloudinaryContent.sImageToBeDeleted);
                                        Intent i = new Intent(AddSyteOptinalDetailsActivity.this, MediaUploadService.class);
                                        startService(i);
                                        mPrgDia.dismiss();
                                    }

                            }
                    });
            }// END mAddBannerImage()
        private void showPicOptionDialog()
            {
                final Dialog mDialog = new Dialog(AddSyteOptinalDetailsActivity.this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mDialog.setContentView(R.layout.layout_image_picker_selector_dialog);
                mDialog.show();
                final Window window = mDialog.getWindow();
                LinearLayout mLinLayCamera = (LinearLayout) window.findViewById(R.id.xLinLayCamera);
                LinearLayout mLinLayGallery = (LinearLayout) window.findViewById(R.id.xLinLayGallery);

                mLinLayGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
       /* @Override
        public void onUploadStarted()
            {
                mPrgDia.show();
            }
        @Override
        public void onUploadCompleted(ArrayList<String> paramCloudinaryIDs, String paramCallingMethod)
            {
                mSyte.setImageUrl(paramCloudinaryIDs.get(0));
                mUpdateSyteBanner();
            }*/
    /*private void mUpdateSyteBanner()
        {
            Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
            HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
            mUpdateMap.put("imageUrl",mSyte.getImageUrl());
            mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
            mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        final Firebase mFireBsYasPaseeObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                        HashMap<String, Object> mUpdateMap1 = new HashMap<String, Object>();
                        mUpdateMap1.put("imageUrl", mSyte.getImageUrl());
                        mFireBsYasPaseeObj.updateChildren(mUpdateMap1, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                                if (firebaseError == null) {
                                    mPrgDia.dismiss();
                                    String cURL = mCloudinary.url().generate(mSyte.getImageUrl());
                                    ImageLoader.getInstance().displayImage(cURL, mIvSyteImage, mDisImgOpt);
                                } else {
                                    mPrgDia.dismiss();
                                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this, AddSyteOptinalDetailsActivity.this);
                                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                }
                            }
                        });


                    } else {
                        mPrgDia.dismiss();
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteOptinalDetailsActivity.this, AddSyteOptinalDetailsActivity.this);
                        customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                    }
                }
            });
        }*/
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
            mBun1.putParcelable(StaticUtils.IPC_SYTE, mSyte);
            mBun1.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
            //mBun1.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
            intent.putExtras(mBun1);
            setResult(786, intent);
            finish();
        }// END mFun786()
    }// END AddSyteOptinalDetailsActivity
