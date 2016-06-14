package com.syte.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;
import com.syte.models.WhatWeDo;

/**
 * Created by khalid.p on 24-02-2016.
 */
public class SyteDetailWhatWeDoFragment extends Fragment
    {

        private View mRootView;
        private TextView mTvSyteWwdTitle,mTvSyteWwdDescription;
        private RelativeLayout mRelLayNoDetails;
        public SyteDetailWhatWeDoFragment(){}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            mRootView = inflater.inflate(R.layout.fragment_syte_detail_what_we_do, container, false);
            return mRootView;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            mInItObjects();
            mInItWidgets();
        }// END onActivityCreated()
        private void mInItObjects(){}//END mInItObjects()
        private void mInItWidgets()
        {
            mTvSyteWwdTitle=(TextView)mRootView.findViewById(R.id.xTvSyteWwdTitle);
            mTvSyteWwdDescription=(TextView)mRootView.findViewById(R.id.xTvSyteWwdDescription);
            mRelLayNoDetails=(RelativeLayout)mRootView.findViewById(R.id.xRelLayNoDetails);
        }//END mInItWidgets()
        public void sUpdateWhatWeDo(WhatWeDo paramWwd)
        {
            if(paramWwd!=null)
                {
                    if(paramWwd.getTitle().trim().length()<=0)
                        {
                            mRelLayNoDetails.setVisibility(View.VISIBLE);
                        }
                    else {
                        mRelLayNoDetails.setVisibility(View.GONE);
                        mTvSyteWwdTitle.setText(paramWwd.getTitle());
                        mTvSyteWwdDescription.setText(paramWwd.getDescription());
                    }
                }
            else
                {
                    mRelLayNoDetails.setVisibility(View.VISIBLE);
                }
        }

    }
