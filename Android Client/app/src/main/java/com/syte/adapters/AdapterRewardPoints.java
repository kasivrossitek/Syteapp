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
 * Created by Developer on 8/18/2016.
 */
public class AdapterRewardPoints extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<String> reward_point_names;
    private ArrayList<Integer> reward_points;

    public AdapterRewardPoints(EditProfileActivity editProfileActivity, ArrayList<String> mRewardPointsnames, ArrayList<Integer> mRewardpoints) {

        this.mContext = editProfileActivity;
        this.reward_point_names = mRewardPointsnames;
        this.reward_points = mRewardpoints;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterrewardpoints, parent, false);
        return new ViewHolderContactlist(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderContactlist viewHolder = (ViewHolderContactlist) holder;
        viewHolder.getmTvRewardName().setText(reward_point_names.get(position));
        viewHolder.getmTvRewardPoint().setText(""+reward_points.get(position));
    }

    @Override
    public int getItemCount() {
        return reward_point_names.size();
    }

    public class ViewHolderContactlist extends RecyclerView.ViewHolder {

        private TextView mTvRewardName, mTvRewardPoint;

        public TextView getmTvRewardName() {
            return mTvRewardName;
        }

        public void setmTvRewardName(TextView mTvRewardName) {
            this.mTvRewardName = mTvRewardName;
        }

        public TextView getmTvRewardPoint() {
            return mTvRewardPoint;
        }

        public void setmTvRewardPoint(TextView mTvRewardPoint) {
            this.mTvRewardPoint = mTvRewardPoint;
        }

        public ViewHolderContactlist(View itemView) {
            super(itemView);

            mTvRewardName = (TextView) itemView.findViewById(R.id.xTvRewardName);
            mTvRewardPoint = (TextView) itemView.findViewById(R.id.xTvRewardpoints);
        }


    }
}
