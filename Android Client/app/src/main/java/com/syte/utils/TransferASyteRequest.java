package com.syte.utils;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.listeners.OnTransferASyteRequest;
import com.syte.models.Message;
import com.syte.models.OwnedYasPases;
import com.syte.models.YasPasManager;

import java.util.HashMap;

/**
 * Created by khalid.p on 21-03-2016.
 */
public class TransferASyteRequest
    {
        private String mSyteId,mApprover,mClaimer;
        //private Context mContext;
        private Firebase firebaseYasPas,firebaseApprover,firebaseClaimer,firebaseChat;
        private OnTransferASyteRequest mOnTransferASyteRequest;
        public TransferASyteRequest(String paramSyteId, String paramApprover, String paramClaimer, OnTransferASyteRequest paramOnTransferASyteRequest)
            {
                //this.mContext=paramContext;
                this.mSyteId=paramSyteId;
                this.mApprover=paramApprover;
                this.mClaimer=paramClaimer;
                this.mOnTransferASyteRequest=paramOnTransferASyteRequest;
                this.firebaseYasPas = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                this.firebaseApprover = new Firebase(StaticUtils.YASPASEE_URL).child(mApprover);
                this.firebaseClaimer = new Firebase(StaticUtils.YASPASEE_URL).child(mClaimer);
                this.firebaseChat = new Firebase(StaticUtils.CHAT_URL).child(mSyteId);
            }// END TransferASyteRequest()
        public void sTransferASyteStart()
            {
                mOnTransferASyteRequest.onTasrStarted();
                //Checking Claimer existance, highly unlikely but might be
                firebaseClaimer.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot == null || dataSnapshot.getValue()==null)
                                    {
                                        mOnTransferASyteRequest.onTasrClaimerExistance(false);
                                    }
                                else
                                    {
                                        mOnTransferASyteRequest.onTasrClaimerExistance(true);
                                        mUpdateChatOwners();
                                    }
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {
                                mOnTransferASyteRequest.onTasrErrorOccured();
                            }
                    });

            }// END TransferASyteStart()
        private void mUpdateChatOwners()
            {
                firebaseChat.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot == null || dataSnapshot.getValue()==null)
                                    {
                                        // No chat exists for this syte
                                        mUpdateOwnedYasPases();
                                    }
                                else
                                    {
                                        mTransferChat();
                                    }
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {

                            }
                    });
            }// END mUpdateChatOwners()
        private void mTransferChat()
            {
                final Firebase firebaseApproverMyYasPasesChat = firebaseApprover.child(StaticUtils.MY_YASPASES_CHATS).child(mSyteId);
                firebaseApproverMyYasPasesChat.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                Message messageObj = dataSnapshot.getValue(Message.class);
                                Firebase firebaseClaimerMyYasPasesChat = firebaseClaimer.child(StaticUtils.MY_YASPASES_CHATS).child(mSyteId);
                                //Adding chat to claimer
                                firebaseClaimerMyYasPasesChat.setValue(messageObj, new Firebase.CompletionListener()
                                    {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                            {
                                                // Removing chat from Approver
                                                firebaseApproverMyYasPasesChat.removeValue(new Firebase.CompletionListener()
                                                    {
                                                        @Override
                                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                            {
                                                                mUpdateOwnedYasPases();
                                                            }
                                                });
                                            }
                                    });
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {

                            }
                    });
            }// END mTransferChat()
        private void mUpdateOwnedYasPases()
            {
                final Firebase firebaseApproverOwnedYasPases = firebaseApprover.child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                firebaseApproverOwnedYasPases.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                OwnedYasPases ownedYasPases = dataSnapshot.getValue(OwnedYasPases.class);
                                Firebase firebaseClaimerOwnedYasPases = firebaseClaimer.child(StaticUtils.OWNED_YASPAS).child(mSyteId);
                                firebaseClaimerOwnedYasPases.setValue(ownedYasPases, new Firebase.CompletionListener()
                                    {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                            {
                                                firebaseApproverOwnedYasPases.removeValue(new Firebase.CompletionListener()
                                                    {
                                                        @Override
                                                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                            {
                                                                mUpdateYasPas();
                                                            }
                                                });
                                            }
                                    });
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {

                            }
                    });
            }// END mUpdateOwnedYasPases()
        private void mUpdateYasPas()
            {
                HashMap<String,Object> hashMapYasPas = new HashMap<String, Object>();
                hashMapYasPas.put("owner",mClaimer);
                hashMapYasPas.put("mobileNo",mClaimer);
                firebaseYasPas.updateChildren(hashMapYasPas, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Firebase firebaseOldManager = firebaseYasPas.child(StaticUtils.YASPAS_MANAGERS);
                        firebaseOldManager.removeValue(new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                mAddManager();
                            }
                        });
                    }
                });
            }// END mUpdateYasPas()
        private void mAddManager()
            {
                YasPasManager mYasPasManager = new YasPasManager();
                mYasPasManager.setManagerId(mClaimer);
                Firebase firebaseNewManager = firebaseYasPas.child(StaticUtils.YASPAS_MANAGERS).child(mClaimer);
                firebaseNewManager.setValue(mYasPasManager, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        mRemoveFromClaimerFollowingSyte();
                    }
                });
            } // END mAddManager()
        private void mRemoveFromClaimerFollowingSyte()
            {
                final Firebase firebaseFollowingYasPas = firebaseClaimer.child(StaticUtils.FOLLOWING_YASPAS).child(mSyteId);
                firebaseFollowingYasPas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                            mOnTransferASyteRequest.onTasrFinshed();
                        } else {
                            firebaseFollowingYasPas.removeValue(new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                                    mRemoveClaimerFromFollowersList();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        private void mRemoveClaimerFromFollowersList()
            {
                Firebase firebaseYasPasFollowers= firebaseYasPas.child(StaticUtils.FOLLOWERS_YASPAS).child(mClaimer);
                firebaseYasPasFollowers.removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        mRemoveFromMyChat();
                    }
                });
            }
        private void mRemoveFromMyChat()
            {
                final Firebase firebaseMyChat = firebaseClaimer.child(StaticUtils.MY_CHAT).child(mSyteId);
                firebaseMyChat.removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase)
                        {
                            final Firebase firebaseClaimerChatChat = firebaseChat.child(mClaimer);
                            firebaseClaimerChatChat.removeValue(new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    mOnTransferASyteRequest.onTasrFinshed();
                                }
                            });

                        }
                });

            }


    } // END TransferASyteRequest()
