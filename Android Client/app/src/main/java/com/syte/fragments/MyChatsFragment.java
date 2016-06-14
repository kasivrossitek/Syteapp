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
import com.syte.activities.chat.UserChatWindowActivity;
import com.syte.adapters.AdapterMyChats;
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
public class MyChatsFragment extends Fragment implements OnMessageItemClick, OnCustomDialogsListener
    {
        private View mRootView;
        private RecyclerView mRvMyChats;
        private RecyclerView.LayoutManager mLayoutManager;
        private TextView mTvCenterLbl;
        private NetworkStatus mNetworkStatus;
        private YasPasPreferences mPref;
        private ProgressDialog mProgressDialog;
        private Firebase firebaseMyChat;
        private ArrayList<Message>yasPaseeChats;
        private AdapterMyChats adapterMyChats;
        private ListenerMyChat listenerMyChat;
        @Override
        public void onResume()
            {
                super.onResume();
                mRvMyChats = (RecyclerView) mRootView.findViewById(R.id.xRvMyChats);
                mRvMyChats.setHasFixedSize(true);
                mRvMyChats.setLayoutManager(mLayoutManager);
                if(mNetworkStatus.isNetworkAvailable())
                    mProgressDialog.show();
                firebaseMyChat.addValueEventListener(listenerMyChat);
            }// END onResume()
        @Override
        public void onPause()
            {
                super.onPause();
                firebaseMyChat.removeEventListener(listenerMyChat);
                adapterMyChats=null;
                mRvMyChats.setAdapter(adapterMyChats);
                mRvMyChats=null;
            }// END onPause()
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                mRootView = inflater.inflate(R.layout.fragment_my_chats, container, false);
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
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.prg_bar_wait));
                mProgressDialog.setCancelable(false);
                mLayoutManager = new LinearLayoutManager(getActivity());
                firebaseMyChat=new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child(StaticUtils.MY_CHAT);
                yasPaseeChats=new ArrayList<>();
                listenerMyChat = new ListenerMyChat();
            }// END mInItObjects()
        @Override
        public void onMessageItemClicked(Message yasPaseeChat)
            {
                if(mNetworkStatus.isNetworkAvailable())
                    {
                        ArrayList<String> arrayListSyteOwners = new ArrayList<>();
                        arrayListSyteOwners.add("0");
                        Bundle bundle = new Bundle();
                        bundle.putString(StaticUtils.IPC_SYTE_ID, yasPaseeChat.getSyteId());
                        bundle.putString(StaticUtils.IPC_SYTE_NAME, yasPaseeChat.getSyteName());
                        bundle.putString(StaticUtils.IPC_SYTE_IMAGE, yasPaseeChat.getSyteImg());
                        bundle.putStringArrayList(StaticUtils.IPC_SYTE_OWNERS, arrayListSyteOwners);
                        Intent intentChatWindowActivity = new Intent(getActivity(), UserChatWindowActivity.class);
                        intentChatWindowActivity.putExtras(bundle);
                        startActivity(intentChatWindowActivity);
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

                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext())
                            {
                                DataSnapshot dataSnapshot1 = (DataSnapshot)iterator.next();
                                Message yasPaseeChat = dataSnapshot1.getValue(Message.class);
                                yasPaseeChats.add(yasPaseeChat);
                                if(!iterator.hasNext())
                                    {
                                         if(adapterMyChats==null)
                                            {
                                                adapterMyChats=new AdapterMyChats(yasPaseeChats,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)),MyChatsFragment.this);
                                                mRvMyChats.setAdapter(adapterMyChats);
                                            }
                                        else
                                            {
                                                adapterMyChats.notifyDataSetChanged();
                                            }

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
                        if(mProgressDialog.isShowing())
                        {
                            mProgressDialog.dismiss();
                        }
                    }
                @Override
                public void onCancelled(FirebaseError firebaseError)
                    {
                        if(mProgressDialog.isShowing())
                        {
                            mProgressDialog.dismiss();
                        }
                    }
            }// END ListenerMyChat
    }// END MyChatsFragment
