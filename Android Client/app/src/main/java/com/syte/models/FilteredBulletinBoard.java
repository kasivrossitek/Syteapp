package com.syte.models;

/**
 * Created by khalid.p on 31-03-2016.
 */
public class FilteredBulletinBoard
    {
        private String syteId,syteName,syteAddress,bulletinId,bulletinSubject, bulletinBody, bulletinImageUrl;
        private Object dateTime;
        private int sendToAllFollowers;
        private boolean isFavourite,isOwned;

        public void setSyteId(String syteId) {
            this.syteId = syteId;
        }

        public void setSyteName(String syteName) {
            this.syteName = syteName;
        }

        public void setSyteAddress(String syteAddress) {
            this.syteAddress = syteAddress;
        }

        public void setBulletinId(String bulletinId) {
            this.bulletinId = bulletinId;
        }

        public void setBulletinSubject(String bulletinSubject) {
            this.bulletinSubject = bulletinSubject;
        }

        public void setBulletinBody(String bulletinBody) {
            this.bulletinBody = bulletinBody;
        }

        public void setBulletinImageUrl(String bulletinImageUrl) {
            this.bulletinImageUrl = bulletinImageUrl;
        }

        public void setDateTime(Object dateTime) {
            this.dateTime = dateTime;
        }

        public void setSendToAllFollowers(int sendToAllFollowers) {
            this.sendToAllFollowers = sendToAllFollowers;
        }

        public String getSyteId() {
            return syteId;
        }

        public String getSyteName() {
            return syteName;
        }

        public String getSyteAddress() {
            return syteAddress;
        }

        public String getBulletinId() {
            return bulletinId;
        }

        public String getBulletinSubject() {
            return bulletinSubject;
        }

        public String getBulletinBody() {
            return bulletinBody;
        }

        public String getBulletinImageUrl() {
            return bulletinImageUrl;
        }

        public Object getDateTime() {
            return dateTime;
        }

        public int getSendToAllFollowers() {
            return sendToAllFollowers;
        }

        public void setIsFavourite(boolean isFavourite) {
            this.isFavourite = isFavourite;
        }
        public boolean getIsFavourite()
            {
                return this.isFavourite;
            }
        public void setIsOwned(boolean isOwned)
            {
                this.isOwned=isOwned;
            }
        public boolean getIsOwned(){return this.isOwned;}
    }
