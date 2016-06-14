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
import com.syte.adapters.AdapterUserChatMessage;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.ChatMessage;
import com.syte.models.Message;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 14-03-2016.
 */
public class UserChatWindowActivity extends AppCompatActivity implements View.OnClickListener, OnCustomDialogsListener
    {
        private RelativeLayout mRelLayBack;
        private TextView mPageTitle,mTvSend;
        private EditText mEtMessage;
        private NetworkStatus mNetworkStatus;
        private YasPasPreferences mYasPasPref;
        private Firebase firebaseChat,firebaseChatMessage,firebaseYasPaseemyChat,firebaseYasPaseemyChatUnReadCount;
        private String mSyteId, mSyteName, mSyteImg;
        private ArrayList<String> mSyteOwnersNumbers;
        private int mCSenderType=0;
        private boolean isChatInitiated=false;
        private ProgressDialog mPrgDia;
        private RecyclerView mRvMessagesContainer;
        private LinearLayoutManager layoutManager;
        private ArrayList<ChatMessage> chatMessages;
        private ArrayList<String> chatMessagesIds;
        private AdapterUserChatMessage adapterUserChatMessage;
        private ChatChildEventListener chatChildEventListener;

        private void setSponsorUnreadCounter()
            {
                firebaseChatMessage.orderByChild("cSenderType").startAt(0).endAt(0).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        firebaseChat.child("unReadCount").setValue(unReadCounter);

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }



        private class ChatChildEventListener implements ChildEventListener
            {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                        chatMessagesIds.add(dataSnapshot.getKey());
                        chatMessages.add(message);
                        if (adapterUserChatMessage == null)
                            {
                                adapterUserChatMessage = new AdapterUserChatMessage(chatMessages,chatMessagesIds,firebaseChatMessage,firebaseYasPaseemyChatUnReadCount);
                                mRvMessagesContainer.setAdapter(adapterUserChatMessage);
                            }
                        else
                            {
                                adapterUserChatMessage.notifyDataSetChanged();
                            }
                        scroll();
                        setSponsorUnreadCounter();
                    }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        int pos = chatMessagesIds.indexOf(dataSnapshot.getKey());
                        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                        chatMessages.set(pos, chatMessage);
                        adapterUserChatMessage.notifyDataSetChanged();
                        //setSponsorUnreadCounter();
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
            }
        @Override
        public void onResume()
            {
                super.onResume();
                if(mNetworkStatus.isNetworkAvailable())
                    {
                        mPrgDia.show();
                        //Checking if chat is already initiated
                        firebaseChat.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if(dataSnapshot!=null && dataSnapshot.getValue()!=null)
                                            {
                                                isChatInitiated=true;

                                                mPrgDia.dismiss();
                                            }
                                        else
                                            {
                                                mPrgDia.dismiss();
                                            }
                                    }
                                @Override
                                public void onCancelled(FirebaseError firebaseError)
                                    {
                                        mPrgDia.dismiss();
                                    }

                            });
                    }
                chatMessages.removeAll(chatMessages);
                chatMessagesIds.removeAll(chatMessagesIds);
                firebaseChatMessage.addChildEventListener(chatChildEventListener);
            }
        @Override
        public void onPause()
            {
                super.onPause();
                firebaseChatMessage.removeEventListener(chatChildEventListener);
            }
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_user_chat_window);
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                mSyteId=getIntent().getExtras().getString(StaticUtils.IPC_SYTE_ID);
                mSyteName=getIntent().getExtras().getString(StaticUtils.IPC_SYTE_NAME);
                mSyteImg=getIntent().getExtras().getString(StaticUtils.IPC_SYTE_IMAGE);
                mSyteOwnersNumbers = new ArrayList<>();
                mSyteOwnersNumbers=getIntent().getExtras().getStringArrayList(StaticUtils.IPC_SYTE_OWNERS);
                mNetworkStatus=new NetworkStatus(UserChatWindowActivity.this);
                mYasPasPref = YasPasPreferences.GET_INSTANCE(UserChatWindowActivity.this);
                mPrgDia=new ProgressDialog(UserChatWindowActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                firebaseChat=new Firebase(StaticUtils.CHAT_URL).child(mSyteId).child(mYasPasPref.sGetRegisteredNum());
                firebaseChatMessage = firebaseChat.child(StaticUtils.CHAT_MESSAGES);
                firebaseYasPaseemyChat = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.MY_CHAT).child(mSyteId);
                firebaseYasPaseemyChatUnReadCount = firebaseYasPaseemyChat.child("unReadCount");
                layoutManager = new LinearLayoutManager(UserChatWindowActivity.this);
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
                mPageTitle.setText(mSyteName);
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
                                                if(isChatInitiated)
                                                    {
                                                        // Send Message
                                                        mSendMessage();
                                                    }
                                                else
                                                    {
                                                        // Initiate chat & Send Message
                                                        mInitiateChat();
                                                    }
                                            }
                                        else
                                            {
                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(UserChatWindowActivity.this,this);
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
        /*private void initiateChat()
            {
                final Firebase firebase = new Firebase(StaticUtils.CHAT_URL).child(mSyteId);
                firebase.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                HashMap<String,Object> hashMap = new HashMap<String, Object>();
                                if(dataSnapshot==null || dataSnapshot.getValue()==null)
                                    {
                                        String syteOwners="";
                                        for(int i=0;i<mSyteOwnersNumbers.size();i++)
                                            {
                                                if(i==0)
                                                    {
                                                        syteOwners=mSyteOwnersNumbers.get(i);
                                                    }
                                                else
                                                    {
                                                        syteOwners=syteOwners+","+mSyteOwnersNumbers.get(i);
                                                    }
                                            }
                                        hashMap.put("syteOwners",syteOwners);
                                        firebase.updateChildren(hashMap, new Firebase.CompletionListener()
                                            {
                                                @Override
                                                public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                    {
                                                        mInitiateChat();
                                                    }
                                            });
                                    }
                                else
                                    {
                                        mInitiateChat();
                                    }
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {

                            }
                    });
            }*/
        private void mInitiateChat()
            {
                // Add chat to YasPasee's (User) myChat
                Firebase firebaseYasPaseeMyChat = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPref.sGetRegisteredNum()).child(StaticUtils.MY_CHAT).child(mSyteId);
                final Message yasPaseeChat = new Message();
                yasPaseeChat.setSyteId(mSyteId);
                yasPaseeChat.setSyteName(mSyteName);
                yasPaseeChat.setSyteImg(mSyteImg);
                firebaseYasPaseeMyChat.setValue(yasPaseeChat, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                            {
                                if(firebaseError==null)
                                    {
                                        // Add chat to YasPasee's (Sponsor) myYasPasesChats
                                        Firebase firebaseYasPaseeMyYasPasesChats = new Firebase(StaticUtils.YASPASEE_URL).child(mSyteOwnersNumbers.get(0)).child(StaticUtils.MY_YASPASES_CHATS).child(mSyteId);
                                        firebaseYasPaseeMyYasPasesChats.setValue(yasPaseeChat, new Firebase.CompletionListener()
                                            {
                                                @Override
                                                public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                    {
                                                        if(firebaseError==null)
                                                            {
                                                                HashMap<String,Object> hashMap = new HashMap<String, Object>();
                                                                hashMap.put("userName",mYasPasPref.sGetUserName());
                                                                hashMap.put("userProfilePic",mYasPasPref.sGetUserProfilePic());
                                                                /*String syteOwners="";
                                                                for(int i=0;i<mSyteOwnersNumbers.size();i++)
                                                                    {
                                                                        if(i==0)
                                                                            {
                                                                                syteOwners=mSyteOwnersNumbers.get(i);
                                                                            }
                                                                        else
                                                                            {
                                                                                syteOwners=syteOwners+","+mSyteOwnersNumbers.get(i);
                                                                            }
                                                                    }
                                                                hashMap.put("syteOwners",syteOwners);*/
                                                                // Adding User's details to Chat
                                                                firebaseChat.updateChildren(hashMap, new Firebase.CompletionListener()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                                            {
                                                                                if(firebaseError==null)
                                                                                    {
                                                                                        mPrgDia.dismiss();
                                                                                        mSendMessage();
                                                                                    }
                                                                                else
                                                                                    {
                                                                                        mPrgDia.dismiss();
                                                                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(UserChatWindowActivity.this,UserChatWindowActivity.this);
                                                                                        customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "ErrorOccured", false, false);
                                                                                    }
                                                                            }
                                                                    });

                                                            }
                                                        else
                                                            {
                                                                mPrgDia.dismiss();
                                                                CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(UserChatWindowActivity.this,UserChatWindowActivity.this);
                                                                customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "ErrorOccured", false, false);
                                                            }
                                                    }
                                            });
                                    }
                                else
                                    {
                                        mPrgDia.dismiss();
                                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(UserChatWindowActivity.this,UserChatWindowActivity.this);
                                        customDialogs.sShowDialog_Common(null, getString(R.string.err_msg_error_occurred), null, null, "OK", "ErrorOccured", false, false);
                                    }
                            }
                    });
            }// End mInitiateChat()
        private void mSendMessage()
            {
                if(mEtMessage.getText().toString().trim().length()>0)
                    {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setcMessage(mEtMessage.getText().toString().trim());
                chatMessage.setcSenderRegisteredNum(mYasPasPref.sGetRegisteredNum());
                chatMessage.setcSenderName(mYasPasPref.sGetUserName());
                chatMessage.setcSenderType(mCSenderType);
                chatMessage.setcIsRead(false);
                chatMessage.setcDateTime(ServerValue.TIMESTAMP);
                mEtMessage.setText("");
                firebaseChatMessage.push().setValue(chatMessage, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                            {
                                firebase.addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                ChatMessage chatMessageAdded = dataSnapshot.getValue(ChatMessage.class);
                                                hashMap.put("lastMessage", chatMessageAdded.getcMessage());
                                                hashMap.put("lastMessageDateTime", chatMessageAdded.getcDateTime());
                                                //hashMap.put("unReadCount", 0);

                                                firebaseChat.updateChildren(hashMap);
                                                firebaseChat.setPriority(0 - (long) chatMessageAdded.getcDateTime());

                                                firebaseYasPaseemyChat.updateChildren(hashMap);
                                                firebaseYasPaseemyChat.setPriority(0 - (long) chatMessageAdded.getcDateTime());
                                            }
                                        @Override
                                        public void onCancelled(FirebaseError firebaseError)
                                            {

                                            }
                                    });
                            }
                    });
                    }



            }// END mSendMessage()
        private void scroll()
            {
                mRvMessagesContainer.scrollToPosition(chatMessages.size()-1);
            }

    }// END UserChatWindowActivity
