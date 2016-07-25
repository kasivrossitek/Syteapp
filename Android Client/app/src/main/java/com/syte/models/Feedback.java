package com.syte.models;

/**
 * Created by Developer on 6/25/2016.
 */
public class Feedback {
    String  feedbackType,description, imageUrl, mRegisteredNum, userName, deviceModel, deviceModelVersion, appVersionName, appBuildversion;
    Object dateTime;

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceModelVersion() {
        return deviceModelVersion;
    }

    public void setDeviceModelVersion(String deviceModelVersion) {
        this.deviceModelVersion = deviceModelVersion;
    }

    public String getAppBuildversion() {
        return appBuildversion;
    }

    public void setAppBuildversion(String appBuildversion) {
        this.appBuildversion = appBuildversion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getmRegisteredNum() {
        return mRegisteredNum;
    }

    public void setmRegisteredNum(String mRegisteredNum) {
        this.mRegisteredNum = mRegisteredNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public Object getDateTime() {
        return dateTime;
    }

    public void setDateTime(Object dateTime) {
        this.dateTime = dateTime;
    }

    public Feedback() {

    }
}
