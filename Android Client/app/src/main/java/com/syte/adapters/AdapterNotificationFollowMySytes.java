package com.syte.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.R;
import com.syte.activities.sytedetailsponsor.SyteDetailSponsorActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.listeners.OnFollowNotificationDelete;
import com.syte.models.YasPasPush;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;

/**
 * Created by khalid.p on 15-02-2016.
 */
public class AdapterNotificationFollowMySytes extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnCustomDialogsListener
{
    private ArrayList<YasPasPush> mNotifications;
    private ArrayList<String> mNotficationKeyLst;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private Context mContext;
    private DisplayImageOptions mDspImgOptions;
    private int dimenProfilePic;
    private NetworkStatus mNetworkStatus;
    private CustomDialogs mCustomDialogs;
    private OnFollowNotificationDelete mOnFollowNotificationDelete;

    public AdapterNotificationFollowMySytes(Context paramCon, OnFollowNotificationDelete paramOnFollowNotificationDelete, ArrayList<String> paramNotificatinKeyLst, ArrayList<YasPasPush> parammNotifications, int paramProfilePicDimen)
        {
            this.mContext = paramCon;
            this.mNetworkStatus = new NetworkStatus(mContext);
            this.mCustomDialogs = CustomDialogs.CREATE_DIALOG(mContext,this);
            this.mOnFollowNotificationDelete = paramOnFollowNotificationDelete;
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
    public int getItemCount()
        {
            return mNotifications.size();
        }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_holder_notifications, parent, false);
            return new ViewHolderNotification(view);
        }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
        {
            ViewHolderNotification viewHolder = (ViewHolderNotification) holder;
            final YasPasPush mNotification = mNotifications.get(position);
            String cURL = mCloudinary.url().transformation(cTransformation).generate(mNotification.getUserProfilePic());
            ImageLoader.getInstance().displayImage(cURL, viewHolder.getmIvNotificationImage(), mDspImgOptions);

            int pushType = mNotification.getPushType();
            if(mNotification.getPushType()==StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_FOLOWER)
                {
                    // New Follower
                    viewHolder.getmTvNotificationMessage().setText(mNotification.getSyteName() + " has a new follower " + mNotification.getUserName());
                }
            else if(mNotification.getPushType()==StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIMED)
                {
                    // Some one claimed the syte
                    viewHolder.getmTvNotificationMessage().setText(mNotification.getUserName()  + " has claimed " + mNotification.getSyteName());
                }
            else if(mNotification.getPushType()==StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_INVITE){
                //  invited to the syte by kasi on 4-10-16
                viewHolder.getmTvNotificationMessage().setText(mNotification.getUserName()  + " has invited to follow the " + mNotification.getSyteName());
            }

            viewHolder.getmTvNotificationTime().setText(StaticUtils.CONVERT_BULLETIN_DATE_TIME((long) mNotification.getDateTime()));


            viewHolder.getmLinLayNotification().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {
                            if (mNetworkStatus.isNetworkAvailable())
                                {
                                    if(mNotification.getPushType()==StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_FOLOWER)
                                        {
                                            Intent mIntSyteDetailSponsor = new Intent(mContext, SyteDetailSponsorActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(StaticUtils.IPC_SYTE_ID, mNotification.getSyteId());
                                            mIntSyteDetailSponsor.putExtras(bundle);
                                            mContext.startActivity(mIntSyteDetailSponsor);
                                        }
                                    else if(mNotification.getPushType()==StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_CLAIMED)
                                        {
                                            mOnFollowNotificationDelete.onSyteClaimNotificationClicked(mNotification,mNotficationKeyLst.get(position));
                                        }
                                    else if(mNotification.getPushType()==StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_INVITE){
                                        mOnFollowNotificationDelete.onFollowSyteInvitationClicked(mNotification,mNotficationKeyLst.get(position));
                                    }
                                }
                            else
                                {
                                    mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                                }
                }
            });

            viewHolder.getmIvDelete().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {
                            if (mNetworkStatus.isNetworkAvailable())
                            {
                                mOnFollowNotificationDelete.onFollowNotificationDelete(mNotficationKeyLst.get(position));
                            }
                            else
                            {
                                mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                            }
                        }
                });

        }


    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
        {
            // Do nothing
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
        {
            mContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
