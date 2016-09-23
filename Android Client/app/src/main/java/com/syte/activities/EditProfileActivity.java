package com.syte.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.adapters.AdapterRewardPoints;
import com.syte.adapters.AdapterRewardlevel;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.services.MediaUploadService;
import com.syte.utils.GalleryHelper;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;
import com.syte.widgets.EnlargeImageDialog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kumar.m on 14-03-2016.
 */
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, OnCustomDialogsListener {
    private ImageView mIvProfileImage;
    private EditText mEtName;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private DisplayImageOptions mDisImgOpt;
    private NetworkStatus mNetworkStatus;
    private YasPasPreferences mPref;
    private ProgressDialog mPrgDia;
    private InputMethodManager mInputManager;
    private ProgressBar mPbLoader;
    private String mImageLocalPath = "";
    private TextView mTvRewardpoints, mTvLevelTypName;
    private ImageView mIvlevelTypeImg;
    private ArrayList<String> mRewardPointsnames;
    private ArrayList<Integer> mRewardpoints;
    private ArrayList<String> mRewardLevelnames;
    private ArrayList<Integer> mRewardLevelImg, mRewardLevelpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mInItObjects();
        mInItWidgets();
        mSetValues();
        mSetArrayvalues();
    }//END onCreate()

    private void mSetArrayvalues() {
        mRewardPointsnames = new ArrayList<>();
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_CREATE_PRIVATE_SYTE);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_CREATE_PUBLIC_SYTE);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_CREATE_BULLETIN);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_EDIT_PUBLIC_SYTE);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_REPORT_SYTE);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_LIKE_BULLETIN);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_RECEIVE_LIKE_BULLETIN);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_DELETE_PRIVATE_SYTE);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_DELETE_PUBLIC_SYTE);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_DELETE_BULLETIN);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_UNLIKE_BULLETIN);
        mRewardPointsnames.add(StaticUtils.REWARD_NAME_RECEIVE_UNLIKE_BULLETIN);

        mRewardpoints = new ArrayList<>();
        mRewardpoints.add(StaticUtils.REWARD_POINTS_CREATE_PRIVATE_SYTE);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_CREATE_PUBLIC_SYTE);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_CREATE_BULLETIN);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_EDIT_PUBLIC_SYTE);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_REPORT_SYTE);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_LIKE_BULLETIN);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_RECEIVE_LIKE_BULLETIN);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_DELETE_PRIVATE_SYTE);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_DELETE_PUBLIC_SYTE);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_DELETE_BULLETIN);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_UNLIKE_BULLETIN);
        mRewardpoints.add(StaticUtils.REWARD_POINTS_RECEIVE_UNLIKE_BULLETIN);
        mRewardLevelnames = new ArrayList<>();
        mRewardLevelnames.add(StaticUtils.REWARD_LEVEL_NAME_6);
        mRewardLevelnames.add(StaticUtils.REWARD_LEVEL_NAME_5);
        mRewardLevelnames.add(StaticUtils.REWARD_LEVEL_NAME_4);
        mRewardLevelnames.add(StaticUtils.REWARD_LEVEL_NAME_3);
        mRewardLevelnames.add(StaticUtils.REWARD_LEVEL_NAME_2);
        mRewardLevelnames.add(StaticUtils.REWARD_LEVEL_NAME_1);
        mRewardLevelpoints = new ArrayList<>();
        mRewardLevelpoints.add(StaticUtils.REWARD_LEVEL_POINTS_6);
        mRewardLevelpoints.add(StaticUtils.REWARD_LEVEL_POINTS_5);
        mRewardLevelpoints.add(StaticUtils.REWARD_LEVEL_POINTS_4);
        mRewardLevelpoints.add(StaticUtils.REWARD_LEVEL_POINTS_3);
        mRewardLevelpoints.add(StaticUtils.REWARD_LEVEL_POINTS_2);
        mRewardLevelpoints.add(StaticUtils.REWARD_LEVEL_POINTS_1);
        mRewardLevelImg = new ArrayList<>();
        mRewardLevelImg.add(R.drawable.ic_reward_6);
        mRewardLevelImg.add(R.drawable.ic_reward_5);
        mRewardLevelImg.add(R.drawable.ic_reward_4);
        mRewardLevelImg.add(R.drawable.ic_reward_3);
        mRewardLevelImg.add(R.drawable.ic_reward_2);
        mRewardLevelImg.add(R.drawable.ic_reward_1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIvProfileImage.setImageResource(0);
        mIvProfileImage = null;
    }//END onDestroy()

    private void mInItObjects() {
        mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        cTransformation = new Transformation().width((int) (getResources().getDimension(R.dimen.profile_image_wd) / getResources().getDisplayMetrics().density))
                .height((int) (getResources().getDimension(R.dimen.profile_image_ht) / getResources().getDisplayMetrics().density)).crop("crop").gravity("face");
        mDisImgOpt = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.img_syte_user_no_image)
                .showImageOnFail(R.drawable.img_syte_user_no_image)
                .showImageOnLoading(R.drawable.img_syte_user_no_image)
                .considerExifParams(true).build();

        mNetworkStatus = new NetworkStatus(EditProfileActivity.this);
        mPref = YasPasPreferences.GET_INSTANCE(EditProfileActivity.this);
        mPrgDia = new ProgressDialog(EditProfileActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }//END mInItObjects()

    private void mInItWidgets() {
        findViewById(R.id.xRelLayBack).setOnClickListener(this);
        findViewById(R.id.xRelLaySave).setOnClickListener(this);
        findViewById(R.id.xFabEditProfilePic).setOnClickListener(this);
        mTvRewardpoints = (TextView) findViewById(R.id.xTvRewardPoints);
        mTvLevelTypName = (TextView) findViewById(R.id.xTvLevelTypName);
        mIvlevelTypeImg = (ImageView) findViewById(R.id.xIvLevelTypeImg);
        findViewById(R.id.xlinlaypointsinfo).setOnClickListener(this);
        findViewById(R.id.xlinlaylevelsinfo).setOnClickListener(this);
        mIvProfileImage = (ImageView) findViewById(R.id.xIvProfileImage);
        mIvProfileImage.setOnClickListener(this);
        mEtName = (EditText) findViewById(R.id.xEtName);
        mPbLoader = (ProgressBar) findViewById(R.id.xPbLoader);
    }//END mInItWidgets()

    private void mSetValues() {
        if (mPref.sGetUserProfilePic().length() > 0) {
            String cURL = mCloudinary.url().transformation(cTransformation).generate(mPref.sGetUserProfilePic());
            ImageLoader.getInstance().displayImage(cURL, mIvProfileImage, mDisImgOpt, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mPbLoader.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mPbLoader.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mPbLoader.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    mPbLoader.setVisibility(View.GONE);
                }
            });
        }
        mEtName.setText(mPref.sGetUserName());
        mGetRewardpoints();
    }//END mSetValues()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xIvProfileImage: {
                if (mPref.sGetUserProfilePic().trim().length() > 0 && !mPref.sGetUserProfilePic().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                    EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(EditProfileActivity.this, mPref.sGetUserProfilePic().trim());
                    enlargeImageDialog.sShowEnlargeImageDialog();
                }
                break;
            }
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xFabEditProfilePic: {
                showPicOptionDialog();
                break;
            }
            case R.id.xRelLaySave: {
                if (mValidateData()) {
                    if (mNetworkStatus.isNetworkAvailable()) {
                        mUpdateProfile();
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditProfileActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                    }
                }
                break;
            }
            case R.id.xlinlaypointsinfo:
                showRewardPointsDialog();
                break;
            case R.id.xlinlaylevelsinfo:
                showRewardLevelDialog();
                break;
            default:
                break;
        }
    }//END onClick()

    private void mGetRewardpoints() {
        Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum());
        mFireBsYasPasObj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot != null) {
                    if (dataSnapshot.hasChild("rewards")) {
                        Long rewards = (Long) dataSnapshot.child("rewards").getValue();
                        int totalrewards = rewards.intValue();
                        if(totalrewards>0) {
                            mTvRewardpoints.setText("" + totalrewards);
                        }else{
                            totalrewards=0;
                            mTvRewardpoints.setText("" + totalrewards);
                        }
                        if (totalrewards >= StaticUtils.REWARD_LEVEL_POINTS_1 && totalrewards < StaticUtils.REWARD_LEVEL_POINTS_2) {
                            mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_1);
                            mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_1);
                        } else if (totalrewards >= StaticUtils.REWARD_LEVEL_POINTS_2 && totalrewards < StaticUtils.REWARD_LEVEL_POINTS_3) {
                            mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_2);
                            mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_2);
                        } else if (totalrewards >= StaticUtils.REWARD_LEVEL_POINTS_3 && totalrewards < StaticUtils.REWARD_LEVEL_POINTS_4) {
                            mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_3);
                            mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_3);
                        } else if (totalrewards >= StaticUtils.REWARD_LEVEL_POINTS_4 && totalrewards < StaticUtils.REWARD_LEVEL_POINTS_5) {
                            mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_4);
                            mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_4);
                        } else if (totalrewards >= StaticUtils.REWARD_LEVEL_POINTS_5 && totalrewards < StaticUtils.REWARD_LEVEL_POINTS_6) {
                            mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_5);
                            mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_5);
                        } else if (totalrewards >= StaticUtils.REWARD_LEVEL_POINTS_6) {
                            mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_6);
                            mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_6);
                        }
                    } else {
                        mTvRewardpoints.setText("0");
                        mTvLevelTypName.setText(StaticUtils.REWARD_LEVEL_NAME_1);
                        mIvlevelTypeImg.setImageResource(R.drawable.ic_reward_1);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showPicOptionDialog() {
        final Dialog mDialog = new Dialog(EditProfileActivity.this);
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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    private void showRewardPointsDialog() {
        final Dialog mDialog = new Dialog(EditProfileActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_reward_points_dialog);
        mDialog.show();
        final Window window = mDialog.getWindow();
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditProfileActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView mListpoints = (RecyclerView) window.findViewById(R.id.xListpoints);
        mListpoints.setLayoutManager(layoutManager);
        AdapterRewardPoints mAdapter = new AdapterRewardPoints(this, mRewardPointsnames, mRewardpoints);
        mListpoints.setAdapter(mAdapter);
        RelativeLayout mRellayclose = (RelativeLayout) window.findViewById(R.id.xRellayclose);
        mRellayclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }// END show reward points Dialog()

    private void showRewardLevelDialog() {
        final Dialog mDialog = new Dialog(EditProfileActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_reward_level_dialog);
        mDialog.show();
        final Window window = mDialog.getWindow();
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditProfileActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView mListlevels = (RecyclerView) window.findViewById(R.id.xListlevels);
        mListlevels.setLayoutManager(layoutManager);
        AdapterRewardlevel mAdapterlevel = new AdapterRewardlevel(this, mRewardLevelnames, mRewardLevelpoints, mRewardLevelImg);
        mListlevels.setAdapter(mAdapterlevel);
        RelativeLayout mRellayclose = (RelativeLayout) window.findViewById(R.id.xRellayclose);
        mRellayclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }// END show reward level Dialog()

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode == StaticUtils.ACTION_GALLERY_PICK) {
                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                imageMap = GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, EditProfileActivity.this);
                mImageLocalPath = imageMap.get("keyImagePath").toString();
                ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvProfileImage, mDisImgOpt);
            }
        }
    } // END onActivityResult()

    private boolean mValidateData() {
        if (mEtName.getText().toString().trim().length() <= 0) {
            mEtName.setError(getString(R.string.err_msg_user_name_invalid));
            StaticUtils.REQUEST_FOCUS(mEtName, mInputManager);
            return false;
        }
        return true;
    }// END mValidateData()

    private void mUpdateProfile() {
        mPrgDia.show();
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("userName", mEtName.getText().toString().trim());
                /*Checking if We are editing profile pic for this member or not
                        * If YES the setting profile pic as MediaUploadService.DUMMY_URL until profile pic is not updated to cloudinary and return cloudinary id for the profile pic
                        * If NO then doing nothing, hence profile pic will be as it is previously*/
        if (mImageLocalPath.trim().length() > 0) {
            mUpdateMap.put("userProfilePic", MediaUploadService.DUMMY_URL);
        }
        Firebase firebaseYasPasee = new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum());
        firebaseYasPasee.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                                        /*Checking if image is updated
                                        * If YES, then starting bg upload service*/
                    if (mImageLocalPath.trim().length() > 0) {
                        String old_cloudinary_id = "";
                        // Checking if there was any previous image for the user
                        if (mPref.sGetUserProfilePic().toString().trim().length() > 0 && !mPref.sGetUserProfilePic().toString().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL)) {
                            old_cloudinary_id = mPref.sGetUserProfilePic().toString().trim();
                        }
                        mPref.sSetUserProfilePic(MediaUploadService.DUMMY_URL);
                        SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(EditProfileActivity.this);
                        syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                                SyteDataBaseConstant.C_MEDIA_TAR_USER_PROFILE_PIC,
                                mPref.sGetRegisteredNum(),
                                "-1",
                                MediaUploadService.DUMMY_URL,
                                mImageLocalPath,
                                old_cloudinary_id);
                        Intent i = new Intent(EditProfileActivity.this, MediaUploadService.class);
                        startService(i);
                    }
                    mPref.sSetUserName(mEtName.getText().toString().trim());
                    mPrgDia.dismiss();
                    setResult(StaticUtils.ACTION_EDIT_PROFILE_RESULT);
                    finish();
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(EditProfileActivity.this, EditProfileActivity.this);
                    customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                }
            }
        });
    }// END mUpdateProfile()

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
}
