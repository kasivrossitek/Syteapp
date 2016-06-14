package com.syte.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;
import com.syte.models.AboutUs;

/**
 * Created by khalid.p on 24-02-2016.
 */
public class SyteDetailAboutUsFragment extends Fragment
    {
        private View mRootView;
        private TextView mTvSyteAboutUsTitle,mTvSyteAboutUsDescription;
        private AboutUs aboutUs;
        private Bundle mBun;
        private RelativeLayout mRelLayNoDetails;
        public SyteDetailAboutUsFragment()
            {

            }
       @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                mRootView = inflater.inflate(R.layout.fragment_syte_detail_about_us, container, false);
                return mRootView;
            }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mInItObjects();
            mInItWidgets();
            mBun=getArguments();
            aboutUs=new AboutUs();
            aboutUs = mBun.getParcelable("abtus");
            sUpdateAboutUs(aboutUs);

            }// END onActivityCreated()
        private void mInItObjects()
            {

            }//END mInItObjects()
        private void mInItWidgets()
            {
                mTvSyteAboutUsTitle=(TextView)mRootView.findViewById(R.id.xTvSyteAboutUsTitle);
                mTvSyteAboutUsDescription=(TextView)mRootView.findViewById(R.id.xTvSyteAboutUsDescription);
                mRelLayNoDetails=(RelativeLayout)mRootView.findViewById(R.id.xRelLayNoDetails);
            }//END mInItWidgets()
        public void sUpdateAboutUs(AboutUs paramAboutUs)
            {
                if(paramAboutUs!=null) {
                    aboutUs = paramAboutUs;
                    if(aboutUs.getTitle().trim().length()<=0)
                        {
                            mRelLayNoDetails.setVisibility(View.VISIBLE);
                        }
                    else
                        {
                            mRelLayNoDetails.setVisibility(View.GONE);
                        mTvSyteAboutUsTitle.setText(paramAboutUs.getTitle());
                        mTvSyteAboutUsDescription.setText(paramAboutUs.getDescription());
                        }
                }
                else
                    {
                        mRelLayNoDetails.setVisibility(View.VISIBLE); // 6.0 issue
                    }
            }
    }
