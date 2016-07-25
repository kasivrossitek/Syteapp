package com.syte.activities.editsyte;

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
import com.syte.activities.sytedetailsponsor.SytePreviewActivity;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
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
 * Created by khalid.p on 01-03-2016.
 */
public class EditSyteMainActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack, mRelLaySyteDetails, mRelLaySyteLocation, mRelLaySyteAbtUs, mRelLaySyteTeamMembers, mRelLaySyteWwd;
    private ImageView mIvSyteImage;
    private FloatingActionButton mIvAddSyteImg;
    private TextView mTvPublish, mTvPreview;
    private View mBtmBtnDivider;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private String mSyteId;
    private Syte mSyte;
    private DisplayImageOptions mDisImgOpt;
    private Cloudinary mCloudinary;
    private Firebase mFirebaseSyte;
    private YasPasPreferences mYasPasPref;
    private ProgressBar mPbUpLoader, mPbLoader;
    private ValueEventListenerYasPas valueEventListenerYasPas;

    private class ValueEventListenerYasPas implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                Syte syte = dataSnapshot.getValue(Syte.class);
                mSyte.setImageUrl(syte.getImageUrl());
                mUpdateImage();
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    } // END ValueEventListenerYasPas

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseSyte.addValueEventListener(valueEventListenerYasPas);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseSyte.removeEventListener(valueEventListenerYasPas);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIvSyteImage.setImageResource(0);
        mIvSyteImage = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_syte_main);
        mInItObjects();
        mInItWidgets();
        mSetValues();
    }// END onCreate()

    private void mInItObjects() {
        mSyte = getIntent().getExtras().getParcelable(StaticUtils.IPC_SYTE);
        mSyteId = getIntent().getExtras().getString(StaticUtils.IPC_SYTE_ID);
        mFirebaseSyte = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        mNetworkStatus = new NetworkStatus(EditSyteMainActivity.this);
        mPrgDia = new ProgressDialog(EditSyteMainActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mDisImgOpt = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.img_syte_no_image)
                .showImageOnFail(R.drawable.img_syte_no_image)
                .showImageOnLoading(R.drawable.img_syte_no_image).build();

        mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        mYasPasPref = YasPasPreferences.GET_INSTANCE(EditSyteMainActivity.this);
        valueEventListenerYasPas = new ValueEventListenerYasPas();
    }//END mInItObjects()

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mRelLaySyteDetails = (RelativeLayout) findViewById(R.id.xRelLaySyteDetails);
        mRelLaySyteDetails.setOnClickListener(this);
        mRelLaySyteLocation = (RelativeLayout) findViewById(R.id.xRelLaySyteLocation);
        mRelLaySyteLocation.setOnClickListener(this);
        mRelLaySyteAbtUs = (RelativeLayout) findViewById(R.id.xRelLaySyteAbtUs);
        mRelLaySyteAbtUs.setOnClickListener(this);
        mRelLaySyteTeamMembers = (RelativeLayout) findViewById(R.id.xRelLaySyteTeamMembers);
        mRelLaySyteTeamMembers.setOnClickListener(this);
        mRelLaySyteWwd = (RelativeLayout) findViewById(R.id.xRelLaySyteWwd);
        mRelLaySyteWwd.setOnClickListener(this);
        mIvSyteImage = (ImageView) findViewById(R.id.xIvSyteImage);
        mIvAddSyteImg = (FloatingActionButton) findViewById(R.id.xIvAddSyteImg);
        mIvAddSyteImg.setOnClickListener(this);
        mTvPublish = (TextView) findViewById(R.id.xTvPublish);
        mTvPublish.setOnClickListener(this);
        mTvPreview = (TextView) findViewById(R.id.xTvPreview);
        mTvPreview.setOnClickListener(this);
        mBtmBtnDivider = (View) findViewById(R.id.xBtmBtnDivider);
        mPbUpLoader = (ProgressBar) findViewById(R.id.xPbUpLoader);
        mPbLoader = (ProgressBar) findViewById(R.id.xPbLoader);
    }//END mInItWidgets()

    private void mSetValues() {
        if (mSyte.getIsActive() == 0) {
            mTvPublish.setVisibility(View.VISIBLE);
            mBtmBtnDivider.setVisibility(View.VISIBLE);
        }
        if (mSyte.getImageUrl().toString().trim().length() > 0) {
            mUpdateImage();
        }
    } // END mSetValues()

    private void mUpdateImage() {
        if (mSyte.getImageUrl().length() > 0) {
            String cURL = mCloudinary.url().generate(mSyte.getImageUrl());
            ImageLoader.getInstance().displayImage(cURL, mIvSyteImage, mDisImgOpt, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if (mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                        mPbUpLoader.setVisibility(View.VISIBLE);
                        mPbLoader.setVisibility(View.GONE);
                    } else {
                        mPbUpLoader.setVisibility(View.GONE);
                        mPbLoader.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if (!mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                        mPbLoader.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (!mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                        mPbLoader.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    } // END mUpdateImage()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xRelLaySyteDetails: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    //  Intent mInt_AddSyteDetails = new Intent(EditSyteMainActivity.this, EditSyteDetailsActivity.class);
                    Intent mInt_AddSyteDetails = new Intent(EditSyteMainActivity.this, EditSyteLocationActivity.class);//kasi changed for public and private
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mInt_AddSyteDetails.putExtras(mBdl);
                    startActivityForResult(mInt_AddSyteDetails, 786);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xRelLaySyteLocation: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mInt_AddSyteDetails = new Intent(EditSyteMainActivity.this, EditSyteLocationActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mInt_AddSyteDetails.putExtras(mBdl);
                    startActivityForResult(mInt_AddSyteDetails, 786);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xRelLaySyteAbtUs: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mInt_AddSyteDetails = new Intent(EditSyteMainActivity.this, EditSyteAboutUsActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mInt_AddSyteDetails.putExtras(mBdl);
                    startActivityForResult(mInt_AddSyteDetails, 786);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xRelLaySyteTeamMembers: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mInt_EditTeamMembers = new Intent(EditSyteMainActivity.this, EditSyteTeamMembersActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mInt_EditTeamMembers.putExtras(mBdl);
                    startActivity(mInt_EditTeamMembers);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xRelLaySyteWwd: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mInt_AddSyteDetails = new Intent(EditSyteMainActivity.this, EditSyteWhatWeDoActivity.class);
                    Bundle mBdl = new Bundle();
                    mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mInt_AddSyteDetails.putExtras(mBdl);
                    startActivityForResult(mInt_AddSyteDetails, 786);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xIvAddSyteImg: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    showPicOptionDialog();
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xTvPublish: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    mPublishSyte();
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xTvPreview: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mIntSytePreview = new Intent(EditSyteMainActivity.this, SytePreviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mIntSytePreview.putExtras(bundle);
                    startActivity(mIntSytePreview);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            default:
                break;
        }
    }// END onClick()

    private void showPicOptionDialog() {
        final Dialog mDialog = new Dialog(EditSyteMainActivity.this);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode == StaticUtils.ACTION_GALLERY_PICK) {
                if (mNetworkStatus.isNetworkAvailable()) {
                    HashMap<String, Object> imageMap = new HashMap<String, Object>();
                    imageMap = GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, EditSyteMainActivity.this);
                    String _old_cloudinary_id = "";
                    if (mSyte.getImageUrl().length() > 0 && !mSyte.getImageUrl().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                        _old_cloudinary_id = mSyte.getImageUrl();
                    }
                    mAddBannerImage(imageMap.get("keyImagePath").toString(), _old_cloudinary_id);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }

            }
        }
        if (resultCode == 786) {
            mSyte = data.getExtras().getParcelable(StaticUtils.IPC_SYTE);
        }
    } // END onActivityResult();

    private void mAddBannerImage(final String newImageLocalPath, final String old_cloudinary_id) {
        mPrgDia.show();
        Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("imageUrl", MediaUploadService.DUMMY_URL);
        mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
        mYasPasPref.sSetSyteType(mSyte.getSyteType().toString().trim());
        mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mSyte.setImageUrl(MediaUploadService.DUMMY_URL);
                mSetValues();
                //Checking if image to be uploaded
                if (newImageLocalPath.trim().length() > 0) {
                    SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(EditSyteMainActivity.this);
                    syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                            SyteDataBaseConstant.C_MEDIA_TAR_SYTE_BANNER,
                            mYasPasPref.sGetRegisteredNum(),
                            mSyteId,
                            MediaUploadService.DUMMY_URL,
                            newImageLocalPath,
                            old_cloudinary_id);
                    Intent i = new Intent(EditSyteMainActivity.this, MediaUploadService.class);
                    startService(i);
                    mPrgDia.dismiss();
                }

            }
        });
    }// END mAddBannerImage()

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

    private void mPublishSyte() {
        mPrgDia.show();
        //Adding this newly added syte to Geofire
        final Firebase mFireBsYasPasGeoObj = new Firebase(StaticUtils.YASPAS_GEO_LOC_URL);
        GeoFire mObjGeoFire = new GeoFire(mFireBsYasPasGeoObj);
        mObjGeoFire.setLocation(mSyteId, new GeoLocation(mSyte.getLatitude(), mSyte.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, FirebaseError error) {
                if (error == null) {
                    // making isActive =1 in yaspas
                    Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                    HashMap<String, Object> mUpdateMap1 = new HashMap<String, Object>();
                    mUpdateMap1.put("isActive", 1);
                    mUpdateMap1.put("dateModified", ServerValue.TIMESTAMP);
                    mFireBsYasPasObj.updateChildren(mUpdateMap1, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) {
                                if (mSyte.getSyteType().toString().trim() == "Private") {
                                    // making isActive =1 in user's owned yaspases
                                    final Firebase mFireBsYasPaseeObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                                    HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                                    mUpdateMap.put("isActive", 1);
                                    mFireBsYasPaseeObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError1, Firebase firebase) {
                                            if (firebaseError1 == null) {
                                                mPrgDia.dismiss();
                                            } else {
                                                mPrgDia.dismiss();
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, EditSyteMainActivity.this);
                                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                            }
                                        }
                                    });
                                } else {
                                    // making isActive =1 in user's owned yaspases
                                    final Firebase mFireBsYasPaseeObj = new Firebase(StaticUtils.PUBLIC_YASPAS).child(mSyteId);
                                    HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                                    mUpdateMap.put("isActive", 1);
                                    mFireBsYasPaseeObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError1, Firebase firebase) {
                                            if (firebaseError1 == null) {
                                                mPrgDia.dismiss();
                                            } else {
                                                mPrgDia.dismiss();
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, EditSyteMainActivity.this);
                                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                                            }
                                        }
                                    });
                                }
                            } else {
                                mPrgDia.dismiss();
                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, EditSyteMainActivity.this);
                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                            }
                        }
                    });
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditSyteMainActivity.this, EditSyteMainActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });
    }// END mAddGeoLocation()

}// END EditSyteMainActivity()
