package com.syte.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.syte.R;
import com.syte.activities.sytedetailsponsor.SyteDetailSponsorActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.OwnedYasPases;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;

/**
 * Created by kumar.m on 28-01-2016.
 */
public class SponseredYasPasesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnCustomDialogsListener
{
    private ArrayList<OwnedYasPases> mOwnedYasPasLst;
    private ArrayList<String> mOwnedYasPasKeyLst;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private Context mContext;
    private NetworkStatus mNetworkStatus;
    private CustomDialogs mCustomDialogs;
    private int dimenProfilePic;
    private DisplayImageOptions mDspImgOptions;
    public SponseredYasPasesAdapter(Context paramContext, ArrayList<OwnedYasPases> paramOwnedYasPasLst, ArrayList<String> paramOwnedYasPasKeyLst,int paramProfilePicDimen)
    {
        this.mContext = paramContext;
        this.mOwnedYasPasLst = paramOwnedYasPasLst;
        this.mOwnedYasPasKeyLst = paramOwnedYasPasKeyLst;
        this.dimenProfilePic = paramProfilePicDimen;
        this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        this.cTransformation = new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("center").radius("max");
        mNetworkStatus = new NetworkStatus(mContext);
        mCustomDialogs = CustomDialogs.CREATE_DIALOG(mContext,this);

        this.mDspImgOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.img_syte_thumbnail_image_list) // All login background to be replaced by default profile pic in Displayoptions
                .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnLoading(R.drawable.img_syte_thumbnail_image_list)
                .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                .displayer(new RoundedBitmapDisplayer(dimenProfilePic))
                .build();
    }

    @Override
    public int getItemCount()
    {
        return mOwnedYasPasLst.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_sytes_sponsered_yaspases, parent, false);
        return new ViewHolderSponseredYaspases(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        ViewHolderSponseredYaspases viewHolder = (ViewHolderSponseredYaspases) holder;
        OwnedYasPases mObjOwnedYasPas = this.mOwnedYasPasLst.get(position);

        viewHolder.getTvManageYaspasName().setText(mObjOwnedYasPas.getName());
        mDisplayImage(mObjOwnedYasPas.getImageUrl(), viewHolder.getIvManageYaspasImage(), viewHolder.getPbLoader());

        if(mObjOwnedYasPas.getIsActive()==1)
            {
                viewHolder.getIvTmProfilePicHide().setVisibility(View.GONE);
            }
        else
            {
                viewHolder.getIvTmProfilePicHide().setVisibility(View.VISIBLE);
            }
        if(position%2==0)
            {
                viewHolder.getLinLayOwnedYaspas().setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        else
            {
                viewHolder.getLinLayOwnedYaspas().setBackgroundColor(Color.parseColor("#F5F5F5"));
            }
        viewHolder.getLinLayOwnedYaspas().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mNetworkStatus.isNetworkAvailable())
                {
                    Intent mIntYasPasDetails = new Intent(mContext, SyteDetailSponsorActivity.class);
                    mIntYasPasDetails.putExtra(StaticUtils.IPC_SYTE_ID, mOwnedYasPasKeyLst.get(position));
                    mContext.startActivity(mIntYasPasDetails);
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


        String cURL = mCloudinary.url().transformation(cTransformation).generate(url);
        ImageLoader.getInstance().displayImage(cURL, mImageView, mDspImgOptions, new SimpleImageLoadingListener()
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
