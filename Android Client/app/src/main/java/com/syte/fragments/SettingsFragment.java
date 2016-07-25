package com.syte.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syte.BuildConfig;
import com.syte.R;
import com.syte.activities.FeedbackActivity;
import com.syte.activities.HomeActivity;
import com.syte.activities.ReportABugActivity;

import com.syte.activities.ReportASyteActivity;
import com.syte.activities.walkthrough.WalkThroughSettingsActivity;

import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.ReportASyte;
import com.syte.models.ReportBug;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private View mRootView;
    private TextView mTvAppVersion;
    private YasPasPreferences mPref;
    private CheckBox mCbNotificationOnOff;
    private ImageView mClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return mRootView;
    }//END onCreateView()

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLaySendFeedback: {
                showFeedbackDialog();
                   /* Intent intentReportABugActivity = new Intent(getActivity(), ReportABugActivity.class);
                    startActivity(intentReportABugActivity);*/

                break;
            }

            case R.id.xRelLayWalkthrough: {
                Intent intentWalkThroughSettingsActivity = new Intent(getActivity(), WalkThroughSettingsActivity.class);
                startActivity(intentWalkThroughSettingsActivity);
                break;
            }


            default:
                break;
        }
    }//END onClick()

    private void showFeedbackDialog() {
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.custom_feedback_dialog);
        mDialog.setCancelable(false);
        mDialog.show();
        final Window window = mDialog.getWindow();
        mClose = (ImageView) window.findViewById(R.id.xvClose);
        LinearLayout mLinLayNewfeature = (LinearLayout) window.findViewById(R.id.xLinLayNewfeature);
        LinearLayout mLinLaySuggestfeature = (LinearLayout) window.findViewById(R.id.xLinLaySuggestfeature);
        LinearLayout mLinLayReportbug = (LinearLayout) window.findViewById(R.id.xLinLayReportbug);
        LinearLayout mLinLayComment = (LinearLayout) window.findViewById(R.id.xLinLayComment);
        LinearLayout mLinLayCompliment = (LinearLayout) window.findViewById(R.id.xLinLayCompliment);
        LinearLayout mLinLayOther = (LinearLayout) window.findViewById(R.id.xLinLayOther);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mLinLayNewfeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                intent.putExtra(StaticUtils.FEED_BACK_NAME, getResources().getString(R.string.feedback_new_feature_lbl_txt));
                startActivity(intent);
            }
        });
        mLinLaySuggestfeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                intent.putExtra(StaticUtils.FEED_BACK_NAME, getResources().getString(R.string.feedback_suggest_feature_lbl_txt));
                startActivity(intent);
            }
        });
        mLinLayReportbug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), ReportABugActivity.class);
                startActivity(intent);
            }
        });

        mLinLayComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                intent.putExtra(StaticUtils.FEED_BACK_NAME, getResources().getString(R.string.feedback_Comment_lbl_txt));
                startActivity(intent);
            }
        });
        mLinLayCompliment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                intent.putExtra(StaticUtils.FEED_BACK_NAME, getResources().getString(R.string.feedback_Compliment_lbl_txt));
                startActivity(intent);
            }
        });
        mLinLayOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                intent.putExtra(StaticUtils.FEED_BACK_NAME, getResources().getString(R.string.feedback_Other_lbl_txt));
                startActivity(intent);
            }
        });
    }// END showFeedbackDialog()

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPref.sSetNotificationOnOffStatus(isChecked);
        mCbNotificationOnOff.setText(isChecked ? getString(R.string.settings_notification_txt_on) : getString(R.string.settings_notification_txt_off));
        mCbNotificationOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, isChecked ? R.drawable.ic_settings_notification_on : R.drawable.ic_settings_notification_off, 0);
    }//END onCheckedChanged()


}