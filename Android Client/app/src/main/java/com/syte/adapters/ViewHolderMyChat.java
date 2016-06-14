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
public class ViewHolderMyChat extends RecyclerView.ViewHolder
    {
        private ImageView mIvSyteImage;
        private RelativeLayout mRelLayVwHolderMessageNew;
        private TextView mTvSyteName,mTvMessage,mTvMsgDateTime,mTvMsgUnreadCount;
        public ViewHolderMyChat(View itemView)
            {
                super(itemView);
                mIvSyteImage=(ImageView)itemView.findViewById(R.id.xIvSyteImage);
                mRelLayVwHolderMessageNew=(RelativeLayout)itemView.findViewById(R.id.xRelLayVwHolderMessageNew);
                mTvSyteName=(TextView)itemView.findViewById(R.id.xTvSyteName);
                mTvMessage=(TextView)itemView.findViewById(R.id.xTvMessage);
                mTvMsgDateTime=(TextView)itemView.findViewById(R.id.xTvMsgDateTime);
                mTvMsgUnreadCount=(TextView)itemView.findViewById(R.id.xTvMsgUnreadCount);
            }// END ViewHolderMyChat()

        public void setmIvSyteImage(ImageView mIvSyteImage) {
            this.mIvSyteImage = mIvSyteImage;
        }

        public ImageView getmIvSyteImage() {
            return mIvSyteImage;
        }

        public RelativeLayout getmRelLayVwHolderMessageNew() {
            return mRelLayVwHolderMessageNew;
        }

        public void setmRelLayVwHolderMessageNew(RelativeLayout mRelLayVwHolderMessageNew) {
            this.mRelLayVwHolderMessageNew = mRelLayVwHolderMessageNew;
        }

        public TextView getmTvSyteName() {
            return mTvSyteName;
        }

        public void setmTvSyteName(TextView mTvSyteName) {
            this.mTvSyteName = mTvSyteName;
        }

        public TextView getmTvMessage() {
            return mTvMessage;
        }

        public void setmTvMessage(TextView mTvMessage) {
            this.mTvMessage = mTvMessage;
        }

        public TextView getmTvMsgDateTime() {
            return mTvMsgDateTime;
        }

        public void setmTvMsgDateTime(TextView mTvMsgDateTime) {
            this.mTvMsgDateTime = mTvMsgDateTime;
        }

        public void setmTvMsgUnreadCount(TextView mTvMsgUnreadCount) {
            this.mTvMsgUnreadCount = mTvMsgUnreadCount;
        }

        public TextView getmTvMsgUnreadCount() {
            return mTvMsgUnreadCount;
        }
    }// END ViewHolderMyChat()
