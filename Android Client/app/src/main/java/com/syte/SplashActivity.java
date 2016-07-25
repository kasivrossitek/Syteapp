package com.syte;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.syte.activities.HomeActivity;
import com.syte.activities.SignUpActivity;
import com.syte.activities.walkthrough.WalkThroughBeforeLoginActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnLocationListener;
import com.syte.listeners.OnPermissionRequestListener;
import com.syte.models.Listcontacts;
import com.syte.models.PhoneContact;
import com.syte.services.UserAnalyticsService;
import com.syte.utils.GoogleApiHelper;
import com.syte.utils.LoginStatus;
import com.syte.utils.NetworkStatus;
import com.syte.utils.PermissionManager;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity implements OnClickListener, OnLocationListener, OnCustomDialogsListener, OnPermissionRequestListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private TextView mTvGetStart;
    private GoogleApiHelper mGoogleApiHelper;
    private String mCountryCode;
    private YasPasPreferences mYasPasPref;
    private NetworkStatus mNetworkStatus;
    private Bundle mBun;
    private ProgressDialog mPrgDia;
    private PermissionManager mPermissionManager;
    public static boolean DEBUG = false;

    //read contacts by kasi
    ArrayList<PhoneContact> contact_numbers;
    private ArrayList<Listcontacts> contact_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Firebase.setAndroidContext(this);
        mGoogleApiHelper = YasPasApp.getGoogleApiHelper(SplashActivity.this, this);
        if (!DEBUG)
            Crittercism.initialize(getApplicationContext(), "e850f97c1b6b4940abd3c1e3e83bd3cb00555300");
        mBun = new Bundle();

        try {
            // if activity is initiated by push notification
            // Checking if it is chat push notification, if yes then putting extra values
            if (getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT) == StaticUtils.HOME_STARTING_FRAG_CHAT) {
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_ID, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_ID));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM));
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME, getIntent().getExtras().getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME));
            }
            mBun.putInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT, getIntent().getExtras().getInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT));
        } catch (Exception e) {
            // if activity is initiated not by push notification, but from app drawer
            mBun.putInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT, StaticUtils.HOME_STARTING_FRAG_MAP);
        }


        mYasPasPref = YasPasPreferences.GET_INSTANCE(SplashActivity.this);
        mNetworkStatus = new NetworkStatus(SplashActivity.this);
        contact_id = new ArrayList<>();
        contact_numbers = new ArrayList<>();
        mInitializeWidgets();
        // mGetContacts();
    }// END onCreate()

    private void mInitializeWidgets() {
        mTvGetStart = (TextView) findViewById(R.id.xTvGetStart);
        mTvGetStart.setOnClickListener(this);
    } // END mInitializeWidgets()

    /***
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }// END checkPlayServices()

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startApp() {
        // Checking if user is already logged in
        if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.COMPLETED)) {

            //User is logged in
            mTvGetStart.setVisibility(View.GONE);
        } else {
            //User is not logged in
            mTvGetStart.setVisibility(View.VISIBLE);
        }
        if (mNetworkStatus.isNetworkAvailable()) {
            if (StaticUtils.IS_GPS_TURNED_ON(SplashActivity.this)) {
                if (checkPlayServices()) {
                    mGoogleApiHelper.sGoogleApiConnect();
                }
            } else {
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SplashActivity.this, this);
                customDialogs.sShowDialog_Common(YasPasMessages.NO_GPS_SUBJECT, YasPasMessages.NO_GPS_HEADING, YasPasMessages.NO_GPS_BODY, "NO", "YES", "GPS", true, false);
            }
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SplashActivity.this, this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionManager = new PermissionManager(this, this);
            if (checkMMPermissions() == 0) {
                startApp();
            }
        } else {
            startApp();
        }

    }// END onResume()

    @Override
    public void onClick(View v) {
        if (v == mTvGetStart) {

            if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.OTP)) {
                Intent mInt_MNES = new Intent(SplashActivity.this, WalkThroughBeforeLoginActivity.class);
                mBun.putString("keyCC", mCountryCode);
                mInt_MNES.putExtras(mBun);
                startActivity(mInt_MNES);
                finish();
            } else if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.SIGNUP)) {
                Intent mInt_SignUp = new Intent(SplashActivity.this, SignUpActivity.class);
                mBun.putString("keyCC", mCountryCode);
                mInt_SignUp.putExtras(mBun);
                startActivity(mInt_SignUp);
                finish();
            } else {

                Intent mInt_Map = new Intent(SplashActivity.this, HomeActivity.class);
                mInt_Map.putExtras(mBun);
                startActivity(mInt_Map);
                finish();


                                /*// Updating user's name and his profile pic, if updated from other phone using same registered number
                                Firebase mYasPaseeUrl = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum());
                                mYasPaseeUrl.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        YasPasee objYasPasee = dataSnapshot.getValue(YasPasee.class);
                                        mYasPasPref.sSetUserName(objYasPasee.getUserName());
                                        mYasPasPref.sSetUserProfilePic(objYasPasee.getUserProfilePic());

                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {


                                    }
                                });*/


            }
        }
    }// END onClick()

    private void mSaveCountryCode(double paramLat, double paramLong) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(paramLat, paramLong, 1);

            mCountryCode = addresses.get(0).getCountryCode();

            Log.e("mCountryCode", "" + mCountryCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// END mSaveCountryCode()


    @Override
    public void onLastLocationKnown(Location lastKnownLoc) {
        if (lastKnownLoc != null) {
            mBun.putDouble("ipcCurrentLat", lastKnownLoc.getLatitude());
            mBun.putDouble("ipcCurrentLong", lastKnownLoc.getLongitude());
            if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.OTP) || mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.SIGNUP))
                mSaveCountryCode(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());
            mGoogleApiHelper.sGoogleApiDisconnect();
            sendUserAnalytics(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());

            if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.COMPLETED)) {
                sendUserAnalytics(lastKnownLoc.getLatitude(), lastKnownLoc.getLongitude());
                goToHomeActivity();
            }
        } else {
            mPrgDia = new ProgressDialog(SplashActivity.this);
            mPrgDia.setCancelable(false);
            mPrgDia.setMessage(getString(R.string.prg_bar_wait));
            mPrgDia.show();
            mGoogleApiHelper.sStartLocationUpdate(SplashActivity.this, 1, 1, 1);
        }
    }// END onLastLocationKnown()

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiHelper.sGoogleApiDisconnect();

    }

    @Override
    public void onLocationUpdate(Location updatedLoc) {
        mBun.putDouble("ipcCurrentLat", updatedLoc.getLatitude());
        mBun.putDouble("ipcCurrentLong", updatedLoc.getLongitude());
        mPrgDia.dismiss();
        if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.OTP) || mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.SIGNUP))
            mSaveCountryCode(updatedLoc.getLatitude(), updatedLoc.getLongitude());
        mGoogleApiHelper.sGoogleApiDisconnect();
        sendUserAnalytics(updatedLoc.getLatitude(), updatedLoc.getLongitude());
        if (mYasPasPref.sGetLoginStatus().equalsIgnoreCase(LoginStatus.COMPLETED)) {
            sendUserAnalytics(updatedLoc.getLatitude(), updatedLoc.getLongitude());
            goToHomeActivity();
        }
    }// END onLocationUpdate()

    @Override
    public void onLocationManagerConnected() {

    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("GPS") && paramIsFinish) {
            finish();
        } else if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            finish();
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("GPS")) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

    }

    private void mGetContacts() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {

                String str = readContacts();
                return str;
            }

            @Override
            protected void onPreExecute() {
               /* mPrgDia = new ProgressDialog(SplashActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mPrgDia.show();*/
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("", result);

                mStorecontacts();
                //   mPrgDia.dismiss();

            }
        }.execute();
    }

    private void mStorecontacts() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                File root1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YasPasLocal" + "/" + "phoneid");
                File root2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YasPasLocal" + "/" + "phonenum");
                if (root1.exists()) {
                    root1.delete();
                }
                if (root2.exists()) {
                    root2.delete();
                }
                String str = storeNoteOnLocal(SplashActivity.this, "phoneid", contact_id, "phonenum", contact_numbers);
                return str;
            }

            @Override
            protected void onPreExecute() {
               /* mPrgDia = new ProgressDialog(SplashActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mPrgDia.show();*/
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("", result);

                // mPrgDia.dismiss();

            }
        }.execute();
    }

    public String storeNoteOnLocal(SplashActivity context, String sFileName, ArrayList<Listcontacts> contactid, String file2, ArrayList<PhoneContact> contactnumbers) {
        String save = "notsaved";
        try {
            File root1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YasPasLocal" + "/" + sFileName);
            File root2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YasPasLocal" + "/" + file2);
            if (!root1.exists()) {
                root1.mkdir();
            }
            if (!root2.exists()) {
                root2.mkdir();
            }

            FileOutputStream fileout = new FileOutputStream(root1.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileout);
            out.writeObject(contactid);
            out.close();
            fileout.close();
            FileOutputStream fileout2 = new FileOutputStream(root2.getAbsolutePath());
            ObjectOutputStream out2 = new ObjectOutputStream(fileout2);
            out2.writeObject(contactnumbers);
            out2.close();
            fileout2.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            save = "saved";
            contact_id.clear();
            contact_numbers.clear();
            contactnumbers.clear();
            contactid.clear();

            return save;

        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return save;
    }

    public String readContacts() {
        //Read phone contacts
        String str = "";
        ContentResolver cr = this.getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        contact_id.clear();

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String image_uri = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Bitmap bp = null;
                byte[] byteArray = null;

                if (image_uri != null) {
                    try {
                        bp = MediaStore.Images.Media
                                .getBitmap(this.getContentResolver(),
                                        Uri.parse(image_uri));

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);
                    Log.d("names ", name + ", IDs : " + id);
                    Listcontacts listcontacts = new Listcontacts();
                    listcontacts.setContact_id(id);
                    listcontacts.setName(name);
                  //  listcontacts.setPhoto_byte(byteArray);
                    contact_id.add(listcontacts);
                }
            }
            cur.close();

            Log.d("idsize" + contact_id.size(), "");
            str = read_phoneNum();


        }
        return str;
    }

    public String read_phoneNum() {

        String success = "";

        ContentResolver cr = this.getContentResolver();
        for (Listcontacts ids : contact_id) {

            Log.d("idsss", ids.getContact_id());
            // get the phone number
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{ids.getContact_id()}, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

            while (pCur.moveToNext()) {
                PhoneContact phncon = new PhoneContact();
                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                switch (phoneType) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        Log.e(": TYPE_MOBILE", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());

                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        Log.e(": TYPE_HOME", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());

                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        Log.e(": TYPE_WORK", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                        Log.e(": TYPE_WORK_MOBILE", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                        Log.e(": TYPE_OTHER", "" + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                        break;
                    default:
                        break;
                }

                phncon.setPhone_Mobile(phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                phncon.setPhone_ID(ids.getContact_id());
                System.out.println("phone" + phone);
                contact_numbers.add(phncon);
            }
            pCur.close();
            success = "success";
        }


        return success;
    }

    private void goToHomeActivity() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mInt_Map = new Intent(SplashActivity.this, HomeActivity.class);
                mInt_Map.putExtras(mBun);
                startActivity(mInt_Map);
                finish();

            }
        }, 2000);

    }

    @Override
    public void onPermissionRequest(String paramPermission, boolean isPermissionDeniedBefore, int paramRequestCode) {
        if (isPermissionDeniedBefore) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{paramPermission}, paramRequestCode);
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{paramPermission}, paramRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_PHONE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        /*if(checkMMPermissions()==0)
                                            {
                                                startApp();
                                            }*/

                } else {
                    finish();
                }
            }
            break;
            case PermissionManager.PERMISSION_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        /*if(checkMMPermissions()==0)
                                            {
                                                startApp();
                                            }*/

                } else {
                    finish();
                }
            }
            break;
            case PermissionManager.PERMISSION_STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        /*if(checkMMPermissions()==0)
                                            {
                                                startApp();
                                            }*/

                } else {
                    finish();
                }
                break;
            }
            case PermissionManager.PERMISSION_SMS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                    /*if(checkMMPermissions()==0)
                                        {
                                            startApp();
                                        }*/

                } else {
                    finish();
                }
                break;
            }
            default:
                break;
        }
    }

    private int checkMMPermissions() {
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_PHONE))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_PHONE, PermissionManager.PERMISSION_PHONE_REQUEST_CODE);
            return PermissionManager.PERMISSION_PHONE_REQUEST_CODE;
        }
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_LOCATION))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_LOCATION, PermissionManager.PERMISSION_LOCATION_REQUEST_CODE);
            return PermissionManager.PERMISSION_LOCATION_REQUEST_CODE;
        }
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_STORAGE))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_STORAGE, PermissionManager.PERMISSION_STORAGE_REQUEST_CODE);
            return PermissionManager.PERMISSION_STORAGE_REQUEST_CODE;
        }
        if (!mPermissionManager.isPermissionGranted((PermissionManager.PERMISSION_SMS))) {
            mPermissionManager.requestPermission(PermissionManager.PERMISSION_SMS, PermissionManager.PERMISSION_SMS_REQUEST_CODE);
            return PermissionManager.PERMISSION_SMS_REQUEST_CODE;
        }
        return 0;
    }

    private void sendUserAnalytics(double paramLat, double paramLong) {
        Intent userAnalytics = new Intent(SplashActivity.this, UserAnalyticsService.class);
        userAnalytics.putExtra("lat", paramLat);
        userAnalytics.putExtra("long", paramLong);
        startService(userAnalytics);
    }
}

