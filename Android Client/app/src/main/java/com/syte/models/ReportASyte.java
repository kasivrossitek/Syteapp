package com.syte.models;

/**
 * Created by Developer on 6/22/2016.
 */
public class ReportASyte {
    String issuetype, title, description, imageUrl, mRegisteredNum, userName;
    Object dateTime;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Object getDateTime() {
        return dateTime;
    }

    public void setDateTime(Object dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getmRegisteredNum() {
        return mRegisteredNum;
    }

    public void setmRegisteredNum(String mRegisteredNum) {
        this.mRegisteredNum = mRegisteredNum;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(String issuetype) {
        this.issuetype = issuetype;
    }

    public ReportASyte() {

    }
}
