package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 15-03-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SponsorInnerMessage implements Parcelable
    {
        String userRegisteredNumber,userName,userProfilePic,lastMessage;
        private Object lastMessageDateTime;
        private int unReadCount;
        public SponsorInnerMessage(){}

        protected SponsorInnerMessage(Parcel in) {
            userRegisteredNumber = in.readString();
            userName = in.readString();
            userProfilePic = in.readString();
            lastMessage = in.readString();
            lastMessageDateTime = in.readValue(Object.class.getClassLoader());
            unReadCount = in.readInt();
        }

        public static final Creator<SponsorInnerMessage> CREATOR = new Creator<SponsorInnerMessage>() {
            @Override
            public SponsorInnerMessage createFromParcel(Parcel in) {
                return new SponsorInnerMessage(in);
            }

            @Override
            public SponsorInnerMessage[] newArray(int size) {
                return new SponsorInnerMessage[size];
            }
        };

        public void setUserRegisteredNumber(String userRegisteredNumber) {
            this.userRegisteredNumber = userRegisteredNumber;
        }

        public String getUserRegisteredNumber() {
            return userRegisteredNumber;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserProfilePic(String userProfilePic) {
            this.userProfilePic = userProfilePic;
        }

        public String getUserProfilePic() {
            return userProfilePic;
        }

        public void setLastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public void setLastMessageDateTime(Object lastMessageDateTime) {
            this.lastMessageDateTime = lastMessageDateTime;
        }

        public Object getLastMessageDateTime() {
            return lastMessageDateTime;
        }

        public void setUnReadCount(int unReadCount) {
            this.unReadCount = unReadCount;
        }

        public int getUnReadCount() {
            return unReadCount;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userRegisteredNumber);
            dest.writeString(userName);
            dest.writeString(userProfilePic);
            dest.writeString(lastMessage);
            dest.writeValue(lastMessageDateTime);
            dest.writeInt(unReadCount);
        }
    }
