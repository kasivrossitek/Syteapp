package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 25-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Followers implements Parcelable
    {
        private String mRegisteredNum,userName,userGender,userProfilePic,deviceToken;
        private Object dateTime;
        public Followers()
        {
        }

        protected Followers(Parcel in) {
            mRegisteredNum = in.readString();
            userName = in.readString();
            userGender = in.readString();
            userProfilePic = in.readString();
            deviceToken=in.readString();
            dateTime= in.readValue(Object.class.getClassLoader());
        }

        public static final Creator<Followers> CREATOR = new Creator<Followers>() {
            @Override
            public Followers createFromParcel(Parcel in) {
                return new Followers(in);
            }

            @Override
            public Followers[] newArray(int size) {
                return new Followers[size];
            }
        };

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


        public void setDeviceToken(String paramDeviceToken)
        {
            this.deviceToken=paramDeviceToken;
        }
        public String getDeviceToken()
        {
            return this.deviceToken;
        }

        public void setDateTime(Object paramDtCreated) {this.dateTime=paramDtCreated;}
        public Object getDateTime() {return this.dateTime;}

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mRegisteredNum);
            dest.writeString(userName);
            dest.writeString(userGender);
            dest.writeString(userProfilePic);
            dest.writeString(deviceToken);
            dest.writeValue(dateTime);
        }
    }
