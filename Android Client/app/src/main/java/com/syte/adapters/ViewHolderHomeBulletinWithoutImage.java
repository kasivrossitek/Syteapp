package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 30-03-2016.
 */
public class ViewHolderHomeBulletinWithoutImage extends RecyclerView.ViewHolder {
    private ImageView mIvFavIcon, mIvLikeIcon;
    private TextView mTvBulletinTitle, mTvBulletinDuration, mTvBulletinBody, mTvSyteName, mTvSyteAddress;
    private RelativeLayout mLinLayItem;

    public ViewHolderHomeBulletinWithoutImage(View itemView) {
        super(itemView);
        mIvFavIcon = (ImageView) itemView.findViewById(R.id.xIvFavIcon);
        mTvBulletinTitle = (TextView) itemView.findViewById(R.id.xTvBulletinTitle);
        mTvBulletinDuration = (TextView) itemView.findViewById(R.id.xTvBulletinDuration);
        mTvBulletinBody = (TextView) itemView.findViewById(R.id.xTvBulletinBody);
        mTvSyteName = (TextView) itemView.findViewById(R.id.xTvSyteName);
        mTvSyteAddress = (TextView) itemView.findViewById(R.id.xTvSyteAddress);
        mLinLayItem = (RelativeLayout) itemView.findViewById(R.id.xLinLayItem);
        mIvLikeIcon = (ImageView) itemView.findViewById(R.id.xIvLikeIcon);

    }


    public void setmIvFavIcon(ImageView mIvFavIcon) {
        this.mIvFavIcon = mIvFavIcon;
    }


    public void setmTvBulletinTitle(TextView mTvBulletinTitle) {
        this.mTvBulletinTitle = mTvBulletinTitle;
    }

    public void setmTvBulletinBody(TextView mTvBulletinBody) {
        this.mTvBulletinBody = mTvBulletinBody;
    }

    public void setmTvBulletinDuration(TextView mTvBulletinDuration) {
        this.mTvBulletinDuration = mTvBulletinDuration;
    }

    public void setmTvSyteName(TextView mTvSyteName) {
        this.mTvSyteName = mTvSyteName;
    }

    public void setmTvSyteAddress(TextView mTvSyteAddress) {
        this.mTvSyteAddress = mTvSyteAddress;
    }


    public ImageView getmIvFavIcon() {
        return mIvFavIcon;
    }


    public TextView getmTvBulletinTitle() {
        return mTvBulletinTitle;
    }

    public ImageView getmIvLikeIcon() {
        return mIvLikeIcon;
    }

    public void setmIvLikeIcon(ImageView mIvLikeIcon) {
        this.mIvLikeIcon = mIvLikeIcon;
    }

    public TextView getmTvBulletinDuration() {
        return mTvBulletinDuration;
    }

    public TextView getmTvBulletinBody() {
        return mTvBulletinBody;
    }

    public TextView getmTvSyteName() {
        return mTvSyteName;
    }

    public TextView getmTvSyteAddress() {
        return mTvSyteAddress;
    }

    public void setmLinLayItem(RelativeLayout mLinLayItem) {
        this.mLinLayItem = mLinLayItem;
    }

    public RelativeLayout getmLinLayItem() {
        return mLinLayItem;
    }
}