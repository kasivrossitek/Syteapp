package com.syte.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.syte.BuildConfig;
import com.syte.R;
import com.syte.activities.ReportABugActivity;

import com.syte.activities.walkthrough.WalkThroughSettingsActivity;

import com.syte.utils.YasPasPreferences;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private View mRootView;
    private TextView mTvAppVersion;
    private YasPasPreferences mPref;
    private CheckBox mCbNotificationOnOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
            return mRootView;
        }//END onCreateView()

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            mPref = YasPasPreferences.GET_INSTANCE(getActivity());
            mRootView.findViewById(R.id.xRelLaySendFeedback).setOnClickListener(this);

            mRootView.findViewById(R.id.xRelLayWalkthrough).setOnClickListener(this);

            mTvAppVersion = (TextView) mRootView.findViewById(R.id.xTvAppVersion);
            mCbNotificationOnOff = (CheckBox) mRootView.findViewById(R.id.xCbNotificationOnOff);
            mCbNotificationOnOff.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf"));
            mCbNotificationOnOff.setOnCheckedChangeListener(this);
            mCbNotificationOnOff.setChecked(mPref.sGetNotificationOnOffStatus());
            mTvAppVersion.setText("Version: " + BuildConfig.VERSION_NAME);
        }//END onActivityCreated()

    @Override
    public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.xRelLaySendFeedback:
                {
                    Intent intentReportABugActivity = new Intent(getActivity(), ReportABugActivity.class);
                    startActivity(intentReportABugActivity);
                    break;
                }

                case R.id.xRelLayWalkthrough:
                {
                    Intent intentWalkThroughSettingsActivity = new Intent(getActivity(), WalkThroughSettingsActivity.class);
                    startActivity(intentWalkThroughSettingsActivity);
                    break;
                }


                default:break;
            }
        }//END onClick()

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            mPref.sSetNotificationOnOffStatus(isChecked);
            mCbNotificationOnOff.setText(isChecked ? getString(R.string.settings_notification_txt_on) : getString(R.string.settings_notification_txt_off));
            mCbNotificationOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, isChecked ? R.drawable.ic_settings_notification_on : R.drawable.ic_settings_notification_off, 0);
        }//END onCheckedChanged()
}