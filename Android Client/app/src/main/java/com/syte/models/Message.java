package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 14-03-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Parcelable
    {
        private String syteId, syteName, syteImg,lastMessage;
        private Object lastMessageDateTime;
        private int unReadCount;
        public Message(){}
        protected Message(Parcel in) {
            syteId = in.readString();
            syteName = in.readString();
            syteImg = in.readString();
            lastMessage = in.readString();
            lastMessageDateTime=in.readValue(Object.class.getClassLoader());
            unReadCount = in.readInt();
        }

        public static final Creator<Message> CREATOR = new Creator<Message>() {
            @Override
            public Message createFromParcel(Parcel in) {
                return new Message(in);
            }

            @Override
            public Message[] newArray(int size) {
                return new Message[size];
            }
        };

        public void setSyteId(String syteId) {
            this.syteId = syteId;
        }

        public void setSyteName(String syteName) {
            this.syteName = syteName;
        }

        public void setSyteImg(String syteImg) {
            this.syteImg = syteImg;
        }

        public void setLastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
        }

        public void setLastMessageDateTime(Object lastMessageDateTime) {
            this.lastMessageDateTime = lastMessageDateTime;
        }

        public void setUnReadCount(int unReadCount) {
            this.unReadCount = unReadCount;
        }

        public String getSyteId() {
            return syteId;
        }

        public String getSyteName() {
            return syteName;
        }

        public String getSyteImg() {
            return syteImg;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public Object getLastMessageDateTime() {
            return lastMessageDateTime;
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
            dest.writeString(syteId);
            dest.writeString(syteName);
            dest.writeString(syteImg);
            dest.writeString(lastMessage);
            dest.writeValue(lastMessageDateTime);
            dest.writeInt(unReadCount);
        }
    }// END Message
