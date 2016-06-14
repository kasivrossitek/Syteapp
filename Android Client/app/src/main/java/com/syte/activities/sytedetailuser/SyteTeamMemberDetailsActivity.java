package com.syte.activities.sytedetailuser;

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
import com.syte.models.YasPasTeam;
import com.syte.utils.StaticUtils;
import com.syte.widgets.EnlargeImageDialog;

/**
 * Created by kumar.m on 14-03-2016.
 */
public class SyteTeamMemberDetailsActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView mTvTmName,mTvTmDesignation,mTvTmProfile;
    private ImageView mIvTeamMemberImage;

    private Bundle mBun;
    private YasPasTeam yasPasTeam;
    private Cloudinary mCloudinary;
    private Transformation cTransformation;
    private DisplayImageOptions mDisImgOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_syte_team_member_details);

            mInItObjects();
            mInItWidgets();
            mSetValues();
        }// END onCreate()

    @Override
    protected void onDestroy()
        {
            super.onDestroy();

            mIvTeamMemberImage.setImageResource(0);
            mIvTeamMemberImage=null;
        }//END onDestroy()

    private void mInItObjects()
        {
            mBun=getIntent().getExtras();
            yasPasTeam=mBun.getParcelable(StaticUtils.IPC_TEAM_MEMBER);
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
            mIvTeamMemberImage=(ImageView)findViewById(R.id.xIvTeamMemberImage);
            mIvTeamMemberImage.setOnClickListener(this);
            mTvTmName=(TextView)findViewById(R.id.xTvTmName);
            mTvTmDesignation=(TextView)findViewById(R.id.xTvTmDesignation);
            mTvTmProfile=(TextView)findViewById(R.id.xTvTmProfile);
        }// END mInItWidgets()

    private void mSetValues()
        {
            //String cURL = mCloudinary.url().transformation().imageTag("wa7bypgcc5hcoiquuv5g.jpg");//mCloudinary.url().transformation(cTransformation).generate(yasPasTeam.getProfilePic());
            String cURL = mCloudinary.url().transformation(cTransformation).generate(yasPasTeam.getProfilePic());
            ImageLoader.getInstance().displayImage(cURL, mIvTeamMemberImage, mDisImgOpt);

            mTvTmName.setText(yasPasTeam.getName().toString().trim());
            mTvTmDesignation.setText(yasPasTeam.getDesignation().toString().trim());
            mTvTmProfile.setText(yasPasTeam.getAbout().toString().trim());
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
                    case R.id.xIvTeamMemberImage:
                        {
                            if(yasPasTeam.getProfilePic().trim().length()>0)
                                {
                                    EnlargeImageDialog enlargeImageDialog = EnlargeImageDialog.CREATE_ENLARGE_IMAGE_DIALOG(this,yasPasTeam.getProfilePic());
                                    enlargeImageDialog.sShowEnlargeImageDialog();
                                }
                            break;
                        }

                    default:break;
                }
        }// END onClick()
}
