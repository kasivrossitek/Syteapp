package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 16-03-2016.
 */
public class ViewHolderOtherUserChatMessage extends RecyclerView.ViewHolder
    {
        TextView mTvMessage,mTvTime;
        public ViewHolderOtherUserChatMessage(View itemView)
            {
                super(itemView);
                mTvMessage=(TextView)itemView.findViewById(R.id.xTvMessage);
                mTvTime=(TextView)itemView.findViewById(R.id.xTvTime);
            }

        public void setmTvMessage(TextView mTvMessage) {
            this.mTvMessage = mTvMessage;
        }

        public TextView getmTvMessage() {
            return mTvMessage;
        }

        public void setmTvTime(TextView mTvTime) {
            this.mTvTime = mTvTime;
        }

        public TextView getmTvTime() {
            return mTvTime;
        }
    }
