package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by kumar.m on 05-03-2016.
 */
public class ViewHolderNotification extends RecyclerView.ViewHolder
{
    private ImageView mIvNotificationImage, mIvDelete;
    private TextView mTvNotificationMessage, mTvNotificationTime;
    private LinearLayout mLinLayNotification;


    public ViewHolderNotification(View itemView)
    {
        super(itemView);

        mIvNotificationImage = (ImageView) itemView.findViewById(R.id.xIvNotificationImage);
        mIvDelete = (ImageView) itemView.findViewById(R.id.xIvDelete);
        mTvNotificationMessage = (TextView) itemView.findViewById(R.id.xTvNotificationMessage);
        mTvNotificationTime = (TextView) itemView.findViewById(R.id.xTvNotificationTime);
        mLinLayNotification = (LinearLayout) itemView.findViewById(R.id.xLinLayNotification);
    }

    public void setmIvNotificationImage(ImageView mIvNotificationImage)
    {
        this.mIvNotificationImage = mIvNotificationImage;
    }

    public ImageView getmIvNotificationImage()
    {
        return mIvNotificationImage;
    }

    public void setmIvDelete(ImageView mIvDelete)
    {
        this.mIvDelete = mIvDelete;
    }

    public ImageView getmIvDelete()
    {
        return mIvDelete;
    }

    public void setmTvNotificationMessage(TextView mTvNotificationMessage)
    {
        this.mTvNotificationMessage = mTvNotificationMessage;
    }

    public void setmLinLayNotification(LinearLayout mLinLayNotification)
    {
        this.mLinLayNotification = mLinLayNotification;
    }

    public TextView getmTvNotificationMessage()
    {
        return mTvNotificationMessage;
    }

    public void setmTvNotificationTime(TextView mTvNotificationTime)
    {
        this.mTvNotificationTime = mTvNotificationTime;
    }

    public TextView getmTvNotificationTime()
    {
        return mTvNotificationTime;
    }

    public LinearLayout getmLinLayNotification()
    {
        return mLinLayNotification;
    }
}
