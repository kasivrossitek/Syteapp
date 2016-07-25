package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 14-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnedYasPases implements Parcelable {
    String name, imageUrl, yasPasId;
    String city, country, syteType,mobileNo;
    private int isActive; // 0 - false & 1 = true

    public OwnedYasPases() {
    }


    protected OwnedYasPases(Parcel in)

    {
        name = in.readString();
        imageUrl = in.readString();
        yasPasId = in.readString();
        city = in.readString();
        country = in.readString();
        isActive = in.readInt();
        syteType=in.readString();
        mobileNo=in.readString();
    }

    public static final Creator<FollowingYasPas> CREATOR = new Creator<FollowingYasPas>() {
        @Override
        public FollowingYasPas createFromParcel(Parcel in) {
            return new FollowingYasPas(in);
        }

        @Override
        public FollowingYasPas[] newArray(int size) {
            return new FollowingYasPas[size];
        }
    };

    public void setName(String paramName) {
        this.name = paramName;
    }

    public String getName() {
        return this.name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setImageUrl(String paramImgUrl) {
        this.imageUrl = paramImgUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setYasPasId(String paramYasPasId) {
        this.yasPasId = paramYasPasId;
    }

    public String getYasPasId() {
        return this.yasPasId;
    }

    public void setCity(String paramCity) {
        this.city = paramCity;
    }

    public String getCity() {
        return this.city;
    }

    public void setCountry(String paramCountry) {
        this.country = paramCountry;
    }

    public String getCountry() {
        return this.country;
    }

    public void setIsActive(int paramIsActive) {
        this.isActive = paramIsActive;
    }

    public int getIsActive() {
        return this.isActive;
    }

    public String getSyteType() {
        return syteType;
    }

    public void setSyteType(String syteType) {
        this.syteType = syteType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(yasPasId);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeInt(isActive);
        dest.writeString(syteType);
        dest.writeString(mobileNo);
    }
}
