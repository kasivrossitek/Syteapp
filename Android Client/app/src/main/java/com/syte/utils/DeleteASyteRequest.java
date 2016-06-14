package com.syte.utils;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.syte.listeners.OnDeleteSyteRequest;

import java.util.HashMap;

/**
 * Created by khalid.p on 19-03-2016.
 */
public class DeleteASyteRequest
    {
        private String mSyteId;
        private OnDeleteSyteRequest onDeleteSyteRequest;
        private YasPasPreferences yasPasPreferences;
        private Firebase firebaseYasPaseeOwnedYasPas,firebaseYasPas,firebaseGeoLoc;
        private GeoFire geoFireGeoLoc;
        private HashMap<String, Object> hashMap;
        public DeleteASyteRequest(Context paramContext, String paramSyteId, OnDeleteSyteRequest paramOnDeleteSyteRequest)
            {
                this.mSyteId=paramSyteId;
                this.onDeleteSyteRequest=paramOnDeleteSyteRequest;
                this.yasPasPreferences=YasPasPreferences.GET_INSTANCE(paramContext);
                this.firebaseYasPaseeOwnedYasPas = new Firebase(StaticUtils.YASPASEE_URL).child(yasPasPreferences.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS);
                this.firebaseYasPas = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
                this.firebaseGeoLoc = new Firebase(StaticUtils.YASPAS_GEO_LOC_URL);
                this.geoFireGeoLoc = new GeoFire(firebaseGeoLoc);
                this.hashMap = new HashMap<String, Object>();
                hashMap.put("dateDeleted", ServerValue.TIMESTAMP);
                hashMap.put("isActive", 0);
                hashMap.put("isDeleted", 1);
            }// END DeleteASyteRequest()
        public void sDeleteSyteStart()
            {
                onDeleteSyteRequest.onDeleteSyteRequestStrated();
                geoFireGeoLoc.removeLocation(mSyteId, new GeoFire.CompletionListener()
                    {
                        @Override
                        public void onComplete(String key, FirebaseError error)
                            {
                                if(error==null)
                                    {
                                        mDeleteSyteInstances();
                                    }
                                else
                                    {
                                        onDeleteSyteRequest.onDeleteSyteErrorOccured();
                                    }
                            }
                    });
            }// END sDeleteSyte()
        private void mDeleteSyteInstances()
            {
                firebaseYasPaseeOwnedYasPas.child(mSyteId).removeValue();
                firebaseYasPaseeOwnedYasPas.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (!dataSnapshot.hasChild(mSyteId))
                                    {
                                        firebaseYasPas.updateChildren(hashMap, new Firebase.CompletionListener()
                                            {
                                                @Override
                                                public void onComplete(FirebaseError firebaseError, Firebase firebase)
                                                    {
                                                        if(firebaseError==null)
                                                            {
                                                                onDeleteSyteRequest.onDeleteSyteRequestFinished();
                                                            }
                                                        else
                                                            {
                                                                mDeleteSyteInstances();
                                                            }
                                                    }
                                            });
                                    }
                                else
                                    {
                                        mDeleteSyteInstances();
                                    }
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {

                            }
                    });
            }// END mDeleteSyteInstances()
    }
