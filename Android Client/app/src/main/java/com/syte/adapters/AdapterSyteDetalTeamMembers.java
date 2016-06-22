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
import com.syte.activities.sytedetailuser.SyteTeamMemberDetailsActivity;
import com.syte.models.YasPasTeam;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by khalid.p on 26-02-2016.
 */
public class AdapterSyteDetalTeamMembers extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<YasPasTeam> mTeamMembers;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private Context mContext;
    private DisplayImageOptions mDspImgOptions;
    private int dimenProfilePic;
    public AdapterSyteDetalTeamMembers(Context paramCon,ArrayList<YasPasTeam> paramTeamMembers, int paramProfilePicDimen)
    {
        this.mContext=paramCon;
        this.mTeamMembers=paramTeamMembers;
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

    }
    @Override
    public int getItemCount()
    {
        return mTeamMembers.size();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_holder_syte_detail_team_member, parent, false);
        return new ViewHolderTeamMember(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        ViewHolderTeamMember viewHolder = (ViewHolderTeamMember) holder;
        YasPasTeam mTeam = mTeamMembers.get(position);
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
        viewHolder.getmRelLayTeamMemberVwHolder().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mInt_TM_Details = new Intent(mContext, SyteTeamMemberDetailsActivity.class);
                Bundle mBdl = new Bundle();
                mBdl.putParcelable(StaticUtils.IPC_TEAM_MEMBER, mTeamMembers.get(position));
                mInt_TM_Details.putExtras(mBdl);
                mContext.startActivity(mInt_TM_Details);
            }
        });

    }


}