package com.syte.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.syte.adapters.AdapterNotificationBulletinYou;
import com.syte.listeners.OnBulletinNotificationDelete;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.BulletinBoardPush;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class NotificationsYouFragment extends Fragment implements OnBulletinNotificationDelete, View.OnClickListener, OnCustomDialogsListener
{
    private View mRootView;
    private RecyclerView mRvNotifications;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTvCenterLbl, mTvClearNow;
    private AdapterNotificationBulletinYou mAdapterNotificationYou;
    private ArrayList<BulletinBoardPush> mNotificationLst;
    private ArrayList<String> mNotificatinKeyLst;

    private NetworkStatus mNetworkStatus;
    private YasPasPreferences mPref;
    private Firebase mFBaseNotificationBulletinRef;
    private ProgressDialog mProgressDialog;
    private CustomDialogs mCustomDialogs;
    private MyBulletinNotificationDataChangeListener myBulletinNotificationDataChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_notification_you, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mNetworkStatus = new NetworkStatus(getActivity());
        mPref = YasPasPreferences.GET_INSTANCE(getActivity());
        mCustomDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.prg_bar_wait));
        mProgressDialog.setCancelable(false);

        mTvClearNow = (TextView) mRootView.findViewById(R.id.xTvClearNow);
        mTvCenterLbl = (TextView) mRootView.findViewById(R.id.xTvCenterLbl);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTvClearNow.setOnClickListener(this);

        myBulletinNotificationDataChangeListener = new MyBulletinNotificationDataChangeListener();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.xTvClearNow:
            {
                if (mNetworkStatus.isNetworkAvailable())
                {
                    if (mProgressDialog!=null)
                        mProgressDialog.show();

                    mFBaseNotificationBulletinRef.removeValue(new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                        {
                            if (mProgressDialog!=null)
                                mProgressDialog.dismiss();
                        }
                    });
                }
                else
                {
                    mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                }

                break;
            }

            default:
                break;
        }
    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        // Do nothing
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
        {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private class MyBulletinNotificationDataChangeListener implements ValueEventListener
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            mNotificationLst.clear();
            mNotificatinKeyLst.clear();

            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
            while (it.hasNext())
            {
                DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                BulletinBoardPush bulletinBoardPush = dataSnapshot1.getValue(BulletinBoardPush.class);
                mNotificationLst.add(bulletinBoardPush);
                mNotificatinKeyLst.add(dataSnapshot1.getKey());
            }

            if (mAdapterNotificationYou == null)
            {
                mAdapterNotificationYou = new AdapterNotificationBulletinYou(getActivity(), NotificationsYouFragment.this, mNotificatinKeyLst, mNotificationLst, ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));
                mRvNotifications.setAdapter(mAdapterNotificationYou);
            }
            else
            {
                mAdapterNotificationYou.notifyDataSetChanged();
            }

            mTvCenterLbl.setText(getString(R.string.notification_bulletin_not_available));
            mTvCenterLbl.setVisibility((mNotificationLst.size() > 0) ? View.GONE : View.VISIBLE);
            mTvClearNow.setVisibility((mNotificationLst.size() > 0) ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError)
        {

        }
    }

    @Override
    public void onBulletinNotificationDelete(String bulletinNotificationKey)
    {
       System.out.println("delete " + bulletinNotificationKey);

        if (mProgressDialog!=null)
            mProgressDialog.show();

        mFBaseNotificationBulletinRef.child(bulletinNotificationKey).removeValue(new Firebase.CompletionListener()
            {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase)
                {
                    if (mProgressDialog!=null)
                        mProgressDialog.dismiss();
                }
            });

    }


    @Override
    public void onResume()
    {
        super.onResume();

        mRvNotifications = (RecyclerView) mRootView.findViewById(R.id.xRvNotifications);
        mRvNotifications.setHasFixedSize(true);
        mRvNotifications.setLayoutManager(mLayoutManager);

        mNotificatinKeyLst = new ArrayList<>();
        mNotificationLst = new ArrayList<>();

        if (mNetworkStatus.isNetworkAvailable())
        {
            mTvCenterLbl.setVisibility(View.GONE);
        }
        else
        {
            mTvCenterLbl.setText(YasPasMessages.NO_INTERNET_ERROR_MESSAGE);
            mTvCenterLbl.setVisibility(View.VISIBLE);
        }

        mFBaseNotificationBulletinRef = new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child(StaticUtils.BULLETIN_BOARD_NOTIFICATION_INBOX);
        mFBaseNotificationBulletinRef.addValueEventListener(myBulletinNotificationDataChangeListener);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mFBaseNotificationBulletinRef.removeEventListener(myBulletinNotificationDataChangeListener);
        mAdapterNotificationYou=null;

        mRvNotifications.setAdapter(null);
        mRvNotifications=null;
    }
}
