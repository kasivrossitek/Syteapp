package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 02-03-2016.
 */

public class ViewHolderFollowers extends RecyclerView.ViewHolder
    {
        private ImageView mIvTmProfilePic;
        private RelativeLayout mRelLayTeamMemberVwHolder;
        private TextView mTvTmName;
        public ViewHolderFollowers(View itemView)
            {
                super(itemView);
                mIvTmProfilePic=(ImageView)itemView.findViewById(R.id.xIvTmProfilePic);
                mRelLayTeamMemberVwHolder=(RelativeLayout)itemView.findViewById(R.id.xRelLayTeamMemberVwHolder);
                mTvTmName=(TextView)itemView.findViewById(R.id.xTvTmName);
            }
        public void setmIvTmProfilePic(ImageView param){this.mIvTmProfilePic=param;}
        public void setmRelLayTeamMemberVwHolder(RelativeLayout param){this.mRelLayTeamMemberVwHolder=param;}
        public void setmTvTmName(TextView param){this.mTvTmName=param;}
        public ImageView getmIvTmProfilePic(){return this.mIvTmProfilePic;}
        public RelativeLayout getmRelLayTeamMemberVwHolder(){return this.mRelLayTeamMemberVwHolder;}
        public TextView getmTvTmName(){return this.mTvTmName;}
    }

