package com.syte.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.R;
import com.syte.listeners.OnCloudinaryUploadListener;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.CloudinaryContent;
import com.syte.models.YasPasee;
import com.syte.utils.CloudinaryUpload;
import com.syte.utils.GalleryHelper;
import com.syte.utils.LoginStatus;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Khalid on 26-10-2015.
 */
public class SignUpActivity extends Activity implements View.OnClickListener, OnCloudinaryUploadListener, RadioGroup.OnCheckedChangeListener, OnCustomDialogsListener
    {
        private ImageView mIvProfilepic;
        private TextView mTvTermandCondition,mTvDone,mEtUsrName;
        private RadioGroup mRgGender;
        private NetworkStatus mNetworkStatus;
        private YasPasPreferences mYasPasPref;
        private CloudinaryContent mCloudinaryContent;
        private DisplayImageOptions mDspImgOptions;
        private ProgressDialog mPrgDia;
        private InputMethodManager mInputManager;
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_signup);
                mYasPasPref = YasPasPreferences.GET_INSTANCE(SignUpActivity.this);
                mYasPasPref.sSetLoginStatus(LoginStatus.SIGNUP);
                mInputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mCloudinaryContent=new CloudinaryContent();
                mCloudinaryContent.setsIndex(0);
                mCloudinaryContent.setsImageToBeUploaded("");
                mCloudinaryContent.setsImageToBeDeleted("");
                mCloudinaryContent.setsDeleteFlag(false);
                mCloudinaryContent.setsUploadFlag(false);
                mDspImgOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(R.dimen.my_profile_image_width_height)).considerExifParams(true).build();
                mPrgDia=new ProgressDialog(SignUpActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mInitWidgets();
                mNetworkStatus = new NetworkStatus(SignUpActivity.this);
            }// END onCreate()
        private void mInitWidgets()
            {
                mTvTermandCondition = (TextView) findViewById(R.id.xTvTermandCondition);
                mTvTermandCondition.setPaintFlags(mTvTermandCondition.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                mTvTermandCondition.setOnClickListener(this);
                mIvProfilepic=(ImageView)findViewById(R.id.xIvProfilepic);
                mIvProfilepic.setOnClickListener(this);
                mEtUsrName=(EditText)findViewById(R.id.xEtUsrName);
                mTvDone =(TextView)findViewById(R.id.xTvDone);
                mTvDone.setOnClickListener(this);
                mRgGender=(RadioGroup)findViewById(R.id.xRgGender);
                mRgGender.setOnCheckedChangeListener(this);
            } // END mInitWidgets()
        @Override
         public void onClick(View v)
            {
                if(v == mIvProfilepic)
                    {
                        showPicOptionDialog();
                    }
                else if(v == mTvTermandCondition)
                    {
                        mShowTermNConDialog();
                    }
                else if(v==mTvDone)
                     {

                        if(mValidateData())
                            {
                                if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        ArrayList<CloudinaryContent> mCloudinaryCnts = new ArrayList<>();
                                        mCloudinaryCnts.add(mCloudinaryContent);
                                        CloudinaryUpload mObjUpload = new CloudinaryUpload(mCloudinaryCnts,this,new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG()),"");
                                        mObjUpload.sManipulateData();
                                    }
                                else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SignUpActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }
                            }

                     }
                } // END onClick()
        private boolean mValidateData()
            {
               if(mEtUsrName.getText().toString().trim().length()<=0)
                {
                    mEtUsrName.setError(getString(R.string.err_msg_user_name_invalid));
                    StaticUtils.REQUEST_FOCUS(mEtUsrName,mInputManager);
                    return false;
                }
                return true;
            }
        private void showPicOptionDialog()
            {
                final Dialog mDialog = new Dialog(SignUpActivity.this);
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
        // @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode==Activity.RESULT_OK)
                {
                    if(requestCode == StaticUtils.ACTION_CAMERA_PICK || requestCode==StaticUtils.ACTION_GALLERY_PICK)
                        {
                            mCloudinaryContent.setsIndex(0);
                            HashMap<String, Object> imageMap = new HashMap<String, Object>();
                            imageMap= GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, SignUpActivity.this);
                            ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvProfilepic, mDspImgOptions);
                            mCloudinaryContent.setsImageToBeUploaded(imageMap.get("keyImagePath").toString());
                            mCloudinaryContent.setsUploadFlag(true);
                        }
                }

        } // END onActivityResult();
    private void mShowTermNConDialog()
        {
            final Dialog mDialog = new Dialog(SignUpActivity.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setContentView(R.layout.layout_tnc_dialog);
            WebView mWvTnC = (WebView)mDialog.getWindow().findViewById(R.id.xWvTnC);
            mWvTnC.loadUrl("file:///android_asset/webpage.html");
            mDialog.show();

        } // END mShowTermNConDialog()
    @Override
    public void onBackPressed()
        {
            //  super.onBackPressed();
        }
        @Override
        public void onUploadStarted()
            {
                mPrgDia.show();
            }

        @Override
        public void onUploadCompleted(ArrayList<String> paramCloudinaryIDs, String paramCallingMethod)
            {

                HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                mUpdateMap.put("userName", mEtUsrName.getText().toString().trim());
                mUpdateMap.put("userGender", mRgGender.getCheckedRadioButtonId()==R.id.xRbMale ? "Male" : "Female");
                mUpdateMap.put("userProfilePic", paramCloudinaryIDs.get(0));
                Firebase mFirebaseAuthDbUrl = new Firebase(StaticUtils.YASPASEE_URL);
                Firebase mChildFirebaseAuthDbUrl = mFirebaseAuthDbUrl.child(mYasPasPref.sGetRegisteredNum());
                mChildFirebaseAuthDbUrl.updateChildren(mUpdateMap);
                mChildFirebaseAuthDbUrl.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        mYasPasPref.sSetLoginStatus(LoginStatus.COMPLETED);
                        YasPasee objYasPasee = dataSnapshot.getValue(YasPasee.class);
                        mYasPasPref.sSetUserName(objYasPasee.getUserName());
                        mYasPasPref.sSetUserGender(objYasPasee.getUserGender());
                        mYasPasPref.sSetUserProfilePic(objYasPasee.getUserProfilePic());
                        Intent mIntMapActivity = new Intent(SignUpActivity.this, HomeActivity.class);
                        Bundle mBun = new Bundle();
                        mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
                        mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
                        mIntMapActivity.putExtras(mBun);
                        mPrgDia.dismiss();
                        startActivity(mIntMapActivity);
                        finish();
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError)
                    {
                        mPrgDia.dismiss();
                    }
                });
            }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
        {

        }

        @Override
        public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
            if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
                {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startMain);
                    finish();
                }

        }

        @Override
        public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
            if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
            {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }

        }
    }
