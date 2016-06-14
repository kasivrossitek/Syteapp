package com.syte.models;

/**
 * Created by khalid.p on 24-03-2016.
 */
public class AnalyticsVisitor
    {
        private String visitorRegisteredNum,visitorGender,visitedSyteId,visitedBulletinId;
        private double visitorLatitude, visitorLongitude;
        private Object visitedTime;

        public void setVisitorRegisteredNum(String visitorRegisteredNum) {
            this.visitorRegisteredNum = visitorRegisteredNum;
        }

        public void setVisitorGender(String visitorGender) {
            this.visitorGender = visitorGender;
        }

        public void setVisitedSyteId(String visitedSyteId) {
            this.visitedSyteId = visitedSyteId;
        }

        public void setVisitedBulletinId(String visitedBulletinId) {
            this.visitedBulletinId = visitedBulletinId;
        }

        public void setVisitorLatitude(double visitorLatitude) {
            this.visitorLatitude = visitorLatitude;
        }

        public void setVisitorLongitude(double visitorLongitude) {
            this.visitorLongitude = visitorLongitude;
        }

        public void setVisitedTime(Object visitedTime) {
            this.visitedTime = visitedTime;
        }

        public String getVisitorRegisteredNum() {
            return visitorRegisteredNum;
        }

        public String getVisitorGender() {
            return visitorGender;
        }

        public String getVisitedSyteId() {
            return visitedSyteId;
        }

        public String getVisitedBulletinId() {
            return visitedBulletinId;
        }

        public double getVisitorLatitude() {
            return visitorLatitude;
        }

        public double getVisitorLongitude() {
            return visitorLongitude;
        }

        public Object getVisitedTime() {
            return visitedTime;
        }
    }
