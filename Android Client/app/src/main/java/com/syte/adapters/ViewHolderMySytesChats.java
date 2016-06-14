package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 14-03-2016.
 */
public class ViewHolderMySytesChats extends RecyclerView.ViewHolder
    {
        private ImageView mIvSyteImage;
        private RelativeLayout mRelLayVwHolderMessageNew;
        private TextView mTvSyteName;
        public ViewHolderMySytesChats(View itemView)
            {
                super(itemView);
                mIvSyteImage=(ImageView)itemView.findViewById(R.id.xIvSyteImage);
                mRelLayVwHolderMessageNew=(RelativeLayout)itemView.findViewById(R.id.xRelLayVwHolderMessageNew);
                mTvSyteName=(TextView)itemView.findViewById(R.id.xTvSyteName);
            }

        public void setmIvSyteImage(ImageView mIvSyteImage) {
            this.mIvSyteImage = mIvSyteImage;
        }

        public void setmTvSyteName(TextView mTvSyteName) {
            this.mTvSyteName = mTvSyteName;
        }

        public void setmRelLayVwHolderMessageNew(RelativeLayout mRelLayVwHolderMessageNew) {
            this.mRelLayVwHolderMessageNew = mRelLayVwHolderMessageNew;
        }

        public TextView getmTvSyteName() {
            return mTvSyteName;
        }

        public ImageView getmIvSyteImage() {
            return mIvSyteImage;
        }

        public RelativeLayout getmRelLayVwHolderMessageNew() {
            return mRelLayVwHolderMessageNew;
        }
    }
