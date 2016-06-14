package com.syte.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.listeners.OnHomeBulletinListener;
import com.syte.models.FilteredBulletinBoard;
import com.syte.utils.StaticUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by khalid.p on 31-03-2016.
 */
public class AdapterHomeBulletinBoard extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private ArrayList<FilteredBulletinBoard> filteredBulletinBoards;
        private Context context;
        private final int B_WITH_IMAGE = 1, B_WITHOUT_IMAGE = 2;
        private Cloudinary mCloudinary;
        private DisplayImageOptions mDisImgOpt;
        private long mSecond,mMinute,mHour,mDay;
        private OnHomeBulletinListener onHomeBulletinListener;
        public AdapterHomeBulletinBoard(Context pContext,ArrayList<FilteredBulletinBoard> pFilteredBulletinBoards,OnHomeBulletinListener pOnHomeBulletinListener)
            {
                this.context=pContext;
                this.filteredBulletinBoards=pFilteredBulletinBoards;
                this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                this.mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true).build();
                this.mSecond=1000;
                this.mMinute=mSecond*60;
                this.mHour=mMinute*60;
                this.mDay=mHour*24;
                this.onHomeBulletinListener=pOnHomeBulletinListener;
            }
        @Override
        public int getItemViewType(int position)
            {
                FilteredBulletinBoard filteredBulletinBoard = filteredBulletinBoards.get(position);
                if (filteredBulletinBoard.getBulletinImageUrl().toString().trim().length()>0)
                    {
                        return B_WITH_IMAGE;
                    }
                else
                    {
                        return B_WITHOUT_IMAGE;
                    }
            }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                RecyclerView.ViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                switch (viewType)
                    {
                        case B_WITH_IMAGE:
                            {
                                View v1 = inflater.inflate(R.layout.vw_holder_home_bulletin_with_img,parent,false);
                                viewHolder = new ViewHolderHomeBulletinWithImage(v1);
                                break;
                            }
                        case B_WITHOUT_IMAGE:
                            {
                                View v2 = inflater.inflate(R.layout.vw_holder_home_bulletin_without_img,parent,false);
                                viewHolder=new ViewHolderHomeBulletinWithoutImage(v2);
                                break;
                            }
                        default:
                            {
                                View v3 = inflater.inflate(R.layout.vw_holder_home_bulletin_without_img,parent,false);
                                viewHolder=new ViewHolderHomeBulletinWithoutImage(v3);
                                break;
                            }
                    }
                return viewHolder;
            }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
            {
                switch (holder.getItemViewType())
                    {
                        case B_WITH_IMAGE:
                            {
                                setB_WITH_IMAGE((ViewHolderHomeBulletinWithImage)holder,position);
                                break;
                            }
                        case B_WITHOUT_IMAGE:
                            {
                                setB_WITHOUT_IMAGE((ViewHolderHomeBulletinWithoutImage) holder, position);
                                break;
                            }
                        default:
                            {
                                setB_WITHOUT_IMAGE((ViewHolderHomeBulletinWithoutImage) holder, position);
                                break;
                            }
                    }
            }
        private void setB_WITH_IMAGE(ViewHolderHomeBulletinWithImage viewHolderHomeBulletinWithImage, int pos)
            {
                final FilteredBulletinBoard filteredBulletinBoard = filteredBulletinBoards.get(pos);
                viewHolderHomeBulletinWithImage.getmTvBulletinTitle().setText(filteredBulletinBoard.getBulletinSubject());
                viewHolderHomeBulletinWithImage.getmTvBulletinBody().setText(filteredBulletinBoard.getBulletinBody());
                viewHolderHomeBulletinWithImage.getmTvSyteName().setText(filteredBulletinBoard.getSyteName());
                viewHolderHomeBulletinWithImage.getmTvSyteAddress().setText(filteredBulletinBoard.getSyteAddress());
                if(filteredBulletinBoard.getIsFavourite())
                    {
                        viewHolderHomeBulletinWithImage.getmIvFavIcon().setVisibility(View.VISIBLE);
                    }
                else
                    {
                        viewHolderHomeBulletinWithImage.getmIvFavIcon().setVisibility(View.GONE);
                    }
                viewHolderHomeBulletinWithImage.getmTvBulletinDuration().setText(convertTime((long) filteredBulletinBoard.getDateTime()));
                viewHolderHomeBulletinWithImage.getmLinLayItem().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                onHomeBulletinListener.onHomeBulletinItemCliecked(filteredBulletinBoard);
                            }
                    });
                loadImage(viewHolderHomeBulletinWithImage.getmPbLoader(), viewHolderHomeBulletinWithImage.getmIvBulletinImage(), filteredBulletinBoard.getBulletinImageUrl());
            }
        private  void  setB_WITHOUT_IMAGE(ViewHolderHomeBulletinWithoutImage viewHolderHomeBulletinWithoutImage,int pos)
            {
                final FilteredBulletinBoard filteredBulletinBoard = filteredBulletinBoards.get(pos);
                viewHolderHomeBulletinWithoutImage.getmTvBulletinTitle().setText(filteredBulletinBoard.getBulletinSubject());
                viewHolderHomeBulletinWithoutImage.getmTvBulletinBody().setText(filteredBulletinBoard.getBulletinBody());
                viewHolderHomeBulletinWithoutImage.getmTvSyteName().setText(filteredBulletinBoard.getSyteName());
                viewHolderHomeBulletinWithoutImage.getmTvSyteAddress().setText(filteredBulletinBoard.getSyteAddress());
                if(filteredBulletinBoard.getIsFavourite())
                    {
                        viewHolderHomeBulletinWithoutImage.getmIvFavIcon().setVisibility(View.VISIBLE);
                    }
                else
                    {
                        viewHolderHomeBulletinWithoutImage.getmIvFavIcon().setVisibility(View.GONE);
                    }
                viewHolderHomeBulletinWithoutImage.getmTvBulletinDuration().setText(convertTime((long)filteredBulletinBoard.getDateTime()));
                viewHolderHomeBulletinWithoutImage.getmLinLayItem().setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                onHomeBulletinListener.onHomeBulletinItemCliecked(filteredBulletinBoard);
                            }
                    });
            }
        @Override
        public int getItemCount()
            {
                return filteredBulletinBoards.size();
            }
        private void loadImage(final ProgressBar progressBar,ImageView imageView,String imageUrl)
            {
                String cURL = mCloudinary.url().generate(imageUrl.trim());
                ImageLoader.getInstance().displayImage(cURL, imageView, new ImageLoadingListener()
                    {
                        @Override
                        public void onLoadingStarted(String imageUri, View view)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                        @Override
                        public void onLoadingCancelled(String imageUri, View view)
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                });
            }
        private String convertTime(long paramTime)
            {
               try
                    {
                        long convertedTime;
                        Calendar calLoc = Calendar.getInstance(TimeZone.getDefault());
                        Date mServerDate = StaticUtils.BULLETIN_TIME_FORMAT.parse(StaticUtils.CONVERT_BULLETIN_DATE_TIME(paramTime));
                        Date mLocalDate = StaticUtils.BULLETIN_TIME_FORMAT.parse(StaticUtils.CONVERT_BULLETIN_DATE_TIME(System.currentTimeMillis()));

                        long differenceTime = mLocalDate.getTime() - mServerDate.getTime();


                        if (differenceTime > mDay || differenceTime == mDay)
                        {
                            convertedTime = (differenceTime/mDay);

                            if(convertedTime>99)
                            {
                                convertedTime=99;
                                return String.valueOf(convertedTime)+"+d";
                            }
                            else
                                return String.valueOf(convertedTime)+"d";
                        }
                        else if (differenceTime > mHour || differenceTime == mHour)
                        {
                            convertedTime = (int)differenceTime/mHour;
                            return String.valueOf(convertedTime)+"h";
                        }
                        else if (differenceTime > mMinute || differenceTime == mMinute)
                        {
                            convertedTime = (int)differenceTime/mMinute;
                            return String.valueOf(convertedTime)+"m";
                        }
                        else if (differenceTime > mSecond || differenceTime == mSecond)
                        {
                            convertedTime = (int)differenceTime/mSecond;
                            return String.valueOf(convertedTime)+"s";
                        }
                        else
                        {
                            return "1s";
                        }

                    }
                catch (ParseException e)
                    {
                        e.printStackTrace();
                    }


                return "";



            }
    }
