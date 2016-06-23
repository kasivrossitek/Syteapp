package com.syte.fragments;

import android.view.View;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.activities.addsyte.AddSyteLocationActivity;
import com.syte.activities.chat.SponsorChatListActivity;
import com.syte.activities.chat.SponsorChatWindowActivity;
import com.syte.activities.chat.UserChatWindowActivity;
import com.syte.activities.sytedetailsponsor.SyteDetailSponsorActivity;
import com.syte.activities.sytedetailuser.SyteDetailUserActivity;
import com.syte.adapters.AdapterHomeBulletinBoard;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnHomeBulletinListener;
import com.syte.models.BulletinBoard;
import com.syte.models.FilteredBulletinBoard;
import com.syte.models.Syte;
import com.syte.models.YasPasTeams;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by khalid.p on 01-04-2016.
 */
public class HomeBulletinFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnCustomDialogsListener, OnHomeBulletinListener

{
    private View mRootView;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private ArrayList<FilteredBulletinBoard> allFilteredBulletins, displayinFilteredBulletins;
    private ArrayList<String> sytesIds, userOwnedSytes;
    private ArrayList<TempSyte> tempSytes;
    private AdapterHomeBulletinBoard adapterHomeBulletinBoard;
    private RecyclerView mRvBulletins;
    private LinearLayoutManager mLayoutManager;
    private YasPasPreferences yasPasPreferences;
    //private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton mFabAddYasPas;
    private NetworkStatus mNetworkStatus;
    //private TextView mTvCenterLbl;
    private RelativeLayout mRellayNoSyte;
    private Button mBtnImLucky;
    private Dialog mDialog;
    Bundle bundle;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xFabAddYasPas: {
                if (mNetworkStatus.isNetworkAvailable()) {
                    Intent mInt_AddSyteLoc = new Intent(getActivity(), AddSyteLocationActivity.class);
                    Bundle mBun = new Bundle();

                    Syte mSyte = new Syte();

                    mSyte.setLatitude(HomeActivity.LOC_VIRTUAL.getLatitude());
                    mSyte.setLongitude(HomeActivity.LOC_VIRTUAL.getLongitude());
                    mSyte.setImageUrl("");

                    ArrayList<YasPasTeams> mTeamMembers = new ArrayList<>();
                    mBun.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBun.putString(StaticUtils.IPC_SYTE_ID, "");
                    mBun.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                    mInt_AddSyteLoc.putExtras(mBun);
                    startActivity(mInt_AddSyteLoc);
                } else {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
            }
            case R.id.xBtnImLucky: {
                setNewGeoLoc(yasPasPreferences.sGetFilterDistance(), true);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onHomeBulletinItemCliecked(FilteredBulletinBoard filteredBulletinBoard) {
        if (mNetworkStatus.isNetworkAvailable()) {
            Log.e("---", "" + filteredBulletinBoard.getIsOwned());
            if (filteredBulletinBoard.getIsOwned()) {
                Intent mIntSyteDetailSponsor = new Intent(getActivity(), SyteDetailSponsorActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, filteredBulletinBoard.getSyteId());
                mIntSyteDetailSponsor.putExtras(bundle);
                startActivity(mIntSyteDetailSponsor);
            } else {
                Intent mIntSyteDetailUser = new Intent(getActivity(), SyteDetailUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, filteredBulletinBoard.getSyteId());
                bundle.putBoolean(StaticUtils.IPC_FOLLOWED_SYTE, filteredBulletinBoard.getIsFavourite());
                mIntSyteDetailUser.putExtras(bundle);
                startActivity(mIntSyteDetailUser);
            }
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    class TempSyte {
        public String syteId, syteName, syteAddress, syteOwner;
        public boolean isFav;

        public TempSyte(String pSyteId, String pSyteName, String pSyteAddress, String pOwner, boolean pIsFav) {
            this.syteId = pSyteId;
            this.syteName = pSyteName;
            this.syteAddress = pSyteAddress;
            this.syteOwner = pOwner;
            this.isFav = pIsFav;
        }
    }// END TempSyte

    private class TimeComparator implements Comparator<FilteredBulletinBoard> {

        @Override
        public int compare(FilteredBulletinBoard lhs, FilteredBulletinBoard rhs) {

            Date lhsDate = StaticUtils.GET_SERVER_DATE((long) lhs.getDateTime());
            Date rhsDate = StaticUtils.GET_SERVER_DATE((long) rhs.getDateTime());
            return rhsDate.compareTo(lhsDate);
        }
    }// END TimeComparator

    @Override
    public void onDestroy() {
        super.onDestroy();
    }// END onDestroy()

    @Override
    public void onPause() {
        super.onPause();
        try {
            geoQuery.removeAllListeners();
        } catch (Exception e) {
            //do not do anything, as listener was removed earlier}
        }


    }// END onPause()

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
        try {

            if (bundle.getString(StaticUtils.IPC_ONGOING_CHAT_ID).length() > 0
                    && bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID).length() > 0
                    && bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE).length() > 0
                    && bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM).length() > 0
                    && bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME).length() > 0) {
                if (bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE).equalsIgnoreCase("0")) {
                    //Starting SponsorChatWindowActivity, as message came from User
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(StaticUtils.IPC_SYTE_ID, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID));
                    bundle1.putString(StaticUtils.IPC_USER_REG_NUM, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_ID));
                    bundle1.putString(StaticUtils.IPC_USER_IMAGE, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERIMG));
                    bundle1.putString(StaticUtils.IPC_USER_NAME, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME));

                    Intent intentChatWindowActivity = new Intent(getActivity(), SponsorChatWindowActivity.class);
                    intentChatWindowActivity.putExtras(bundle1);
                    bundle = null;
                    startActivity(intentChatWindowActivity);
                } else {
                    //Starting UserChatWindowActivity, as message came from Sponsor

                    ArrayList<String> arrayListSyteOwners = new ArrayList<>();
                    arrayListSyteOwners.add("");
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(StaticUtils.IPC_SYTE_ID, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID));
                    bundle1.putString(StaticUtils.IPC_SYTE_NAME, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME));
                    bundle1.putString(StaticUtils.IPC_SYTE_IMAGE, bundle.getString(StaticUtils.IPC_ONGOING_CHAT_SENDERIMG));
                    bundle1.putStringArrayList(StaticUtils.IPC_SYTE_OWNERS, arrayListSyteOwners);

                    Intent intentChatWindowActivity = new Intent(getActivity(), UserChatWindowActivity.class);
                    intentChatWindowActivity.putExtras(bundle1);
                    bundle = null;
                    startActivity(intentChatWindowActivity);
                }
            } else {
                try {
                    mDialog.dismiss();
                } catch (Exception e) {
                }
                setNewGeoLoc(yasPasPreferences.sGetFilterDistance(), false);
            }
        } catch (Exception e) {
            try {
                mDialog.dismiss();
            } catch (Exception e1) {
            }
            setNewGeoLoc(yasPasPreferences.sGetFilterDistance(), false);
        }

    }// END onResume()

    @Override
    public void onRefresh() {
        setNewGeoLoc(yasPasPreferences.sGetFilterDistance(), false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home_bulletin, container, false);
        return mRootView;
    }// END onCreateView()

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        mInItObjects();
        mInItWidgets();
    }// END onActivityCreated()

    public boolean sIsBulletinRefreshing() {
        return swipeRefreshLayout.isRefreshing();
    }

    public void setNewGeoLoc(double radius, boolean iMFeelingLucky) {
        if (mNetworkStatus.isNetworkAvailable()) {
            sytesIds.removeAll(sytesIds);
            //mProgressDialog.show();
            //swipeRefreshLayout.setRefreshing(true);


            //SK change
/*            swipeRefreshLayout.post(new Runnable() {

                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

*/
//            swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(true);
//                }
//            });

            //end SK Change



            geoFire = new GeoFire(new Firebase(StaticUtils.YASPAS_GEO_LOC_URL));
            if (!iMFeelingLucky)
                geoQuery = geoFire.queryAtLocation(new GeoLocation(HomeActivity.LOC_VIRTUAL.getLatitude(), HomeActivity.LOC_VIRTUAL.getLongitude()), radius);
            else
                geoQuery = geoFire.queryAtLocation(new GeoLocation(37.313788, -121.967114), radius);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    System.out.println("onKeyEntered -> " + key);
                    if (!sytesIds.contains(key))
                        sytesIds.add(key);
                }// END onKeyEntered()

                @Override
                public void onKeyExited(String key) {
                    System.out.println("onKeyExited -> " + key);
                    if (sytesIds.contains(key)) {
                        sytesIds.remove(key);
                    }
                }// END onKeyExited()

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }// END onKeyMoved()

                @Override
                public void onGeoQueryReady() {
                    geoQuery.removeAllListeners();
                    System.out.println("*********onGeoQueryReady*********");
                    System.out.println("SIZE " + sytesIds.size());
                    if (sytesIds.size() > 0) {
                        getSyteData();
                    } else {
                        noDataFound();
                    }
                }// END onGeoQueryReady()

                @Override
                public void onGeoQueryError(FirebaseError error) {

                }// END onGeoQueryError()
            });
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }


    }// END setNewGeoLoc()

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            getActivity().finish();
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void mInItObjects() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sytesIds = new ArrayList<>();
        allFilteredBulletins = new ArrayList<>();
        displayinFilteredBulletins = new ArrayList<>();
        tempSytes = new ArrayList<>();
        yasPasPreferences = YasPasPreferences.GET_INSTANCE(getActivity());
        userOwnedSytes = new ArrayList<>();
        //mProgressDialog = new ProgressDialog(getActivity());
        //mProgressDialog.setMessage("Please Wait...");
        //mProgressDialog.setCancelable(false);
        mNetworkStatus = new NetworkStatus(getActivity());
    }// END mInItObjects()

    private void mInItWidgets() {
        mRvBulletins = (RecyclerView) mRootView.findViewById(R.id.xRvBulletins);
        mRvBulletins.setHasFixedSize(true);
        mRvBulletins.setLayoutManager(mLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.color_orange, R.color.color_dark_orange);

        swipeRefreshLayout.setEnabled(false);

        mFabAddYasPas = (FloatingActionButton) mRootView.findViewById(R.id.xFabAddYasPas);
        mFabAddYasPas.setOnClickListener(this);
        //sk Hide this button for now.
        mFabAddYasPas.setVisibility(View.GONE);
        mRellayNoSyte = (RelativeLayout) mRootView.findViewById(R.id.xRellayNoSyte);
        mRellayNoSyte.setVisibility(View.GONE);
        mBtnImLucky = (Button) mRootView.findViewById(R.id.xBtnImLucky);
        mBtnImLucky.setOnClickListener(this);
        mInItInstructionPopUp();
    }// END mInItWidgets()

    private void getSyteData() {
        tempSytes.removeAll(tempSytes);
        for (int i = 0; i < sytesIds.size(); i++) {
            Log.e("getSyteData : ", "" + i);
            if (!HomeBulletinFragment.this.isResumed()) {
                Log.e("getSyteData", "HomeBulletinFragment is not active");
                mNullifyAllData();
                break;
            }
            final String finalSyteId = sytesIds.get(i);
            Firebase firebaseSyte = new Firebase(StaticUtils.YASPAS_URL).child(finalSyteId);
            final int finalI = i;
            firebaseSyte.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && dataSnapshot != null) {
                        Syte syte = dataSnapshot.getValue(Syte.class);
                        if (syte.getIsDeleted() == 0 && syte.getIsActive() == 1) {
                            TempSyte tempSyte = new TempSyte(dataSnapshot.getKey(), syte.getName(), syte.getCity() + " " + syte.getZipCode(), syte.getOwner(),
                                    dataSnapshot.child(StaticUtils.FOLLOWERS_YASPAS).hasChild(yasPasPreferences.sGetRegisteredNum()));
                            tempSytes.add(tempSyte);
                        }
                    }
                    if (finalI == sytesIds.size() - 1) {
                        if (tempSytes.size() > 0) {
                            //getOwnedSytes();
                            allFilteredBulletins.removeAll(allFilteredBulletins);
                            getBulletinData(0);
                        } else {
                            noDataFound();
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }// END getSyteData()

    /*
    private void getOwnedSytes()
    {
        userOwnedSytes.removeAll(userOwnedSytes);
        Firebase firebaseYasPaseeOwnedSyte = new Firebase(StaticUtils.YASPASEE_URL).child(yasPasPreferences.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS);
        firebaseYasPaseeOwnedSyte.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot != null && dataSnapshot.getValue() != null)
                {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext())
                    {
                        if (!HomeBulletinFragment.this.isResumed())
                        {
                            Log.e("getOwnedSytes", "HomeBulletinFragment is not active");
                            mNullifyAllData();
                            break;
                        }
                        DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                        userOwnedSytes.add(dataSnapshot1.getKey());
                        if (!iterator.hasNext())
                        {
                            allFilteredBulletins.removeAll(allFilteredBulletins);
                            getBulletinData(0);
                        }
                    }
                }
                else
                {
                    allFilteredBulletins.removeAll(allFilteredBulletins);
                    getBulletinData(0);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError)
            {
            }
        });
    }// END getOwnedSytes()

    */

    private void getBulletinData(final int pIndex) {
        if (!HomeBulletinFragment.this.isResumed()) {
            Log.e("getBulletinData", "HomeBulletinFragment is not active");
            mNullifyAllData();
        } else {
            Log.e("*************pIndex ", "" + pIndex);
            if (pIndex == tempSytes.size()) {
                if (allFilteredBulletins.size() > 0) {
                    //mTvCenterLbl.setVisibility(View.GONE);
                    mRellayNoSyte.setVisibility(View.GONE);
                    display(true);
                } else {
                    noDataFound();
                }
            } else {
                Firebase firebaseBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(tempSytes.get(pIndex).syteId);
                firebaseBulletinBoard.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                                BulletinBoard bulletinBoard = dataSnapshot1.getValue(BulletinBoard.class);
                                FilteredBulletinBoard filteredBulletinBoard = new FilteredBulletinBoard();
                                filteredBulletinBoard.setSyteId(tempSytes.get(pIndex).syteId);
                                filteredBulletinBoard.setSyteName(tempSytes.get(pIndex).syteName);
                                filteredBulletinBoard.setSyteAddress(tempSytes.get(pIndex).syteAddress);
                                filteredBulletinBoard.setBulletinId(dataSnapshot1.getKey());
                                filteredBulletinBoard.setBulletinSubject(bulletinBoard.getSubject());
                                filteredBulletinBoard.setBulletinBody(bulletinBoard.getBody());
                                filteredBulletinBoard.setBulletinImageUrl(bulletinBoard.getImageUrl());
                                filteredBulletinBoard.setDateTime(bulletinBoard.getDateTime());
                                filteredBulletinBoard.setSendToAllFollowers(bulletinBoard.getSendToAllFollowers());
                                //filteredBulletinBoard.setIsOwned((userOwnedSytes.contains(tempSytes.get(pIndex).syteId)) ? true : false);
                                filteredBulletinBoard.setIsOwned(tempSytes.get(pIndex).syteOwner.equals(yasPasPreferences.sGetRegisteredNum()));
                                System.out.println(tempSytes.get(pIndex).syteName + "--" + tempSytes.get(pIndex).isFav);
                                filteredBulletinBoard.setIsFavourite(tempSytes.get(pIndex).isFav);
                                allFilteredBulletins.add(filteredBulletinBoard);
                                if (allFilteredBulletins.size() == 10) {
                                    display(false);
                                }
                                if (!iterator.hasNext()) {
                                    getBulletinData(pIndex + 1);
                                }
                            }
                        } else {
                            getBulletinData(pIndex + 1);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }// END getBulletinData()

    private void display(boolean refreshDisable) {

                   /* for (int i = 0; i < allFilteredBulletins.size(); i++)
                    {
                        System.out.println(i + " -> " + allFilteredBulletins.get(i).getSyteName() + " : " + allFilteredBulletins.get(i).getDateTime() + " : " + allFilteredBulletins.get(i).getBulletinSubject());
                    }*/

        Collections.sort(allFilteredBulletins, new TimeComparator());

                /*System.out.println("SIZE : ******** "+allFilteredBulletins.size());
                    for (int i = 0; i < allFilteredBulletins.size(); i++)
                    {
                        System.out.println(i + " -> " + allFilteredBulletins.get(i).getSyteName() + " : " + StaticUtils.CONVERT_BULLETIN_DATE_TIME((long)(allFilteredBulletins.get(i).getDateTime())) + " : " + allFilteredBulletins.get(i).getBulletinSubject());
                    }*/

        if (allFilteredBulletins.size() > 0) {
            //mTvCenterLbl.setVisibility(View.GONE);
            mRellayNoSyte.setVisibility(View.GONE);
        }
        displayinFilteredBulletins.removeAll(displayinFilteredBulletins);
        for (int i = 0; i < allFilteredBulletins.size(); i++) {
            FilteredBulletinBoard f = allFilteredBulletins.get(i);
            if (HomeActivity.IS_FAVOURITE_CHECKED) {
                if (f.getIsFavourite())
                    displayinFilteredBulletins.add(f);
            } else {
                displayinFilteredBulletins.add(f);
            }

        }
        if (adapterHomeBulletinBoard == null) {
            adapterHomeBulletinBoard = new AdapterHomeBulletinBoard(getActivity(), displayinFilteredBulletins, this);
            mRvBulletins.setAdapter(adapterHomeBulletinBoard);
        } else {
            adapterHomeBulletinBoard.notifyDataSetChanged();
        }
        if (refreshDisable)
            swipeRefreshLayout.setRefreshing(false);
    }

    public void sSetFavourite() {
        ProgressDialog mProgressDialog1 = new ProgressDialog(getActivity());
        mProgressDialog1.setMessage("Please Wait...");
        mProgressDialog1.setCancelable(false);
        mProgressDialog1.show();
        displayinFilteredBulletins.removeAll(displayinFilteredBulletins);
        for (int i = 0; i < allFilteredBulletins.size(); i++) {
            FilteredBulletinBoard f = allFilteredBulletins.get(i);
            if (HomeActivity.IS_FAVOURITE_CHECKED) {
                if (f.getIsFavourite())
                    displayinFilteredBulletins.add(f);
            } else {
                displayinFilteredBulletins.add(f);
            }

        }
        if (adapterHomeBulletinBoard == null) {
            adapterHomeBulletinBoard = new AdapterHomeBulletinBoard(getActivity(), displayinFilteredBulletins, this);
            mRvBulletins.setAdapter(adapterHomeBulletinBoard);
        } else {
            adapterHomeBulletinBoard.notifyDataSetChanged();
        }
        mProgressDialog1.dismiss();
    }

    private void noDataFound() {
        //mProgressDialog.dismiss(); // needs to write code for pop up
        swipeRefreshLayout.setRefreshing(false);
        mRellayNoSyte.setVisibility(View.VISIBLE);
        allFilteredBulletins.removeAll(allFilteredBulletins);
        displayinFilteredBulletins.removeAll(displayinFilteredBulletins);
        if (adapterHomeBulletinBoard != null)
            adapterHomeBulletinBoard.notifyDataSetChanged();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mDialog.show();

            }
        }, 500);
    }// END noDataFound()

    private void mNullifyAllData() {
        try {
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
        }
        try {
            allFilteredBulletins.removeAll(allFilteredBulletins);
        } catch (Exception e) {
        }
        try {
            displayinFilteredBulletins.removeAll(displayinFilteredBulletins);
        } catch (Exception e) {
        }
        try {
            sytesIds.removeAll(sytesIds);
        } catch (Exception e) {
        }
        try {
            userOwnedSytes.removeAll(userOwnedSytes);
        } catch (Exception e) {
        }
        try {
            tempSytes.removeAll(tempSytes);
        } catch (Exception e) {
        }
        Log.e("mNullifyAllData", "ALL DATA NULLIFIED");
    }

    private void mInItInstructionPopUp() {
        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.layout_no_syte_instruction);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        LinearLayout mLinLayInst = (LinearLayout) window.findViewById(R.id.xLinLayInst);
        mLinLayInst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }// END mShowInstructions()
}

