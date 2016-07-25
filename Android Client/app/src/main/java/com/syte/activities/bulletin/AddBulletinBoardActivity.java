package com.syte.activities.bulletin;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.syte.R;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.BulletinBoard;
import com.syte.models.BulletinBoardPush;
import com.syte.models.CloudinaryContent;
import com.syte.services.MediaUploadService;
import com.syte.utils.GalleryHelper;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;

/**
 * Created by khalid.p on 27-02-2016.
 */
public class AddBulletinBoardActivity extends Activity implements View.OnClickListener, OnCustomDialogsListener {
    private EditText mEtTmName, mEtTmDesignation;
    private TextView mTvSave;
    private RelativeLayout mRelLayBack;
    private CheckBox mCbSndToFlw;
    private ImageView mIvTeamMemberImage;
    private InputMethodManager mInputManager;
    private CloudinaryContent mCloudinaryContent;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private DisplayImageOptions mDisImgOpt;
    private String mSyteId;
    private BulletinBoard mBulletinBoard;
    private FloatingActionButton mIvAddTmProfilePic;
    private YasPasPreferences yasPasPreferences;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIvTeamMemberImage.setImageResource(0);
        mIvTeamMemberImage = null;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bulletin);
        mInItObjects();
        mInItWidgets();
    }// END onCreate()

    private void mInItObjects() {
        mSyteId = getIntent().getExtras().getString(StaticUtils.IPC_SYTE_ID);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mCloudinaryContent = new CloudinaryContent();
        mCloudinaryContent.setsIndex(0);
        mCloudinaryContent.setsImageToBeUploaded("");
        mCloudinaryContent.setsImageToBeDeleted("");
        mCloudinaryContent.setsDeleteFlag(false);
        mCloudinaryContent.setsUploadFlag(false);

        mNetworkStatus = new NetworkStatus(AddBulletinBoardActivity.this);
        mPrgDia = new ProgressDialog(AddBulletinBoardActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);

        mDisImgOpt = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();

        mBulletinBoard = new BulletinBoard();
        mBulletinBoard.setSubject("");
        mBulletinBoard.setBody("");
        mBulletinBoard.setImageUrl("");
        mBulletinBoard.setSendToAllFollowers(1);
        yasPasPreferences = YasPasPreferences.GET_INSTANCE(AddBulletinBoardActivity.this);
    }//END mInItObjects()

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mTvSave = (TextView) findViewById(R.id.xTvSave);
        mTvSave.setOnClickListener(this);
        mEtTmName = (EditText) findViewById(R.id.xEtTmName);
        mEtTmDesignation = (EditText) findViewById(R.id.xEtTmDesignation);
        mIvAddTmProfilePic = (FloatingActionButton) findViewById(R.id.xIvAddTmProfilePic);
        mIvAddTmProfilePic.setOnClickListener(this);
        mIvTeamMemberImage = (ImageView) findViewById(R.id.xIvTeamMemberImage);
        mCbSndToFlw = (CheckBox) findViewById(R.id.xCbSndToFlw);
        // Making Send to all followers as checked as default
        mCbSndToFlw.setChecked(true);
    }//END mInItWidgets()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xTvSave: {
                if (mValidateData()) {
                    if (mNetworkStatus.isNetworkAvailable()) {
                        mPrgDia.show();
                        addBulletin();
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddBulletinBoardActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
                }
                break;
            }
            case R.id.xIvAddTmProfilePic: {
                showPicOptionDialog();
                break;
            }
            default:
                break;
        }
    }// END onClick()

    private boolean mValidateData() {
        if (mEtTmName.getText().toString().trim().length() <= 0) {
            mEtTmName.setError(getString(R.string.err_add_bulletin_page_empty_title));
            StaticUtils.REQUEST_FOCUS(mEtTmName, mInputManager);
            return false;
        }
        if (mEtTmDesignation.getText().toString().trim().length() <= 0) {
            mEtTmDesignation.setError(getString(R.string.err_add_bulletin_page_empty_description));
            StaticUtils.REQUEST_FOCUS(mEtTmDesignation, mInputManager);
            return false;
        }
        mBulletinBoard.setSubject(mEtTmName.getText().toString().trim());
        mBulletinBoard.setBody(mEtTmDesignation.getText().toString().trim());
        mBulletinBoard.setSendToAllFollowers((mCbSndToFlw.isChecked()) == true ? 1 : 0);
        return true;
    }// END mValidateData()

    private void showPicOptionDialog() {
        final Dialog mDialog = new Dialog(AddBulletinBoardActivity.this);
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
                        //intent.setType("file*//*");
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
                HashMap<String, Object> imageMap = new HashMap<String, Object>();
                imageMap = GalleryHelper.GET_IMAGE_FILE_PATH(requestCode, data, AddBulletinBoardActivity.this);
                mCloudinaryContent.setsIndex(0);
                mCloudinaryContent.setsImageToBeUploaded(imageMap.get("keyImagePath").toString());
                mCloudinaryContent.setsImageToBeDeleted("");
                mCloudinaryContent.setsDeleteFlag(false);
                mCloudinaryContent.setsUploadFlag(true);
                ImageLoader.getInstance().displayImage(imageMap.get(GalleryHelper.KEY_IMAGE_URI).toString(), mIvTeamMemberImage, mDisImgOpt);
            }
        }
    } // END onActivityResult();

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            finish();
        }
    }// END onDialogLeftBtnClicked()

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }// END onDialogRightBtnClicked()

    private void addBulletin() {
        Firebase mFirebaseBulletin = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId);
        mBulletinBoard.setDateTime(ServerValue.TIMESTAMP);
       // mBulletinBoard.setOwner(yasPasPreferences.sGetRegisteredNum());
                /*Checking if bulletin is having image
                * 1. if no - then making Image Url is empty so that It will appear without image at Syte detail & bulletin read more pages
                * 2. if yes - then hardcoding "dummyUrl" as Image URL, so that It will appear with image at Syte detail & bulletin read more pages
                * and spinner will be shown until image is not uploaded from MediaUploadService BG*/
        if (mCloudinaryContent.sImageToBeUploaded.trim().length() > 0) {
            mBulletinBoard.setImageUrl(MediaUploadService.DUMMY_URL);
        } else {
            mBulletinBoard.setImageUrl("");
        }
        mFirebaseBulletin.push().setValue(mBulletinBoard, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, final Firebase firebase) {
                if (firebaseError == null) {
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long bulletinDtTm = (long) dataSnapshot.child("dateTime").getValue();
                                                                                    /*Setting the priority to bulletin so that it appears top of the bulletins' list
                                                                                    (0 - bulletinDtTm means the smallest value and we know data with lowest priority will be first if fetched normally that is by value) */
                            firebase.setPriority(0 - bulletinDtTm);

                            //Checking if image to be uploaded
                            if (mCloudinaryContent.sImageToBeUploaded.length() > 0) {
                                SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(AddBulletinBoardActivity.this);
                                syteDataBase.sInsertMedia(SyteDataBaseConstant.C_MEDIA_TYPE_IMAGE,
                                        SyteDataBaseConstant.C_MEDIA_TAR_BULLETIN,
                                        dataSnapshot.getKey(),
                                        mSyteId,
                                        MediaUploadService.DUMMY_URL,
                                        mCloudinaryContent.sImageToBeUploaded,
                                        mCloudinaryContent.sImageToBeDeleted);
                                Intent i = new Intent(AddBulletinBoardActivity.this, MediaUploadService.class);
                                startService(i);

                            }
                            mUpdateYasPasLatestBulletin(mBulletinBoard.getSubject(), dataSnapshot.getKey(), dataSnapshot.child("dateTime").getValue());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                } else {
                    mPrgDia.dismiss();
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddBulletinBoardActivity.this, AddBulletinBoardActivity.this);
                    customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "ErrEcc", false, false);
                }
            }
        });
    }// END addBulletin()

    private void mUpdateYasPasLatestBulletin(String paramSub, final String paramBulletinId, final Object paramBulletinDateTime) {
        // Updating added bulletin's subject/body as "latestBulletin" for particular YasPas
        Firebase oFrBse = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child("latestBulletin");
        oFrBse.setValue(paramSub, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    // Checking if push notification is supposed to be sent
                    if (mBulletinBoard.getSendToAllFollowers() == 1) {
                        // push notification is supposed to be sent, Writing Push message
                        Firebase firebasePushNotification = new Firebase(StaticUtils.BULLETIN_PUSH_NOTIFICATION_URL);
                        BulletinBoardPush bulletinBoardPush = new BulletinBoardPush();
                        bulletinBoardPush.setSyteId(mSyteId);
                        bulletinBoardPush.setSyteImageUrl("");
                        bulletinBoardPush.setSyteName("");
                        bulletinBoardPush.setBulletinId(paramBulletinId);
                        bulletinBoardPush.setBulletinSubject(mBulletinBoard.getSubject());
                        bulletinBoardPush.setBulletinImageUrl(mBulletinBoard.getImageUrl());
                        bulletinBoardPush.setBulletinDateTime(paramBulletinDateTime);
                        bulletinBoardPush.setPushType(StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_BULETTIN);

                        firebasePushNotification.push().setValue(bulletinBoardPush, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                try {
                                    mPrgDia.dismiss();
                                } catch (Exception e) {
                                }
                                finish();
                            }
                        });
                    } else {
                        // push notification is not supposed to be sent
                        try {
                            mPrgDia.dismiss();
                        } catch (Exception e) {
                        }
                        finish();
                    }
                } else {
                    try {
                        mPrgDia.dismiss();
                    } catch (Exception e) {
                    }
                    finish();
                }


            }
        });
    } // END mUpdateYasPasLatestBulletin()
} // END AddBulletinBoardActivity()
