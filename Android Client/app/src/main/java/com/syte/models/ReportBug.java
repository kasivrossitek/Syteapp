package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 25-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportBug
    {
        String title,description,mRegisteredNum,userName;
        Object dateTime;
        int bugStatus; // 0 - new , 1 - replied
public ReportBug(){}






        public void setTitle(String paramTitle)
            {
                this.title=paramTitle;
            }
        public String getTitle()
            {
                return this.title;
            }
        public void setDescription(String paramDesc)
            {
                this.description=paramDesc;
            }
        public String getDescription()
            {
                return this.description;
            }
        public void setmRegisteredNum(String paramRegNumber)
            {
                this.mRegisteredNum=paramRegNumber;
            }
        public String getmRegisteredNum()
            {
                return  this.mRegisteredNum;
            }
        public void setDateTime(Object paramDtTm)
            {
                this.dateTime=paramDtTm;
            }
        public Object getDateTime()
            {
                return this.dateTime;
            }
        public void setReplied(int parambugStatus)
            {
                this.bugStatus=parambugStatus;
            }
        public int getIsReplied()
            {
                return this.bugStatus;
            }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }
    }
