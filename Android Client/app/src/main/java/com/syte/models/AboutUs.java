package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 15-02-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AboutUs implements Parcelable
    {
        String title, description;

        public AboutUs(){}
        protected AboutUs(Parcel in) {
            title = in.readString();
            description = in.readString();
        }

        public static final Creator<AboutUs> CREATOR = new Creator<AboutUs>() {
            @Override
            public AboutUs createFromParcel(Parcel in) {
                return new AboutUs(in);
            }

            @Override
            public AboutUs[] newArray(int size) {
                return new AboutUs[size];
            }
        };

        public void setTitle(String paramTitle)
            {
                this.title=paramTitle;
            }
        public void setDescription(String paramDescription)
            {
                this.description=paramDescription;
            }
        public String getTitle()
            {
                return this.title;
            }
        public String getDescription()
            {
                return this.description;
            }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(description);
        }
    }
