package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 14-03-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage
    {
        private String cMessage,cSenderRegisteredNum,cSenderName;
        private int cSenderType; // 0 for User, 1 for Sponsor
        private boolean cIsRead;
        private Object cDateTime;

        public void setcMessage(String cMessage) {
            this.cMessage = cMessage;
        }

        public void setcSenderRegisteredNum(String cSenderRegisteredNum) {
            this.cSenderRegisteredNum = cSenderRegisteredNum;
        }

        public void setcSenderName(String cSenderName) {
            this.cSenderName = cSenderName;
        }

        public void setcSenderType(int cSenderType) {
            this.cSenderType = cSenderType;
        }

        public void setcIsRead(boolean cIsRead) {
            this.cIsRead = cIsRead;
        }

        public void setcDateTime(Object cDateTime) {
            this.cDateTime = cDateTime;
        }

        public String getcMessage() {
            return cMessage;
        }

        public String getcSenderRegisteredNum() {
            return cSenderRegisteredNum;
        }

        public String getcSenderName() {
            return cSenderName;
        }

        public int getcSenderType() {
            return cSenderType;
        }
        public boolean getcIsRead()
            {
                return this.cIsRead;
            }

        public Object getcDateTime() {
            return cDateTime;
        }
    }// END ChatMessage
