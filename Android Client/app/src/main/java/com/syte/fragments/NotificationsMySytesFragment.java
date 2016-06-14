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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.AdapterNotificationFollowMySytes;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnFollowNotificationDelete;
import com.syte.listeners.OnTransferASyteRequest;
import com.syte.models.YasPasPush;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.TransferASyteRequest;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class NotificationsMySytesFragment extends Fragment implements OnFollowNotificationDelete, View.OnClickListener, OnCustomDialogsListener, OnTransferASyteRequest {
    private View mRootView;

    private RecyclerView mRvNotifications;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTvCenterLbl, mTvClearNow;
    private AdapterNotificationFollowMySytes mAdapterNotificationMySytes;
    private ArrayList<YasPasPush> mNotificationLst;
    private ArrayList<String> mNotificatinKeyLst;

    private NetworkStatus mNetworkStatus;
    private YasPasPreferences mPref;
    private Firebase mFBaseNotificationMyYaspasRef;
    private ProgressDialog mProgressDialog;
    private CustomDialogs mCustomDialogs;
    private MySytesNotificationDataChangeListener mMySytesNotificationDataChangeListener;
    // Claim Syte
    private YasPasPush claimedYasPasPush;
    private String claimedYasPasPushKey;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_notification_my_sytes, container, false);
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
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mTvClearNow = (TextView) mRootView.findViewById(R.id.xTvClearNow);
        mTvCenterLbl = (TextView) mRootView.findViewById(R.id.xTvCenterLbl);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTvClearNow.setOnClickListener(this);

        mMySytesNotificationDataChangeListener = new MySytesNotificationDataChangeListener();
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

                    mFBaseNotificationMyYaspasRef.removeValue(new Firebase.CompletionListener()
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
        if(paramCallingMethod.equalsIgnoreCase("ClaimSyteDecision"))
            {
                if(mNetworkStatus.isNetworkAvailable())
                    {
                        mClaimSyteRequestRejected();
                    }
                else
                    {
                        mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                    }
            }
    }// END onDialogLeftBtnClicked();

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
            {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        if(paramCallingMethod.equalsIgnoreCase("ClaimSyteDecision"))
            {
                if(mNetworkStatus.isNetworkAvailable())
                    {
                        TransferASyteRequest transferASyteRequest = new TransferASyteRequest(claimedYasPasPush.getSyteId(),mPref.sGetRegisteredNum(),
                                claimedYasPasPush.getRegisteredNum(),NotificationsMySytesFragment.this);
                        transferASyteRequest.sTransferASyteStart();

                    }
                else
                    {
                        mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                    }
            }
    }// END onDialogRightBtnClicked()

    @Override
    public void onTasrStarted()
        {
            mProgressDialog.show();
        }

    @Override
    public void onTasrErrorOccured()
        {
            mProgressDialog.dismiss();
            mCustomDialogs.sShowDialog_Common(YasPasMessages.ERR_OCC_SUBJECT, YasPasMessages.CLAIM_SYTE_ERR_OCCURED_HEADING, null, null, "OK", "claimSyteErr", false, false);
        }

    @Override
    public void onTasrClaimerExistance(boolean paramIsClaimerExists)
        {
            if(!paramIsClaimerExists)
                {
                    mProgressDialog.dismiss();
                    mCustomDialogs.sShowDialog_Common(YasPasMessages.ERR_OCC_SUBJECT, YasPasMessages.CLAIM_SYTE_CLAIMER_NOT_EXISTS, null, null, "OK", "claimSyteErr", false, false);
                }
        }

    @Override
    public void onTasrFinshed()
        {

            mClaimSyteRequestAccepted();

        }

    private class MySytesNotificationDataChangeListener implements ValueEventListener
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
                YasPasPush notificationBulletIn = dataSnapshot1.getValue(YasPasPush.class);
                mNotificationLst.add(notificationBulletIn);
                mNotificatinKeyLst.add(dataSnapshot1.getKey());
            }

            if (mAdapterNotificationMySytes == null)
            {
                mAdapterNotificationMySytes = new AdapterNotificationFollowMySytes(getActivity(), NotificationsMySytesFragment.this, mNotificatinKeyLst, mNotificationLst, ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));
                mRvNotifications.setAdapter(mAdapterNotificationMySytes);
            }
            else
            {
                mAdapterNotificationMySytes.notifyDataSetChanged();
            }

            mTvCenterLbl.setText(getString(R.string.notification_my_sytes_not_available));
            mTvCenterLbl.setVisibility((mNotificationLst.size() > 0) ? View.GONE : View.VISIBLE);
            mTvClearNow.setVisibility((mNotificationLst.size() > 0) ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError)
        {

        }
    }

    @Override
    public void onFollowNotificationDelete(String followNotificationKey)
    {
        System.out.println("delete " + followNotificationKey);

        if (mProgressDialog!=null)
            mProgressDialog.show();

        mFBaseNotificationMyYaspasRef.child(followNotificationKey).removeValue(new Firebase.CompletionListener()
        {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase)
            {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onSyteClaimNotificationClicked(YasPasPush yasPasPush,String followNotificationKey)
        {
            mCustomDialogs.sShowDialog_Common(getString(R.string.err_claim_syte_subject), yasPasPush.getSyteName() + " is claimed by " + yasPasPush.getUserName(), null, "REJECT", "ACCEPT", "ClaimSyteDecision", false, false);
            claimedYasPasPush=yasPasPush;
            claimedYasPasPushKey=followNotificationKey;
        }// END onSyteClaimNotificationClicked()


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

        mFBaseNotificationMyYaspasRef = new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child(StaticUtils.MY_YASPASES_NOTIFICATION_INBOX);
        mFBaseNotificationMyYaspasRef.addValueEventListener(mMySytesNotificationDataChangeListener);
    }

    @Override
    public void onPause()
        {
    super.onPause();

    mFBaseNotificationMyYaspasRef.removeEventListener(mMySytesNotificationDataChangeListener);
    mAdapterNotificationMySytes=null;

    mRvNotifications.setAdapter(null);
    mRvNotifications=null;
    }

    private void mClaimSyteRequestRejected()
        {
            // Syte Claim Request Rejected
            mProgressDialog.show();
            Firebase firebasePushNotification = new Firebase(StaticUtils.BULLETIN_PUSH_NOTIFICATION_URL);

            HashMap<String ,Object> hashMap = new HashMap<String,Object>();
            hashMap.put("syteId", claimedYasPasPush.getSyteId());
            hashMap.put("syteImageUrl", "");
            hashMap.put("syteName", "");
            hashMap.put("bulletinDateTime", ServerValue.TIMESTAMP);
            hashMap.put("pushType", StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_REJECTED);
            hashMap.put("registeredNum", claimedYasPasPush.getRegisteredNum());



            firebasePushNotification.push().setValue(hashMap, new Firebase.CompletionListener()
                {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase)
                        {
                            try
                            {
                                mProgressDialog.dismiss();
                            }
                            catch (Exception e)
                            {}

                }
            });
        }// END mClaimSyteRequestRejected()
    private void mClaimSyteRequestAccepted()
        {
            // Syte Claim Request Rejected
            Firebase firebasePushNotification = new Firebase(StaticUtils.BULLETIN_PUSH_NOTIFICATION_URL);

            HashMap<String ,Object> hashMap = new HashMap<String,Object>();
            hashMap.put("syteId", claimedYasPasPush.getSyteId());
            hashMap.put("syteImageUrl", "");
            hashMap.put("syteName", "");
            hashMap.put("bulletinDateTime", ServerValue.TIMESTAMP);
            hashMap.put("pushType", StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_ACCEPTED);
            hashMap.put("registeredNum", claimedYasPasPush.getRegisteredNum());



            firebasePushNotification.push().setValue(hashMap, new Firebase.CompletionListener()
            {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase)
                {
                    try
                    {
                        mProgressDialog.dismiss();
                    }
                    catch (Exception e)
                    {}
                    Toast.makeText(getActivity(), YasPasMessages.CLAIM_SYTE_SUCCESS, Toast.LENGTH_LONG).show();

                }
            });
        }// END mClaimSyteRequestAccepted()
    }
