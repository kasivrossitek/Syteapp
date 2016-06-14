package com.syte.activities.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.AdapterSponsorChatMessage;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.ChatMessage;
import com.syte.models.ChatMessagePush;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 16-03-2016.
 */
public class SponsorChatWindowActivity extends AppCompatActivity implements View.OnClickListener, OnCustomDialogsListener
    {
        private RelativeLayout mRelLayBack;
        private TextView mPageTitle,mTvSend;
        private EditText mEtMessage;
        private NetworkStatus mNetworkStatus;
        private YasPasPreferences mYasPasPref;
        private Firebase firebaseChat,firebaseChatMessage,firebaseYasPaseemyChat,firebaseYasPaseemyChatUnReadCount;
        private String mSyteId, mUserName, mUserImg,mUserRegisterdNum;
        private int mCSenderType=1;
        private ProgressDialog mPrgDia;
        private RecyclerView mRvMessagesContainer;
        private LinearLayoutManager layoutManager;
        private ArrayList<ChatMessage> chatMessages;
        private ArrayList<String> chatMessagesIds;
        private AdapterSponsorChatMessage adapterSponsorChatMessage;
        private ChatChildEventListener chatChildEventListener;
        private Firebase firebaseChatPush;
        private void setUserUnreadCounter()
            {
                firebaseChatMessage.orderByChild("cSenderType").startAt(1).endAt(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int unReadCounter = 0;
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                            ChatMessage chatMsg = dataSnapshot1.getValue(ChatMessage.class);
                            if (!chatMsg.getcIsRead()) {
                                unReadCounter++;
                            }
                        }
                        firebaseYasPaseemyChatUnReadCount.setValue(unReadCounter);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }// END setSponsorUnreadCounter()
        private class ChatChildEventListener implements ChildEventListener
            {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                        chatMessagesIds.add(dataSnapshot.getKey());
                        chatMessages.add(message);
                        if (adapterSponsorChatMessage == null)
                        {
                            adapterSponsorChatMessage = new AdapterSponsorChatMessage(chatMessages,chatMessagesIds,firebaseChatMessage,firebaseChat);
                            mRvMessagesContainer.setAdapter(adapterSponsorChatMessage);
                        }
                        else
                        {
                            adapterSponsorChatMessage.notifyDataSetChanged();
                        }
                        scroll();
                        setUserUnreadCounter();
                    }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        int pos = chatMessagesIds.indexOf(dataSnapshot.getKey());
                        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                        chatMessages.set(pos, chatMessage);
                        adapterSponsorChatMessage.notifyDataSetChanged();
                        //setUserUnreadCounter();
                    }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot)
                    {

                    }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s)
                    {

                    }
                @Override
                public void onCancelled(FirebaseError firebaseError)
                    {

                    }
            } // END ChatChildEventListener
        @Override
        public void onResume()
            {
                super.onResume();
                chatMessages.removeAll(chatMessages);
                chatMessagesIds.removeAll(chatMessagesIds);
                firebaseChatMessage.addChildEventListener(chatChildEventListener);
                mYasPasPref.sSetOnGoingChat_SyteId(mSyteId);
                mYasPasPref.sSetOnGoingChatId(mUserRegisterdNum);
            }// END onResume()
        @Override
        public void onPause()
            {
                super.onPause();
                firebaseChatMessage.removeEventListener(chatChildEventListener);
                mYasPasPref.sSetOnGoingChat_SyteId("");
                mYasPasPref.sSetOnGoingChatId("");
            }// END onPause()
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_sponsor_chat_window);
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                mSyteId=getIntent().getExtras().getString(StaticUtils.IPC_SYTE_ID);
                mUserRegisterdNum=getIntent().getExtras().getString(StaticUtils.IPC_USER_REG_NUM);
                mUserImg=getIntent().getExtras().getString(StaticUtils.IPC_USER_IMAGE);
                mUserName=getIntent().getExtras().getString(StaticUtils.IPC_USER_NAME);
                mNetworkStatus=new NetworkStatus(SponsorChatWindowActivity.this);
                mYasPasPref = YasPasPreferences.GET_INSTANCE(SponsorChatWindowActivity.this);
                mPrgDia=new ProgressDialog(SponsorChatWindowActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                firebaseChatMessage=new Firebase(StaticUtils.CHAT_URL).child(mSyteId).child(mUserRegisterdNum).child(StaticUtils.CHAT_MESSAGES);
                firebaseChat=new Firebase(StaticUtils.CHAT_URL).child(mSyteId).child(mUserRegisterdNum);
                firebaseYasPaseemyChat = new Firebase(StaticUtils.YASPASEE_URL).child(mUserRegisterdNum).child(StaticUtils.MY_CHAT).child(mSyteId);
                firebaseYasPaseemyChatUnReadCount = firebaseYasPaseemyChat.child("unReadCount");
                firebaseChatPush=new Firebase(StaticUtils.CHAT_PUSH_NOTIFICATION_URL);

                layoutManager = new LinearLayoutManager(SponsorChatWindowActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                chatMessages=new ArrayList<>();
                chatMessagesIds=new ArrayList<>();
                chatChildEventListener=new ChatChildEventListener();

            }// END mInItObjects()
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mPageTitle=(TextView)findViewById(R.id.xPageTitle);
                mPageTitle.setText(mUserName);
                mTvSend=(TextView)findViewById(R.id.xTvSend);
                mTvSend.setOnClickListener(this);
                mEtMessage=(EditText)findViewById(R.id.xEtMessage);
                mRvMessagesContainer=(RecyclerView)findViewById(R.id.xRvMessagesContainer);
                mRvMessagesContainer.setHasFixedSize(true);
                mRvMessagesContainer.setLayoutManager(layoutManager);
            }//END mInItWidgets()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xTvSend:
                            {
                                if(mEtMessage.getText().toString().trim().length()>0)
                                {
                                    if(mNetworkStatus.isNetworkAvailable())
                                    {
                                        mSendMessage();
                                    }
                                    else
                                    {
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(SponsorChatWindowActivity.this,this);
                                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                                    }
                                }
                                break;
                            }
                        case R.id.xRelLayBack:
                            {
                                finish();
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
        private void mSendMessage()
            {
                if(mEtMessage.getText().toString().trim().length()>0){
                final ChatMessage chatMessage = new ChatMessage();
                chatMessage.setcMessage(mEtMessage.getText().toString().trim());
                chatMessage.setcSenderRegisteredNum(mYasPasPref.sGetRegisteredNum());
                chatMessage.setcSenderName(mYasPasPref.sGetUserName());
                chatMessage.setcSenderType(mCSenderType);
                chatMessage.setcIsRead(false);
                chatMessage.setcDateTime(ServerValue.TIMESTAMP);
                mEtMessage.setText("");
                firebaseChatMessage.push().setValue(chatMessage, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                ChatMessage chatMessageAdded = dataSnapshot.getValue(ChatMessage.class);
                                hashMap.put("lastMessage", chatMessageAdded.getcMessage());
                                hashMap.put("lastMessageDateTime", chatMessageAdded.getcDateTime());
                                //hashMap.put("unReadCount", 0);

                                firebaseChat.updateChildren(hashMap);
                                firebaseChat.setPriority(0 - (long) chatMessageAdded.getcDateTime());

                                firebaseYasPaseemyChat.updateChildren(hashMap);
                                firebaseYasPaseemyChat.setPriority(0 - (long) chatMessageAdded.getcDateTime());
                                sendChatPushNotification(chatMessage);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                });}

            }// END mSendMessage()
        private void scroll()
            {
                mRvMessagesContainer.scrollToPosition(chatMessages.size() - 1);
            }


        private void sendChatPushNotification(ChatMessage chatMessage)
        {
            ChatMessagePush chatMessagePush= new ChatMessagePush();
            chatMessagePush.setSyteId(mSyteId);
            chatMessagePush.setcMessage(chatMessage.getcMessage());
            chatMessagePush.setcSenderRegisteredNum(mYasPasPref.sGetRegisteredNum());
            chatMessagePush.setcChatId(mUserRegisterdNum);
            chatMessagePush.setcSenderName(chatMessage.getcSenderName());
            chatMessagePush.setImgUrl(mUserImg);
            chatMessagePush.setcSenderType(mCSenderType);
            chatMessagePush.setcDateTime(chatMessage.getcDateTime());
            firebaseChatPush.push().setValue(chatMessagePush);
        }// END sendChatPushNotification()
    }// END SponsorChatWindowActivity
