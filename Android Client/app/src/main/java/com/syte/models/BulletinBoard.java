package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 13-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BulletinBoard implements Parcelable {
    private String subject, body, imageUrl;//, owner;
    private Object dateTime;
    private int sendToAllFollowers; // 0 - false, 1 - True

    public BulletinBoard() {
    }

    protected BulletinBoard(Parcel in) {
        subject = in.readString();
        body = in.readString();
        imageUrl = in.readString();
        dateTime = (Object) in.readValue(Object.class.getClassLoader());
        sendToAllFollowers = in.readInt();
        //owner = in.readString();
    }

    public static final Creator<BulletinBoard> CREATOR = new Creator<BulletinBoard>() {
        @Override
        public BulletinBoard createFromParcel(Parcel in) {
            return new BulletinBoard(in);
        }

        @Override
        public BulletinBoard[] newArray(int size) {
            return new BulletinBoard[size];
        }
    };

    public void setSubject(String paramSubject) {
        this.subject = paramSubject;
    }

    public void setDateTime(Object paramDateTime) {
        this.dateTime = paramDateTime;
    }

    public void setBody(String paramBody) {
        this.body = paramBody;
    }

    public void setImageUrl(String paramImgUrl) {
        this.imageUrl = paramImgUrl;
    }

    public String getSubject() {
        return this.subject;
    }

    public Object getDateTime() {
        return this.dateTime;
    }

    public String getBody() {
        return this.body;
    }

   /* public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
*/
    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setSendToAllFollowers(int paramSendToAllFollowers) {
        this.sendToAllFollowers = paramSendToAllFollowers;
    }

    public int getSendToAllFollowers() {
        return this.sendToAllFollowers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subject);
        dest.writeString(body);
        dest.writeString(imageUrl);
        dest.writeValue(dateTime);
        dest.writeInt(sendToAllFollowers);
       // dest.writeString(owner);
    }
}
