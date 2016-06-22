package com.syte.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.CloudinaryContent;
import com.syte.models.ReportASyte;
import com.syte.models.ReportBug;
import com.syte.utils.GalleryHelper;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;


public class ReportASyteActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack;
    private EditText mEtReportTitle, mEtReportBody;
    private TextView mTvReportimg, mTvUpload, mTvReport, mTvWrdLft;
    private RadioGroup mIssue_typeGroup;
    private RadioButton mIssue_type;
    private InputMethodManager mInputManager;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private CloudinaryContent mCloudinaryContent;
    private DisplayImageOptions mDspImgOptions;
    private YasPasPreferences yasPasPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_asyte);
        mInItObjects();
        mInItWidgets();
    }

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mIssue_typeGroup = (RadioGroup) findViewById(R.id.xRadiotype);
        mEtReportTitle = (EditText) findViewById(R.id.xEtReportTitle);
        mEtReportBody = (EditText) findViewById(R.id.xEtReportBody);
        mTvReportimg = (TextView) findViewById(R.id.xTVimgpath);
        mTvUpload = (TextView) findViewById(R.id.xTvUpload);
        mTvUpload.setOnClickListener(this);
        mTvReport = (TextView) findViewById(R.id.xTvReport);
        mTvReport.setOnClickListener(this);
        mTvWrdLft = (TextView) findViewById(R.id.xTvWrdLft);
        yasPasPreferences = YasPasPreferences.GET_INSTANCE(ReportASyteActivity.this);
    }

    private void mInItObjects() {
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mCloudinaryContent = new CloudinaryContent();
        mCloudinaryContent.setsIndex(0);
        mCloudinaryContent.setsImageToBeUploaded("");
        mCloudinaryContent.setsImageToBeDeleted("");
        mCloudinaryContent.setsDeleteFlag(false);
        mCloudinaryContent.setsUploadFlag(false);
        mDspImgOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(R.dimen.my_profile_image_width_height)).considerExifParams(true).build();

        mNetworkStatus = new NetworkStatus(ReportASyteActivity.this);
        mPrgDia = new ProgressDialog(ReportASyteActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
    }

    private boolean mValidateData() {
        if (mEtReportTitle.getText().toString().trim().length() <= 0) {
            mEtReportTitle.setError(getString(R.string.err_report_a_bug_page_empty_title));
            StaticUtils.REQUEST_FOCUS(mEtReportTitle, mInputManager);
            return false;
        }
        if (mEtReportBody.getText().toString().trim().length() <= 0) {
            mEtReportBody.setError(getString(R.string.err_report_a_bug_page_empty_description));
            StaticUtils.REQUEST_FOCUS(mEtReportBody, mInputManager);
            return false;
        }
        return true;
    }// END mValidateData()

    private void showPicOptionDialog() {
        final Dialog mDialog = new Dialog(ReportASyteActivity.this);
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

    private void mPostReport() {
        Firebase mObjFireBaseReportBug = new Firebase(StaticUtils.REPORT_A_SYTE);
        ReportASyte reportsyte = new ReportASyte();
        reportsyte.setIssuetype(mIssue_type.getText().toString());
        reportsyte.setTitle(mEtReportTitle.getText().toString().trim());
        reportsyte.setDescription(mEtReportBody.getText().toString().trim());
        reportsyte.setmRegisteredNum(yasPasPreferences.sGetRegisteredNum());
        reportsyte.setUserName(yasPasPreferences.sGetUserName());
        reportsyte.setDateTime(ServerValue.TIMESTAMP);
        reportsyte.setImageUrl(mTvReportimg.getText().toString());
        mObjFireBaseReportBug.push().setValue(reportsyte, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mPrgDia.dismiss();
                if (firebaseError == null) {
                    mIssue_typeGroup.check(R.id.xIncorrecttype);
                    mEtReportTitle.setText("");
                    mEtReportBody.setText("");
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(ReportASyteActivity.this, ReportASyteActivity.this);
                    customDialogs.sShowDialog_Common("Thankyou!", getString(R.string.err_report_a_syte_page_reported), null, null, "OK", "RB", false, false);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(ReportASyteActivity.this, ReportASyteActivity.this);
                    customDialogs.sShowDialog_Common(null, getString(R.string.err_report_a_syte_page_not_reported), null, null, "OK", "RB", false, false);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode == StaticUtils.ACTION_GALLERY_PICK) {
                mTvReportimg.setVisibility(View.VISIBLE);
                mCloudinaryContent.setsIndex(0);
                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                imageMap = GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, ReportASyteActivity.this);
                // ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvProfilepic, mDspImgOptions);
                mCloudinaryContent.setsImageToBeUploaded(imageMap.get("keyImagePath").toString());
                mCloudinaryContent.setsUploadFlag(true);
                Log.d("Report image", imageMap.get("keyImagePath").toString());
                mTvReportimg.setText(imageMap.get("keyImagePath").toString());
            }
        }
    }// END onActivityResult();

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
                    // get selected radio button from radioGroup
                    int selectedId = mIssue_typeGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    mIssue_type = (RadioButton) findViewById(selectedId);
                    Log.d("Report image", mTvReportimg.getText().toString());
                    mPostReport();
                    Toast.makeText(ReportASyteActivity.this,
                            mIssue_type.getText() + mTvReportimg.getText().toString(), Toast.LENGTH_SHORT).show();

                }
                break;
            }
            default: {
                break;
            }

        }
    }

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
}//END ReportASyteActivity
