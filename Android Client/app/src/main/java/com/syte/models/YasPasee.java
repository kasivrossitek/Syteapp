package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 30-12-2015.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class YasPasee {
    private String mRegisteredNum,userName,userGender,userProfilePic;
    public YasPasee()
        {
        }
    public void setRegisteredNum(String mMobile_No) {
        this.mRegisteredNum = mMobile_No;
    }
    public String  getRegisteredNum() {
        return mRegisteredNum;
    }
    public void setUserName(String paramUserName){
        this.userName=paramUserName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserGender(String paramGender){this.userGender=paramGender;}
    public String getUserGender(){return  userGender;}
    public void setUserProfilePic(String paramUserProfilePic)
        {
            this.userProfilePic=paramUserProfilePic;
        }
    public String getUserProfilePic()
        {
            return this.userProfilePic;
        }
}
