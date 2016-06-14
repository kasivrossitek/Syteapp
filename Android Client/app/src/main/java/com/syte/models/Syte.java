package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 15-02-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Syte implements Parcelable
    {
        private double latitude,longitude; // Syte Geo location
        private String streetAddress1,streetAddress2,city,state,country,zipCode; // Syte Address
        private String name,mobileNo,category; // Syte details
        private String imageUrl; // Syte optional details - 1
        private AboutUs aboutUs; // Syte optional details - 2
        private WhatWeDo whatWeDo; // // Syte optional details - 4, as 3 is team
        //Transactional Data
        private Object dateCreated,dateModified,dateDeleted;
        private int isDeleted,openForBiz,messagingEnabled,isActive; // 0 - false & 1 = true
        private String latestBulletin,owner;


        public Syte(){}
        protected Syte(Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
            streetAddress1 = in.readString();
            streetAddress2 = in.readString();
            city = in.readString();
            state = in.readString();
            country = in.readString();
            zipCode = in.readString();
            name = in.readString();
            mobileNo = in.readString();
            category = in.readString();
            imageUrl = in.readString();
            aboutUs = in.readParcelable(AboutUs.class.getClassLoader());
            whatWeDo = in.readParcelable(WhatWeDo.class.getClassLoader());
            dateCreated= in.readValue(Object.class.getClassLoader());
            dateModified= in.readValue(Object.class.getClassLoader());
            dateDeleted= in.readValue(Object.class.getClassLoader());
            isDeleted = in.readInt();
            openForBiz = in.readInt();
            messagingEnabled = in.readInt();
            isActive=in.readInt();
            //isChat=in.readInt();
            latestBulletin = in.readString();
            owner = in.readString();

        }

        public static final Creator<Syte> CREATOR = new Creator<Syte>() {
            @Override
            public Syte createFromParcel(Parcel in) {
                return new Syte(in);
            }

            @Override
            public Syte[] newArray(int size) {
                return new Syte[size];
            }
        };

        public void setLatitude(double paramLatitude) {this.latitude=paramLatitude;}
        public double getLatitude() {return this.latitude;}

        public void setLongitude(double paramLongitude) {this.longitude=paramLongitude;}
        public double getLongitude() {return this.longitude;}

        public void setStreetAddress1(String paramStAdd1) {this.streetAddress1=paramStAdd1;}
        public String getStreetAddress1() {return this.streetAddress1;}

        public void setStreetAddress2(String paramStAdd2) {this.streetAddress2=paramStAdd2;}
        public String getStreetAddress2() {return this.streetAddress2;}

        public void setCity(String paramCity) {this.city=paramCity;}
        public String getCity() {return this.city;}

        public void setState(String paramState) {this.state=paramState;}
        public String getState() {return this.state;}

        public void setCountry(String paramCountry) {this.country=paramCountry;}
        public String getCountry() {return this.country;}

        public void setZipCode(String paramZipCode) {this.zipCode=paramZipCode;}
        public String getZipCode() {return this.zipCode;}

        public void setName(String paramName) {this.name=paramName;}
        public String getName() {return this.name;}

        public void setMobileNo(String paramMobile) {this.mobileNo=paramMobile;}
        public String getMobileNo() {return this.mobileNo;}

        public void setCategory(String paramCategory) {this.category=paramCategory;}
        public String getCategory() {return this.category;}

        public void setImageUrl(String paramImgUrl) {this.imageUrl=paramImgUrl;}
        public String getImageUrl() {return this.imageUrl;}

        public void setAboutUs(AboutUs paramAboutUs){this.aboutUs=paramAboutUs;}
        public AboutUs getAboutUs(){return this.aboutUs;}

        public void setWhatWeDo (WhatWeDo paramWhatWeDo){this.whatWeDo=paramWhatWeDo;}
        public WhatWeDo getWhatWeDo(){return this.whatWeDo;}

        public void setDateCreated(Object paramDtCreated) {this.dateCreated=paramDtCreated;}
        public Object getDateCreated() {return this.dateCreated;}

        public void setDateModified(Object paramDtModified) {this.dateModified=paramDtModified;}
        public Object getDateModified() {return this.dateModified;}

        public void setDateDeleted(Object paramDtDeleted) {this.dateDeleted=paramDtDeleted;}
        public Object getDateDeleted() {return this.dateDeleted;}

        public void setIsDeleted(int paramIsDeleted) {this.isDeleted=paramIsDeleted;}
        public int getIsDeleted() {return this.isDeleted;}

        public void setOpenForBiz(int paramOpenForBiz) {this.openForBiz=paramOpenForBiz;}
        public int getOpenForBiz() {return this.openForBiz;}

        public void setMessagingEnabled(int paramMessagingEnabled) {this.messagingEnabled=paramMessagingEnabled;}
        public int getMessagingEnabled() {return this.messagingEnabled;}

        public void setIsActive(int paramIsActive) {this.isActive=paramIsActive;}
        public int getIsActive() {return this.isActive;}

       /* public void setIsChat(int paramIsChat) {this.isChat=paramIsChat;}
        public int getIsChat() {return this.isChat;}*/

        public void setLatestBulletin(String paramBulletin) {this.latestBulletin=paramBulletin;}
        public String getLatestBulletin() {return this.latestBulletin;}

        public void setOwner(String paramOwner)
        {
            this.owner=paramOwner;
        }
        public String getOwner()
        {
            return this.owner;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            dest.writeString(streetAddress1);
            dest.writeString(streetAddress2);
            dest.writeString(city);
            dest.writeString(state);
            dest.writeString(country);
            dest.writeString(zipCode);
            dest.writeString(name);
            dest.writeString(mobileNo);
            dest.writeString(category);
            dest.writeString(imageUrl);
            dest.writeParcelable(aboutUs, flags);
            dest.writeParcelable(whatWeDo, flags);
            dest.writeValue(dateCreated);
            dest.writeValue(dateModified);
            dest.writeValue(dateDeleted);
            dest.writeInt(isDeleted);
            dest.writeInt(openForBiz);
            dest.writeInt(messagingEnabled);
            dest.writeInt(isActive);
            //dest.writeInt(isChat);
            dest.writeString(latestBulletin);
            dest.writeString(owner);
        }
    }
