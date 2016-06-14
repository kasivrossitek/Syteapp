package com.syte.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.R;
import com.syte.activities.sytedetailuser.SyteDetailUserActivity;
import com.syte.listeners.OnBulletinNotificationDelete;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.BulletinBoardPush;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;

/**
 * Created by khalid.p on 15-02-2016.
 */

public class AdapterNotificationBulletinYou extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnCustomDialogsListener {

    private ArrayList<BulletinBoardPush> mNotifications;
    private ArrayList<String> mNotficationKeyLst;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private Context mContext;
    private DisplayImageOptions mDspImgOptions;
    private int dimenProfilePic;
    private NetworkStatus mNetworkStatus;
    private CustomDialogs mCustomDialogs;
    private OnBulletinNotificationDelete mOnBulletinNotificationDelete;

    public AdapterNotificationBulletinYou(Context paramCon, OnBulletinNotificationDelete paramBulletinNotificationDelete, ArrayList<String> paramNotificatinKeyLst, ArrayList<BulletinBoardPush> parammNotifications, int paramProfilePicDimen) {
        this.mContext = paramCon;
        this.mNetworkStatus = new NetworkStatus(mContext);
        this.mCustomDialogs = CustomDialogs.CREATE_DIALOG(mContext, this);
        this.mOnBulletinNotificationDelete = paramBulletinNotificationDelete;
        this.mNotficationKeyLst = paramNotificatinKeyLst;
        this.mNotifications = parammNotifications;
        this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        this.dimenProfilePic = paramProfilePicDimen;
        cTransformation = new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("face").radius("max").quality(100);

        this.mDspImgOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.img_syte_thumbnail_image_list) // All login background to be replaced by default profile pic in Displayoptions
                .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnLoading(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                .displayer(new RoundedBitmapDisplayer(dimenProfilePic))
                .build();
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_holder_notifications, parent, false);
        return new ViewHolderNotification(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolderNotification viewHolder = (ViewHolderNotification) holder;
        final BulletinBoardPush bulletinBoardPush = mNotifications.get(position);
        String cURL = mCloudinary.url().transformation(cTransformation).generate(bulletinBoardPush.getSyteImageUrl());
        ImageLoader.getInstance().displayImage(cURL, viewHolder.getmIvNotificationImage(), mDspImgOptions);

        String message = "";
        if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_BULETTIN) {
            message = bulletinBoardPush.getSyteName() + " has posted a new Bulletin " + bulletinBoardPush.getBulletinSubject();
        } else if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_REJECTED) {
            message = "Your syte claim request for " + bulletinBoardPush.getSyteName() + " has been rejected";
        } else if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_ACCEPTED) {
            message = "Your syte claim request for " + bulletinBoardPush.getSyteName() + " has been accepted";
        } else if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_ACCEPTED) {//for user accepted invite for syte by kasi
            message = "Your syte follow request for " + bulletinBoardPush.getSyteName() + " has been accepted";
        } else if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_REJECTED) { //for user rejected invite for syte by kasi
            message = "Your syte follow request for " + bulletinBoardPush.getSyteName() + " has been rejected";
        }

        viewHolder.getmTvNotificationMessage().setText(message);

        viewHolder.getmTvNotificationTime().setText(StaticUtils.CONVERT_BULLETIN_DATE_TIME((long) bulletinBoardPush.getBulletinDateTime()));

        final String finalMessage = message;
        viewHolder.getmLinLayNotification().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_BULETTIN) {
                    if (mNetworkStatus.isNetworkAvailable()) {
                        Intent mIntSyteDetailUser = new Intent(mContext, SyteDetailUserActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(StaticUtils.IPC_SYTE_ID, bulletinBoardPush.getSyteId());
                        bundle.putBoolean(StaticUtils.IPC_FOLLOWED_SYTE, true);
                        mIntSyteDetailUser.putExtras(bundle);
                        mContext.startActivity(mIntSyteDetailUser);
                    } else {
                        mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                    }
                } else if (bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_ACCEPTED ||
                        bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIM_REQUEST_REJECTED) {
                    Toast.makeText(mContext, finalMessage, Toast.LENGTH_LONG).show();
                }else if(bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_ACCEPTED ||
                        bulletinBoardPush.getPushType() == StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_REJECTED){
                    Toast.makeText(mContext, finalMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewHolder.getmIvDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkStatus.isNetworkAvailable()) {
                    mOnBulletinNotificationDelete.onBulletinNotificationDelete(mNotficationKeyLst.get(position));
                } else {
                    mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                }
            }
        });

    }


    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {

            // Do nothing
        }
    }

    @Override

    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {

            mContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
