package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 16-03-2016.
 */
public class ViewHolderUserOwnChatMessage extends RecyclerView.ViewHolder
    {
        private TextView mTvMessage,mTvTime;
        private ImageView mIvReadStatus;
        public ViewHolderUserOwnChatMessage(View itemView) {
            super(itemView);
            mTvMessage=(TextView)itemView.findViewById(R.id.xTvMessage);
            mTvTime=(TextView)itemView.findViewById(R.id.xTvTime);
            mIvReadStatus=(ImageView)itemView.findViewById(R.id.xIvReadStatus);
        }

        public void setmTvMessage(TextView mTvMessage) {
            this.mTvMessage = mTvMessage;
        }

        public void setmTvTime(TextView mTvTime) {
            this.mTvTime = mTvTime;
        }

        public void setmIvReadStatus(ImageView mIvReadStatus) {
            this.mIvReadStatus = mIvReadStatus;
        }

        public TextView getmTvMessage() {
            return mTvMessage;
        }

        public TextView getmTvTime() {
            return mTvTime;
        }

        public ImageView getmIvReadStatus() {
            return mIvReadStatus;
        }
    }
