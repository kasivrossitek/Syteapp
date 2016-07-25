package com.syte.activities.sytedetailuser;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.activities.ReportASyteActivity;
import com.syte.activities.chat.UserChatWindowActivity;
import com.syte.fragments.BulletinBoardUserFragment;
import com.syte.fragments.SyteDetailAboutUsFragment;
import com.syte.fragments.SyteDetailTeamMembersFragment;
import com.syte.fragments.SyteDetailWhatWeDoFragment;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.AboutUs;
import com.syte.models.AnalyticsVisitor;
import com.syte.models.Followers;
import com.syte.models.FollowingYasPas;
import com.syte.models.Syte;
import com.syte.models.WhatWeDo;
import com.syte.models.YasPasPush;
import com.syte.models.YasPasTeam;
import com.syte.utils.NetworkStatus;
import com.syte.utils.SendAnalyticsVisitor;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;
import com.syte.widgets.EnlargeImageDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by khalid.p on 23-02-2016.
 */
public class SyteDetailUserActivity extends AppCompatActivity implements View.OnClickListener, OnCustomDialogsListener {
    private Bundle mBundle;
    private String mSyteId;
    private boolean mIsFollowedSyte;
    private DisplayImageOptions mDisImgOpt;
    //private Transformation cTransformation;
    private Cloudinary mCloudinary;
    private NetworkStatus mNetworkStatus;
    private Syte mSyte;
    private Firebase mFirebaseGenInfo;
    private EventListenerGenInfo eventListenerGenInfo;
    private AboutUs aboutUs;
    private WhatWeDo whatWeDo;
    private YasPasPreferences mYasPasPref;
    private ProgressDialog mPrgDia;
    //TOOLBAR USER SPECIFIC
    private RelativeLayout mRelLayBack, mRelLayChat, mRelLayFavourite, mRelLayMenuSettings, mRellayTopBarUser;
    private ImageView mIvFavourite;
    //SYTE INFO
    private TextView mTvSyteName, mTvSyteCity;
    private ImageView mIvSyteBanner;
    private ProgressBar mPbSyteBannerImage;
    private LinearLayout mLinLaySyteCategory, mLinLaySyteCategoryMain;
    //SYTE ABOUT US, TEAM MEMBERS, WHAT WE DO
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter mAdapterTab;
    // BULLETIN BOARD
    private FrameLayout mFlBulletinBoard;
    // CONTACT INFO
    private LinearLayout mLinLaySytePhone, mLinLayContactUs;
    private TextView mTvMobileNumber;
    private ImageView mIvPhIcn;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIvSyteBanner.setImageResource(0);
        mIvSyteBanner = null;

    } //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syte_detail_user);
        mInItObjects();
        mInItWidgets();
        mSendAnalyticsData();
        mFirebaseGenInfo = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        eventListenerGenInfo = new EventListenerGenInfo();
        mAdapterTab = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapterTab);
        tabLayout.setupWithViewPager(viewPager);
        mSetBulletinBoard();
    }// END onCreate()

    private void mSendAnalyticsData() {
        AnalyticsVisitor analyticsSyteCumBulletin = new AnalyticsVisitor();
        analyticsSyteCumBulletin.setVisitorRegisteredNum(mYasPasPref.sGetRegisteredNum());
        analyticsSyteCumBulletin.setVisitorGender(mYasPasPref.sGetUserGender());
        analyticsSyteCumBulletin.setVisitedSyteId(mSyteId);
        analyticsSyteCumBulletin.setVisitedBulletinId("");
        analyticsSyteCumBulletin.setVisitorLatitude(HomeActivity.LOC_ACTUAL.getLatitude());
        analyticsSyteCumBulletin.setVisitorLongitude(HomeActivity.LOC_ACTUAL.getLongitude());
        analyticsSyteCumBulletin.setVisitedTime(ServerValue.TIMESTAMP);
        new SendAnalyticsVisitor(analyticsSyteCumBulletin);
    }// END mSendAnalyticsData()

    @Override
    protected void onResume() {
        super.onResume();
        mPrgDia.show();
        mFirebaseGenInfo.addValueEventListener(eventListenerGenInfo);
    } // END onResume()

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseGenInfo.removeEventListener(eventListenerGenInfo);
    }// END onPause()

    private void mInItObjects() {
        mYasPasPref = YasPasPreferences.GET_INSTANCE(SyteDetailUserActivity.this);
        mBundle = getIntent().getExtras();
        mSyteId = mBundle.getString(StaticUtils.IPC_SYTE_ID);
        mIsFollowedSyte = mBundle.getBoolean(StaticUtils.IPC_FOLLOWED_SYTE);
        mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        //cTransformation=new Transformation().width(720).height(240).crop("scale").quality(100);
        mNetworkStatus = new NetworkStatus(SyteDetailUserActivity.this);
        mPrgDia = new ProgressDialog(SyteDetailUserActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mDisImgOpt = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.img_syte_no_image)
                .showImageOnFail(R.drawable.img_syte_no_image)
                .showImageOnLoading(R.drawable.img_syte_no_image).build();
        aboutUs = new AboutUs();
        aboutUs.setTitle("");
        aboutUs.setDescription("");
        whatWeDo = new WhatWeDo();
        whatWeDo.setTitle("");
        whatWeDo.setDescription("");
    }// END mInItObjects()

    private void mInItWidgets() {
        //TOOLBAR USER SPECIFIC
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mRelLayChat = (RelativeLayout) findViewById(R.id.xRelLayChat);
        mRelLayChat.setOnClickListener(this);
        mRelLayFavourite = (RelativeLayout) findViewById(R.id.xRelLayFavourite);
        mRelLayFavourite.setOnClickListener(this);
        mRelLayMenuSettings = (RelativeLayout) findViewById(R.id.xRelLayMenuSettings);
        mRelLayMenuSettings.setOnClickListener(this);
        mRellayTopBarUser = (RelativeLayout) findViewById(R.id.xRellayTopBarUser);
        mIvFavourite = (ImageView) findViewById(R.id.xIvFavourite);
        if (mIsFollowedSyte) {
            mIvFavourite.setImageResource(R.drawable.ic_yp_action_bar_fav_wite_selected);
        } else {
            mIvFavourite.setImageResource(R.drawable.ic_yp_action_bar_fav_wite_un_selected);
        }
        //SYTE INFO
        mTvSyteName = (TextView) findViewById(R.id.xTvSyteName);
        mTvSyteCity = (TextView) findViewById(R.id.xTvSyteCity);
        mIvSyteBanner = (ImageView) findViewById(R.id.xIvSyteBanner);
        mIvSyteBanner.setOnClickListener(this);
        mPbSyteBannerImage = (ProgressBar) findViewById(R.id.xPbBannerImage);
        // SYTE CATEGORY
        mLinLaySyteCategory = (LinearLayout) findViewById(R.id.xLinLaySyteCategory);
        mLinLaySyteCategoryMain = (LinearLayout) findViewById(R.id.xLinLaySyteCategoryMain);
        // ABOUT US, TEAM MEMBERS, WHAT WE DO
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // BULLETIN BOARD
        mFlBulletinBoard = (FrameLayout) findViewById(R.id.xFlBulletinBoard);
        //CONTACT INFO
        mLinLaySytePhone = (LinearLayout) findViewById(R.id.xLinLaySytePhone);
        mLinLaySytePhone.setOnClickListener(this);
        mLinLayContactUs = (LinearLayout) findViewById(R.id.xLinLayContactUs);
        mLinLayContactUs.setOnClickListener(this);
        mTvMobileNumber = (TextView) findViewById(R.id.xTvMobileNumber);
        mIvPhIcn = (ImageView) findViewById(R.id.xIvPhIcn);
    }// END mInItWidgets()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //TOOLBAR USER SPECIFIC
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xRelLayChat: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    ArrayList<String> arrayListSyteOwners = new ArrayList<>();
                    arrayListSyteOwners.add(mSyte.getOwner());
                    Bundle bundle = new Bundle();
                    bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                    bundle.putString(StaticUtils.IPC_SYTE_NAME, mSyte.getName());
                    bundle.putString(StaticUtils.IPC_SYTE_IMAGE, mSyte.getImageUrl());
                    bundle.putStringArrayList(StaticUtils.IPC_SYTE_OWNERS, arrayListSyteOwners);

                    Intent intentChatWindowActivity = new Intent(SyteDetailUserActivity.this, UserChatWindowActivity.class);
                    intentChatWindowActivity.putExtras(bundle);
                    startActivity(intentChatWindowActivity);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailUserActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xRelLayFavourite: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    mFavouriteManipulation();
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailUserActivity.this, this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
                break;
            }
            case R.id.xRelLayMenuSettings: {
                showMenuSettingsPopup();
                break;
            }
            case R.id.xLinLaySytePhone: {
                if (mSyte.getMobileNo().trim().length() > 0) {
                    String number = "tel:" + mSyte.getMobileNo();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    if (ActivityCompat.checkSelfPermission(SyteDetailUserActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }
                break;
            }
            case R.id.xLinLayContactUs: {
                showContactInfoPopup();
                break;
            }
            case R.id.xIvSyteBanner: {
                if (mSyte.getImageUrl().length() > 0) {
                    EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(SyteDetailUserActivity.this, mSyte.getImageUrl());
                    enlargeImageDialog.sShowEnlargeImageDialog();
                }
                break;
            }
            default:
                break;
        }
    }// END onClick()

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            finish();
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public final List<Fragment> mFragmentTab = new ArrayList<>();
        private final List<String> mFragmentTabTitle = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            mFragmentTab.add(0, new SyteDetailAboutUsFragment());
            mFragmentTab.add(1, new SyteDetailTeamMembersFragment());
            mFragmentTab.add(2, new SyteDetailWhatWeDoFragment());
            mFragmentTabTitle.add(0, getString(R.string.syte_detail_page_about_us));
            mFragmentTabTitle.add(1, getString(R.string.syte_detail_page_team));
            mFragmentTabTitle.add(2, getString(R.string.syte_detail_page_wwd));
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();

            Fragment fragment = mFragmentTab.get(position);
            if (position == 0) {
                data.putParcelable("abtus", aboutUs);
                fragment.setArguments(data);
            } else if (position == 1) {
                data.putParcelable("abtus", aboutUs);
                fragment.setArguments(data);
            } else {
                data.putParcelable("whatwedo", whatWeDo);
                fragment.setArguments(data);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentTab.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTabTitle.get(position);
        }
    }// END ViewPagerAdapter

    private void mSetBulletinBoard() {
        Fragment mCurrentFragment = new BulletinBoardUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
        // bundle.putSerializable("test",this);
        mCurrentFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.xFlBulletinBoard, mCurrentFragment).commit();
    }

    private class EventListenerGenInfo implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            mSyte = dataSnapshot.getValue(Syte.class);
            if (mSyte != null) {
                mTvSyteName.setText(mSyte.getName());
                mTvSyteCity.setText(mSyte.getCity());

                if (mSyte.getMobileNo().trim().length() > 0) {
                    mIvPhIcn.setVisibility(View.VISIBLE);
                    mTvMobileNumber.setText(mSyte.getMobileNo());
                } else {
                    mIvPhIcn.setVisibility(View.GONE);
                    mTvMobileNumber.setText(getString(R.string.syte_detail_page_no_mob_num));
                }

                String[] arrSyteCategory = mSyte.getCategory().split(",");
                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mLinLaySyteCategory.removeAllViews();
                for (int i = 0; i < arrSyteCategory.length; i++) {
                    LinearLayout.LayoutParams param;
                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout linLayHor = new LinearLayout(SyteDetailUserActivity.this);
                    linLayHor.setOrientation(LinearLayout.HORIZONTAL);
                    View viewTemp = li.inflate(R.layout.layout_category, null);
                    viewTemp.setLayoutParams(param);
                    TextView textViewTemp = (TextView) viewTemp.findViewById(R.id.xTvSyteCategory);
                    textViewTemp.setText(arrSyteCategory[i].toString().trim().toUpperCase());
                    linLayHor.addView(viewTemp);
                    mLinLaySyteCategory.addView(linLayHor);
                }
                if (mSyte.getCategory().toString().trim().length() > 0) {
                    mLinLaySyteCategoryMain.setVisibility(View.VISIBLE);
                } else {
                    mLinLaySyteCategoryMain.setVisibility(View.GONE);
                }
                                /*if(mSyte.getImageUrl().toString().trim().length()>0 || mSyte.getImageUrl().toString().trim().length()<=0)
                                    {*/
                String cURL = mCloudinary.url().generate(mSyte.getImageUrl().toString().trim());

                ImageLoader.getInstance().displayImage(cURL, mIvSyteBanner, mDisImgOpt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        mPbSyteBannerImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        mPbSyteBannerImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mPbSyteBannerImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        mPbSyteBannerImage.setVisibility(View.GONE);
                    }
                });
                //}
                aboutUs = mSyte.getAboutUs();
                whatWeDo = mSyte.getWhatWeDo();

                SyteDetailAboutUsFragment syteDetailAboutUsFragment = (SyteDetailAboutUsFragment) mAdapterTab.mFragmentTab.get(0);
                syteDetailAboutUsFragment.sUpdateAboutUs(aboutUs);

                SyteDetailWhatWeDoFragment syteDetailWhatWeDoFragment = (SyteDetailWhatWeDoFragment) mAdapterTab.mFragmentTab.get(2);
                syteDetailWhatWeDoFragment.sUpdateWhatWeDo(whatWeDo);

                //Setting Team Members
                ArrayList<YasPasTeam> yasPasTeams = new ArrayList<>();

                DataSnapshot dataSnapshotTeam = (DataSnapshot) dataSnapshot.child(StaticUtils.YASPAS_TEAM);
                if (dataSnapshotTeam.getValue() != null) {
                    Iterator<DataSnapshot> it = dataSnapshotTeam.getChildren().iterator();
                    while (it.hasNext()) {
                        DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                        YasPasTeam mTeam = dataSnapshot1.getValue(YasPasTeam.class);
                        if (mTeam.getIsHide() == 0) {
                            yasPasTeams.add(mTeam);
                        }
                        if (!it.hasNext()) {
                            SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment) mAdapterTab.mFragmentTab.get(1);
                            syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
                        }
                    }
                } else {
                    SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment) mAdapterTab.mFragmentTab.get(1);
                    syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
                }
                // Bulletin Board
                if (mSyte.getLatestBulletin().toString().trim().length() > 0) {
                    mFlBulletinBoard.setVisibility(View.VISIBLE);
                } else {
                    mFlBulletinBoard.setVisibility(View.GONE);
                }
                mPrgDia.dismiss();

            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    } //END EventListenerGenInfo

    private void mFavouriteManipulation() {
        final ProgressDialog mPrgDia = new ProgressDialog(SyteDetailUserActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mPrgDia.show();
        // Checking if current yaspas is logged in yaspasee's favourite or not
        if (mIsFollowedSyte) {
            // This Syte exists in YasPasee's following list
            // Removing logged in yaspasee from current syte's followers list
            Firebase mFireBaseYasPasFollower = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.FOLLOWERS_YASPAS).child(mYasPasPref.sGetRegisteredNum());
            //Removing YasPasee from Syte's Followers list
            mFireBaseYasPasFollower.removeValue();
            // Adding event listener to Syte's Followers list, if YasPasee is removed or not
            final Firebase mFireBaseYasPasFollowers = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.FOLLOWERS_YASPAS);
            mFireBaseYasPasFollowers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(mYasPasPref.sGetRegisteredNum())) {
                        // YasPasee is removed, hence removing event listener
                        mFireBaseYasPasFollowers.removeEventListener(this);
                        //Removing Syte from YasPasee's followings list
                        Firebase mFireBaseFollowing = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.FOLLOWING_YASPAS).child(mSyteId);
                        mFireBaseFollowing.removeValue();
                        final Firebase mFireBaseFollowings = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.FOLLOWING_YASPAS);
                        mFireBaseFollowings.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.hasChild(mSyteId)) {
                                    mFireBaseFollowings.removeEventListener(this);
                                    mIvFavourite.setImageResource(R.drawable.ic_yp_action_bar_fav_wite_un_selected);
                                    mIsFollowedSyte = false;
                                    mPrgDia.dismiss();
                                    //mPrgDia=null;
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        } else {
            // This Syte does not exist in YasPasee's following list
            // Getting YasPasee's personal data
            Followers mObjFollowers = new Followers();
            mObjFollowers.setRegisteredNum(mYasPasPref.sGetRegisteredNum());
            mObjFollowers.setUserGender(mYasPasPref.sGetUserGender());
            mObjFollowers.setUserName(mYasPasPref.sGetUserName());
            mObjFollowers.setUserProfilePic(mYasPasPref.sGetUserProfilePic());
            mObjFollowers.setDeviceToken(mYasPasPref.sGetGCMToken());
            mObjFollowers.setDateTime(ServerValue.TIMESTAMP);
            // Adding YasPasee to Syte's Followers list
            Firebase mFireBaseYasPas = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.FOLLOWERS_YASPAS).child(mYasPasPref.sGetRegisteredNum());
            mFireBaseYasPas.setValue(mObjFollowers, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        FollowingYasPas mObjFollowing = new FollowingYasPas();
                        mObjFollowing.setYasPasId(mSyteId);
                        mObjFollowing.setName(mSyte.getName());
                        mObjFollowing.setImageUrl(mSyte.getImageUrl());
                        // Adding Syte to YasPasee's Following List
                        Firebase mFireBaseFollowings = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.FOLLOWING_YASPAS).child(mSyteId);
                        mFireBaseFollowings.setValue(mObjFollowing, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError == null) {
                                    mIvFavourite.setImageResource(R.drawable.ic_yp_action_bar_fav_wite_selected);
                                    mIsFollowedSyte = true;
                                    mSendPushNotification(mPrgDia);
                                    //mPrgDia=null;
                                } else {
                                    mPrgDia.dismiss();
                                    //mPrgDia=null;
                                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailUserActivity.this, SyteDetailUserActivity.this);
                                    customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "ErrEcc", false, false);
                                }
                            }
                        });
                    } else {
                        mPrgDia.dismiss();
                        //mPrgDia=null;
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailUserActivity.this, SyteDetailUserActivity.this);
                        customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "ErrEcc", false, false);
                    }
                }
            });
        }
    }  // END mFavouriteManipulation()

    private void mSendPushNotification(final ProgressDialog progressDialog) {
        Firebase firebaseYasPasPushNotification = new Firebase(StaticUtils.YASPAS_PUSH_NOTIFICATION_URL);
        YasPasPush yasPasPush = new YasPasPush();
        yasPasPush.setSyteId(mSyteId);
        yasPasPush.setSyteName(mSyte.getName());
        yasPasPush.setSyteOwner(mSyte.getOwner());
        yasPasPush.setRegisteredNum(mYasPasPref.sGetRegisteredNum());
        yasPasPush.setUserName(mYasPasPref.sGetUserName());
        yasPasPush.setUserProfilePic(mYasPasPref.sGetUserProfilePic());
        yasPasPush.setDateTime(ServerValue.TIMESTAMP);
        yasPasPush.setPushType(StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_FOLOWER);
        firebaseYasPasPushNotification.push().setValue(yasPasPush, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                progressDialog.dismiss();
            }
        });
    }

    private void showContactInfoPopup() {
        final Dialog mDialog = new Dialog(SyteDetailUserActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_popup_contact);
        mDialog.show();
        final Window window = mDialog.getWindow();
        RelativeLayout mRelLayClose = (RelativeLayout) window.findViewById(R.id.xRelLayClose);
        mRelLayClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView mTvAddress = (TextView) window.findViewById(R.id.xTvAddress);
        String address = "";
        address = mSyte.getStreetAddress1();
        try {
            address = address + ", " + mSyte.getStreetAddress2();
        } catch (Exception e) {
        }
        address = address + ", " + mSyte.getCity();
        address = address + ", " + mSyte.getState();
        address = address + " - " + mSyte.getZipCode();
        address = address + " " + mSyte.getCountry();
        mTvAddress.setText(address);
        TextView mTvDirection = (TextView) window.findViewById(R.id.xTvDirection);
        mTvDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String googleMapQuery = "saddr=" + HomeActivity.LOC_ACTUAL.getLatitude() + "," + HomeActivity.LOC_ACTUAL.getLongitude()
                        + "&daddr=" + mSyte.getLatitude() + "," + mSyte.getLongitude();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" + googleMapQuery));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"); // Remove this if user must have option to open this in webview
                startActivity(intent);

            }
        });
        TextView mTvPhone = (TextView) window.findViewById(R.id.xTvPhone);
        mTvPhone.setText(mSyte.getMobileNo());
        TextView mTvCall = (TextView) window.findViewById(R.id.xTvCall);
        mTvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = "tel:" + mSyte.getMobileNo();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(SyteDetailUserActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);

            }
        });
        View mviewEmail = (View) window.findViewById(R.id.xViewEmail);
        TextView mHeaderEmail = (TextView) window.findViewById(R.id.xHeaderEmail);
        View mviewWebsite = (View) window.findViewById(R.id.xViewWebsite);
        TextView mHeaderWebsite = (TextView) window.findViewById(R.id.xHeaderWebsite);
        TextView mTvEmailId = (TextView) window.findViewById(R.id.xTvEmail);
        TextView mTvWebsite = (TextView) window.findViewById(R.id.xTvWebsite);
        ImageView mImgEmail = (ImageView) window.findViewById(R.id.ximgemail);
        ImageView mImgWeb = (ImageView) window.findViewById(R.id.ximgweb);
        if (!mSyte.getEmailid().toString().isEmpty()) {
            mImgEmail.setVisibility(View.VISIBLE);
            mTvEmailId.setText(mSyte.getEmailid());
        } else {
            mImgEmail.setVisibility(View.GONE);
            mviewEmail.setVisibility(View.GONE);
            mHeaderEmail.setVisibility(View.GONE);
        }
        if (!mSyte.getWebsite().toString().isEmpty()) {
            mImgWeb.setVisibility(View.VISIBLE);
            mTvWebsite.setText(mSyte.getWebsite());
            mTvWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mImgWeb.setVisibility(View.GONE);
            mviewWebsite.setVisibility(View.GONE);
            mHeaderWebsite.setVisibility(View.GONE);
        }
    }// END showContactInfoPopup()

    private void showMenuSettingsPopup() {
        final Dialog mDialog = new Dialog(SyteDetailUserActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_menu_settings);

        WindowManager.LayoutParams wmlp = mDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wmlp.y = mRellayTopBarUser.getHeight();


        mDialog.show();
        final Window window = mDialog.getWindow();
        TextView mTvClaimThisSyte = (TextView) window.findViewById(R.id.xTvClaimThisSyte);
        TextView mTvReportSyte = (TextView) window.findViewById(R.id.xTvReportSyte);
        mTvReportSyte.setVisibility(View.VISIBLE);
        mTvClaimThisSyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mNetworkStatus.isNetworkAvailable()) {
                    claimThisSyte();
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailUserActivity.this, SyteDetailUserActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            }
        });

        mTvReportSyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mNetworkStatus.isNetworkAvailable()) {
                    //report a  syte
                    Intent report_intent = new Intent(SyteDetailUserActivity.this, ReportASyteActivity.class);
                    report_intent.putExtra(StaticUtils.IPC_SYTE_ID,mSyteId);
                    report_intent.putExtra(StaticUtils.IPC_SYTE_NAME,mSyte.getName());
                    report_intent.putExtra(StaticUtils.IPC_SYTE_OWNERS,mSyte.getOwner());
                    startActivity(report_intent);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailUserActivity.this, SyteDetailUserActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            }
        });
    }// END showMenuSettingsPopup()

    private void claimThisSyte() {
        mPrgDia.show();
        Firebase firebaseYasPasPushNotification = new Firebase(StaticUtils.YASPAS_PUSH_NOTIFICATION_URL);
        YasPasPush yasPasPush = new YasPasPush();
        yasPasPush.setSyteId(mSyteId);
        yasPasPush.setSyteName(mSyte.getName());
        yasPasPush.setSyteOwner(mSyte.getOwner());
        yasPasPush.setRegisteredNum(mYasPasPref.sGetRegisteredNum());
        yasPasPush.setUserName(mYasPasPref.sGetUserName());
        yasPasPush.setUserProfilePic(mYasPasPref.sGetUserProfilePic());
        yasPasPush.setDateTime(ServerValue.TIMESTAMP);
        yasPasPush.setPushType(StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIMED);
        firebaseYasPasPushNotification.push().setValue(yasPasPush, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mPrgDia.dismiss();
            }
        });
    }

}
