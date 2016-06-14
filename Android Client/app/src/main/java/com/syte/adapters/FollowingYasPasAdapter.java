package com.syte.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.syte.R;
import com.syte.activities.sytedetailuser.SyteDetailUserActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.FollowingYasPas;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;

/**
 * Created by kumar.m on 26-01-2016.
 */
public class FollowingYasPasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnCustomDialogsListener
{
    private ArrayList<FollowingYasPas> mFollowingYasPasLst;
    private ArrayList<String> mFollowingYasPasKeyLst;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private Context mContext;
    private NetworkStatus mNetworkStatus;
    private CustomDialogs mCustomDialogs;
    private int dimenProfilePic;
    private DisplayImageOptions mDspImgOptions;

    public FollowingYasPasAdapter(Context paramContext, ArrayList<FollowingYasPas> paramFollowingYasPasLst, ArrayList<String> paramFollowingYasPasKeyLst,int paramProfilePicDimen)
    {
        this.mContext = paramContext;
        this.mFollowingYasPasLst = paramFollowingYasPasLst;
        this.mFollowingYasPasKeyLst = paramFollowingYasPasKeyLst;
        this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        this.dimenProfilePic = paramProfilePicDimen;
        this.cTransformation=new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("center").radius("max");
        mNetworkStatus = new NetworkStatus(mContext);
        this.mDspImgOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnLoading(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                .displayer(new RoundedBitmapDisplayer(dimenProfilePic))
                .build();
        mCustomDialogs = CustomDialogs.CREATE_DIALOG(mContext,this);
    }

    @Override
    public int getItemCount()
    {
        return mFollowingYasPasLst.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_favourite_sytes_following_yaspases, parent, false);
        return new ViewHolderFollowingYaspas(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        ViewHolderFollowingYaspas viewHolder = (ViewHolderFollowingYaspas) holder;
        FollowingYasPas mObjFollowingYasPas = this.mFollowingYasPasLst.get(position);

        viewHolder.getTvFollowingYaspasName().setText(mObjFollowingYasPas.getName());
        mDisplayImage(mObjFollowingYasPas.getImageUrl(), viewHolder.getIvFollowingYaspasImage(), viewHolder.getPbLoader());

        viewHolder.getLinLayFollowingYaspas().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mNetworkStatus.isNetworkAvailable())
                {
                    Intent mIntSyteDetailUser = new Intent(mContext, SyteDetailUserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(StaticUtils.IPC_SYTE_ID, mFollowingYasPasKeyLst.get(position));
                    bundle.putBoolean(StaticUtils.IPC_FOLLOWED_SYTE,true);
                    mIntSyteDetailUser.putExtras(bundle);
                    mContext.startActivity(mIntSyteDetailUser);
                }
                else
                {
                    mCustomDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", false, false);
                }
            }
        });

    }

    private void mDisplayImage(String url, final ImageView mImageView, final ProgressBar progressBar)
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                    /*.displayer(new RoundedBitmapDisplayer(12))*/
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        String cURL = mCloudinary.url().transformation(cTransformation).generate(url);
        ImageLoader.getInstance().displayImage(cURL, mImageView, options, new SimpleImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
        {
            // Do nothing
        }
    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
    {
        if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
        {
            mContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

}
