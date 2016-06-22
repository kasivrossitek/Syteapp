package com.syte.activities.sytedetailsponsor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.fragments.BulletinBoardUserFragment;
import com.syte.fragments.SyteDetailAboutUsFragment;
import com.syte.fragments.SyteDetailTeamMembersFragment;
import com.syte.fragments.SyteDetailWhatWeDoFragment;
import com.syte.models.AboutUs;
import com.syte.models.Syte;
import com.syte.models.WhatWeDo;
import com.syte.models.YasPasTeam;
import com.syte.utils.StaticUtils;
import com.syte.widgets.EnlargeImageDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by khalid.p on 05-03-2016.
 */
public class SytePreviewActivity extends AppCompatActivity implements View.OnClickListener
    {
        private Bundle mBundle;
        private String mSyteId;
        private DisplayImageOptions mDisImgOpt;
        //private Transformation cTransformation;
        private Cloudinary mCloudinary;
        private Syte mSyte;
        private Firebase mFirebaseGenInfo;
        private EventListenerGenInfo eventListenerGenInfo;
        private AboutUs aboutUs;
        private WhatWeDo whatWeDo;
        private ProgressDialog mPrgDia;
        //TOOLBAR USER SPECIFIC
        private RelativeLayout mRelLayBack;
        //SYTE INFO
        private TextView mTvSyteName,mTvSyteCity;
        private ImageView mIvSyteBanner;
        private ProgressBar mPbSyteBannerImage;
        private LinearLayout mLinLaySyteCategory,mLinLaySyteCategoryMain;
        //SYTE ABOUT US, TEAM MEMBERS, WHAT WE DO
        private TabLayout tabLayout;
        private ViewPager viewPager;
        ViewPagerAdapter mAdapterTab;
        // BULLETIN BOARD
        private FrameLayout mFlBulletinBoard;
        // CONTACT INFO
        private TextView mTvMobileNumber;
        private LinearLayout mLinLayContactUs;
        private ImageView mIvPhIcn;
        @Override
        protected void onDestroy()
            {
                super.onDestroy();
                mIvSyteBanner.setImageResource(0);
                mIvSyteBanner=null;
            }
        @Override
        protected void onResume()
            {
                super.onResume();
                mPrgDia.show();
                mFirebaseGenInfo.addValueEventListener(eventListenerGenInfo);
            } // END onResume()
        @Override
        protected void onPause()
            {
                super.onPause();
                mFirebaseGenInfo.removeEventListener(eventListenerGenInfo);
            }// END onPause()
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_syte_preview);
                mInItObjects();
                mInItWidgets();
                mFirebaseGenInfo= new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                eventListenerGenInfo=new EventListenerGenInfo();
                mAdapterTab = new ViewPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(mAdapterTab);
                tabLayout.setupWithViewPager(viewPager);
                mSetBulletinBoard();
            }// END onCreate()
        private void mInItObjects()
            {
                mBundle=getIntent().getExtras();
                mSyteId=mBundle.getString(StaticUtils.IPC_SYTE_ID);
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                //cTransformation=new Transformation().width(1440).height(480).crop("scale").quality(100);
                mPrgDia = new ProgressDialog(SytePreviewActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .showImageForEmptyUri(R.drawable.img_syte_no_image)
                        .showImageOnFail(R.drawable.img_syte_no_image)
                        .showImageOnLoading(R.drawable.img_syte_no_image).build();
                aboutUs=new AboutUs();
                aboutUs.setTitle("");
                aboutUs.setDescription("");
                whatWeDo=new WhatWeDo();
                whatWeDo.setTitle("");
                whatWeDo.setDescription("");
            }// END mInItObjects()
        private void mInItWidgets()
            {
                //TOOLBAR USER SPECIFIC
                mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);

                //SYTE INFO
                mTvSyteName=(TextView)findViewById(R.id.xTvSyteName);
                mTvSyteCity=(TextView)findViewById(R.id.xTvSyteCity);
                mIvSyteBanner=(ImageView)findViewById(R.id.xIvSyteBanner);
                mIvSyteBanner.setOnClickListener(this);
                mPbSyteBannerImage=(ProgressBar)findViewById(R.id.xPbBannerImage);
                // SYTE CATEGORY
                mLinLaySyteCategory=(LinearLayout)findViewById(R.id.xLinLaySyteCategory);
                mLinLaySyteCategoryMain=(LinearLayout)findViewById(R.id.xLinLaySyteCategoryMain);
                // ABOUT US, TEAM MEMBERS, WHAT WE DO
                viewPager = (ViewPager) findViewById(R.id.viewpager);
                viewPager.setOffscreenPageLimit(2);
                tabLayout = (TabLayout) findViewById(R.id.tabs);
                // BULLETIN BOARD
                mFlBulletinBoard=(FrameLayout)findViewById(R.id.xFlBulletinBoard);
                //CONTACT INFO
                mTvMobileNumber=(TextView)findViewById(R.id.xTvMobileNumber);
                mLinLayContactUs = (LinearLayout) findViewById(R.id.xLinLayContactUs);
                mLinLayContactUs.setOnClickListener(this);
                mIvPhIcn=(ImageView)findViewById(R.id.xIvPhIcn);

            }// END mInItWidgets()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        //TOOLBAR USER SPECIFIC
                        case R.id.xRelLayBack:
                            {
                                finish();
                                break;
                            }
                        case R.id.xLinLayContactUs: {
                            showContactInfoPopup();
                            break;
                        }
                        case R.id.xIvSyteBanner:
                        {
                            if(mSyte.getImageUrl().length()>0)
                            {
                                EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(SytePreviewActivity.this,mSyte.getImageUrl());
                                enlargeImageDialog.sShowEnlargeImageDialog();
                            }
                            break;
                        }
                        default:
                        break;
                    }
            }// END onClick()
        class ViewPagerAdapter extends FragmentPagerAdapter
            {
                public final List<Fragment> mFragmentTab = new ArrayList<>();
                private final List<String> mFragmentTabTitle = new ArrayList<>();
                public ViewPagerAdapter(FragmentManager manager)
                    {
                        super(manager);
                        mFragmentTab.add(0, new SyteDetailAboutUsFragment());
                        mFragmentTab.add(1, new SyteDetailTeamMembersFragment());
                        mFragmentTab.add(2,new SyteDetailWhatWeDoFragment());
                        mFragmentTabTitle.add(0,getString(R.string.syte_detail_page_about_us));
                        mFragmentTabTitle.add(1,getString(R.string.syte_detail_page_team));
                        mFragmentTabTitle.add(2,getString(R.string.syte_detail_page_wwd));
                    }
                @Override
                public Fragment getItem(int position)
                    {
                        Bundle data = new Bundle();

                        Fragment fragment = mFragmentTab.get(position);
                        if(position==0)
                            {
                                data.putParcelable("abtus",aboutUs);
                                fragment.setArguments(data);
                            }
                        else if(position==1)
                            {
                                data.putParcelable("abtus",aboutUs);
                                fragment.setArguments(data);
                            }
                        else
                            {
                                data.putParcelable("whatwedo",whatWeDo);
                                fragment.setArguments(data);
                            }
                        return fragment;
                    }
                @Override
                public int getCount()
                    {
                        return mFragmentTab.size();
                    }
                @Override
                public CharSequence getPageTitle(int position)
                    {
                        return mFragmentTabTitle.get(position);
                    }
            }// END ViewPagerAdapter
        private class EventListenerGenInfo implements ValueEventListener
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        mSyte = dataSnapshot.getValue(Syte.class);
                        if(mSyte!=null)
                            {
                                mTvSyteName.setText(mSyte.getName());
                                mTvSyteCity.setText(mSyte.getCity());
                                if(mSyte.getMobileNo().trim().length()>0)
                                    {
                                        mIvPhIcn.setVisibility(View.VISIBLE);
                                        mTvMobileNumber.setText(mSyte.getMobileNo());
                                    }
                                else
                                    {
                                        mIvPhIcn.setVisibility(View.GONE);
                                        mTvMobileNumber.setText(getString(R.string.syte_detail_page_no_mob_num));
                                    }
                                String[] arrSyteCategory = mSyte.getCategory().split(",");
                                LayoutInflater li =  (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                mLinLaySyteCategory.removeAllViews();
                                for(int i=0;i<arrSyteCategory.length;i++)
                                    {
                                        LinearLayout.LayoutParams param;
                                        param = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        LinearLayout linLayHor = new LinearLayout(SytePreviewActivity.this);
                                        linLayHor.setOrientation(LinearLayout.HORIZONTAL);
                                        View viewTemp = li.inflate(R.layout.layout_category,null);
                                        viewTemp.setLayoutParams(param);
                                        TextView textViewTemp = (TextView) viewTemp.findViewById(R.id.xTvSyteCategory);
                                        textViewTemp.setText(arrSyteCategory[i].toString().trim().toUpperCase());
                                        linLayHor.addView(viewTemp);
                                        mLinLaySyteCategory.addView(linLayHor);
                                    }
                                if(mSyte.getCategory().toString().trim().length()>0)
                                    {
                                        mLinLaySyteCategoryMain.setVisibility(View.VISIBLE);
                                    }
                                else
                                    {
                                        mLinLaySyteCategoryMain.setVisibility(View.GONE);
                                    }
                                if(mSyte.getImageUrl().toString().trim().length()>0)
                                    {
                                        String cURL = mCloudinary.url().generate(mSyte.getImageUrl().toString().trim());
                                        ImageLoader.getInstance().displayImage(cURL, mIvSyteBanner, mDisImgOpt, new ImageLoadingListener()
                                            {
                                                @Override
                                                public void onLoadingStarted(String imageUri, View view)
                                                    {
                                                        mPbSyteBannerImage.setVisibility(View.VISIBLE);
                                                    }
                                                @Override
                                                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                                                    {
                                                        mPbSyteBannerImage.setVisibility(View.GONE);
                                                    }
                                                @Override
                                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                                                    {
                                                        mPbSyteBannerImage.setVisibility(View.GONE);
                                                    }
                                                @Override
                                                public void onLoadingCancelled(String imageUri, View view)
                                                    {
                                                        mPbSyteBannerImage.setVisibility(View.GONE);
                                                    }
                                            });
                                    }
                                aboutUs=mSyte.getAboutUs();
                                whatWeDo=mSyte.getWhatWeDo();

                            SyteDetailAboutUsFragment syteDetailAboutUsFragment = (SyteDetailAboutUsFragment)mAdapterTab.mFragmentTab.get(0);
                            syteDetailAboutUsFragment.sUpdateAboutUs(aboutUs);

                            SyteDetailWhatWeDoFragment syteDetailWhatWeDoFragment = (SyteDetailWhatWeDoFragment)mAdapterTab.mFragmentTab.get(2);
                            syteDetailWhatWeDoFragment.sUpdateWhatWeDo(whatWeDo);

                            //Setting Team Members
                            ArrayList<YasPasTeam>yasPasTeams=new ArrayList<>();

                            DataSnapshot dataSnapshotTeam = (DataSnapshot)dataSnapshot.child(StaticUtils.YASPAS_TEAM);
                            if(dataSnapshotTeam.getValue()!=null)
                                {
                                    Iterator<DataSnapshot> it = dataSnapshotTeam.getChildren().iterator();
                                    while (it.hasNext())
                                        {
                                            DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                                            YasPasTeam mTeam = dataSnapshot1.getValue(YasPasTeam.class);
                                            if(mTeam.getIsHide()==0)
                                                {
                                                    yasPasTeams.add(mTeam);
                                                }
                                            if (!it.hasNext())
                                                {
                                                    SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment)mAdapterTab.mFragmentTab.get(1);
                                                    syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
                                                }
                                        }
                                    }
                                else
                                    {
                                        SyteDetailTeamMembersFragment syteDetailTeamMembersFragment = (SyteDetailTeamMembersFragment)mAdapterTab.mFragmentTab.get(1);
                                        syteDetailTeamMembersFragment.sUpdateTeamMember(yasPasTeams);
                                    }
                                // Bulletin Board
                                if(mSyte.getLatestBulletin().toString().trim().length()>0)
                                    {
                                        mFlBulletinBoard.setVisibility(View.VISIBLE);
                                    }
                                else
                                    {
                                        mFlBulletinBoard.setVisibility(View.GONE);
                                    }
                                mPrgDia.dismiss();
                            }
                    }
                @Override
                public void onCancelled(FirebaseError firebaseError)
                    {

                    }
            } //END EventListenerGenInfo
        private void mSetBulletinBoard()
            {
                Fragment mCurrentFragment = new BulletinBoardUserFragment();
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                // bundle.putSerializable("test",this);
                mCurrentFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.xFlBulletinBoard, mCurrentFragment).commit();
            }
        private void showContactInfoPopup() {
            final Dialog mDialog = new Dialog(SytePreviewActivity.this);
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
            //TextView mTvDirection = (TextView) window.findViewById(R.id.xTvDirection);
            /*mTvDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String googleMapQuery = "saddr=" + HomeActivity.LOC_ACTUAL.getLatitude() + "," + HomeActivity.LOC_ACTUAL.getLongitude()
                            + "&daddr=" + mSyte.getLatitude() + "," + mSyte.getLongitude();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?" + googleMapQuery));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"); // Remove this if user must have option to open this in webview
                    startActivity(intent);

                }
            });*/
            TextView mTvPhone = (TextView) window.findViewById(R.id.xTvPhone);
            mTvPhone.setText(mSyte.getMobileNo());
            //TextView mTvCall = (TextView) window.findViewById(R.id.xTvCall);
            /*mTvCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String number = "tel:" + mSyte.getMobileNo();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    if (ActivityCompat.checkSelfPermission(SytePreviewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
            });*/
        }// END showContactInfoPopup()

    }// END SytePreviewActivity
