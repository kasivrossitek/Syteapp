package com.syte.activities.sytedetailsponsor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.syte.R;
import com.syte.models.Followers;
import com.syte.utils.StaticUtils;

/**
 * Created by kumar.m on 17-03-2016.
 */
public class SyteFollowerDetailsActivity extends AppCompatActivity implements View.OnClickListener
    {
        private TextView mTvFollowerName,mTvFollowerGender;
        private ImageView mIvFollowerImage;

        private Bundle mBun;
        private Followers mFollower;
        private Cloudinary mCloudinary;
        private Transformation cTransformation;
        private DisplayImageOptions mDisImgOpt;

        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_syte_follower_details);
                mInItObjects();
                mInItWidgets();
                mSetValues();
            }//END onCreate()
        @Override
        protected void onDestroy()
            {
                super.onDestroy();

                mIvFollowerImage.setImageResource(0);
                mIvFollowerImage=null;
            }//END onDestroy()

        private void mInItObjects()
            {
                mBun=getIntent().getExtras();
                mFollower=mBun.getParcelable(StaticUtils.IPC_FOLLOWER);
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                cTransformation=new Transformation().width((int) (getResources().getDimension(R.dimen.profile_image_wd) / getResources().getDisplayMetrics().density))
                        .height((int) (getResources().getDimension(R.dimen.profile_image_ht) / getResources().getDisplayMetrics().density)).crop("crop").gravity("face");
                mDisImgOpt= new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageForEmptyUri(R.drawable.img_syte_user_no_image)
                        .showImageOnFail(R.drawable.img_syte_user_no_image)
                        .showImageOnLoading(R.drawable.img_syte_user_no_image)
                        .considerExifParams(true).build();
            }// END mInItObjects()

        private void mInItWidgets()
            {
                findViewById(R.id.xRelLayBack).setOnClickListener(this);
                mIvFollowerImage=(ImageView)findViewById(R.id.xIvFollowerImage);
                mTvFollowerName=(TextView)findViewById(R.id.xTvFollowerName);
                mTvFollowerGender=(TextView)findViewById(R.id.xTvFollowerGender);
            }// END mInItWidgets()

        private void mSetValues()
            {
                String cURL = mCloudinary.url().transformation(cTransformation).generate(mFollower.getUserProfilePic());
                ImageLoader.getInstance().displayImage(cURL, mIvFollowerImage, mDisImgOpt);
                mTvFollowerName.setText(mFollower.getUserName().toString().trim());
                mTvFollowerGender.setText(mFollower.getUserGender().toString().trim());
            }// END mSetValues()

        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.xRelLayBack:
                    {
                        finish();
                        break;
                    }

                    default:break;
                }
            }// END onClick()
    }
