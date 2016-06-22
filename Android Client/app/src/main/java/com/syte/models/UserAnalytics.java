package com.syte.models;

/**
 *
 */
public class UserAnalytics
    {
        private String osName,osVersion,osVersionName;
        private double userLatitude,userLongitude;
        private Object activityTimeStamp;
        public void setOsName(String osName1){this.osName=osName1;}
        public void setOsVersion(String osVersion1){this.osVersion=osVersion1;}
        public void setOsVersionName(String osVersionName1){this.osVersionName=osVersionName1;}
        public void setUserLatitude(double userLatitude1){this.userLatitude=userLatitude1;}
        public void setUserLongitude(double userLongitude1){this.userLongitude=userLongitude1;}
        public String getOsName(){return this.osName;}
        public String getOsVersion(){return this.osVersion;}
        public String getOsVersionName(){return this.osVersionName;}
        public double getUserLatitude(){return this.userLatitude;}
        public double getUserLongitude(){return this.userLongitude;}
        public void setActivityTimeStamp(Object activityTimeStamp1){this.activityTimeStamp=activityTimeStamp1;}
        public Object getActivityTimeStamp(){return this.activityTimeStamp;}
    }
