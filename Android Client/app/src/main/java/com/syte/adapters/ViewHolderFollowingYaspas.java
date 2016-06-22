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
public class ViewHolderFollowingYaspas extends RecyclerView.ViewHolder
{
    private LinearLayout linLayFollowingYaspas;
    private TextView tvFollowingYaspasName;
    private ImageView ivFollowingYaspasImage;
    private ProgressBar pbLoader;

    public ViewHolderFollowingYaspas(View itemView)
    {
        super(itemView);

        linLayFollowingYaspas = (LinearLayout) itemView.findViewById(R.id.xLinLayFollowingYaspas);
        tvFollowingYaspasName = (TextView) itemView.findViewById(R.id.xTvFollowingYaspasName);
        ivFollowingYaspasImage = (ImageView) itemView.findViewById(R.id.xIvFollowingYaspasImage);
        pbLoader = (ProgressBar) itemView.findViewById(R.id.xPbLoader);
    }

    public void setLinLayFollowingYaspas(LinearLayout paramLinLayFollowingYaspas)
    {
        this.linLayFollowingYaspas = paramLinLayFollowingYaspas;
    }

    public void setTvFollowingYaspasName(TextView paramTvName)
    {
        this.tvFollowingYaspasName = paramTvName;
    }

    public void setIvFollowingYaspasImage(ImageView paramIvImage)
    {
        this.ivFollowingYaspasImage = paramIvImage;
    }

    public void setPbLoader(ProgressBar paramPbLoader)
    {
        this.pbLoader = paramPbLoader;
    }

    public LinearLayout getLinLayFollowingYaspas()
    {
        return this.linLayFollowingYaspas;
    }

    public TextView getTvFollowingYaspasName()
    {
        return this.tvFollowingYaspasName;
    }

    public ImageView getIvFollowingYaspasImage()
    {
        return this.ivFollowingYaspasImage;
    }

    public ProgressBar getPbLoader()
    {
        return this.pbLoader;
    }

}
