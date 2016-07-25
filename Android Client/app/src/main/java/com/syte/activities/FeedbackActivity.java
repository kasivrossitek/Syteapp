package com.syte.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.BuildConfig;
import com.syte.R;
import com.syte.YasPasApp;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.CloudinaryContent;
import com.syte.models.Feedback;
import com.syte.services.MediaUploadService;
import com.syte.utils.GalleryHelper;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

public class FeedbackActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack;
    private LinearLayout mLinlayimg;
    private EditText mEtFeedbackTitle, mEtFeedbackBody;
    private TextView mTvFeedbackimg, mTvUpload, mTvReport, mTvDeviceModel, mTvDeviceVersion, mTvAppVersion, mTvAppBuild, mTvWrdLft;
    private InputMethodManager mInputManager;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private CloudinaryContent mCloudinaryContent;
    private DisplayImageOptions mDspImgOptions;
    private YasPasPreferences yasPasPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mInItWidgets();
        mInItObjects();

    }

    private void mInItWidgets() {
        String title = getIntent().getExtras().getString(StaticUtils.FEED_BACK_NAME);
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mLinlayimg = (LinearLayout) findViewById(R.id.xLinlay);
        mEtFeedbackTitle = (EditText) findViewById(R.id.xEtFeedbackTitle);
        mEtFeedbackTitle.setText(title);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        StaticUtils.REMOVE_FOCUS(mEtFeedbackTitle, mInputManager);
        mEtFeedbackBody = (EditText) findViewById(R.id.xEtFeedbackBody);
        mTvUpload = (TextView) findViewById(R.id.xTvUpload);
        mTvUpload.setOnClickListener(this);
        mTvFeedbackimg = (TextView) findViewById(R.id.xTVimgpath);
        mTvReport = (TextView) findViewById(R.id.xTvReport);
        mTvReport.setOnClickListener(this);
        mTvDeviceModel = (TextView) findViewById(R.id.xTvModelName);
        mTvDeviceVersion = (TextView) findViewById(R.id.xTvModelversion);
        mTvAppVersion = (TextView) findViewById(R.id.xTvAppVersionName);
        mTvAppBuild = (TextView) findViewById(R.id.xTvAppBuildName);
        mTvWrdLft = (TextView) findViewById(R.id.xTvWrdLft);
        mEtFeedbackBody.addTextChangedListener(mTextWatcherWordCount);
        yasPasPreferences = YasPasPreferences.GET_INSTANCE(FeedbackActivity.this);
    }//END mInItWidgets

    private void mInItObjects() {
        mCloudinaryContent = new CloudinaryContent();
        mCloudinaryContent.setsIndex(0);
        mCloudinaryContent.setsImageToBeUploaded("");
        mCloudinaryContent.setsImageToBeDeleted("");
        mCloudinaryContent.setsDeleteFlag(false);
        mCloudinaryContent.setsUploadFlag(false);
        mDspImgOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(R.dimen.my_profile_image_width_height)).considerExifParams(true).build();
        mNetworkStatus = new NetworkStatus(FeedbackActivity.this);
        mPrgDia = new ProgressDialog(FeedbackActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
    }//END mInItObjects

    @Override
    protected void onResume() {
        super.onResume();
        mTvDeviceModel.setText(YasPasApp.getDeviceName());
        mTvDeviceVersion.setText(YasPasApp.getDeviceVersion());
        mTvAppVersion.setText(BuildConfig.VERSION_NAME);
        mTvAppBuild.setText("" + BuildConfig.VERSION_CODE);
    }

    private boolean mValidateData() {
        if (mEtFeedbackTitle.getText().toString().trim().length() <= 0) {
            mEtFeedbackTitle.setError(getString(R.string.err_feedback_page_empty_title));
            StaticUtils.REMOVE_FOCUS(mEtFeedbackTitle, mInputManager);
            return false;
        }
        if (mEtFeedbackBody.getText().toString().trim().length() <= 0) {
            mEtFeedbackBody.setError(getString(R.string.err_feedback_page_empty_description));
            StaticUtils.REQUEST_FOCUS(mEtFeedbackBody, mInputManager);
            return false;
        }
        if (mTvFeedbackimg.getVisibility() == View.GONE) {
            yasPasPreferences.sSetFeedbackImgUrl("");
        }
        return true;
    }// END mValidateData()

    private void showPicOptionDialog() {
        final Dialog mDialog = new Dialog(FeedbackActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode == StaticUtils.ACTION_GALLERY_PICK) {
                mLinlayimg.setVisibility(View.VISIBLE);
                mCloudinaryContent.setsIndex(0);
                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                imageMap = GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, FeedbackActivity.this);
                // ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvProfilepic, mDspImgOptions);
                mCloudinaryContent.setsImageToBeUploaded(imageMap.get("keyImagePath").toString());
                mCloudinaryContent.setsUploadFlag(true);
                mCloudinaryContent.setsDeleteFlag(false);
                mCloudinaryContent.setsImageToBeDeleted("");
                Log.d("Report image", imageMap.get("keyImagePath").toString());
                mTvUpload.setVisibility(View.GONE);
                mTvFeedbackimg.setText(imageMap.get("keyImagePath").toString());
                if (mNetworkStatus.isNetworkAvailable()) {
                    mAddFeedbackImage();
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(FeedbackActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }

            }
        }
    }// END onActivityResult();

    private void mPostReport() {
        Firebase mObjFireBaseReportBug = new Firebase(StaticUtils.REPORT_FEEDBACK);
        Feedback feedback = new Feedback();
        feedback.setFeedbackType(mEtFeedbackTitle.getText().toString().trim());
        feedback.setDescription(mEtFeedbackBody.getText().toString().trim());
        feedback.setmRegisteredNum(yasPasPreferences.sGetRegisteredNum());
        feedback.setUserName(yasPasPreferences.sGetUserName().trim());
        feedback.setDeviceModel(mTvDeviceModel.getText().toString().trim());
        feedback.setDeviceModelVersion(mTvDeviceVersion.getText().toString().trim());
        feedback.setAppVersionName(mTvAppVersion.getText().toString().trim());
        feedback.setAppBuildversion(mTvAppBuild.getText().toString().trim());
        feedback.setDateTime(ServerValue.TIMESTAMP);
        feedback.setImageUrl(yasPasPreferences.sGetFeedbackImgUrl());
        mObjFireBaseReportBug.push().setValue(feedback, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mPrgDia.dismiss();
                if (firebaseError == null) {
                    mEtFeedbackBody.setText("");
                    mLinlayimg.setVisibility(View.GONE);
                    mTvUpload.setVisibility(View.VISIBLE);
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(FeedbackActivity.this, FeedbackActivity.this);
                    customDialogs.sShowDialog_Common("Thankyou!", getString(R.string.err_feedback_page_reported), null, null, "OK", "RB", false, false);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(FeedbackActivity.this, FeedbackActivity.this);
                    customDialogs.sShowDialog_Common(null, getString(R.string.err_feedback_page_not_reported), null, null, "OK", "RB", false, false);
                }
            }
        });
    }

    private TextWatcher mTextWatcherWordCount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mTvWrdLft.setText(500 - (s.toString().length()) + " characters left");

        }
    }; // END mTextWatcherWordCount

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xTvUpload: {
                showPicOptionDialog();
                break;
            }
            case R.id.xTvReport: {
                if (mValidateData()) {
                    mPostReport();
                    Toast.makeText(FeedbackActivity.this,
                            mTvFeedbackimg.getText().toString(), Toast.LENGTH_SHORT).show();

                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void mAddFeedbackImage() {
        mPrgDia.show();
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("imageUrl", mTvFeedbackimg.getText().toString().trim());
        Firebase firebaseYasPasee = new Firebase(StaticUtils.YASPASEE_URL).child(yasPasPreferences.sGetRegisteredNum()).child("feedback");
        firebaseYasPasee.push().setValue(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                                        /*Checking if image is updated
                                        * If YES, then starting bg upload service*/

                    SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(FeedbackActivity.this);
                    syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                            SyteDataBaseConstant.C_MEDIA_TAR_FEED_BACK_PIC,
                            yasPasPreferences.sGetRegisteredNum(),
                            "-1",
                            MediaUploadService.DUMMY_URL,
                            mCloudinaryContent.sImageToBeUploaded,
                            mCloudinaryContent.sImageToBeDeleted);
                    Intent i = new Intent(FeedbackActivity.this, MediaUploadService.class);
                    startService(i);
                    mPrgDia.dismiss();


                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(FeedbackActivity.this, FeedbackActivity.this);
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
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
