package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 26-02-2016.
 */
public class ViewHolderSyteDetailTeamMember extends RecyclerView.ViewHolder
    {
        private ImageView mIvTmProfilePic,mIvTmProfilePicHide;
        private RelativeLayout mRelLayTeamMemberVwHolder;
        private TextView mTvTmName,mTvTmDesignation;
        public ViewHolderSyteDetailTeamMember(View itemView)
        {
            super(itemView);
            mIvTmProfilePic=(ImageView)itemView.findViewById(R.id.xIvTmProfilePic);
            mIvTmProfilePicHide=(ImageView)itemView.findViewById(R.id.xIvTmProfilePicHide);
            mRelLayTeamMemberVwHolder=(RelativeLayout)itemView.findViewById(R.id.xRelLayTeamMemberVwHolder);
            mTvTmName=(TextView)itemView.findViewById(R.id.xTvTmName);
            mTvTmDesignation=(TextView)itemView.findViewById(R.id.xTvTmDesignation);
        }
        public void setmIvTmProfilePic(ImageView param){this.mIvTmProfilePic=param;}
        public void setmIvTmProfilePicHide(ImageView param){this.mIvTmProfilePicHide=param;}

        public void setmRelLayTeamMemberVwHolder(RelativeLayout param){this.mRelLayTeamMemberVwHolder=param;}
        public void setmTvTmName(TextView param){this.mTvTmName=param;}
        public void setmTvTmDesignation(TextView param){this.mTvTmDesignation=param;}
        public ImageView getmIvTmProfilePic(){return this.mIvTmProfilePic;}
        public ImageView getmIvTmProfilePicHide(){return this.mIvTmProfilePicHide;}
        public RelativeLayout getmRelLayTeamMemberVwHolder(){return this.mRelLayTeamMemberVwHolder;}
        public TextView getmTvTmName(){return this.mTvTmName;}
        public TextView getmTvTmDesignation(){return this.mTvTmDesignation;}
    }