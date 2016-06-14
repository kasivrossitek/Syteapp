package com.syte.activities.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.AdapterSponsorInnerChat;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnMessageItemClick;
import com.syte.models.Message;
import com.syte.models.SponsorInnerMessage;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 15-03-2016.
 */
public class SponsorChatListActivity extends Activity implements View.OnClickListener, OnMessageItemClick, OnCustomDialogsListener
    {
        private RelativeLayout mRelLayBack;
        private TextView mPageTitle,mTvCenterLbl;
        private RecyclerView mRvUsers;
        private LinearLayoutManager layoutManager;
        private Bundle mBun;
        private String mSyteId,mSyteName;
        private Firebase firebaseSyteChat;
        private ListenerSyteChat listenerSyteChat;
        private ArrayList<SponsorInnerMessage> sponsorInnerMessages;
        private AdapterSponsorInnerChat adapterSponsorInnerChat;
        private NetworkStatus mNetworkStatus;

        @Override
        public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
            {
                if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
                    {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startMain);
                        finish();
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
        @Override
        public void onResume()
            {
                super.onResume();
                mRvUsers = (RecyclerView)findViewById(R.id.xRvUsers);
                mRvUsers.setHasFixedSize(true);
                mRvUsers.setLayoutManager(layoutManager);
                /*if(mNetworkStatus.isNetworkAvailable())
                    mProgressDialog.show();
                */firebaseSyteChat.addValueEventListener(listenerSyteChat);
            }// END onResume()
        @Override
        public void onPause()
            {
                super.onPause();
                firebaseSyteChat.removeEventListener(listenerSyteChat);
                adapterSponsorInnerChat=null;
                mRvUsers.setAdapter(adapterSponsorInnerChat);
                mRvUsers=null;
            }// END onPause()
        @Override
        public void onMessageItemClicked(Message yasPaseeChat)
            {

            }// END onMessageItemClicked()
        @Override
        public void onSponsorInnerMessageItemClciked(SponsorInnerMessage sponsorInnerMessage)
            {
                if(mNetworkStatus.isNetworkAvailable())
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                        bundle.putString(StaticUtils.IPC_USER_REG_NUM, sponsorInnerMessage.getUserRegisteredNumber());
                        bundle.putString(StaticUtils.IPC_USER_IMAGE, sponsorInnerMessage.getUserProfilePic());
                        bundle.putString(StaticUtils.IPC_USER_NAME, sponsorInnerMessage.getUserName());

                        Intent intentChatWindowActivity = new Intent(SponsorChatListActivity.this, SponsorChatWindowActivity.class);
                        intentChatWindowActivity.putExtras(bundle);
                        startActivity(intentChatWindowActivity);
                    }
                else
                    {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SponsorChatListActivity.this,this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
            }// END onSponsorInnerMessageItemClciked()
        private class ListenerSyteChat implements ValueEventListener
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        sponsorInnerMessages.removeAll(sponsorInnerMessages);
                        if(dataSnapshot!=null)
                            {
                                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                while (iterator.hasNext())
                                    {
                                        DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                                        SponsorInnerMessage sponsorInnerMessage = new SponsorInnerMessage();
                                        sponsorInnerMessage.setUserRegisteredNumber(dataSnapshot1.getKey());
                                        sponsorInnerMessage.setUserName((String) dataSnapshot1.child("userName").getValue());
                                        sponsorInnerMessage.setUserProfilePic((String) dataSnapshot1.child("userProfilePic").getValue());
                                        sponsorInnerMessage.setLastMessage((String) dataSnapshot1.child("lastMessage").getValue());
                                        sponsorInnerMessage.setLastMessageDateTime((Object) dataSnapshot1.child("lastMessageDateTime").getValue());
                                        try
                                            {
                                                sponsorInnerMessage.setUnReadCount(((Long) dataSnapshot1.child("unReadCount").getValue()).intValue());
                                            }
                                        catch (Exception e)
                                            {
                                                sponsorInnerMessage.setUnReadCount(1);
                                            }
                                        sponsorInnerMessages.add(sponsorInnerMessage);
                                        if(!iterator.hasNext())
                                            {
                                                if(adapterSponsorInnerChat==null)
                                                    {
                                                        adapterSponsorInnerChat=new AdapterSponsorInnerChat(sponsorInnerMessages,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)),SponsorChatListActivity.this);
                                                        mRvUsers.setAdapter(adapterSponsorInnerChat);
                                                    }
                                                else
                                                    {
                                                        adapterSponsorInnerChat.notifyDataSetChanged();
                                                    }
                                            }

                                    }
                                if(sponsorInnerMessages.size()>0)
                                    {
                                        mTvCenterLbl.setVisibility(View.GONE);
                                    }
                                else
                                    {
                                        mTvCenterLbl.setVisibility(View.VISIBLE);
                                    }
                            }
                        else
                            {
                                if(sponsorInnerMessages.size()>0)
                                    {
                                        mTvCenterLbl.setVisibility(View.GONE);
                                    }
                                else
                                    {
                                        mTvCenterLbl.setVisibility(View.VISIBLE);
                                    }
                            }
                    }
                @Override
                public void onCancelled(FirebaseError firebaseError)
                    {

                    }
            }// END ListenerSyteChat
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_sponsor_chat_list);
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                layoutManager = new LinearLayoutManager(SponsorChatListActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mBun=getIntent().getExtras();
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                mSyteName=mBun.getString(StaticUtils.IPC_SYTE_NAME);
                firebaseSyteChat=new Firebase(StaticUtils.CHAT_URL).child(mSyteId);
                sponsorInnerMessages=new ArrayList<>();
                listenerSyteChat=new ListenerSyteChat();
                mNetworkStatus = new NetworkStatus(SponsorChatListActivity.this);
            }//END mInItObjects()
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mPageTitle=(TextView)findViewById(R.id.xPageTitle);
                mPageTitle.setText(mSyteName.trim());
                mTvCenterLbl=(TextView)findViewById(R.id.xTvCenterLbl);
            }//END mInItWidgets()
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
                        default:break;
                    }
            }// END onClick()
    }// END SponsorChatListActivity
