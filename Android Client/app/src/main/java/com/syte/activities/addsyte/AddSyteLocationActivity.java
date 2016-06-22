package com.syte.activities.addsyte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.Syte;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.HashMap;
import java.util.List;

/**
 * Created by khalid.p on 11-02-2016.
 */
public class AddSyteLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, View.OnClickListener, OnCustomDialogsListener {
        private GoogleMap mMap;
        private SupportMapFragment mapFragment;
        private RelativeLayout mRelLayBack,mRelLayForward;
        private Bundle mBun;
        private Syte mSyte;
        private double mSyteLatitude,mSyteLongitude;
        private NetworkStatus mNetworkStatus;
        private ProgressDialog mPrgDia;
        private String mSyteId;
        private ImageView mIvPin;
        private LinearLayout mLinLayContent;
        // TO BE DELETED - http://stackoverflow.com/questions/10379134/finish-an-activity-from-another-activity
        public static Activity TO_BE_DELETED;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_add_syte_location);
                TO_BE_DELETED=null;
                TO_BE_DELETED=this;
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                mBun = getIntent().getExtras();
                mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                //mTeamMembers = mBun.getParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST);
                mSyteLatitude=mSyte.getLatitude();
                mSyteLongitude=mSyte.getLongitude();
                mNetworkStatus=new NetworkStatus(AddSyteLocationActivity.this);
                mPrgDia = new ProgressDialog(AddSyteLocationActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
            }// END mInItObjects()
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mRelLayForward=(RelativeLayout)findViewById(R.id.xRelLayForward);
                mRelLayForward.setOnClickListener(this);
                mIvPin=(ImageView)findViewById(R.id.xIvPin);
                mIvPin.setOnClickListener(this);
                mLinLayContent=(LinearLayout)findViewById(R.id.xLinLayContent);
                mLinLayContent.setOnClickListener(this);
            }// END mInItWidgets()
        @Override
        public void onCameraChange(CameraPosition cameraPosition)
            {
                mSyteLatitude=cameraPosition.target.latitude;
                mSyteLongitude=cameraPosition.target.longitude;
            }// END onCameraChange()
        @Override
        public void onMapReady(GoogleMap googleMap)
            {
                mMap = googleMap;
                LatLng location = new LatLng(mSyteLatitude, mSyteLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, StaticUtils.MAP_DEFAULT_ZOOM_LEVEL));
                mMap.setOnCameraChangeListener(this);
            }// END onMapReady()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xRelLayBack:
                            {
                                finish();
                                break;
                            }
                        case R.id.xRelLayForward:
                        case R.id.xIvPin:
                        case R.id.xLinLayContent:
                            {
                                if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        new GetAddressAutomatically().execute();
                                    }
                                else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteLocationActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }

                                break;
                            }
                        default:break;
                    }
            }// END onClick()

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
        {
            if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
                {
                    finish();
                }
        }// END onDialogLeftBtnClicked()

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
        {
            if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
                {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
        }// END onDialogRightBtnClicked()

    private class GetAddressAutomatically extends AsyncTask<String, Void, String>
            {
                List<Address> mAddresses=null;
                @Override
                protected String doInBackground(String... params)
                    {
                        mAddresses = StaticUtils.CONVERT_GEO_LOC_To_ADDRESSES(AddSyteLocationActivity.this,mSyteLatitude,mSyteLongitude);
                        return null;
                    }
                @Override
                protected void onPostExecute(String result)
                    {
                        if(mAddresses!=null)
                            {
                                try
                                    {
                                        Address firstAddress = mAddresses.get(0);
                                        mSyte.setLatitude(mSyteLatitude);
                                        mSyte.setLongitude(mSyteLongitude);
                                        if(firstAddress.getAddressLine(0)!=null)
                                            mSyte.setStreetAddress1(firstAddress.getAddressLine(0));
                                        else
                                            mSyte.setStreetAddress1("");
                                        if(firstAddress.getAddressLine(1)!=null)
                                            mSyte.setStreetAddress2(firstAddress.getAddressLine(1));
                                        else
                                            mSyte.setStreetAddress2("");
                                        if (firstAddress.getLocality() != null)
                                            mSyte.setCity(firstAddress.getLocality());
                                        else
                                            mSyte.setCity("");
                                        if (firstAddress.getAdminArea() != null)
                                            mSyte.setState(firstAddress.getAdminArea());
                                        else
                                            mSyte.setState("");
                                        if (firstAddress.getCountryName() != null)
                                            mSyte.setCountry(firstAddress.getCountryName());
                                        if (firstAddress.getPostalCode() != null)
                                            mSyte.setZipCode(firstAddress.getPostalCode());
                                        else
                                            {
                                                if(firstAddress.getAddressLine(2) !=null)
                                                    {
                                                        int pos = firstAddress.getAddressLine(2).trim().lastIndexOf(" ");
                                                        String zipCodeString = firstAddress.getAddressLine(2).substring(pos + 1, firstAddress.getAddressLine(2).trim().length());
                                                        if (zipCodeString.matches("[0-9]+"))
                                                            {
                                                                mSyte.setZipCode(zipCodeString);
                                                            }
                                                        else
                                                            {
                                                                mSyte.setZipCode("");
                                                            }
                                                    }
                                                else
                                                    {
                                                        mSyte.setZipCode("");
                                                    }
                                            }
                                    }
                                catch (Exception e)
                                    {
                                        // mAddresses is not null but firstAddress is null
                                        mSyte.setLatitude(mSyteLatitude);
                                        mSyte.setLongitude(mSyteLongitude);
                                        mSyte.setStreetAddress1("");
                                        mSyte.setStreetAddress2("");
                                        mSyte.setCity("");
                                        mSyte.setState("");
                                        mSyte.setCountry("");
                                        mSyte.setZipCode("");
                                    }
                            }
                        else
                            {
                                // mAddresses is null .i.e., we did not get any address associated with mSyteLatitude & mSyteLongitude
                                mSyte.setLatitude(mSyteLatitude);
                                mSyte.setLongitude(mSyteLongitude);
                                mSyte.setStreetAddress1("");
                                mSyte.setStreetAddress2("");
                                mSyte.setCity("");
                                mSyte.setState("");
                                mSyte.setCountry("");
                                mSyte.setZipCode("");
                            }
                        if(mSyteId.toString().trim().length()<=0)
                            {
                                Intent mInt_AddSyteAdd = new Intent(AddSyteLocationActivity.this,AddSyteAddressActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                                mInt_AddSyteAdd.putExtras(mBdl);
                                mPrgDia.dismiss();
                                startActivityForResult(mInt_AddSyteAdd, 786);
                            }
                        else
                            {
                                mUpdateSyte();
                            }


                    }
                @Override
                protected void onPreExecute()
                    {
                        mPrgDia.show();
                    }
            } // END GetAddressAutomatically()
        private void mUpdateSyte()
            {
                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                mUpdateMap.put("latitude",mSyte.getLatitude());
                mUpdateMap.put("longitude", mSyte.getLongitude());
                mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
                mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener()
                {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase)
                    {
                        if(firebaseError==null)
                            {
                                Intent mInt_AddSyteDetails = new Intent(AddSyteLocationActivity.this, AddSyteAddressActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                //mBdl.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                                mInt_AddSyteDetails.putExtras(mBdl);
                                mPrgDia.dismiss();
                                startActivityForResult(mInt_AddSyteDetails, 786);
                            }
                        else
                            {
                                mPrgDia.dismiss();
                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(AddSyteLocationActivity.this, AddSyteLocationActivity.this);
                                customDialogs.sShowDialog_Common("", getString(R.string.err_msg_error_occurred), "", "", "OK", "ErrorOccurredGenData", false, false);
                            }
                    }
                });
            }// END mUpdateSyte()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == 786)
                        {
                            /*mTeamMembers.removeAll(mTeamMembers);
                            ArrayList<YasPasTeams> yasPasTeams = data.getExtras().getParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST);
                            for(int i=0;i<yasPasTeams.size();i++)
                                {
                                    YasPasTeams yasPasTeam = yasPasTeams.get(i);
                                    mTeamMembers.add(yasPasTeam);
                                }*/
                            mSyte=data.getExtras().getParcelable(StaticUtils.IPC_SYTE);
                            mSyteId=data.getExtras().getString(StaticUtils.IPC_SYTE_ID);
                        }
        } // END onActivityResult();
    }
