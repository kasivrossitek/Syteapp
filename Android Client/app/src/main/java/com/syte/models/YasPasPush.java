package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 12-03-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class YasPasPush
    {
        private String syteId,syteName,syteOwner,registeredNum,userName,userProfilePic;
        private Object dateTime;
        private int pushType; // 1 = Bulletin Board, 2 = Follower, 3 = Syte Claim Request, 4 = Syte Claim Rejected, 5 = Syte Claim Accepted

        public YasPasPush(){}



        public void setSyteId(String syteId) {
            this.syteId = syteId;
        }

        public void setSyteName(String syteName) {
            this.syteName = syteName;
        }

        public void setSyteOwner(String syteOwner) {
            this.syteOwner = syteOwner;
        }

        public void setRegisteredNum(String registeredNum) {
            this.registeredNum = registeredNum;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setUserProfilePic(String userProfilePic) {
            this.userProfilePic = userProfilePic;
        }

        public void setDateTime(Object dateTime) {
            this.dateTime = dateTime;
        }

        public String getSyteId() {
            return syteId;
        }

        public String getSyteName() {
            return syteName;
        }

        public String getSyteOwner() {
            return syteOwner;
        }

        public String getRegisteredNum() {
            return registeredNum;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserProfilePic() {
            return userProfilePic;
        }

        public Object getDateTime() {
            return dateTime;
        }

        public void setPushType(int pushType) {
            this.pushType = pushType;
        }

        public int getPushType() {
            return pushType;
        }


    }
