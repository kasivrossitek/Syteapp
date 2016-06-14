/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syte.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class RegistrationIntentService extends IntentService
{

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        final YasPasPreferences mPref = YasPasPreferences.GET_INSTANCE(this);

        try
        {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG)
            {
                // Initially this call goes out to the network to retrieve the token, subsequent calls are local.

                InstanceID instanceID = InstanceID.getInstance(this);
                String token="";
              token = instanceID.getToken("755282053053 ", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
             //   token = instanceID.getToken("703962495781 ", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);//KASI SENDERID
                /*NOTE:
                * The sender id 755282053053 is generated using below credentials:
                * E-mail : prod@gosyte.com
                * Server API Key : AIzaSyDaMRNoD12hEFH2lil1bLBHCDvrUXcQRJ0*/


                if(!token.equalsIgnoreCase("") && token!=null)
                    {
                        mPref.sSetGCMToken(token);
                        // Notify UI that registration has completed, so the progress indicator can be hidden.
                        Intent registrationComplete = new Intent("GCM_REGISTRATION_COMPLETED");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                        Firebase firebaseFollowingYasPases= new Firebase(StaticUtils.YASPASEE_URL).child(mPref.sGetRegisteredNum()).child(StaticUtils.FOLLOWING_YASPAS);
                        final String finalToken = token;
                        firebaseFollowingYasPases.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot!=null && dataSnapshot.getValue()!=null)
                                    {
                                        Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                                        HashMap<String,Object> hashMap = new HashMap<String, Object>();
                                        hashMap.put("deviceToken", finalToken);
                                        while (dataSnapshotIterator.hasNext())
                                            {
                                                DataSnapshot dataSnapshotFollowing = (DataSnapshot)dataSnapshotIterator.next();
                                                Firebase firebaseFollowerYasPasees = new Firebase(StaticUtils.YASPAS_URL).child(dataSnapshotFollowing.getKey()).child(StaticUtils.FOLLOWERS_YASPAS).child(mPref.sGetRegisteredNum());
                                                firebaseFollowerYasPasees.updateChildren(hashMap);

                                            }
                                    }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }
                else
                    {
                        // Notify UI that registration has completed, so the progress indicator can be hidden.
                        Intent registrationComplete = new Intent("GCM_REGISTRATION_COMPLETED");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                    }


                // Subscribe to topic channels
                // subscribeTopics(token);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to complete token refresh", e);
        }


    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException
    {
        for (String topic : TOPICS)
        {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
