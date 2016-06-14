package com.syte.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.syte.R;
import com.syte.activities.sytedetailsponsor.SyteFollowerDetailsActivity;
import com.syte.models.Followers;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by khalid.p on 02-03-2016.
 */
public class AdapterFollowers extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private ArrayList<Followers> mFollowerses;
        private Cloudinary mCloudinary;
        private Transformation cTransformation;
        private Context mContext;
        private DisplayImageOptions mDspImgOptions;
        private int dimenProfilePic;
        public AdapterFollowers(Context paramCon,ArrayList<Followers> paramFollowers, int paramProfilePicDimen)
            {
                this.mFollowerses=paramFollowers;
                this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                this.dimenProfilePic = paramProfilePicDimen;//
                cTransformation=new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("face").radius("max").quality(100);
                this.mDspImgOptions = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .showImageForEmptyUri(R.drawable.img_user_thumbnail_image_list) // All login background to be replaced by default profile pic in Displayoptions
                        .showImageOnFail(R.drawable.img_user_thumbnail_image_list)
                        .showImageOnLoading(R.drawable.img_user_thumbnail_image_list)
                        .showImageOnFail(R.drawable.img_user_thumbnail_image_list)
                        .displayer(new RoundedBitmapDisplayer(dimenProfilePic))
                        .build();
                this.mContext=paramCon;
            }
        @Override
        public int getItemCount()
    {
        return mFollowerses.size();
    }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_holder_followers, parent, false);
                return new ViewHolderFollowers(view);
            }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
            {
                ViewHolderFollowers viewHolder = (ViewHolderFollowers) holder;
                Followers followers = mFollowerses.get(position);
                String cURL = mCloudinary.url().transformation(cTransformation).generate(followers.getUserProfilePic());
                ImageLoader.getInstance().displayImage(cURL,viewHolder.getmIvTmProfilePic(),mDspImgOptions);
                if(position%2==0)
                    {
                        viewHolder.getmRelLayTeamMemberVwHolder().setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                else
                    {
                        viewHolder.getmRelLayTeamMemberVwHolder().setBackgroundColor(Color.parseColor("#F5F5F5"));
                    }
                viewHolder.getmTvTmName().setText(followers.getUserName());

                viewHolder.getmRelLayTeamMemberVwHolder().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mInt_Follower_Details = new Intent(mContext, SyteFollowerDetailsActivity.class);
                        Bundle mBdl = new Bundle();
                        mBdl.putParcelable(StaticUtils.IPC_FOLLOWER, mFollowerses.get(position));
                        mInt_Follower_Details.putExtras(mBdl);
                        mContext.startActivity(mInt_Follower_Details);
                    }
                });
            }
    }
