package com.syte.fragments;

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
import com.syte.activities.chat.SponsorChatListActivity;
import com.syte.adapters.AdapterMySytesChats;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnMessageItemClick;
import com.syte.models.Message;
import com.syte.models.SponsorInnerMessage;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 14-03-2016.
 */
public class MySytesChatsFragment extends Fragment implements OnCustomDialogsListener, OnMessageItemClick {
        private View mRootView;
        private RecyclerView mRvMySytesChats;
        private RecyclerView.LayoutManager mLayoutManager;
        private TextView mTvCenterLbl;
        private NetworkStatus mNetworkStatus;
        private YasPasPreferences mPref;
        //private ProgressDialog mProgressDialog;
        private Firebase firebaseMySyteChats;
        private ArrayList<Message> yasPaseeChats;
        private AdapterMySytesChats adapterMySytesChats;
        private ListenerMyChat listenerMyChat;
        @Override
        public void onResume()
        {
            super.onResume();
            mRvMySytesChats = (RecyclerView) mRootView.findViewById(R.id.xRvMySytesChats);
            mRvMySytesChats.setHasFixedSize(true);
            mRvMySytesChats.setLayoutManager(mLayoutManager);
            /*if(mNetworkStatus.isNetworkAvailable())
                mProgressDialog.show();*/
            firebaseMySyteChats.addValueEventListener(listenerMyChat);
        }// END onResume()
        @Override
        public void onPause()
        {
            super.onPause();
            firebaseMySyteChats.removeEventListener(listenerMyChat);
            adapterMySytesChats=null;
            mRvMySytesChats.setAdapter(adapterMySytesChats);
            mRvMySytesChats=null;
        }// END onPause()
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            mRootView = inflater.inflate(R.layout.fragment_my_sytes_chat, container, false);
            return mRootView;
        }// END onCreateView()
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            mInItObjects();
            mInItWidgets();
        }// END onActivityCreated()
        private void mInItWidgets()
        {
            mTvCenterLbl = (TextView) mRootView.findViewById(R.id.xTvCenterLbl);
        }// END mInItWidgets()
        private void mInItObjects()
        {
            mNetworkStatus = new NetworkStatus(getActivity());
            mPref = YasPasPreferences.GET_INSTANCE(getActivity());
           /* mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.prg_bar_wait));
            mProgressDialog.setCancelable(false);*/
            mLayoutManager = new LinearLayoutManager(getActivity());
            firebaseMySyteChats=new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child(StaticUtils.MY_YASPASES_CHATS);
            yasPaseeChats=new ArrayList<>();
            listenerMyChat = new ListenerMyChat();
        }// END mInItObjects()
        @Override
        public void onMessageItemClicked(Message yasPaseeChat)
        {
            if(mNetworkStatus.isNetworkAvailable())
            {
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, yasPaseeChat.getSyteId());
                bundle.putString(StaticUtils.IPC_SYTE_NAME, yasPaseeChat.getSyteName());

                Intent intentSponsorChatListActivity = new Intent(getActivity(), SponsorChatListActivity.class);
                intentSponsorChatListActivity.putExtras(bundle);
                startActivity(intentSponsorChatListActivity);
            }
            else
            {
                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(),this);
                customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
            }
        }

    @Override
    public void onSponsorInnerMessageItemClciked(SponsorInnerMessage sponsorInnerMessage) {

    }

    @Override
        public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
        {
            if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
            {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startMain);
                getActivity().finish();
            }
        }// END onDialogLeftBtnClicked()

        @Override
        public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
        {
            if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
            {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }// END onDialogRightBtnClicked()

        private class ListenerMyChat implements ValueEventListener
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                yasPaseeChats.removeAll(yasPaseeChats);
                /*if(dataSnapshot==null && mProgressDialog.isShowing())
                    {
                        mProgressDialog.dismiss();
                    }*/
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    DataSnapshot dataSnapshot1 = (DataSnapshot)iterator.next();
                    Message yasPaseeChat = dataSnapshot1.getValue(Message.class);
                    yasPaseeChats.add(yasPaseeChat);
                    if(!iterator.hasNext())
                    {
                        if(adapterMySytesChats==null)
                        {
                            adapterMySytesChats=new AdapterMySytesChats(yasPaseeChats,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)),MySytesChatsFragment.this);
                            mRvMySytesChats.setAdapter(adapterMySytesChats);
                        }
                        else
                        {
                            adapterMySytesChats.notifyDataSetChanged();
                        }
                        /*if(mProgressDialog.isShowing())
                        {
                            mProgressDialog.dismiss();
                        }*/
                    }
                }
                if(yasPaseeChats.size()<=0)
                {
                    mTvCenterLbl.setVisibility(View.VISIBLE);
                }
                else
                {
                    mTvCenterLbl.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError)
            {

            }
        }// END ListenerMyChat
    }// END MySytesChatsFragment
