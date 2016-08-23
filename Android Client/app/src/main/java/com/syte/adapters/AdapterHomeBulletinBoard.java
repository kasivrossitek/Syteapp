package com.syte.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.listeners.OnHomeBulletinListener;
import com.syte.models.FilteredBulletinBoard;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by khalid.p on 31-03-2016.
 */
public class AdapterHomeBulletinBoard extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FilteredBulletinBoard> filteredBulletinBoards;
    private Context context;
    private final int B_WITH_IMAGE = 1, B_WITHOUT_IMAGE = 2;
    private Cloudinary mCloudinary;
    private DisplayImageOptions mDisImgOpt;
    private long mSecond, mMinute, mHour, mDay;
    private OnHomeBulletinListener onHomeBulletinListener;
    YasPasPreferences mYaspaspreferences;

    public AdapterHomeBulletinBoard(Context pContext, ArrayList<FilteredBulletinBoard> pFilteredBulletinBoards, OnHomeBulletinListener pOnHomeBulletinListener) {
        this.context = pContext;
        mYaspaspreferences = new YasPasPreferences(context);
        this.filteredBulletinBoards = pFilteredBulletinBoards;
        this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
        this.mDisImgOpt = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();
        this.mSecond = 1000;
        this.mMinute = mSecond * 60;
        this.mHour = mMinute * 60;
        this.mDay = mHour * 24;
        this.onHomeBulletinListener = pOnHomeBulletinListener;
    }

    @Override
    public int getItemViewType(int position) {
        FilteredBulletinBoard filteredBulletinBoard = filteredBulletinBoards.get(position);
        if (filteredBulletinBoard.getBulletinImageUrl().toString().trim().length() > 0) {
            return B_WITH_IMAGE;
        } else {
            return B_WITHOUT_IMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case B_WITH_IMAGE: {
                View v1 = inflater.inflate(R.layout.vw_holder_home_bulletin_with_img, parent, false);
                viewHolder = new ViewHolderHomeBulletinWithImage(v1);
                break;
            }
            case B_WITHOUT_IMAGE: {
                View v2 = inflater.inflate(R.layout.vw_holder_home_bulletin_without_img, parent, false);
                viewHolder = new ViewHolderHomeBulletinWithoutImage(v2);
                break;
            }
            default: {
                View v3 = inflater.inflate(R.layout.vw_holder_home_bulletin_without_img, parent, false);
                viewHolder = new ViewHolderHomeBulletinWithoutImage(v3);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case B_WITH_IMAGE: {
                setB_WITH_IMAGE((ViewHolderHomeBulletinWithImage) holder, position);
                break;
            }
            case B_WITHOUT_IMAGE: {
                setB_WITHOUT_IMAGE((ViewHolderHomeBulletinWithoutImage) holder, position);
                break;
            }
            default: {
                setB_WITHOUT_IMAGE((ViewHolderHomeBulletinWithoutImage) holder, position);
                break;
            }
        }
    }

    private void setB_WITH_IMAGE(final ViewHolderHomeBulletinWithImage viewHolderHomeBulletinWithImage, final int pos) {
        final FilteredBulletinBoard filteredBulletinBoard = filteredBulletinBoards.get(pos);
        viewHolderHomeBulletinWithImage.getmTvBulletinTitle().setText(filteredBulletinBoard.getBulletinSubject());
        viewHolderHomeBulletinWithImage.getmTvBulletinBody().setText(filteredBulletinBoard.getBulletinBody());
        viewHolderHomeBulletinWithImage.getmTvSyteName().setText(filteredBulletinBoard.getSyteName());
        viewHolderHomeBulletinWithImage.getmTvSyteAddress().setText(filteredBulletinBoard.getSyteAddress());

        if (filteredBulletinBoard.getIsFavourite()) {
            viewHolderHomeBulletinWithImage.getmIvFavIcon().setVisibility(View.VISIBLE);
        } else {
            viewHolderHomeBulletinWithImage.getmIvFavIcon().setVisibility(View.GONE);
        }
        viewHolderHomeBulletinWithImage.getmTvBulletinDuration().setText(convertTime((long) filteredBulletinBoard.getDateTime()));
        viewHolderHomeBulletinWithImage.getmLinLayItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeBulletinListener.onHomeBulletinItemCliecked(filteredBulletinBoard);
            }
        });
        loadImage(viewHolderHomeBulletinWithImage.getmPbLoader(), viewHolderHomeBulletinWithImage.getmIvBulletinImage(), filteredBulletinBoard.getBulletinImageUrl());
        if (filteredBulletinBoard.isLiked()) {
            viewHolderHomeBulletinWithImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_active_grey);
        } else {
            viewHolderHomeBulletinWithImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_in_active_grey);
        }
        viewHolderHomeBulletinWithImage.getmIvLikeIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filteredBulletinBoard.isLiked()) {
                    bulletinUpdateLikes(pos);
                    filteredBulletinBoard.setLiked(true);
                    viewHolderHomeBulletinWithImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_active_grey);
                    mAddRewardPoints(StaticUtils.REWARD_POINTS_LIKE_BULLETIN);
                } else {
                    filteredBulletinBoard.setLiked(false);
                    bulletinRemoveLike(pos);
                    viewHolderHomeBulletinWithImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_in_active_grey);
                    mAddRewardPoints(StaticUtils.REWARD_POINTS_UNLIKE_BULLETIN);
                }
            }
        });
    }

    private void setB_WITHOUT_IMAGE(final ViewHolderHomeBulletinWithoutImage viewHolderHomeBulletinWithoutImage, final int pos) {
        final FilteredBulletinBoard filteredBulletinBoard = filteredBulletinBoards.get(pos);
        viewHolderHomeBulletinWithoutImage.getmTvBulletinTitle().setText(filteredBulletinBoard.getBulletinSubject());
        viewHolderHomeBulletinWithoutImage.getmTvBulletinBody().setText(filteredBulletinBoard.getBulletinBody());
        viewHolderHomeBulletinWithoutImage.getmTvSyteName().setText(filteredBulletinBoard.getSyteName());
        viewHolderHomeBulletinWithoutImage.getmTvSyteAddress().setText(filteredBulletinBoard.getSyteAddress());

        if (filteredBulletinBoard.getIsFavourite()) {
            viewHolderHomeBulletinWithoutImage.getmIvFavIcon().setVisibility(View.VISIBLE);
        } else {
            viewHolderHomeBulletinWithoutImage.getmIvFavIcon().setVisibility(View.GONE);
        }
        viewHolderHomeBulletinWithoutImage.getmTvBulletinDuration().setText(convertTime((long) filteredBulletinBoard.getDateTime()));
        viewHolderHomeBulletinWithoutImage.getmLinLayItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeBulletinListener.onHomeBulletinItemCliecked(filteredBulletinBoard);
            }
        });
        if (filteredBulletinBoard.isLiked()) {
            viewHolderHomeBulletinWithoutImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_active_grey);
        } else {
            viewHolderHomeBulletinWithoutImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_in_active_grey);
        }
        viewHolderHomeBulletinWithoutImage.getmIvLikeIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filteredBulletinBoard.isLiked()) {
                    bulletinUpdateLikes(pos);

                    filteredBulletinBoard.setLiked(true);
                    viewHolderHomeBulletinWithoutImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_active_grey);
                    mAddRewardPoints(StaticUtils.REWARD_POINTS_LIKE_BULLETIN);
                } else {
                    filteredBulletinBoard.setLiked(false);
                    bulletinRemoveLike(pos);
                    viewHolderHomeBulletinWithoutImage.getmIvLikeIcon().setImageResource(R.drawable.ic_like_in_active_grey);
                    mAddRewardPoints(StaticUtils.REWARD_POINTS_UNLIKE_BULLETIN);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredBulletinBoards.size();
    }

    public void bulletinUpdateLikes(int pos) {
        HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
        mUpdateMap.put("registeredNum", mYaspaspreferences.sGetRegisteredNum());
        Firebase mFBLikes = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(filteredBulletinBoards.get(pos).getSyteId()).child(filteredBulletinBoards.get(pos).getBulletinId()).child("Likes").child(mYaspaspreferences.sGetRegisteredNum());

        mFBLikes.setValue(mUpdateMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {


                }
            }
        });
    }

    public void bulletinRemoveLike(int pos) {

        Firebase mFBremoveLikes = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(filteredBulletinBoards.get(pos).getSyteId()).child(filteredBulletinBoards.get(pos).getBulletinId()).child("Likes");

        mFBremoveLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                        if (dataSnapshot1.getKey().equalsIgnoreCase(mYaspaspreferences.sGetRegisteredNum())) {
                            dataSnapshot1.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void mAddRewardPoints(final int rewardPoints) {

        Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYaspaspreferences.sGetRegisteredNum());
        mFireBsYasPasObj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot != null) {
                    if (dataSnapshot.hasChild("rewards")) {
                        Long rewards = (Long) dataSnapshot.child("rewards").getValue();
                        int totalrewards = rewards.intValue();
                        totalrewards = totalrewards + rewardPoints;
                        StaticUtils.addReward(mYaspaspreferences.sGetRegisteredNum(), totalrewards);
                    } else {
                        StaticUtils.addReward(mYaspaspreferences.sGetRegisteredNum(), rewardPoints);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }//END REWARDS
    private void loadImage(final ProgressBar progressBar, ImageView imageView, String imageUrl) {

        imageView.setImageResource(android.R.color.transparent);
        //String cURL = mCloudinary.url().generate(imageUrl.trim());
        String cURL = mCloudinary.url().cdnSubdomain(true).transformation(new Transformation().width(0.5).crop("scale")).generate(imageUrl.trim());
        Glide.with(this.context).load(cURL).into(imageView);
        progressBar.setVisibility(View.GONE);
//
//                ImageLoader.getInstance().displayImage(cURL, imageView, new ImageLoadingListener()
//                    {
//                        @Override
//                        public void onLoadingStarted(String imageUri, View view)
//                            {
//                                progressBar.setVisibility(View.VISIBLE);
//                            }
//                        @Override
//                        public void onLoadingFailed(String imageUri, View view, FailReason failReason)
//                            {
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
//                            {
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        @Override
//                        public void onLoadingCancelled(String imageUri, View view)
//                            {
//                                progressBar.setVisibility(View.GONE);
//                            }
//                });

    }

    private String convertTime(long paramTime) {
        try {
            long convertedTime;
            Calendar calLoc = Calendar.getInstance(TimeZone.getDefault());
            Date mServerDate = StaticUtils.BULLETIN_TIME_FORMAT.parse(StaticUtils.CONVERT_BULLETIN_DATE_TIME(paramTime));
            Date mLocalDate = StaticUtils.BULLETIN_TIME_FORMAT.parse(StaticUtils.CONVERT_BULLETIN_DATE_TIME(System.currentTimeMillis()));

            long differenceTime = mLocalDate.getTime() - mServerDate.getTime();


            if (differenceTime > mDay || differenceTime == mDay) {
                convertedTime = (differenceTime / mDay);

                if (convertedTime > 99) {
                    convertedTime = 99;
                    return String.valueOf(convertedTime) + "+d";
                } else
                    return String.valueOf(convertedTime) + "d";
            } else if (differenceTime > mHour || differenceTime == mHour) {
                convertedTime = (int) differenceTime / mHour;
                return String.valueOf(convertedTime) + "h";
            } else if (differenceTime > mMinute || differenceTime == mMinute) {
                convertedTime = (int) differenceTime / mMinute;
                return String.valueOf(convertedTime) + "m";
            } else if (differenceTime > mSecond || differenceTime == mSecond) {
                convertedTime = (int) differenceTime / mSecond;
                return String.valueOf(convertedTime) + "s";
            } else {
                return "1s";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "";


    }
}
