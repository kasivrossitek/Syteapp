package com.syte.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.syte.models.Listcontacts;
import com.syte.models.PhoneContact;

import java.io.IOException;
import java.util.ArrayList;

public class YasPasPreferences {
    private static YasPasPreferences mYasPasPreference;
    private SharedPreferences mPref;
    private Editor mEditor;
    private String mFileName = "com.yaspas.preferences";

    public YasPasPreferences(Context context) {
        this.mPref = context.getSharedPreferences(mFileName, Activity.MODE_PRIVATE);
        this.mEditor = mPref.edit();
    }

    public static YasPasPreferences GET_INSTANCE(Context context) {
        if (mYasPasPreference == null)
            mYasPasPreference = new YasPasPreferences(context);
        return mYasPasPreference;
    }

    public void sSetRegisteredNum(String paramRegisteredNum) {
        mEditor.putString("prefRegisteredNum", paramRegisteredNum);
        mEditor.commit();
    }

    public String sGetRegisteredNum() {
        return mPref.getString("prefRegisteredNum", "");
    }

    public void sSetUserName(String paramUserName) {
        mEditor.putString("prefUserName", paramUserName);
        mEditor.commit();
    }

    public String sGetUserName() {
        return mPref.getString("prefUserName", "");
    }

    public void sSetUserGender(String paramUserGender) {
        mEditor.putString("prefUserGender", paramUserGender);
        mEditor.commit();
    }

    public String sGetUserGender() {
        return mPref.getString("prefUserGender", "");
    }

    public void sSetUserProfilePic(String paramUserProfilePic) {
        mEditor.putString("prefUserProfilePic", paramUserProfilePic);
        mEditor.commit();
    }

    public String sGetUserProfilePic() {
        return mPref.getString("prefUserProfilePic", "");
    }


    public void sSetLoginStatus(String paramLoginStatus) {
        mEditor.putString("prefLoginStatus", paramLoginStatus);
        mEditor.commit();
    }

    public String sGetLoginStatus() {
        return mPref.getString("prefLoginStatus", LoginStatus.OTP);
    }

    public void sSetFollowingFlag(boolean paramFollowingFlag) {
        mEditor.putBoolean("prefFollowingFlag", paramFollowingFlag);
        mEditor.commit();
    }

    public boolean sGetFollowingFlag() {
        return mPref.getBoolean("prefFollowingFlag", false);
    }

    //For GcmRegId
    public void sSetGCMToken(String val) {
        mEditor.putString("preGCMToken", val).commit();
    }

    public String sGetGCMToken() {
        return mPref.getString("preGCMToken", "");
    }

    public void sSetNotificationIconUpdate(boolean paramFollowingFlag) {
        mEditor.putBoolean("prefNotificationIconUpdate", paramFollowingFlag);
        mEditor.commit();
    }

    public boolean sGetNotificationIconUpdate() {
        return mPref.getBoolean("prefNotificationIconUpdate", false);
    }

    public void sSetNotificationOnOffStatus(boolean paramFollowingFlag) {
        mEditor.putBoolean("prefNotificationStatus", paramFollowingFlag).commit();
    }

    public boolean sGetNotificationOnOffStatus() {
        return mPref.getBoolean("prefNotificationStatus", true);
    }

    public void sSetFilterDistance(int paramDistance) {
        mEditor.putInt("prefFilterDistance", paramDistance).commit();
    }

    public int sGetFilterDistance() {
        return mPref.getInt("prefFilterDistance", 10);
    }


    public void sSetOnGoingChatId(String paramChatId) {
        mEditor.putString(StaticUtils.IPC_ONGOING_CHAT_ID, paramChatId);
        mEditor.commit();
    }

    public String sGetOnGoingChatId() {
        return mPref.getString(StaticUtils.IPC_ONGOING_CHAT_ID, "");
    }

    public void sSetOnGoingChat_SyteId(String paramChatSyteId) {
        mEditor.putString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID, paramChatSyteId);
        mEditor.commit();
    }

    public String sGetOnGoingChat_SyteId() {
        return mPref.getString(StaticUtils.IPC_ONGOING_CHAT_SYTE_ID, "");
    }


    public String sGetFeedbackImgUrl() {
        return mPref.getString("prefFeedbackImg", "");
    }


    public void sSetFeedbackImgUrl(String f_img) {
        mEditor.putString("prefFeedbackImg", f_img);
        mEditor.commit();
    }

    public void sSetSyteType(String syteType) {
        mEditor.putString(StaticUtils.IPC_SYTE_TYPE, syteType);
        mEditor.commit();
    }

    public String sGetSyteType() {

        return mPref.getString(StaticUtils.IPC_SYTE_NAME, "");
    }

    public void sSetSyteLat(String syteLat) {
        mEditor.putString(StaticUtils.IPC_SYTE_LOC_LAT, syteLat);
        mEditor.commit();
    }

    public String sGetSyteLat() {

        return mPref.getString(StaticUtils.IPC_SYTE_LOC_LAT, "12.958");
    }

    public void sSetSyteLongitude(String syteLong) {
        mEditor.putString(StaticUtils.IPC_SYTE_LOC_LONG, syteLong);
        mEditor.commit();
    }

    public String sGetSyteLongitude() {

        return mPref.getString(StaticUtils.IPC_SYTE_LOC_LONG, "77.646");
    }

    //for bulletin like position
    public void sSetbulletinposition(int pos) {
        mEditor.putInt(StaticUtils.IPC_VIEWPAGER_BULETIN_POS, pos);
        mEditor.commit();
    }

    public int sGetbulletinposition() {

        return mPref.getInt(StaticUtils.IPC_VIEWPAGER_BULETIN_POS, 0);
    }

    //for bulletin like boolean
    public void sSetlikeboolean(boolean flag) {
        mEditor.putBoolean(StaticUtils.IPC_BULETIN_BOOLEAN, flag);
        mEditor.commit();
    }

    public boolean sGetlikeboolean() {

        return mPref.getBoolean(StaticUtils.IPC_BULETIN_BOOLEAN, false);
    }

}
