package com.syte.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.FollowingYasPasAdapter;
import com.syte.models.FollowingYasPas;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class MyFavouriteSyteFragment extends Fragment
{
    private View mRootView;

    private TextView mTvCenterLbl;
    private RecyclerView mRvFollowingYaspas;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<FollowingYasPas> mFollowingYasPasLst;
    private ArrayList<String> mFollowingYasPasKeyLst;

    private NetworkStatus mNetworkStatus;
    private YasPasPreferences mPref;
    private FollowingYasPasAdapter mFollowingYasPasAdapter;
    private Firebase mFBYasPaseesFollowersUrl;
    private ProgressDialog mProgressDialog;
    private FollowingSytesDataChangeListener mFollowingSytesChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_my_favourite_sytes, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Initialize fragment

        mNetworkStatus = new NetworkStatus(getActivity());
        mPref = YasPasPreferences.GET_INSTANCE(getActivity());
        mFollowingSytesChangeListener = new FollowingSytesDataChangeListener();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mTvCenterLbl = (TextView) mRootView.findViewById(R.id.xTvCenterLbl);
        mLayoutManager = new LinearLayoutManager(getActivity());



    }

    private class FollowingSytesDataChangeListener implements ValueEventListener
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            System.out.println("following " + dataSnapshot);

            if (mProgressDialog.isShowing())//This listener will be running continously. I am showing progress dialog only in onCreate(), so i am dismisssing the progress dialog only if it is showing.
                mProgressDialog.dismiss();

            mFollowingYasPasLst.clear();
            mFollowingYasPasKeyLst.clear();

            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
            while (it.hasNext())
            {
                DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                FollowingYasPas b = dataSnapshot1.getValue(FollowingYasPas.class);
                mFollowingYasPasLst.add(b);
                mFollowingYasPasKeyLst.add(dataSnapshot1.getKey().toString());
            }

            if (mFollowingYasPasAdapter == null)
            {
                mFollowingYasPasAdapter = new FollowingYasPasAdapter(getActivity(), mFollowingYasPasLst, mFollowingYasPasKeyLst,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));
                mRvFollowingYaspas.setAdapter(mFollowingYasPasAdapter);
            }
            else
            {
                mFollowingYasPasAdapter.notifyDataSetChanged();
            }

            mTvCenterLbl.setText(getString(R.string.favourite_sytes_not_available));
            mTvCenterLbl.setVisibility((mFollowingYasPasLst.size() > 0) ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError)
        {
            if (mProgressDialog.isShowing())//This listener will be running continously. I am showing progress dialog only in onCreate(), so i am dismisssing the progress dialog only if it is showing.
                mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mRvFollowingYaspas = (RecyclerView) mRootView.findViewById(R.id.xRvFollowingYaspas);
        mRvFollowingYaspas.setLayoutManager(mLayoutManager);
        mRvFollowingYaspas.setHasFixedSize(true);



        mFollowingYasPasLst = new ArrayList<>();
        mFollowingYasPasKeyLst = new ArrayList<>();

        mFBYasPaseesFollowersUrl = new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child("followingYasPases");
        if (mNetworkStatus.isNetworkAvailable())
        {
            mTvCenterLbl.setVisibility(View.GONE);
            mProgressDialog.show();
        }
        else
        {
            mTvCenterLbl.setText(YasPasMessages.NO_INTERNET_ERROR_MESSAGE);
            mTvCenterLbl.setVisibility(View.VISIBLE);
        }

        mFBYasPaseesFollowersUrl.addValueEventListener(mFollowingSytesChangeListener);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mFBYasPaseesFollowersUrl.removeEventListener(mFollowingSytesChangeListener);
        mFollowingYasPasAdapter=null;
        mRvFollowingYaspas.setAdapter(null);
        mRvFollowingYaspas=null;
    }
}
