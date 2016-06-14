package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 30-03-2016.
 */
public class ViewHolderHomeBulletinWithImage extends RecyclerView.ViewHolder
    {
        private ImageView mIvBulletinImage,mIvFavIcon;
        private ProgressBar mPbLoader;
        private TextView mTvBulletinTitle,mTvBulletinDuration,mTvBulletinBody,mTvSyteName,mTvSyteAddress;
        private LinearLayout mLinLayItem;
        public ViewHolderHomeBulletinWithImage(View itemView)
            {
                super(itemView);
                mIvBulletinImage = (ImageView)itemView.findViewById(R.id.xIvBulletinImage);
                mIvFavIcon = (ImageView)itemView.findViewById(R.id.xIvFavIcon);
                mPbLoader = (ProgressBar)itemView.findViewById(R.id.xPbLoader);
                mTvBulletinTitle = (TextView)itemView.findViewById(R.id.xTvBulletinTitle);
                mTvBulletinDuration = (TextView)itemView.findViewById(R.id.xTvBulletinDuration);
                mTvBulletinBody = (TextView)itemView.findViewById(R.id.xTvBulletinBody);
                mTvSyteName = (TextView)itemView.findViewById(R.id.xTvSyteName);
                mTvSyteAddress = (TextView)itemView.findViewById(R.id.xTvSyteAddress);
                mLinLayItem=(LinearLayout)itemView.findViewById(R.id.xLinLayItem);
            }

        public void setmIvBulletinImage(ImageView mIvBulletinImage) {
            this.mIvBulletinImage = mIvBulletinImage;
        }

        public void setmIvFavIcon(ImageView mIvFavIcon) {
            this.mIvFavIcon = mIvFavIcon;
        }

        public void setmPbLoader(ProgressBar mPbLoader) {
            this.mPbLoader = mPbLoader;
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

        public ImageView getmIvBulletinImage() {
            return mIvBulletinImage;
        }

        public ImageView getmIvFavIcon() {
            return mIvFavIcon;
        }

        public ProgressBar getmPbLoader() {
            return mPbLoader;
        }

        public TextView getmTvBulletinTitle() {
            return mTvBulletinTitle;
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

        public LinearLayout getmLinLayItem() {
            return mLinLayItem;
        }

        public void setmLinLayItem(LinearLayout mLinLayItem) {
            this.mLinLayItem = mLinLayItem;
        }
    }
