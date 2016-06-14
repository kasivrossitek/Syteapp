package com.syte.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.syte.listeners.OnEditButtonListener;
import com.syte.models.YasPasTeam;
import com.syte.models.YasPasTeams;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by khalid.p on 15-02-2016.
 */
public class AdapterTeamMembers extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private ArrayList<YasPasTeams> mTeamMembers;
        private Cloudinary mCloudinary;
        private Transformation cTransformation;
        private Context mContext;
        private DisplayImageOptions mDspImgOptions;
        private int dimenProfilePic;
        private OnEditButtonListener onEditButtonListener;
        public AdapterTeamMembers(Context paramCon,ArrayList<YasPasTeams> paramTeamMembers, int paramProfilePicDimen, OnEditButtonListener paramEditListener)
            {
                this.mTeamMembers=paramTeamMembers;
                this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                this.dimenProfilePic = paramProfilePicDimen;//
                /*cTransformation=new Transformation()
                        .radius("max").width(dimenProfilePic).height(dimenProfilePic).crop("crop").gravity("face").chain()
                        .width(dimenProfilePic);*/
                cTransformation=new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("face").radius("max").quality(100);
                this.mDspImgOptions = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .showImageForEmptyUri(R.drawable.img_user_thumbnail_image_list) // All login background to be replaced by default profile pic in Displayoptions
                        .showImageOnFail(R.drawable.img_user_thumbnail_image_list)
                        .showImageOnLoading(R.drawable.img_user_thumbnail_image_list)
                        .displayer(new RoundedBitmapDisplayer(dimenProfilePic))
                        .build();
                this.onEditButtonListener=paramEditListener;
            }
        @Override
        public int getItemCount()
            {
                return mTeamMembers.size();
            }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_holder_team_member, parent, false);
                return new ViewHolderTeamMember(view);
            }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
            {
                ViewHolderTeamMember viewHolder = (ViewHolderTeamMember) holder;
                YasPasTeam mTeam = mTeamMembers.get(position).getYasPasTeam();
                String cURL = mCloudinary.url().transformation(cTransformation).generate(mTeam.getProfilePic());
                ImageLoader.getInstance().displayImage(cURL,viewHolder.getmIvTmProfilePic(),mDspImgOptions);
                if(mTeam.getIsHide()==1)
                    {
                        viewHolder.getmIvTmProfilePicHide().setVisibility(View.VISIBLE);
                    }
                else
                    {
                        viewHolder.getmIvTmProfilePicHide().setVisibility(View.GONE);
                    }
                if(position%2==0)
                    {
                        viewHolder.getmRelLayTeamMemberVwHolder().setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                else
                    {
                        viewHolder.getmRelLayTeamMemberVwHolder().setBackgroundColor(Color.parseColor("#F5F5F5"));
                    }
                viewHolder.getmTvTmName().setText(mTeam.getName());
                viewHolder.getmTvTmDesignation().setText(mTeam.getDesignation());
                viewHolder.getmRelLayEdit().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEditButtonListener.onEditButtonClicked(position);
                    }
                });

            }


    }
