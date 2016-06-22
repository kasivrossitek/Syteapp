package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 08-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class YasPasTeam implements Parcelable
    {
        String name,designation,profilePic,about;
        int isHide; // 0 - false 1 - true
        public YasPasTeam(){}

        protected YasPasTeam(Parcel in) {
            name = in.readString();
            designation = in.readString();
            profilePic = in.readString();
            about = in.readString();
            isHide=in.readInt();
        }

        public static final Creator<YasPasTeam> CREATOR = new Creator<YasPasTeam>() {
            @Override
            public YasPasTeam createFromParcel(Parcel in) {
                return new YasPasTeam(in);
            }

            @Override
            public YasPasTeam[] newArray(int size) {
                return new YasPasTeam[size];
            }
        };

        public void setName(String paramName)
            {
                this.name=paramName;
            }
        public void setDesignation(String paramTitle)
            {
                this.designation=paramTitle;
            }
        public void setProfilePic(String paramProfilePic)
        {
            this.profilePic=paramProfilePic;
        }
        public String getName()
            {
                return this.name;
            }
        public String getDesignation(){return this.designation;}
        public String getProfilePic() {return this.profilePic;}

        public String getAbout(){return this.about;}
        public void setAbout(String paramAbout)
        {
            this.about=paramAbout;
        }
        public void setIsHide(int paramIsHide){this.isHide=paramIsHide;}
        public int getIsHide(){return this.isHide;}

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(designation);
            dest.writeString(profilePic);
            dest.writeString(about);
            dest.writeInt(isHide);
        }
    }
