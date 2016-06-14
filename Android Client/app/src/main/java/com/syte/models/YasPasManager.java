package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 08-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class YasPasManager implements Parcelable
    {
        String managerId;
        public YasPasManager()
            {}

        protected YasPasManager(Parcel in) {
            managerId = in.readString();
        }

        public static final Creator<YasPasManager> CREATOR = new Creator<YasPasManager>() {
            @Override
            public YasPasManager createFromParcel(Parcel in) {
                return new YasPasManager(in);
            }

            @Override
            public YasPasManager[] newArray(int size) {
                return new YasPasManager[size];
            }
        };

        public void setManagerId(String paramManagerId)
            {
                this.managerId=paramManagerId;
            }
        public String getManagerId()
            {
                return this.managerId;
            }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(managerId);
        }
    }
