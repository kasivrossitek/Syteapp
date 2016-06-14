package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.syte.R;


/**
 * Created by kumar.m on 26-01-2016.
 */
public class ViewHolderSponseredYaspases extends RecyclerView.ViewHolder
{
    private LinearLayout linLayOwnedYasPas;
    private TextView tvManageYaspasName;
    private ImageView ivManageYaspasImage,mIvTmProfilePicHide/*, ivDeleteYasPas*/;
    private ProgressBar pbLoader;

    public ViewHolderSponseredYaspases(View itemView)
    {
        super(itemView);

        linLayOwnedYasPas = (LinearLayout) itemView.findViewById(R.id.xLinLayOwnedYasPas);
        tvManageYaspasName = (TextView) itemView.findViewById(R.id.xTvManageYaspasName);
        ivManageYaspasImage = (ImageView) itemView.findViewById(R.id.xIvManageYaspasImage);
        mIvTmProfilePicHide=(ImageView)itemView.findViewById(R.id.xIvTmProfilePicHide);
        pbLoader = (ProgressBar) itemView.findViewById(R.id.xPbLoader);
    }

    public void setLinLayOwnedYaspas(LinearLayout paramLinLayOwnedYaspas)
    {
        this.linLayOwnedYasPas = paramLinLayOwnedYaspas;
    }

    public void setTvManageYaspasName(TextView paramTvName)
    {
        this.tvManageYaspasName = paramTvName;
    }

    public void setIvManageYaspasImage(ImageView paramIvImage)
    {
        this.ivManageYaspasImage = paramIvImage;
    }

    /*public void setIvDeleteYaspas(ImageView paramIvDelete)
    {
        this.ivDeleteYasPas = paramIvDelete;
    }*/

    public void setPbLoader(ProgressBar paramPbLoader)
    {
        this.pbLoader = paramPbLoader;
    }

    public LinearLayout getLinLayOwnedYaspas()
    {
        return this.linLayOwnedYasPas;
    }

    public TextView getTvManageYaspasName()
    {
        return this.tvManageYaspasName;
    }

    public ImageView getIvManageYaspasImage()
    {
        return this.ivManageYaspasImage;
    }

   public ProgressBar getPbLoader()
    {
        return this.pbLoader;
    }
    public void setIvTmProfilePicHide(ImageView param){this.mIvTmProfilePicHide=param;}
    public ImageView getIvTmProfilePicHide(){return this.mIvTmProfilePicHide;}

}
