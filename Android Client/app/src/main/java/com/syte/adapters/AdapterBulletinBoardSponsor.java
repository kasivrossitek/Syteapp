package com.syte.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.listeners.OnBulletinBoardUpdate;
import com.syte.models.BulletinBoard;
import com.syte.utils.StaticUtils;
import com.syte.widgets.EnlargeImageDialog;

import java.util.ArrayList;

/**
 * Created by khalid.p on 27-02-2016.
 */
public class AdapterBulletinBoardSponsor extends PagerAdapter
    {
        private Context mContext;
        //private Transformation cTransformation;
        private Cloudinary mCloudinary;
        private DisplayImageOptions mDisImgOpt;
        private LayoutInflater mLayoutInflater;
        private ArrayList<BulletinBoard> mBulletinBoards;
        private ArrayList<String> mBulletinBoardsIds;
        private OnBulletinBoardUpdate onBulletinBoardUpdate;
        public AdapterBulletinBoardSponsor(Context paramCon,ArrayList<BulletinBoard> paramBulletinBoards,ArrayList<String> paramBulletinBoardIds, OnBulletinBoardUpdate paramOnBulletinBoardUpdate)
            {
                mContext=paramCon;
                mLayoutInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mBulletinBoards=paramBulletinBoards;
                mBulletinBoardsIds=paramBulletinBoardIds;
                //cTransformation=new Transformation().width(1202).height(676).crop("scale").quality(100);
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true).build();
                onBulletinBoardUpdate=paramOnBulletinBoardUpdate;
            }
        @Override
        public int getCount()
            {
                return mBulletinBoards.size();
            }
        @Override
        public boolean isViewFromObject(View view, Object object)
            {
                return view.equals(object);
            }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader)
            {
            }
        @Override
        public Parcelable saveState()
            {
                return null;
            }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
            {
                container.removeView((View) object);
            }
        @Override
        public Object instantiateItem(ViewGroup container, final int position)
            {
                View itemView;
                TextView mTvBulletinDay,mTvBulletinMonth,mTvBulletinSubject,mTvBulletinBody,mTvBulletinReadMore;
                RelativeLayout mRelLayBulletinEdit,mRelLayBulletinDelete;

                final BulletinBoard bulletinBoard = mBulletinBoards.get(position);
                if(bulletinBoard.getImageUrl().toString().trim().length()>0)
                    {
                        itemView = mLayoutInflater.inflate(R.layout.vw_holder_bulletin_sponsor_with_img, null);
                        ImageView mIvBulletinImage=(ImageView)itemView.findViewById(R.id.xIvBulletinImage);
                        final ProgressBar mPbLoader=(ProgressBar)itemView.findViewById(R.id.xPbLoader);
                        String cURL = mCloudinary.url().generate(bulletinBoard.getImageUrl().toString().trim());
                        ImageLoader.getInstance().displayImage(cURL, mIvBulletinImage, mDisImgOpt, new ImageLoadingListener()
                            {
                                @Override
                                public void onLoadingStarted(String imageUri, View view)
                                    {
                                        mPbLoader.setVisibility(View.VISIBLE);
                                    }
                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                                    {
                                        //mPbLoader.setVisibility(View.GONE);
                                    }
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                                    {
                                        mPbLoader.setVisibility(View.GONE);
                                    }
                                @Override
                                public void onLoadingCancelled(String imageUri, View view)
                                    {
                                        //mPbLoader.setVisibility(View.GONE);
                                    }
                            });
                        mIvBulletinImage.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                    {
                                        if(bulletinBoard.getImageUrl().toString().trim().length()>0)
                                            {
                                                EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(mContext,bulletinBoard.getImageUrl().toString().trim());
                                                enlargeImageDialog.sShowEnlargeImageDialog();
                                            }
                                    }
                            }); // END OnClickListener()
                    }
                else
                    {
                        itemView = mLayoutInflater.inflate(R.layout.vw_holder_bulletin_sponsor_without_img, null);
                    }
                mTvBulletinDay=(TextView)itemView.findViewById(R.id.xTvBulletinDay);
                mTvBulletinMonth=(TextView)itemView.findViewById(R.id.xTvBulletinMonth);
                mTvBulletinSubject=(TextView)itemView.findViewById(R.id.xTvBulletinSubject);
                mTvBulletinBody=(TextView)itemView.findViewById(R.id.xTvBulletinBody);
                mTvBulletinReadMore=(TextView)itemView.findViewById(R.id.xTvBulletinReadMore);
                mRelLayBulletinEdit=(RelativeLayout)itemView.findViewById(R.id.xRelLayBulletinEdit);
                mRelLayBulletinEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBulletinBoardUpdate.onBulletinEdit(mBulletinBoardsIds.get(position),mBulletinBoards.get(position));
                    }
                });
                mRelLayBulletinDelete=(RelativeLayout)itemView.findViewById(R.id.xRelLayBulletinDelete);
                mRelLayBulletinDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBulletinBoardUpdate.onBulletinDelete(mBulletinBoardsIds.get(position));
                    }
                });
                //Setting Values
                mTvBulletinDay.setText(StaticUtils.GET_DAY_FROM_MILLISECONDS((long)bulletinBoard.getDateTime()));
                mTvBulletinMonth.setText(StaticUtils.GET_MONTH_FROM_MILLISECONDS((long) bulletinBoard.getDateTime()).toString().toUpperCase());
                mTvBulletinSubject.setText(bulletinBoard.getSubject());
                mTvBulletinBody.setText(bulletinBoard.getBody());
                mTvBulletinReadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBulletinBoardUpdate.onBulletinReadMore(position);
                    }
                });
                ((ViewPager) container).addView(itemView, 0);
                // Return the page
                return itemView;
            }
    }// END AdapterBulletinBoardSponsor()
