package com.syte.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by khalid.p on 30-12-2015.
 */
public class StaticUtils
    {
        //URLs --
        //public static String FIREBASE_BASE_URL="https://yaspasbeta.firebaseIO.com/"; // STAGING
       // public static String FIREBASE_BASE_URL="https://gosyte.firebaseio.com/"; // PRODUCTION
        public static String FIREBASE_BASE_URL="https://yaspastest.firebaseio.com/";//kasi test firebase url
        public static String AUTH_DB_URL=FIREBASE_BASE_URL+"authDB";
        public static String YASPASEE_URL= FIREBASE_BASE_URL+"yaspasees";
        public static String YASPAS_URL= FIREBASE_BASE_URL+"yaspas";
        public static String YASPAS_GEO_LOC_URL= FIREBASE_BASE_URL+"yaspasGeoLoc";
        public static String YASPAS_BULLETIN_BOARD_URL= FIREBASE_BASE_URL+"bulletinBoard";
        public static String BULLETIN_PUSH_NOTIFICATION_URL=FIREBASE_BASE_URL+"bulletinBoardPushNotification";
        public static String CHAT_URL= FIREBASE_BASE_URL+"chat";
        public static String TEMP_ANALYTICS_VISITOR_URL= FIREBASE_BASE_URL+"tempAnalyticsVisitor";
        public static String ANALYTICS_SYTE_URL= FIREBASE_BASE_URL+"analyticsSytes";
        public static String ANALYTICS_DAILY= "daily";
        public static String ANALYTICS_MONTHLY= "monthly";
        public static String BULLETIN_BOARD_NOTIFICATION_INBOX="bulletinBoardNotificationInbox";
        public static String YASPAS_PUSH_NOTIFICATION_URL=FIREBASE_BASE_URL+"yasPasPushNotification";
        public static String MY_YASPASES_NOTIFICATION_INBOX="myYasPasesNotificationInbox";
        public static String YASPAS_TEAM="/team";
        public static String YASPAS_MANAGERS="/managers";
        public static String YASPAS_ABOUT_US="/aboutUs";
        public static String YASPAS_WHAT_WE_DO="/whatWeDo";
        public static String OWNED_YASPAS="ownedYasPases";
        public static String FOLLOWING_YASPAS="followingYasPases";
        public static String FOLLOWERS_YASPAS="followerYasPasees";
        public static String MY_CHAT="myChat";
        public static String MY_YASPASES_CHATS="myYasPasesChats";
        public static String CHAT_MESSAGES="chatMessages";
        public static String REPORT_A_BUG=FIREBASE_BASE_URL+"reportBugs";


        //MAP ZOOM LEVEL
        public static float MAP_DEFAULT_ZOOM_LEVEL=17;
        // ACTIONs
        public static int ACTION_CAMERA_PICK=1;
        public static int ACTION_GALLERY_PICK=2;

        public static int ACTION_EDIT_PROFILE_REQUEST=100;
        public static int ACTION_EDIT_PROFILE_RESULT=200;
        //public static int ACTION_CAMERA_PICK_1=1002;
        //public static int ACTION_GALLERY_PICK_1=1003;
        // IPCs' KEYs
        /*public static String IPC_SYTE_BANNER_LOCAL_IMAGE_PATH="ipcSyteBannerLocalImgpth";
        public static String IPC_SYTE_BANNER_LOCAL_IMAGE_URI="ipcSyteBannerLocalImguri";*/
        public static String IPC_SYTE="ipcSyte";
        public static String IPC_SYTE_ID="ipcSyteId";
        public static String IPC_SYTE_NAME="ipcSyteName";
        public static String IPC_SYTE_IMAGE="ipcSyteImg";
        public static String IPC_SYTE_OWNERS="ipcSyteOwners";
        //public static String IPC_CHAT_SENDER_TYPE="ipcChatSenderType";
        //public static String IPC_CHAT_WINDOW_PAGE_TITLE="ipcChatWinPgTitle";
        public static String IPC_TEAM_MEMBERS_LIST="ipctmlist";
        public static String IPC_TEAM_MEMBER="ipctm";
        public static String IPC_TEAM_MEMBER_INDEX="ipctmindex";
        public static String IPC_ACTION_EDIT="ipcactionedit";
        public static String IPC_ACTION_DELETE="ipcactiondelete";
        public static String IPC_ACTION="ipcaction";
        public static String IPC_BULLETIN_ID="ipcbulletinId";
        public static String IPC_BULLETIN="ipcbulletinboard";
        public static String IPC_BULLETIN_PAGE_POSITION="ipcbulletinboardpageposition";
        public static String IPC_FOLLOWED_SYTE="ipcsytefollowed";
        public static String IPC_HOME_STARTING_FRAGMENT="ipchomestratingFragment";
        public static String IPC_USER_NAME="ipcUserName";
        public static String IPC_USER_REG_NUM="ipcUserRegisteredNumber";
        public static String IPC_USER_IMAGE="ipcUserImg";
        public static String IPC_FOLLOWER="ipcFollower";
        public static String IPC_FOLLOWERS="ipcFollowers";
        // BROADCASTS' ACTION
        public static String BRD_CST_AC_UPDATE_NOTIFICATION_ICON= "brdcstupdatenotificationicon";
        public static String BRD_CST_AC_CHAT= "brdcstchat";
        public static String BRD_CST_AC_CHAT_SERVICE_STATUS= "brdcstchatstatus";
        public static String BRD_CST_AC_CHAT_SERVICE_STARTED= "brdcstchatstarted";
        public static String BRD_CST_AC_CHAT_SERVICE_NO_NEW_MESSAGE= "brdcstchatnonewmsg";
        public static String BRD_CST_AC_CHAT_SERVICE_NEW_MESSAGE= "brdcstchatnewmsg";

        // HOME STARTING FRAGMENT
        public static int HOME_STARTING_FRAG_MAP= 0;
        public static int HOME_STARTING_FRAG_NOTIFICATION_BULETTIN= 1;
        public static int HOME_STARTING_FRAG_NOTIFICATION_FOLOWER= 2;
        public static int HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIMED= 3;
        public static int HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_REJECTED= 4;
        public static int HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_ACCEPTED= 5;
        public static int HOME_STARTING_FRAG_CHAT= 6;
        //Invite friend push functionality tags by kasi on 4/6/16
        public static int HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_INVITE= 7;
        public static int HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_ACCEPTED= 8;
        public static int HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_REJECTED= 9;



        //public static String IPC_OWNED_SYTE="ipcsyteowned";

        /*public static String IPC_SYTE_NAME="ipcsytename";
        public static String IPC_SYTE_CITY="ipcsytecity";
        public static String IPC_SYTE_BANNER_IMG="ipcsytebannerimg";
        public static String IPC_SYTE_CATEGORY="ipcsytecategory";*/

        /*public static String IPC_LATITUDE="ipcLat";
        public static String IPC_LONGITUDE="ipcLong";*/

        //CLOUDINARY
        public static Map GET_CLOUDINARY_CONFIG()
            {
                /*The below cloudinary is configured using below
                * Name						:	Syte
                 E-mail						:	prod@gosyte.com
                Password					:	5yte@cloudinary
                Country						:	United States
                Phone						:	Nil, as optional
                Company Name or Site Name	:	Nil, as optional*/
                Map config = new HashMap();
                config.put("cloud_name", "syte");
                config.put("api_key", "389419766643116");
                config.put("api_secret", "d3FNKMenyHLsCVcq1iv-_bQIDvs");
                return config;
            }
        // EDITTEXT FOCUS
        public static void REQUEST_FOCUS(View paramVw,InputMethodManager paramIpMhdMgr)
            {
                paramVw.setFocusable(true);
                paramVw.setFocusableInTouchMode(true);
                paramVw.requestFocus();
                paramIpMhdMgr.showSoftInput(paramVw, InputMethodManager.SHOW_IMPLICIT);
            }
        public static void REMOVE_FOCUS(View paramVw,InputMethodManager paramIpMhdMgr)
            {
                paramVw.setFocusable(false);
                paramVw.setFocusableInTouchMode(false);
                paramIpMhdMgr.hideSoftInputFromWindow(paramVw.getWindowToken(), 0);
            }
        //TIME FORMATEs --
        public static SimpleDateFormat BULLETIN_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        //METHODs --
        public static int GET_VIEW_WIDTH(final View paramView)
            {
                 final ArrayList<Integer> resultSet = new ArrayList<>();
                paramView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                    {
                        @Override
                        public void onGlobalLayout()
                            {
                                if (Build.VERSION.SDK_INT < 16)
                                    {
                                        // Deprecated but handled
                                        paramView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    }
                                else
                                    {
                                        paramView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }

                                resultSet.add(paramView.getWidth());
                                //return resultSet;


                            }
                    });
                return resultSet.get(0);
            }
        public static int GET_VIEW_HEIGHT(final View paramView)
            {
                final ArrayList<Integer> resultSet = new ArrayList<>();
                paramView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        if (Build.VERSION.SDK_INT < 16)
                        {
                            // Deprecated but handled
                            paramView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        else
                        {
                            paramView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        resultSet.add(paramView.getHeight());


                    }
                });
                return resultSet.get(0);
            }
        public static boolean IS_GPS_TURNED_ON(Context paramContext)
            {
                LocationManager mLocMagr = (LocationManager)paramContext.getSystemService(Context.LOCATION_SERVICE);
                return  mLocMagr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } // END IS_GPS_TURNED_ON()
        public static List<Address> CONVERT_GEO_LOC_To_ADDRESSES(Context paramCon, double paramLat, double paramLong)
            {
                Geocoder geocoder;
                List<Address> addresses=null;
                geocoder = new Geocoder(paramCon, Locale.getDefault());
                try
                    {
                        addresses = geocoder.getFromLocation(paramLat, paramLong, 1);
                    }
                catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                return  addresses;
            }// END CONVERT_GEO_LOC_To_ADDRESSES()
        public static List<Address> CONVERT_ADDRESS_GEO_LOC(Context paramCon, String paramAddress)
            {
                Geocoder geocoder;
                List<Address> addresses=null;
                geocoder = new Geocoder(paramCon, Locale.getDefault());
                try
                    {
                        addresses = geocoder.getFromLocationName(paramAddress, 1);
                    }
                catch (IOException e)
                    {
                         e.printStackTrace();
                    }
                return  addresses;
            }// END CONVERT_ADDRESS_GEO_LOC()
        public static int GET_DEVICE_SCREEN_SIZE(Context paramCon)
            {
                return paramCon.getResources().getDisplayMetrics().widthPixels;
            }
        public static String CONVERT_BULLETIN_DATE_TIME(long paramServerUTC)
            {
                BULLETIN_TIME_FORMAT.setTimeZone(TimeZone.getDefault());
                Date mServerDate = new Date(paramServerUTC);
                return BULLETIN_TIME_FORMAT.format(mServerDate);
            }
        public static Date GET_SERVER_DATE(long paramServerUTC)
            {
                return  new Date(paramServerUTC);
            }
        public static String GET_DAY_FROM_MILLISECONDS(long paramServerUTC)
            {
                BULLETIN_TIME_FORMAT.setTimeZone(TimeZone.getDefault());
                Date mServerDate = new Date(paramServerUTC);
                return BULLETIN_TIME_FORMAT.format(mServerDate).substring(4, 6);
            }
        public static String GET_MONTH_FROM_MILLISECONDS(long paramServerUTC)
            {
                BULLETIN_TIME_FORMAT.setTimeZone(TimeZone.getDefault());
                Date mServerDate = new Date(paramServerUTC);
                return BULLETIN_TIME_FORMAT.format(mServerDate).substring(0,3);
            }
       /* public static boolean IS_MEDIA_UPLOAD_SERVICE_RUNNING(Context context)
            {

                ActivityManager manager = (ActivityManager)  context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

                    System.out.println("*********** "+service.service.getClassName());
                    if ("com.syte.services.MediaUploadService1111".equals(service.service.getClassName())) {
                        return true;
                    }
                }
                return false;

            }*/
       /*public static boolean isServiceRunning(Context context) {
            ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

            if (serviceList.size() <= 0)
                {
                    return false;
                }
            for (int i = 0; i < serviceList.size(); i++) {
                ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
                ComponentName serviceName = serviceInfo.service;
                System.out.println("----------------- "+serviceName.getClassName());
                if(serviceName.getClassName().equals(MediaUploadService222.class.getName())) {
                    return true;
                }
            }

            return false;
        }*/
    }
