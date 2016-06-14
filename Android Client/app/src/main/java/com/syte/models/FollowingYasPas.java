package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 25-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FollowingYasPas implements Parcelable
    {
        String name,imageUrl,yasPasId;

        public FollowingYasPas(){}

        protected FollowingYasPas(Parcel in) {
            name = in.readString();
            imageUrl = in.readString();
            yasPasId = in.readString();
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

        public void setName(String paramName)
            {
                this.name=paramName;
            }
        public String getName()
            {
                return this.name;
            }
        public void setImageUrl(String paramImgUrl)
            {
                this.imageUrl=paramImgUrl;
            }
        public String getImageUrl()
            {
                return this.imageUrl;
            }
        public void setYasPasId(String paramYasPasId )
            {
                this.yasPasId=paramYasPasId;
            }
        public String getYasPasId()
            {
                return this.yasPasId;
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
        }
    }
