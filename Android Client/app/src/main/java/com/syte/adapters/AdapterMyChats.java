package com.syte.adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.syte.R;
import com.syte.listeners.OnMessageItemClick;
import com.syte.models.Message;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by khalid.p on 14-03-2016.
 */
public class AdapterMyChats extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private ArrayList<Message> mMyChats;
        private Cloudinary mCloudinary;
        private Transformation cTransformation;
        private DisplayImageOptions mDspImgOptions;
        private int dimenProfilePic;
        private OnMessageItemClick onMessageItemClick;
        public AdapterMyChats(ArrayList<Message> yasPaseeChats, int paramDimenProfilePic,OnMessageItemClick MessageItemClick)
            {
                this.mMyChats=yasPaseeChats;
                this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                this.dimenProfilePic = paramDimenProfilePic;//
                cTransformation=new Transformation().width(dimenProfilePic).height(dimenProfilePic).crop("thumb").gravity("face").radius("max").quality(100);
                this.mDspImgOptions = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .showImageForEmptyUri(R.drawable.img_syte_thumbnail_image_list)
                        .showImageOnFail(R.drawable.img_syte_thumbnail_image_list)
                        .showImageOnLoading(R.drawable.img_syte_thumbnail_image_list)
                        .displayer(new RoundedBitmapDisplayer(dimenProfilePic))
                        .build();
                onMessageItemClick=MessageItemClick;
            }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_holder_my_chat, parent, false);
                return new ViewHolderMyChat(view);
            }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
            {
                ViewHolderMyChat viewHolder = (ViewHolderMyChat) holder;
                final Message yasPaseeChat = mMyChats.get(position);
                viewHolder.getmTvSyteName().setText(yasPaseeChat.getSyteName().toString().trim());
                viewHolder.getmTvMessage().setText(yasPaseeChat.getLastMessage().toString().trim());
                viewHolder.getmTvMsgDateTime().setText(StaticUtils.CONVERT_BULLETIN_DATE_TIME((long)yasPaseeChat.getLastMessageDateTime()));
                mDisplayImage(yasPaseeChat.getSyteImg(), viewHolder.getmIvSyteImage());
                RelativeLayout relativeLayout = viewHolder.getmRelLayVwHolderMessageNew();
                if(position%2==0)
                    {
                        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                else
                    {
                        relativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    }
                relativeLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                onMessageItemClick.onMessageItemClicked(yasPaseeChat);
                            }
                    });

                if(yasPaseeChat.getUnReadCount()<=0)
                    {
                        viewHolder.getmTvMsgUnreadCount().setVisibility(View.GONE);
                    }
                else
                    {
                        viewHolder.getmTvMsgUnreadCount().setVisibility(View.VISIBLE);
                        String count = yasPaseeChat.getUnReadCount()<=99 ? Integer.toString(yasPaseeChat.getUnReadCount()) : "99+";
                        viewHolder.getmTvMsgUnreadCount().setText(count);
                    }

            }
        @Override
        public int getItemCount()
            {
                return mMyChats.size();
            }
        private void mDisplayImage(String url, final ImageView mImageView)
            {
                String cURL = mCloudinary.url().transformation(cTransformation).generate(url);
                ImageLoader.getInstance().displayImage(cURL, mImageView, mDspImgOptions, new SimpleImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String imageUri, View view)
                    {
                       // progressBar.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                    {
                       // progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                    {
                        //progressBar.setVisibility(View.GONE);

                    }
                });
            }
    }
