package com.syte.activities;


import android.app.Dialog;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.R;


import com.syte.YasPasApp;
import com.syte.fragments.HomeBulletinFragment;
import com.syte.fragments.MapFragment;
import com.syte.fragments.MyFavouriteSyteFragment;
import com.syte.fragments.MyMessagesNewFragment;
import com.syte.fragments.MyNotificationsFragment;
import com.syte.fragments.MySytesFragment;
import com.syte.fragments.SettingsFragment;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnLocationListener;
import com.syte.services.ChatCounterService;
import com.syte.utils.GoogleApiHelper;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

/**
 * Created by khalid.p on 08-02-2016.
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener, OnCustomDialogsListener, OnLocationListener {

    //Hamburger Drawer Menu Items
    private RelativeLayout mRelLayHome, mRelLayMySytes, mRelLayMyFriends, mRelLayMyMessages, mRelLayMyFavouriteSytes, mRelLayContactSupport, mRelLaySettings, mRelLayMap;
    private DrawerLayout mDrawerLayout;
    private TextView mTvMsgUnreadCount;
    private ProgressBar mPbChatMsgCounterLoader;
    // User's Related Data
    public ImageView mIvUserProfilepic;
    TextView mTvUserName;
    //Action Bar Items
    private RelativeLayout mRelLayHamBurger, mRelLayNotification, mRelLayFavourite, mRelLaySearch, mRelLayFilter, mRellayTop, mRelLaySwitch;
    private ImageView mIvFavourite, mIvNotificationIcon, mIvSwitch;
    private TextView mPageTitle;
    private Fragment mCurrentFragment;
    //Others
    private Bundle mBun;
    private YasPasPreferences mYasPasPref;
    public static boolean IS_FAVOURITE_CHECKED;
    private DisplayImageOptions mDspImgOptions;
    public static Location LOC_ACTUAL, LOC_VIRTUAL;
    public static float ZOOM_LEVEL;
    private GoogleApiHelper mGoogleApiHelper;
    // Cloudinary
    private Transformation mCUserTransformation;
    private Cloudinary mCloudinary;
    private BrdcstNotificationReceived brdcstNotificationReceived;
    private BrdcstChatMessages brdcstChatMessages;
    private Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mYasPasPref = YasPasPreferences.GET_INSTANCE(HomeActivity.this);
        mYasPasPref.sSetFollowingFlag(false);
        mYasPasPref.sSetFilterDistance(10);
        cont = this;
        brdcstNotificationReceived = new BrdcstNotificationReceived();
        brdcstChatMessages = new BrdcstChatMessages();
        int dime = (int) (getResources().getDimension(R.dimen.cloudinary_user_profile_pic_image_sz) / getResources().getDisplayMetrics().density);
        mCUserTransformation = new Transformation().width(dime).height(dime).crop("thumb").gravity("face").radius("max").quality(100);
        mDspImgOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.img_user_thumbnail_image_list) // All login background to be replaced by default profile pic in Displayoptions
                .showImageOnFail(R.drawable.img_user_thumbnail_image_list)
                .showImageOnLoading(R.drawable.img_user_thumbnail_image_list)
                .displayer(new RoundedBitmapDisplayer(dime))
                .build();
        mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());

        mInItWidgets();

        LOC_ACTUAL = new Location("");
        LOC_VIRTUAL = new Location("");
        if (getIntent().getExtras().getDouble("ipcCurrentLat") == 0.000000 && getIntent().getExtras().getDouble("ipcCurrentLong") == 0.000000) {
            LOC_ACTUAL.setLatitude(12.9589854);
            LOC_ACTUAL.setLongitude(77.6467042);
            LOC_VIRTUAL.setLatitude(12.9589854);
            LOC_VIRTUAL.setLongitude(77.6467042);
        } else {
            LOC_ACTUAL.setLatitude(getIntent().getExtras().getDouble("ipcCurrentLat"));
            LOC_ACTUAL.setLongitude(getIntent().getExtras().getDouble("ipcCurrentLong"));
            LOC_VIRTUAL.setLatitude(getIntent().getExtras().getDouble("ipcCurrentLat"));
            LOC_VIRTUAL.setLongitude(getIntent().getExtras().getDouble("ipcCurrentLong"));
        }
        ZOOM_LEVEL = StaticUtils.MAP_DEFAULT_ZOOM_LEVEL;
        mGoogleApiHelper = YasPasApp.getGoogleApiHelper(HomeActivity.this, this);
        Log.d("LOC_ACTUAL", LOC_ACTUAL + "");
        Log.d("LOC_VIRTUAL", LOC_VIRTUAL + "");
        mBun = new Bundle();
        mBun.putDouble("ipcCurrentLat", getIntent().getExtras().getDouble("ipcCurrentLat"));
        mBun.putDouble("ipcCurrentLong", getIntent().getExtras().getDouble("ipcCurrentLong"));
        setStartingFragment();

    }// END HomeActivity()

    private void setStartingFragment() {

        if (getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_BULETTIN ||
                getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_FOLOWER ||
                getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIMED ||
                getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_REJECTED ||
                getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_ACCEPTED) {
            mPageTitle.setText(getString(R.string.notification_screen_page_title));
            mCurrentFragment = new MyNotificationsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
            showFavourite_Search_Filter_Icon(false, false, false, false, false);
        } else if (getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_CHAT) {
            mBun.putString(StaticUtils.IPC_ONGOING_CHAT_ID, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_ID));
            mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID));
            mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE));
            mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM));
            mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME));

                        /*mPageTitle.setText(getString(R.string.my_messages_screen_page_title))
                        mCurrentFragment = new MyMessagesNewFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                        showFavourite_Search_Filter_Icon(false, false, false,false,true);*/

            mPageTitle.setText(getString(R.string.home_bulletin_screen_page_title));
            mCurrentFragment = new HomeBulletinFragment();
            mCurrentFragment.setArguments(mBun);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
            showFavourite_Search_Filter_Icon(true, false, true, true, true);
        } else {
            mPageTitle.setText(getString(R.string.home_bulletin_screen_page_title));
            mCurrentFragment = new HomeBulletinFragment();
            mCurrentFragment.setArguments(mBun);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
            showFavourite_Search_Filter_Icon(true, false, true, true, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = new Intent(HomeActivity.this, ChatCounterService.class);
        startService(i);

        System.out.println("mYasPasPref.sGetUserProfilePic() - " + mYasPasPref.sGetUserProfilePic());
        String cURL = mCloudinary.url().transformation(mCUserTransformation).generate(mYasPasPref.sGetUserProfilePic());
        ImageLoader.getInstance().displayImage(cURL, mIvUserProfilepic, mDspImgOptions);

        LocalBroadcastManager.getInstance(this).registerReceiver(brdcstNotificationReceived, new IntentFilter(StaticUtils.BRD_CST_AC_UPDATE_NOTIFICATION_ICON));
        LocalBroadcastManager.getInstance(this).registerReceiver(brdcstChatMessages, new IntentFilter(StaticUtils.BRD_CST_AC_CHAT));

        sUpdateNotificationIcon();
        if (StaticUtils.IS_GPS_TURNED_ON(HomeActivity.this)) {
            mGoogleApiHelper.sGoogleApiConnect();
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(HomeActivity.this, this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_GPS_SUBJECT, YasPasMessages.NO_GPS_HEADING, YasPasMessages.NO_GPS_BODY, "NO", "YES", "GPS", true, false);
        }
    }// END onResume()

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiHelper.sGoogleApiDisconnect();
        if (brdcstNotificationReceived != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(brdcstNotificationReceived);
        }
        if (brdcstChatMessages != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(brdcstChatMessages);
        }

    }

    //kasi
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void mInItWidgets() {
        mRelLayHome = (RelativeLayout) findViewById(R.id.xRelLayHome);
        mRelLayHome.setOnClickListener(this);
        mRelLayMap = (RelativeLayout) findViewById(R.id.xRelLayMap);
        mRelLayMap.setOnClickListener(this);
        mRelLayMySytes = (RelativeLayout) findViewById(R.id.xRelLayMySytes);
        mRelLayMySytes.setOnClickListener(this);
        mRelLayMyFriends = (RelativeLayout) findViewById(R.id.xRelLayMyFriends);
        mRelLayMyFriends.setOnClickListener(this);
        mRelLayMyMessages = (RelativeLayout) findViewById(R.id.xRelLayMyMessages);
        mRelLayMyMessages.setOnClickListener(this);
        mRelLayMyFavouriteSytes = (RelativeLayout) findViewById(R.id.xRelLayMyFavouriteSytes);
        mRelLayMyFavouriteSytes.setOnClickListener(this);
        mRelLayContactSupport = (RelativeLayout) findViewById(R.id.xRelLayContactSupport);
        mRelLayContactSupport.setOnClickListener(this);
        mRelLaySettings = (RelativeLayout) findViewById(R.id.xRelLaySettings);
        mRelLaySettings.setOnClickListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRelLayHamBurger = (RelativeLayout) findViewById(R.id.xRelLayHamBurger);
        mRelLayHamBurger.setOnClickListener(this);
        mRelLayNotification = (RelativeLayout) findViewById(R.id.xRelLayNotification);
        mRelLayNotification.setOnClickListener(this);
        mRelLayFavourite = (RelativeLayout) findViewById(R.id.xRelLayFavourite);
        mRelLayFavourite.setOnClickListener(this);
        mRelLaySearch = (RelativeLayout) findViewById(R.id.xRelLaySearch);
        mRelLaySearch.setOnClickListener(this);
        mRelLayFilter = (RelativeLayout) findViewById(R.id.xRelLayFilter);
        mRelLayFilter.setOnClickListener(this);
        mRellayTop = (RelativeLayout) findViewById(R.id.xRellayTop);
        mRelLaySwitch = (RelativeLayout) findViewById(R.id.xRelLaySwitch);
        mRelLaySwitch.setOnClickListener(this);
        mIvSwitch = (ImageView) findViewById(R.id.xIvSwitch);
        mIvFavourite = (ImageView) findViewById(R.id.xIvFavourite);
        mIvNotificationIcon = (ImageView) findViewById(R.id.xIvNotificationIcon);
        if (mYasPasPref.sGetFollowingFlag()) {
            IS_FAVOURITE_CHECKED = true;
            mIvFavourite.setImageResource(R.drawable.ic_action_bar_favourite);
        } else {
            IS_FAVOURITE_CHECKED = false;
            mIvFavourite.setImageResource(R.drawable.ic_action_bar_un_favourite);
        }
        mPageTitle = (TextView) findViewById(R.id.xPageTitle);
        mIvUserProfilepic = (ImageView) findViewById(R.id.xIvUserProfilepic);
        mTvUserName = (TextView) findViewById(R.id.xTvUserName);
        mTvUserName.setText(mYasPasPref.sGetUserName());
        mTvUserName.setOnClickListener(this);

        mTvMsgUnreadCount = (TextView) findViewById(R.id.xTvMsgUnreadCount);
        mPbChatMsgCounterLoader = (ProgressBar) findViewById(R.id.xPbChatMsgCounterLoader);
    }// END mInItWidgets()

    private void showFavourite_Search_Filter_Icon(boolean showFavourite, boolean showSearch, boolean showFilter, boolean showSwitch, boolean showNotification) {
        if (showFavourite) {
            mRelLayFavourite.setVisibility(View.VISIBLE);
        } else {
            mRelLayFavourite.setVisibility(View.GONE);
        }
        if (showSearch) {
            mRelLaySearch.setVisibility(View.VISIBLE);
        } else {
            mRelLaySearch.setVisibility(View.GONE);
        }
        if (showFilter) {
            mRelLayFilter.setVisibility(View.VISIBLE);
        } else {
            mRelLayFilter.setVisibility(View.GONE);
        }
        if (showSwitch) {
            mRelLaySwitch.setVisibility(View.VISIBLE);
        } else {
            mRelLaySwitch.setVisibility(View.GONE);
        }
        if (showNotification) {
            mRelLayNotification.setVisibility(View.VISIBLE);
        } else {
            mRelLayNotification.setVisibility(View.GONE);
        }
    }// END showFavourite_Search_Filter_Icon()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayHamBurger: {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayHome: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof HomeBulletinFragment)) {
                    mPageTitle.setText(getString(R.string.home_bulletin_screen_page_title));
                    mCurrentFragment = new HomeBulletinFragment();
                    mCurrentFragment.setArguments(mBun);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                    mIvSwitch.setImageResource(R.drawable.ic_action_bar_map_view);
                }
                showFavourite_Search_Filter_Icon(true, false, true, true, true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayMap: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MapFragment)) {
                    mPageTitle.setText(getString(R.string.map_screen_page_title));
                    mCurrentFragment = new MapFragment();
                    mCurrentFragment.setArguments(mBun);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                    mIvSwitch.setImageResource(R.drawable.ic_action_bar_home);
                }
                showFavourite_Search_Filter_Icon(true, true, false, true, true);

                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayMySytes: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MySytesFragment)) {
                    mPageTitle.setText(getString(R.string.my_sytes_screen_page_title));
                    mCurrentFragment = new MySytesFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                }
                showFavourite_Search_Filter_Icon(false, false, false, false, true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayMyFriends: {
                                /*if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MyFriendsFragment))
                                {
                                    mPageTitle.setText(getString(R.string.my_friends_screen_page_title));
                                    mCurrentFragment = new MyFriendsFragment();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                                }
                                showSearchFavouriteIcons(false);*/
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                String smsMsg = "Hi I am using Syte App. Create your own free Syte & start managing it. Download : https://play.google.com/store/apps/details?id=com.syte";
                sendIntent.putExtra("sms_body", smsMsg);
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);

                break;
            }
            case R.id.xRelLayMyMessages: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MyMessagesNewFragment)) {
                    mPageTitle.setText(getString(R.string.my_messages_screen_page_title));
                    mCurrentFragment = new MyMessagesNewFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                }
                showFavourite_Search_Filter_Icon(false, false, false, false, true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayMyFavouriteSytes: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MyFavouriteSyteFragment)) {
                    mPageTitle.setText(getString(R.string.my_favourite_sytes_screen_page_title));
                    mCurrentFragment = new MyFavouriteSyteFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                }
                showFavourite_Search_Filter_Icon(false, false, false, false, true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayContactSupport: {

                                /*if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof ContactSupportFragment))
                                {
                                    mPageTitle.setText(getString(R.string.contact_support_screen_page_title));
                                    mCurrentFragment = new ContactSupportFragment();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit()
                                }
                                showFavourite_Search_Filter_Icon(false, false, false,false,true);
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;*/

                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intentReportABugActivity = new Intent(HomeActivity.this, ReportABugActivity.class);
                startActivity(intentReportABugActivity);
                break;
            }
            case R.id.xRelLaySettings: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof SettingsFragment)) {
                    mPageTitle.setText(getString(R.string.settings_screen_page_title));
                    mCurrentFragment = new SettingsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                }
                showFavourite_Search_Filter_Icon(false, false, false, false, true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayNotification: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MyNotificationsFragment)) {
                    mPageTitle.setText(getString(R.string.notification_screen_page_title));
                    mCurrentFragment = new MyNotificationsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                }
                showFavourite_Search_Filter_Icon(false, false, false, false, false);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.xRelLayFavourite: {
                if ((getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MapFragment)) {
                    if (mYasPasPref.sGetFollowingFlag()) {
                        IS_FAVOURITE_CHECKED = false;
                        mYasPasPref.sSetFollowingFlag(false);
                        mIvFavourite.setImageResource(R.drawable.ic_action_bar_un_favourite);
                        MapFragment mapFragment = (MapFragment) mCurrentFragment;
                        mapFragment.sCHECK_MARKER_VISIBILITY();
                    } else {
                        IS_FAVOURITE_CHECKED = true;
                        mYasPasPref.sSetFollowingFlag(true);
                        mIvFavourite.setImageResource(R.drawable.ic_action_bar_favourite);
                        MapFragment mapFragment = (MapFragment) mCurrentFragment;
                        mapFragment.sCHECK_MARKER_VISIBILITY();
                    }
                } else if ((getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof HomeBulletinFragment)) {
                    if (mYasPasPref.sGetFollowingFlag()) {
                        IS_FAVOURITE_CHECKED = false;
                        mYasPasPref.sSetFollowingFlag(false);
                        mIvFavourite.setImageResource(R.drawable.ic_action_bar_un_favourite);
                        HomeBulletinFragment homeBulletinFragment = (HomeBulletinFragment) mCurrentFragment;
                        homeBulletinFragment.sSetFavourite();
                    } else {
                        IS_FAVOURITE_CHECKED = true;
                        mYasPasPref.sSetFollowingFlag(true);
                        mIvFavourite.setImageResource(R.drawable.ic_action_bar_favourite);
                        HomeBulletinFragment homeBulletinFragment = (HomeBulletinFragment) mCurrentFragment;
                        homeBulletinFragment.sSetFavourite();
                    }
                }

                break;
            }
            case R.id.xRelLaySwitch: {
                if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MapFragment)) {
                    mIvSwitch.setImageResource(R.drawable.ic_action_bar_home);
                    mPageTitle.setText(getString(R.string.map_screen_page_title));
                    mCurrentFragment = new MapFragment();
                    mCurrentFragment.setArguments(mBun);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                    showFavourite_Search_Filter_Icon(true, true, false, true, true);
                } else {
                    mIvSwitch.setImageResource(R.drawable.ic_action_bar_map_view);
                    mPageTitle.setText(getString(R.string.home_bulletin_screen_page_title));
                    mCurrentFragment = new HomeBulletinFragment();
                    mCurrentFragment.setArguments(mBun);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, mCurrentFragment).commit();
                    showFavourite_Search_Filter_Icon(true, false, true, true, true);
                }

                break;
            }
            case R.id.xRelLayFilter: {
                showFilterOptionsPopup();
                break;
            }
            case R.id.xRelLaySearch: {
                MapFragment mapFragment = (MapFragment) mCurrentFragment;
                mapFragment.sAnimateSearchBox();
                break;
            }
            case R.id.xTvUserName: {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                startActivityForResult(new Intent(HomeActivity.this, EditProfileActivity.class), StaticUtils.ACTION_EDIT_PROFILE_REQUEST);
                break;
            }
            default:
                break;
        }
    }//END onClick()

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("GPS") && paramIsFinish) {
            finish();
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("GPS")) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onLastLocationKnown(Location lastKnownLoc) {
        mGoogleApiHelper.sStartLocationUpdate(cont, 1000 * 30, 1000 * 30, 1000);
    }

    @Override
    public void onLocationUpdate(Location updatedLoc) {
        LOC_ACTUAL.setLatitude(updatedLoc.getLatitude());
        LOC_ACTUAL.setLongitude(updatedLoc.getLongitude());
        if (getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MapFragment) {
            MapFragment f = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
            f.sSetCurrentLocation(updatedLoc.getLatitude(), updatedLoc.getLongitude());
        }
    }


    @Override
    public void onLocationManagerConnected() {

    }

    public class BrdcstNotificationReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            sUpdateNotificationIcon();
        }
    }// END RegistrationReceiver

    public class BrdcstChatMessages extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(StaticUtils.BRD_CST_AC_CHAT_SERVICE_STATUS);
            if (message.equalsIgnoreCase(StaticUtils.BRD_CST_AC_CHAT_SERVICE_STARTED)) {
                mTvMsgUnreadCount.setVisibility(View.GONE);
                mPbChatMsgCounterLoader.setVisibility(View.VISIBLE);
            }
            if (message.equalsIgnoreCase(StaticUtils.BRD_CST_AC_CHAT_SERVICE_NEW_MESSAGE)) {
                mTvMsgUnreadCount.setVisibility(View.VISIBLE);
                mPbChatMsgCounterLoader.setVisibility(View.GONE);
            }
            if (message.equalsIgnoreCase(StaticUtils.BRD_CST_AC_CHAT_SERVICE_NO_NEW_MESSAGE)) {
                mTvMsgUnreadCount.setVisibility(View.GONE);
                mPbChatMsgCounterLoader.setVisibility(View.GONE);
            }
        }
    }// END BrdcstChatMessages

    public void sUpdateNotificationIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mYasPasPref.sGetNotificationIconUpdate()) {
                    if (!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof MyNotificationsFragment)) {
                        mIvNotificationIcon.setImageResource(R.drawable.ic_action_bar_notification_un_read);
                    }
                } else {
                    mIvNotificationIcon.setImageResource(R.drawable.ic_action_bar_notification);
                }
            }
        });

    }// END mUpdateNotificationIcon()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == StaticUtils.ACTION_EDIT_PROFILE_REQUEST && resultCode == StaticUtils.ACTION_EDIT_PROFILE_RESULT) {
            mTvUserName.setText(mYasPasPref.sGetUserName());
            String cURL = mCloudinary.url().transformation(mCUserTransformation).generate(mYasPasPref.sGetUserProfilePic());
            ImageLoader.getInstance().displayImage(cURL, mIvUserProfilepic, mDspImgOptions);
        }
    } //END onActivityResult()

    private void showFilterOptionsPopup() {
        final Dialog mDialog = new Dialog(HomeActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_filter_options);

        WindowManager.LayoutParams wmlp = mDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wmlp.y = mRellayTop.getHeight();


        mDialog.show();
        final Window window = mDialog.getWindow();

        final int distance = mYasPasPref.sGetFilterDistance();
        final TextView mTv5Km = (TextView) window.findViewById(R.id.xTv5Km);
        final TextView mTv10Km = (TextView) window.findViewById(R.id.xTv10Km);
        final TextView mTv25Km = (TextView) window.findViewById(R.id.xTv25Km);
        final TextView mTv50Km = (TextView) window.findViewById(R.id.xTv50Km);

        if (distance == 5) {

            mTv5Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
            mTv10Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv25Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv50Km.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }
        if (distance == 10) {

            mTv5Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv10Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
            mTv25Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv50Km.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }
        if (distance == 25) {

            mTv5Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv10Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv25Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
            mTv50Km.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }
        if (distance == 50) {

            mTv5Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv10Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv25Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTv50Km.setBackgroundColor(Color.parseColor("#F5F5F5"));

        }

        mTv5Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeBulletinFragment homeBulletinFragment = (HomeBulletinFragment) mCurrentFragment;
                if (!homeBulletinFragment.sIsBulletinRefreshing()) {
                    mYasPasPref.sSetFilterDistance(5);
                    mTv5Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    mTv10Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv25Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv50Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mDialog.dismiss();
                    if (distance != 5) {

                        homeBulletinFragment.setNewGeoLoc(5, false);
                    }
                } else {
                    mDialog.dismiss();
                }
            }
        });
        mTv10Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeBulletinFragment homeBulletinFragment = (HomeBulletinFragment) mCurrentFragment;
                if (!homeBulletinFragment.sIsBulletinRefreshing()) {
                    mYasPasPref.sSetFilterDistance(10);
                    mTv5Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv10Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    mTv25Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv50Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mDialog.dismiss();
                    if (distance != 10) {
                        homeBulletinFragment.setNewGeoLoc(10, false);
                    }
                } else {
                    mDialog.dismiss();
                }
            }
        });
        mTv25Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeBulletinFragment homeBulletinFragment = (HomeBulletinFragment) mCurrentFragment;
                if (!homeBulletinFragment.sIsBulletinRefreshing()) {
                    mYasPasPref.sSetFilterDistance(25);
                    mTv5Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv10Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv25Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    mTv50Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mDialog.dismiss();
                    if (distance != 25) {
                        homeBulletinFragment.setNewGeoLoc(25, false);
                    }
                } else {
                    mDialog.dismiss();
                }
            }
        });
        mTv50Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeBulletinFragment homeBulletinFragment = (HomeBulletinFragment) mCurrentFragment;
                if (!homeBulletinFragment.sIsBulletinRefreshing()) {
                    mYasPasPref.sSetFilterDistance(50);
                    mTv5Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv10Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv25Km.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    mTv50Km.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    mDialog.dismiss();
                    if (distance != 50) {
                        homeBulletinFragment.setNewGeoLoc(50, false);
                    }
                } else {
                    mDialog.dismiss();
                }
            }
        });
    }// END showMenuSettingsPopup()

} // END HomeActivity()
