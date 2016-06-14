package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 10-03-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BulletinBoardPush
    {
        private String syteId,syteImageUrl,syteName,bulletinId,bulletinSubject,bulletinImageUrl;
        private Object bulletinDateTime;
        private int pushType; // 1 = Bulletin Board, 2 = Follower, 3 = Claim Request, 4 = Syte Claim Rejected, 5 = Syte Claim Accepted
        public void setSyteId(String param)
            {
                this.syteId=param;
            }
        public void setSyteImageUrl(String param)
            {
                this.syteImageUrl=param;
            }
        public void setSyteName(String param)
            {
                this.syteName=param;
            }
        public void setBulletinId(String param)
            {
                this.bulletinId=param;
            }
        public void setBulletinSubject(String param)
            {
                this.bulletinSubject=param;
            }
        public void setBulletinImageUrl(String param)
            {
                this.bulletinImageUrl=param;
            }
        public void setBulletinDateTime(Object paramDateTime)
            {
                this.bulletinDateTime=paramDateTime;
            }
        public String getSyteId(){return this.syteId;}
        public String getSyteImageUrl(){return this.syteImageUrl;}
        public String getSyteName(){return this.syteName;}
        public String getBulletinId(){return this.bulletinId;}
        public String getBulletinSubject(){return this.bulletinSubject;}

        public String getBulletinImageUrl() {
            return bulletinImageUrl;
        }

        public Object getBulletinDateTime()
            {
                return bulletinDateTime;
            }

        public void setPushType(int pushType) {
            this.pushType = pushType;
        }

        public int getPushType() {
            return pushType;
        }
    }
