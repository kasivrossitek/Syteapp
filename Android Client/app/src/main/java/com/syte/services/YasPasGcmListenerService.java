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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.syte.R;
import com.syte.SplashActivity;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class YasPasGcmListenerService extends com.google.android.gms.gcm.GcmListenerService
{

    private static final String TAG = "YasPasGcmListenerService";
    private  YasPasPreferences mPref;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, final Bundle data)
    {
        try
        {
            mPref = YasPasPreferences.GET_INSTANCE(this);

           /* Log.e(TAG, "syteName: " + data.getString("title"));
            Log.e(TAG, "Message: " + data.getString("message"));
            Log.e(TAG, "bigImage: " + data.getString("bigImage"));
            Log.e(TAG, "bigIconImage: " + data.getString("bigIconImage"));
            Log.e("pushType ",""+data.getString("pushType"));*/

            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */

           // sendNotification(message);

            if(mPref.sGetNotificationOnOffStatus() && !data.getString("pushType").equalsIgnoreCase("6"))
            {
                new AsyncTask<String,Void,String>()
                    {
                        Bitmap imgBig=null;
                        Bitmap imgIcn=null;
                        @Override
                        protected String doInBackground(String... params)
                            {
                                if(data.getString("bigImage").toString().trim().length()>0)
                                    {
                                        URL urlIcon = null;
                                        //Transformation cTransformation=new Transformation().width(1202).height(676).crop("scale").quality(100);
                                        Cloudinary mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                                        String cURL = mCloudinary.url().generate(data.getString("bigImage"));
                                        try
                                            {
                                                urlIcon = new URL(cURL);
                                                imgBig = BitmapFactory.decodeStream(urlIcon.openConnection().getInputStream());
                                            }
                                        catch (MalformedURLException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        catch (IOException e)
                                            {
                                                e.printStackTrace();
                                            }
                                    }
                                if(data.getString("bigIconImage").toString().trim().length()>0)
                                    {
                                        URL urlIcon = null;
                                        Cloudinary mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                                        int dimenProfilePic = ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density));
                                        Transformation cTransformation=new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("face").radius("max").quality(100);
                                        String cURL = mCloudinary.url().transformation(cTransformation).generate(data.getString("bigIconImage"));
                                        try
                                            {
                                                urlIcon = new URL(cURL);
                                                imgIcn = BitmapFactory.decodeStream(urlIcon.openConnection().getInputStream());
                                            }
                                        catch (MalformedURLException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        catch (IOException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                return null;
                            }
                        @Override
                        protected void onPostExecute(String param)
                            {
                                mSendNotification(data.getString("title"),
                                        data.getString("message"),
                                        imgBig, imgIcn,
                                        data.getString("pushType"),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "");
                            }
                    }.execute();
                }
            // Chat Notification
            if(mPref.sGetNotificationOnOffStatus() && data.getString("pushType").equalsIgnoreCase("6")
                    && !data.getString("chatId").equalsIgnoreCase(mPref.sGetOnGoingChatId())
                    && !data.getString("syteId").equalsIgnoreCase(mPref.sGetOnGoingChat_SyteId())
                    )
            {
                new AsyncTask<String,Void,String>()
                {
                    Bitmap imgBig=null;
                    Bitmap imgIcn=null;
                    @Override
                    protected String doInBackground(String... params)
                    {
                        if(data.getString("bigImage").toString().trim().length()>0)
                        {
                            URL urlIcon = null;
                            //Transformation cTransformation=new Transformation().width(1202).height(676).crop("scale").quality(100);
                            Cloudinary mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                            String cURL = mCloudinary.url().generate(data.getString("bigImage"));
                            try
                            {
                                urlIcon = new URL(cURL);
                                imgBig = BitmapFactory.decodeStream(urlIcon.openConnection().getInputStream());
                            }
                            catch (MalformedURLException e)
                            {
                                e.printStackTrace();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if(data.getString("bigIconImage").toString().trim().length()>0)
                        {
                            URL urlIcon = null;
                            Cloudinary mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                            int dimenProfilePic = ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density));
                            Transformation cTransformation=new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("face").radius("max").quality(100);
                            String cURL = mCloudinary.url().transformation(cTransformation).generate(data.getString("bigIconImage"));
                            try
                            {
                                urlIcon = new URL(cURL);
                                imgIcn = BitmapFactory.decodeStream(urlIcon.openConnection().getInputStream());
                            }
                            catch (MalformedURLException e)
                            {
                                e.printStackTrace();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String param)
                    {
                        Log.e("syteId : ",""+data.getString("syteId"));
                        Log.e("chatId : ",""+data.getString("chatId"));
                        Log.e("senderType : ",""+data.getString("senderType"));

                        mSendNotification(data.getString("title"),
                                data.getString("message"),
                                imgBig, imgIcn,
                                data.getString("pushType"),
                                data.getString("chatId"),
                                data.getString("syteId"),
                                data.getString("senderType"),
                                data.getString("senderNum"),
                                data.getString("bigImage").toString());
                    }
                }.execute();
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    /*private void sendNotification(String message)
    {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setColor(Color.parseColor("#119789"))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
    }*/

    /*private int getNotificationIcon()
    {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? *//*R.drawable.ic_notification*//*R.mipmap.ic_launcher : R.mipmap.ic_launcher;//Todo need to update the transparent image to support devices grater than lollipop
    }*/

    private void mSendNotification(String paramSub,
                                   String paramTitle,
                                   Bitmap paramBigImgage,
                                   Bitmap paramBigIcon,
                                   String pushType,
                                   String paramOnGoingChatId,
                                   String paramOnGoingChatSyteId,
                                   String paramOnGoingChatSenderType,
                                   String paramOnGoingChatSenderNum,
                                   String paramOnGoingChatSenderImg)
        {

            Bundle mBun = new Bundle();
            mBun.putInt(StaticUtils.IPC_HOME_STARTING_FRAGMENT, Integer.parseInt(pushType));
            //For chat notification, putting extra values
            if(Integer.parseInt(pushType)==6) {
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_ID, paramOnGoingChatId);
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID, paramOnGoingChatSyteId);
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERTYPE, paramOnGoingChatSenderType);
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNUM,paramOnGoingChatSenderNum);
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERNAME,paramSub);
                mBun.putString(StaticUtils.IPC_ONGOING_CHAT_SENDERIMG,paramOnGoingChatSenderImg);
            }
            //END --- For chat notification, putting extra values
            Intent mIntSplashScreen = new Intent(getApplicationContext(), SplashActivity.class);
            mIntSplashScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntSplashScreen.putExtras(mBun);

            if(android.os.Build.VERSION.SDK_INT > 15)
                {
                        // Greater Than ICS .i.e., > 15
                    if(paramBigImgage!=null)
                        {
                            Notification.Builder builder = new Notification.Builder(getApplicationContext());
                            builder.setSmallIcon(getNotificationIcon());
                            builder.setContentTitle(paramSub);
                           // builder.setColor(Color.parseColor("#FFFFFF"));
                            builder.setContentText(paramTitle);
                            if(paramBigIcon!=null)
                                builder.setLargeIcon(paramBigIcon);
                            builder.setAutoCancel(true);
                            builder.setDefaults(Notification.DEFAULT_ALL);

                            builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, mIntSplashScreen, PendingIntent.FLAG_UPDATE_CURRENT));
                            Notification.BigPictureStyle bigPicutureStyle = new Notification.BigPictureStyle(builder);
                            //bigPicutureStyle.bigLargeIcon(paramResultSet.get(i).sIcon);
                            bigPicutureStyle.bigPicture(paramBigImgage);
                            bigPicutureStyle.setBigContentTitle(paramSub);
                            bigPicutureStyle.setSummaryText(paramTitle);
                            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, bigPicutureStyle.build());
                        }
                    else
                        {
                            if(paramBigIcon!=null)
                                {
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                                    Notification notification = new Notification.Builder(getApplicationContext())
                                            .setContentTitle(paramSub)
                                            .setContentText(paramTitle)
                                            /*.setColor(Color.parseColor("#FFFFFF"))*/
                                            .setSmallIcon(getNotificationIcon())
                                            .setLargeIcon(paramBigIcon)
                                            .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, mIntSplashScreen, PendingIntent.FLAG_UPDATE_CURRENT)).build();
                                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                        notification.defaults |= Notification.DEFAULT_ALL;
                                        notificationManager.notify(0, notification);
                                }
                            else
                                {
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                                    Notification notification = new Notification.Builder(getApplicationContext())
                                            .setContentTitle(paramSub)
                                            .setContentText(paramTitle)
                                            .setSmallIcon(getNotificationIcon())
                                            .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, mIntSplashScreen, PendingIntent.FLAG_UPDATE_CURRENT)).build();
                                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                    notification.defaults |= Notification.DEFAULT_ALL;
                                    notificationManager.notify(0, notification);
                                }

                        }

                }
            else //if(android.os.Build.VERSION.SDK_INT == 15 || android.os.Build.VERSION.SDK_INT == 14)
                {
                    //If device running on ICS .i.e., = 14 & 15
                    if(paramBigIcon!=null)
                        {
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                            Notification notification = new Notification.Builder(getApplicationContext())
                                    .setContentTitle(paramSub)
                                    .setContentText(paramTitle)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, mIntSplashScreen, PendingIntent.FLAG_UPDATE_CURRENT))
                                    .setLargeIcon(paramBigIcon).build();
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                    notification.defaults |= Notification.DEFAULT_ALL;
                                    notificationManager.notify(0, notification);
                        }
                    else
                        {
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                            Notification notification = new Notification.Builder(getApplicationContext())
                                    .setContentTitle(paramSub)
                                    .setContentText(paramTitle)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, mIntSplashScreen, PendingIntent.FLAG_UPDATE_CURRENT)).build();
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            notification.defaults |= Notification.DEFAULT_ALL;
                            notificationManager.notify(0, notification);
                        }
                }
            // update Home only for My Activity Notifications
            if(pushType.equalsIgnoreCase("1") || pushType.equalsIgnoreCase("2") || pushType.equalsIgnoreCase("3") || pushType.equalsIgnoreCase("4") || pushType.equalsIgnoreCase("5"))
                {
                    mPref.sSetNotificationIconUpdate(true);
                    Intent updateNotificationIcon = new Intent(StaticUtils.BRD_CST_AC_UPDATE_NOTIFICATION_ICON);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(updateNotificationIcon);
                }
        } // END mSendNotification()
    public int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.mipmap.ic_launcher: R.mipmap.ic_launcher;
    }


}
