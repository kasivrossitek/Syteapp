package com.syte.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.syte.R;
import com.syte.activities.EditProfileActivity;

import java.util.ArrayList;

/**
 * Created by Developer on 8/19/2016.
 */
public class AdapterRewardlevel extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<String> reward_level_names;
    private ArrayList<Integer> reward_level_img, reward_level_points;

    public AdapterRewardlevel(EditProfileActivity editProfileActivity, ArrayList<String> mRewardPointsnames, ArrayList<Integer> mRewardpoints, ArrayList<Integer> mRewardLevelImg) {

        this.mContext = editProfileActivity;
        this.reward_level_names = mRewardPointsnames;
        this.reward_level_points = mRewardpoints;
        this.reward_level_img = mRewardLevelImg;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterrewardlevels, parent, false);
        return new ViewHolderContactlist(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderContactlist viewHolder = (ViewHolderContactlist) holder;
        viewHolder.getmTvRewardName().setText(reward_level_names.get(position));
        viewHolder.getmTvRewardPoint().setText("" + reward_level_points.get(position));
        viewHolder.getmRewardImg().setImageResource(reward_level_img.get(position));
    }

    @Override
    public int getItemCount() {
        return reward_level_names.size();
    }

    public class ViewHolderContactlist extends RecyclerView.ViewHolder {

        private TextView mTvRewardName, mTvRewardPoint;
        private ImageView mRewardImg;

        public TextView getmTvRewardName() {
            return mTvRewardName;
        }

        public void setmTvRewardName(TextView mTvRewardName) {
            this.mTvRewardName = mTvRewardName;
        }

        public TextView getmTvRewardPoint() {
            return mTvRewardPoint;
        }

        public ImageView getmRewardImg() {
            return mRewardImg;
        }

        public void setmTvRewardPoint(TextView mTvRewardPoint) {
            this.mTvRewardPoint = mTvRewardPoint;

        }

        public ViewHolderContactlist(View itemView) {
            super(itemView);

            mTvRewardName = (TextView) itemView.findViewById(R.id.xTvRewardlevelname);
            mTvRewardPoint = (TextView) itemView.findViewById(R.id.xTvRewardlevelpoints);
            mRewardImg = (ImageView) itemView.findViewById(R.id.xIvlevelImg);
        }


    }
}

