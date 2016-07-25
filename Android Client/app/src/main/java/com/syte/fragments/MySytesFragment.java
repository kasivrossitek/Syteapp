package com.syte.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.activities.addsyte.AddSyteLocationActivity;
import com.syte.adapters.SponseredYasPasesAdapter;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.OwnedYasPases;
import com.syte.models.Syte;
import com.syte.models.YasPasTeams;
import com.syte.services.MediaUploadService;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class MySytesFragment extends Fragment implements View.OnClickListener, OnCustomDialogsListener {
    private View mRootView;

    private TextView mTvCenterLbl;
    private RecyclerView mRvManageYaspas;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<OwnedYasPases> mOwnedYasPasLst;
    private ArrayList<String> mOwnedYasPasKeyLst;
    private NetworkStatus mNetworkStatus;
    private YasPasPreferences mPref;
    private SponseredYasPasesAdapter mManageYasPasesAdapter;
    private Firebase mFBOwnedYasPaseesUrl, mFBPublicYasPaseesUrl;
    private ProgressDialog mProgressDialog;
    private OwnedYasPasesDataChangeListener mOwnedYasPasesDataChangeListener;
    private PublicYasPasesDataChangeListener mPublicYasPasesDataChangeListener;//Public sytes by kasi on 16-7-16


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_sytes, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize fragment
        mNetworkStatus = new NetworkStatus(getActivity());
        mPref = YasPasPreferences.GET_INSTANCE(getActivity());

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mTvCenterLbl = (TextView) mRootView.findViewById(R.id.xTvCenterLbl);
        ((TextView) mRootView.findViewById(R.id.xTvAddNewSyte)).setOnClickListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mOwnedYasPasesDataChangeListener = new OwnedYasPasesDataChangeListener();
        mPublicYasPasesDataChangeListener = new PublicYasPasesDataChangeListener();


    }


    private class OwnedYasPasesDataChangeListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (mProgressDialog.isShowing())//This listener will be running continously. I am showing progress dialog only in onCreate(), so i am dismisssing the progress dialog only if it is showing.
                mProgressDialog.dismiss();

            mOwnedYasPasLst.clear();
            mOwnedYasPasKeyLst.clear();

            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
            while (it.hasNext()) {
                DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                OwnedYasPases b = dataSnapshot1.getValue(OwnedYasPases.class);
                mOwnedYasPasLst.add(b);
                mOwnedYasPasKeyLst.add(b.getYasPasId());
            }
            mFBPublicYasPaseesUrl = new Firebase(StaticUtils.PUBLIC_YASPAS);
            mFBPublicYasPaseesUrl.addValueEventListener(mPublicYasPasesDataChangeListener);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            if (mProgressDialog.isShowing())//This listener will be running continously. I am showing progress dialog only in onCreate(), so i am dismisssing the progress dialog only if it is showing.
                mProgressDialog.dismiss();
        }
    }

    private class PublicYasPasesDataChangeListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (mProgressDialog.isShowing())//This listener will be running continously. I am showing progress dialog only in onCreate(), so i am dismisssing the progress dialog only if it is showing.
                mProgressDialog.dismiss();
            if (dataSnapshot != null) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                    OwnedYasPases b = dataSnapshot1.getValue(OwnedYasPases.class);
                    mOwnedYasPasLst.add(b);
                    mOwnedYasPasKeyLst.add(b.getYasPasId());
                }
            }
            if (mManageYasPasesAdapter == null) {
                mManageYasPasesAdapter = new SponseredYasPasesAdapter(getActivity(), mOwnedYasPasLst, mOwnedYasPasKeyLst, ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));
                mRvManageYaspas.setAdapter(mManageYasPasesAdapter);

            } else {
                mManageYasPasesAdapter.notifyDataSetChanged();
            }

            mTvCenterLbl.setText(getString(R.string.owned_sytes_not_available));
            mTvCenterLbl.setVisibility((mOwnedYasPasLst.size() > 0) ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            if (mProgressDialog.isShowing())//This listener will be running continously. I am showing progress dialog only in onCreate(), so i am dismisssing the progress dialog only if it is showing.
                mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mRvManageYaspas = (RecyclerView) mRootView.findViewById(R.id.xRvManageYaspas);
        mRvManageYaspas.setHasFixedSize(true);
        mRvManageYaspas.setLayoutManager(mLayoutManager);

        mOwnedYasPasLst = new ArrayList<>();
        mOwnedYasPasKeyLst = new ArrayList<>();


        if (mNetworkStatus.isNetworkAvailable()) {
            mTvCenterLbl.setVisibility(View.GONE);
            mProgressDialog.show();
        } else {
            mTvCenterLbl.setText(YasPasMessages.NO_INTERNET_ERROR_MESSAGE);
            mTvCenterLbl.setVisibility(View.VISIBLE);
        }
        mFBOwnedYasPaseesUrl = new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child("ownedYasPases");

        mFBOwnedYasPaseesUrl.addValueEventListener(mOwnedYasPasesDataChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        mFBOwnedYasPaseesUrl.removeEventListener(mOwnedYasPasesDataChangeListener);
        mFBPublicYasPaseesUrl.removeEventListener(mPublicYasPasesDataChangeListener);
        mManageYasPasesAdapter = null;

        mRvManageYaspas.setAdapter(null);
        mRvManageYaspas = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xTvAddNewSyte: {
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

                break;
            }

            default:
                break;
        }
    }

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
}
