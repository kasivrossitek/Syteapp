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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.syte.R;
import com.syte.listeners.OnBulletinBoardUpdate;
import com.syte.models.BulletinBoard;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.EnlargeImageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 29-02-2016.
 */
public class AdapterBulletinBoardUser extends PagerAdapter
    {
        private Context mContext;
        //private Transformation cTransformation;
        private Cloudinary mCloudinary;
        private DisplayImageOptions mDisImgOpt;
        private LayoutInflater mLayoutInflater;
        private ArrayList<BulletinBoard> mBulletinBoards;
        private ArrayList<String> mBulletinBoardsIds;
        private OnBulletinBoardUpdate onBulletinBoardUpdate;
        private YasPasPreferences mYaspasPreference;
        private String syteId;

        public AdapterBulletinBoardUser(Context paramCon, ArrayList<BulletinBoard> paramBulletinBoards, OnBulletinBoardUpdate paramOnBulletinBoardUpdate, ArrayList<String> bulletinBoardsIds, String mSyteId)
            {
                mContext=paramCon;
                mLayoutInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mYaspasPreference = YasPasPreferences.GET_INSTANCE(paramCon);
                mBulletinBoards=paramBulletinBoards;
                //cTransformation=new Transformation().width(1202).height(676).crop("scale").quality(100);
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true).build();
                onBulletinBoardUpdate=paramOnBulletinBoardUpdate;
                mBulletinBoardsIds=bulletinBoardsIds;
                syteId = mSyteId;
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
                RelativeLayout mRelLayBulletinLike;
                final ImageView mIvlike;
                final BulletinBoard bulletinBoard = mBulletinBoards.get(position);
                if(bulletinBoard.getImageUrl().toString().trim().length()>0)
                {
                    itemView = mLayoutInflater.inflate(R.layout.vw_holder_bulletin_user_with_img, null);
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
                                mPbLoader.setVisibility(View.GONE);
                            }
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                            {
                                mPbLoader.setVisibility(View.GONE);
                            }
                            @Override
                            public void onLoadingCancelled(String imageUri, View view)
                            {
                                mPbLoader.setVisibility(View.GONE);
                            }
                        });
                    mIvBulletinImage.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                                {
                                    if (bulletinBoard.getImageUrl().toString().trim().length() > 0)
                                        {
                                            EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(mContext, bulletinBoard.getImageUrl().toString().trim());
                                            enlargeImageDialog.sShowEnlargeImageDialog();
                                        }
                                }
                        }); // END OnClickListener()
                }
                else
                {
                    itemView = mLayoutInflater.inflate(R.layout.vw_holder_bulletin_user_without_img, null);
                }
                mTvBulletinDay=(TextView)itemView.findViewById(R.id.xTvBulletinDay);
                mTvBulletinMonth=(TextView)itemView.findViewById(R.id.xTvBulletinMonth);
                mTvBulletinSubject=(TextView)itemView.findViewById(R.id.xTvBulletinSubject);
                mTvBulletinBody=(TextView)itemView.findViewById(R.id.xTvBulletinBody);
                mTvBulletinReadMore=(TextView)itemView.findViewById(R.id.xTvBulletinReadMore);
                mRelLayBulletinLike=(RelativeLayout)itemView.findViewById(R.id.xRelLayBulletinLike);
                mIvlike=(ImageView)itemView.findViewById(R.id.xIvlike);
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
                mRelLayBulletinLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mBulletinBoards.get(position).isLiked()) {
                            mBulletinBoards.get(position).setLiked(true);
                            mIvlike.setImageResource(R.drawable.ic_like_active_grey);
                            bulletinUpdateLikes(mBulletinBoardsIds.get(position));
                        } else {
                            mBulletinBoards.get(position).setLiked(false);
                            mIvlike.setImageResource(R.drawable.ic_like_in_active_grey);
                            bulletinRemoveLike(mBulletinBoardsIds.get(position));
                        }
                    }
                });
                if (bulletinBoard.isLiked()) {
                    mIvlike.setImageResource(R.drawable.ic_like_active_grey);
                } else {
                    mIvlike.setImageResource(R.drawable.ic_like_in_active_grey);
                }
                ((ViewPager) container).addView(itemView, 0);
                // Return the page
                return itemView;
        }

        public void bulletinUpdateLikes(String bullletinid) {
            HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
            mUpdateMap.put("registeredNum", mYaspasPreference.sGetRegisteredNum());
            Firebase mFBLikes = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(syteId).child(bullletinid).child("Likes").child(mYaspasPreference.sGetRegisteredNum());

            mFBLikes.setValue(mUpdateMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {


                    }
                }
            });
        }

        public void bulletinRemoveLike(String bullletinid) {

            Firebase mFBremoveLikes = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(syteId).child(bullletinid).child("Likes");

            mFBremoveLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                            if (dataSnapshot1.getKey().equalsIgnoreCase(mYaspasPreference.sGetRegisteredNum())) {
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
}// END AdapterBulletinBoardUser()