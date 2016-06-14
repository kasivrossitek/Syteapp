package com.syte.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 15-04-2016.
 */
public class ChatCounterService extends Service
    {
        private NetworkStatus networkStatus;
        public ChatCounterService()
            {

            }// END ChatCounterService()
        @Override
        public IBinder onBind(Intent intent)
            {
                throw new UnsupportedOperationException("Not yet implemented");
            }// END onBind()
        /*@Override
       public int onStartCommand(Intent intent, int flags, int startId) {}*/
        @Override
        public void onCreate ()
            {
                networkStatus = new NetworkStatus(getApplicationContext());
                if(networkStatus.isNetworkAvailable())
                    {
                        mStartReadingChatMessages();
                    }
                else
                    {
                        stopServiceWithNoMessage();
                    }
            }// END onCreate()
        @Override
        public void onDestroy ()
            {

            }// END onDestroy()
        private void mStartReadingChatMessages()
            {
                Intent intentChatBrdCast = new Intent(StaticUtils.BRD_CST_AC_CHAT);
                intentChatBrdCast.putExtra(StaticUtils.BRD_CST_AC_CHAT_SERVICE_STATUS,StaticUtils.BRD_CST_AC_CHAT_SERVICE_STARTED);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentChatBrdCast);

                YasPasPreferences yasPasPreferences = YasPasPreferences.GET_INSTANCE(getApplicationContext());
                Firebase firebaseYasPaseeChat = new Firebase(StaticUtils.YASPASEE_URL).child(yasPasPreferences.sGetRegisteredNum()).child(StaticUtils.MY_CHAT);
                firebaseYasPaseeChat.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                // Checking if myChat data is available or not
                                if(dataSnapshot!=null && dataSnapshot.getValue()!=null)
                                    {
                                        //myChat data is available
                                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                        while (iterator.hasNext())
                                            {
                                                DataSnapshot dataSnapshot1 = (DataSnapshot)iterator.next();
                                                int unReadCount=0;
                                                try
                                                    {
                                                        unReadCount=Integer.parseInt(dataSnapshot1.child("unReadCount").getValue().toString());
                                                    }
                                                catch (Exception e){}
                                                if(unReadCount>0)
                                                    {
                                                        //myChat data is available & there is unRead messages. Therefore Broadcasting the same to home activity & stopping the service
                                                        System.out.println("ChatCounterService -- Stopped data found in myChat");
                                                        stopServiceWithNewMessage();
                                                        break;
                                                    }
                                                if(!iterator.hasNext())
                                                    {
                                                        //myChat data is available but there is no unRead messages. Therefore, initiating mStartReadingSponsorChatMessages
                                                        if(networkStatus.isNetworkAvailable())
                                                            {
                                                                mStartReadingSponsorChatMessages();
                                                            }
                                                        else
                                                            {
                                                                stopServiceWithNoMessage();
                                                            }
                                                    }
                                            }
                                    }
                                else
                                    {
                                        //myChat data is not available. Therefore, initiating mStartReadingSponsorChatMessages
                                        if(networkStatus.isNetworkAvailable())
                                            {
                                                mStartReadingSponsorChatMessages();
                                            }
                                        else
                                            {
                                                stopServiceWithNoMessage();
                                            }
                                    }
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {
                                //Error. Therefore, initiating mStartReadingSponsorChatMessages
                                if(networkStatus.isNetworkAvailable())
                                    {
                                        mStartReadingSponsorChatMessages();
                                    }
                                else
                                    {
                                        stopServiceWithNoMessage();
                                    }
                            }
                    });
            }// END mStartReadingChatMessages()
        private void mStartReadingSponsorChatMessages()
            {
                YasPasPreferences yasPasPreferences = YasPasPreferences.GET_INSTANCE(getApplicationContext());
                Firebase firebaseYasPaseeSponsorChat = new Firebase(StaticUtils.YASPASEE_URL).child(yasPasPreferences.sGetRegisteredNum()).child(StaticUtils.MY_YASPASES_CHATS);
                firebaseYasPaseeSponsorChat.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                // Checking if there is any MY_YASPASES_CHATS data available
                                if(dataSnapshot!=null && dataSnapshot.getValue()!=null)
                                    {
                                        //MY_YASPASES_CHATS data available
                                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                        ArrayList<String> myYasPasesChatsIds = new ArrayList<String>();
                                        while (iterator.hasNext())
                                            {
                                                DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                                                myYasPasesChatsIds.add(dataSnapshot1.getKey());
                                                if(!iterator.hasNext())
                                                    {
                                                        if(networkStatus.isNetworkAvailable() && myYasPasesChatsIds.size()>0)
                                                            {
                                                                readSponsorMsgs(myYasPasesChatsIds);
                                                            }
                                                        else
                                                            {
                                                                stopServiceWithNoMessage();
                                                            }
                                                    }
                                            }
                                    }
                                else
                                    {
                                        //MY_YASPASES_CHATS data not available. Therefore, stoping service with broadcasting 0 unread messages to Home activity
                                        stopServiceWithNoMessage();
                                    }
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {
                                //Error. Therefore, stoping service with broadcasting 0 unread messages to Home activity
                                stopServiceWithNoMessage();
                            }
                });
            }// END mStartReadingSponsorChatMessages()
        private void readSponsorMsgs(ArrayList<String> myYasPasesChatsIds)
            {
                // Running a loop for every myYasPasesChats
                for (int i=0;i<myYasPasesChatsIds.size();i++)
                    {
                        // Checking Internet
                        if(networkStatus.isNetworkAvailable())
                            {
                                // Internet available
                                // Reading specific myYasPasesChats in chat node
                                Firebase firebaseChat = new Firebase(StaticUtils.CHAT_URL).child(myYasPasesChatsIds.get(i));
                                firebaseChat.addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                // Checking if data is availble for this specific myYasPasesChats
                                                if(dataSnapshot!=null && dataSnapshot.getValue()!=null)
                                                    {
                                                        // data is availble for this specific myYasPasesChats
                                                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                                        // Iterating each and every children of this specific myYasPasesChats
                                                        while (iterator.hasNext())
                                                            {
                                                                DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                                                                int unReadCount=0;
                                                                try
                                                                    {
                                                                        unReadCount=Integer.parseInt(dataSnapshot1.child("unReadCount").getValue().toString());
                                                                    }
                                                                catch (Exception e){}
                                                                if(unReadCount>0)
                                                                    {
                                                                        //there is unRead messages. Therefore Broadcasting the same to home activity & stopping the service
                                                                        stopServiceWithNewMessage();
                                                                        break;
                                                                    }

                                                            }
                                                    }
                                            }
                                        @Override
                                        public void onCancelled(FirebaseError firebaseError)
                                            {

                                            }
                                    });
                                if(i==myYasPasesChatsIds.size()-1)
                                    {
                                        // Last data reached but no unread message found
                                        stopServiceWithNoMessage();
                                    }
                            }
                        else
                            {
                                // Internet not available
                                stopServiceWithNoMessage();
                            }
                    }
            }
        private void stopServiceWithNoMessage()
            {
                Intent intentChatBrdCast = new Intent(StaticUtils.BRD_CST_AC_CHAT);
                intentChatBrdCast.putExtra(StaticUtils.BRD_CST_AC_CHAT_SERVICE_STATUS,StaticUtils.BRD_CST_AC_CHAT_SERVICE_NO_NEW_MESSAGE);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentChatBrdCast);
                stopSelf();
            }// END stopServiceWithNoMessage()
        private void stopServiceWithNewMessage()
            {
                Intent intentChatBrdCast = new Intent(StaticUtils.BRD_CST_AC_CHAT);
                intentChatBrdCast.putExtra(StaticUtils.BRD_CST_AC_CHAT_SERVICE_STATUS,StaticUtils.BRD_CST_AC_CHAT_SERVICE_NEW_MESSAGE);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentChatBrdCast);
                stopSelf();
            }// END stopServiceWithNewMessage()
    }// END ChatCounterService
