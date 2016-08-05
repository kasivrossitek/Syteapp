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
public class DeleteASyteRequest {
    private String mSyteId;
    private OnDeleteSyteRequest onDeleteSyteRequest;
    private YasPasPreferences yasPasPreferences;
    private Firebase firebaseYasPaseeOwnedYasPas, firebaseYasPas, firebaseGeoLoc, firebasePublicYasPas, firebaseAnalyticsDeletedYaspas;
    private GeoFire geoFireGeoLoc;
    private HashMap<String, Object> hashMap, hashMapPublic;
    private Boolean isPublic;

    public DeleteASyteRequest(Context paramContext, String paramSyteId, OnDeleteSyteRequest paramOnDeleteSyteRequest, boolean isPublicType) {
        this.mSyteId = paramSyteId;
        this.onDeleteSyteRequest = paramOnDeleteSyteRequest;
        this.yasPasPreferences = YasPasPreferences.GET_INSTANCE(paramContext);
        this.firebaseYasPaseeOwnedYasPas = new Firebase(StaticUtils.YASPASEE_URL).child(yasPasPreferences.sGetRegisteredNum()).child(StaticUtils.OWNED_YASPAS);
        this.firebasePublicYasPas = new Firebase(StaticUtils.PUBLIC_YASPAS);
        this.firebaseYasPas = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId);
        this.firebaseGeoLoc = new Firebase(StaticUtils.YASPAS_GEO_LOC_URL);
        this.firebaseAnalyticsDeletedYaspas = new Firebase(StaticUtils.ANALYTICS_SYTE_URL).child(mSyteId).child("deleted");
        this.geoFireGeoLoc = new GeoFire(firebaseGeoLoc);
        this.isPublic = isPublicType;
        this.hashMap = new HashMap<String, Object>();
        hashMap.put("dateDeleted", ServerValue.TIMESTAMP);
        hashMap.put("isActive", 0);
        hashMap.put("isDeleted", 1);
        this.hashMapPublic = new HashMap<String, Object>();
        hashMapPublic.put("deleted by", yasPasPreferences.sGetRegisteredNum());
        hashMapPublic.put("dateDeleted", ServerValue.TIMESTAMP);
        hashMapPublic.put("isActive", 0);
        hashMapPublic.put("isDeleted", 1);
    }// END DeleteASyteRequest()

    public void sDeleteSyteStart() {
        onDeleteSyteRequest.onDeleteSyteRequestStrated();
        geoFireGeoLoc.removeLocation(mSyteId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, FirebaseError error) {
                if (error == null) {

                    if (isPublic) {
                        mDeletePublicSyteInstances();
                    } else {
                        mDeleteSyteInstances();
                    }
                } else {
                    onDeleteSyteRequest.onDeleteSyteErrorOccured();
                }
            }
        });
    }// END sDeleteSyte()

    private void mDeleteSyteInstances() {
        firebaseYasPaseeOwnedYasPas.child(mSyteId).removeValue();
        firebaseYasPaseeOwnedYasPas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mSyteId)) {
                    firebaseYasPas.updateChildren(hashMap, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) {
                                onDeleteSyteRequest.onDeleteSyteRequestFinished();
                            } else {
                                mDeleteSyteInstances();
                            }
                        }
                    });
                } else {
                    mDeleteSyteInstances();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }// END mDeleteSyteInstances()

    private void mDeletePublicSyteInstances() {
        firebasePublicYasPas.child(mSyteId).removeValue();
        firebasePublicYasPas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mSyteId)) {
                    firebaseYasPas.updateChildren(hashMap, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) {
                                firebaseAnalyticsDeletedYaspas.setValue(hashMapPublic, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError == null) {
                                            onDeleteSyteRequest.onDeleteSyteRequestFinished();
                                        }
                                    }
                                });

                            } else {
                                mDeletePublicSyteInstances();
                            }
                        }
                    });
                } else {
                    mDeletePublicSyteInstances();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }// END mDeleteSyteInstances()
}
