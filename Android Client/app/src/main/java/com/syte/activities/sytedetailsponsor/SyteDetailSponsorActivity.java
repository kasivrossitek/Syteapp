package com.syte.activities.sytedetailsponsor;

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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.activities.ReportASyteActivity;
import com.syte.activities.analytics.SyteAnalyticsActivity;
import com.syte.activities.chat.SponsorChatListActivity;
import com.syte.activities.editsyte.EditSyteMainActivity;
import com.syte.fragments.BulletinBoardSponsorFragment;
import com.syte.fragments.SyteDetailAboutUsFragment;
import com.syte.fragments.SyteDetailTeamMembersFragment;
import com.syte.fragments.SyteDetailWhatWeDoFragment;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnDeleteSyteRequest;
import com.syte.models.AboutUs;
import com.syte.models.Syte;
import com.syte.models.WhatWeDo;
import com.syte.models.YasPasTeam;
import com.syte.utils.DeleteASyteRequest;
import com.syte.utils.NetworkStatus;
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
public class SyteDetailSponsorActivity extends AppCompatActivity implements View.OnClickListener, OnCustomDialogsListener, OnDeleteSyteRequest {
    private Bundle mBundle;
    private String mSyteId;
    private DisplayImageOptions mDisImgOpt;
    //private Transformation cTransformation;
    private Cloudinary mCloudinary;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private Syte mSyte;
    private Firebase mFirebaseGenInfo, mFirebaseAboutUs, mFirebaseWwd, mFirebaseTeamMembers;
    private EventListenerGenInfo eventListenerGenInfo;
    private AboutUs aboutUs;
    private WhatWeDo whatWeDo;
    //TOOLBAR SPONSOR SPECIFIC
    private RelativeLayout mRelLayBackSponsor, mRelLayEditSyte, mRelLayFollowers, mRelLayAnalytics, mRelLayChat, mRelLayMenuSettings, mRellayTopBarSponsor;
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
    //private FrameLayout mFlBulletinBoard;

    // CONTACT INFO
    private LinearLayout mLinLaySytePhone, mLinLayContactUs;
    private TextView mTvMobileNumber;
    private ImageView mIvPhIcn;
    private YasPasPreferences mYasPasPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syte_detail_sponsor);
        mInItObjects();
        mInItWidgets();
        mFirebaseGenInfo = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        eventListenerGenInfo = new EventListenerGenInfo();
        mAdapterTab = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapterTab);
        tabLayout.setupWithViewPager(viewPager);
        //   mSetBulletinBoard();//kasi commented on 21-7-16
    }// END onCreate()

    @Override
    protected void onResume() {
        super.onResume();
        mPrgDia.show();
        mFirebaseGenInfo.addValueEventListener(eventListenerGenInfo);
    } // END onResume()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIvSyteBanner.setImageResource(0);
        mIvSyteBanner = null;

    } // END onResume()

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseGenInfo.removeEventListener(eventListenerGenInfo);
    }// END onPause()

    private void mInItObjects() {
        mBundle = getIntent().getExtras();
        mSyteId = mBundle.getString(StaticUtils.IPC_SYTE_ID);
        mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        mNetworkStatus = new NetworkStatus(SyteDetailSponsorActivity.this);
        mPrgDia = new ProgressDialog(SyteDetailSponsorActivity.this);
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
        mYasPasPreferences = YasPasPreferences.GET_INSTANCE(SyteDetailSponsorActivity.this);
    }// END mInItObjects()

    private void mInItWidgets() {
        mRelLayBackSponsor = (RelativeLayout) findViewById(R.id.xRelLayBackSponsor);
        mRelLayBackSponsor.setOnClickListener(this);
        mRelLayEditSyte = (RelativeLayout) findViewById(R.id.xRelLayEditSyte);
        mRelLayEditSyte.setOnClickListener(this);
        mRelLayFollowers = (RelativeLayout) findViewById(R.id.xRelLayFollowers);
        mRelLayFollowers.setOnClickListener(this);
        mRelLayAnalytics = (RelativeLayout) findViewById(R.id.xRelLayAnalytics);
        mRelLayAnalytics.setOnClickListener(this);
        mRelLayChat = (RelativeLayout) findViewById(R.id.xRelLayChat);
        mRelLayChat.setOnClickListener(this);
        mRelLayMenuSettings = (RelativeLayout) findViewById(R.id.xRelLayMenuSettings);
        mRelLayMenuSettings.setOnClickListener(this);
        mRellayTopBarSponsor = (RelativeLayout) findViewById(R.id.xRellayTopBarSponsor);
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
        //mFlBulletinBoard = (FrameLayout) findViewById(R.id.xFlBulletinBoard);

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
            case R.id.xRelLayBackSponsor: {
                finish();
                break;
            }
            case R.id.xRelLayEditSyte: {
                Intent mInt_EditSyte = new Intent(SyteDetailSponsorActivity.this, EditSyteMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                bundle.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                mInt_EditSyte.putExtras(bundle);
                startActivity(mInt_EditSyte);
                break;
            }
            case R.id.xRelLayFollowers: {
                Intent mInt_Followers = new Intent(SyteDetailSponsorActivity.this, SyteDetailFollowersActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);

                bundle.putParcelable(StaticUtils.IPC_SYTE, mSyte);

                mInt_Followers.putExtras(bundle);
                startActivity(mInt_Followers);
                break;
            }
            case R.id.xRelLayAnalytics: {
                Intent mInt_Analytics = new Intent(this, SyteAnalyticsActivity.class);
                mInt_Analytics.putExtra(StaticUtils.IPC_SYTE_ID, mSyteId);
                startActivity(mInt_Analytics);
                break;
            }
            case R.id.xLinLaySytePhone: {

                if (mSyte.getMobileNo().trim().length() > 0) {
                    String number = "tel:" + mSyte.getMobileNo();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    if (ActivityCompat.checkSelfPermission(SyteDetailSponsorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
            case R.id.xRelLayChat: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    if (mSyte.getSyteType().equalsIgnoreCase("Public")) {
                        Bundle bundle = new Bundle();
                        bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                        bundle.putString(StaticUtils.IPC_SYTE_NAME, mSyte.getName());

                        Intent intentSponsorChatListActivity = new Intent(SyteDetailSponsorActivity.this, SponsorChatListActivity.class);
                        intentSponsorChatListActivity.putExtras(bundle);
                        startActivity(intentSponsorChatListActivity);
                    } else {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
                }

                break;
            }
            case R.id.xRelLayMenuSettings: {
                if (mSyte.getSyteType().toString().trim().equalsIgnoreCase("Private")) {
                    showMenuSettingsPopup();
                } else if (mSyte.getSyteType().toString().trim().equalsIgnoreCase("Public")) {

                    showPublicMenuSettingsPopup();

                }


                break;
            }
            case R.id.xIvSyteBanner: {
                if (mSyte.getImageUrl().length() > 0) {

                    EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(SyteDetailSponsorActivity.this, mSyte.getImageUrl());
                    enlargeImageDialog.sShowEnlargeImageDialog();
                }
                break;
            }
            default:
                break;
        }
    }// END onClick()

    @Override
    public void onDeleteSyteRequestStrated() {
        mPrgDia.show();
    }// END onDeleteSyteRequestStrated()

    @Override
    public void onDeleteSyteRequestFinished() {
        mPrgDia.dismiss();
        Toast.makeText(this, YasPasMessages.DELETE_SYTE_SUCCESS, Toast.LENGTH_LONG).show();
        finish();
    }// END onDeleteSyteRequestFinished()

    @Override
    public void onDeleteSyteErrorOccured() {
        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, this);
        customDialogs.sShowDialog_Common(YasPasMessages.ERR_OCC_SUBJECT, YasPasMessages.DELETE_SYTE_ERR_OCC_HEADING, null, null, "OK", "deleteSyteError", false, false);
    }// END onDeleteSyteErrorOccured()

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
        Fragment mCurrentFragment = new BulletinBoardSponsorFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(StaticUtils.IPC_SYTE, mSyte);//kasi  on 21-7-16
        bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
        mCurrentFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.xFlBulletinBoard, mCurrentFragment).commit();
    }


    private class EventListenerGenInfo implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mSyte = dataSnapshot.getValue(Syte.class);
            if (mSyte != null) {
                mSetBulletinBoard();//kasi  on 21-7-16
                if (mSyte.getSyteType().toString().trim().equalsIgnoreCase("Private")) {
                    genPrivateInfo(dataSnapshot);
                } else if (mSyte.getSyteType().toString().trim().equalsIgnoreCase("Public")) {

                    genPublicInfo(dataSnapshot);

                }
            }

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    } //END EventListenerGenInfo

    private void genPrivateInfo(DataSnapshot dataSnapshot) {
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
            LinearLayout linLayHor = new LinearLayout(SyteDetailSponsorActivity.this);
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
                yasPasTeams.add(mTeam);
                if (!it.hasNext()) {
                    SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment) mAdapterTab.mFragmentTab.get(1);
                    syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
                }
            }
        } else {
            SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment) mAdapterTab.mFragmentTab.get(1);
            syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
        }

        mPrgDia.dismiss();

    }

    private void genPublicInfo(DataSnapshot dataSnapshot) {

        mTvSyteName.setText(mSyte.getName());
        mTvSyteCity.setText(mSyte.getCity());
        mRelLayChat.setOnClickListener(null);
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
            LinearLayout linLayHor = new LinearLayout(SyteDetailSponsorActivity.this);
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
                yasPasTeams.add(mTeam);
                if (!it.hasNext()) {
                    SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment) mAdapterTab.mFragmentTab.get(1);
                    syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
                }
            }
        } else {
            SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment) mAdapterTab.mFragmentTab.get(1);
            syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
        }

        mPrgDia.dismiss();

    }

    private void showContactInfoPopup() {
        final Dialog mDialog = new Dialog(SyteDetailSponsorActivity.this);
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
                if (ActivityCompat.checkSelfPermission(SyteDetailSponsorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

    private void showPublicContactInfoPopup() {
        final Dialog mDialog = new Dialog(SyteDetailSponsorActivity.this);
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
        LinearLayout mcontact = (LinearLayout) window.findViewById(R.id.xContactid);
        TextView mTvPhone = (TextView) window.findViewById(R.id.xTvPhone);
        mTvPhone.setText(mSyte.getMobileNo());
        TextView mTvCall = (TextView) window.findViewById(R.id.xTvCall);
        mTvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = "tel:" + mSyte.getMobileNo();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(SyteDetailSponsorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

        mcontact.setVisibility(View.GONE);
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
    }// END showPublicContactInfoPopup()

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
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("deleteSyte")) {
            Log.e("DELETE SYTE", mSyteId + "syte Type" + mSyte.getSyteType());
            if (mSyte.getSyteType().equalsIgnoreCase("Public")) {
                DeleteASyteRequest deleteASyteRequest = new DeleteASyteRequest(SyteDetailSponsorActivity.this, mSyteId, SyteDetailSponsorActivity.this, true);
                deleteASyteRequest.sDeleteSyteStart();
            } else {
                DeleteASyteRequest deleteASyteRequest = new DeleteASyteRequest(SyteDetailSponsorActivity.this, mSyteId, SyteDetailSponsorActivity.this, false);
                deleteASyteRequest.sDeleteSyteStart();
            }
        }

    }// END onDialogRightBtnClicked()

    private void showMenuSettingsPopup() {
        final Dialog mDialog = new Dialog(SyteDetailSponsorActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_menu_settings);
        WindowManager.LayoutParams wmlp = mDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wmlp.y = mRellayTopBarSponsor.getHeight();
        mDialog.show();
        final Window window = mDialog.getWindow();
        TextView mTvClaimThisSyte = (TextView) window.findViewById(R.id.xTvClaimThisSyte);
        mTvClaimThisSyte.setText("Delete this Syte");
        mTvClaimThisSyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mNetworkStatus.isNetworkAvailable()) {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, SyteDetailSponsorActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.DELETE_SYTE_SUBJECT, YasPasMessages.DELETE_SYTE_HEADING, null, "CANCEL", "DELETE", "deleteSyte", false, false);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, SyteDetailSponsorActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            }
        });
    }// END showMenuSettingsPopup()

    private void showPublicMenuSettingsPopup() {
        final Dialog mDialog = new Dialog(SyteDetailSponsorActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_menu_settings);
        WindowManager.LayoutParams wmlp = mDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wmlp.y = mRellayTopBarSponsor.getHeight();
        mDialog.show();
        final Window window = mDialog.getWindow();
        TextView mTvClaimThisSyte = (TextView) window.findViewById(R.id.xTvClaimThisSyte);
        TextView mTvReportSyte = (TextView) window.findViewById(R.id.xTvReportSyte);
        TextView mTvDeleteSyte = (TextView) window.findViewById(R.id.xTvDeleteSyte);
        mTvReportSyte.setVisibility(View.VISIBLE);
        mTvDeleteSyte.setVisibility(View.VISIBLE);
        mTvDeleteSyte.setText("Delete this Syte");
        mTvClaimThisSyte.setText("Claim this syte");
        mTvDeleteSyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mNetworkStatus.isNetworkAvailable()) {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, SyteDetailSponsorActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.DELETE_SYTE_SUBJECT, YasPasMessages.DELETE_SYTE_HEADING, null, "CANCEL", "DELETE", "deleteSyte", false, false);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, SyteDetailSponsorActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            }
        });

        mTvReportSyte.setText("Report Syte");
        mTvReportSyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mIntent_Reportasyte = new Intent(SyteDetailSponsorActivity.this, ReportASyteActivity.class);
                    mIntent_Reportasyte.putExtra(StaticUtils.IPC_SYTE_ID, mSyteId);
                    mIntent_Reportasyte.putExtra(StaticUtils.IPC_SYTE_NAME, mSyte.getName());
                    mIntent_Reportasyte.putExtra(StaticUtils.IPC_SYTE_OWNERS, mSyte.getOwner());
                    startActivity(mIntent_Reportasyte);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SyteDetailSponsorActivity.this, SyteDetailSponsorActivity.this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            }
        });
    }// END showMenuSettingsPopup()

}
