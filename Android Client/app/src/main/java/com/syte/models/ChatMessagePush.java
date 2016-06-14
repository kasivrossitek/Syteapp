package com.syte.models;

/**
 * Created by KP on 15-05-2016.
 */
public class ChatMessagePush
    {
        String syteId,cMessage,cSenderRegisteredNum,cSenderName,imgUrl,cChatId;
        private int cSenderType; // 0 for User, 1 for Sponsor
        private Object cDateTime;

        public void setcChatId(String pCChatId)
            {
                this.cChatId=pCChatId;
            }
        public String getcChatId(){return this.cChatId;}

        public void setSyteId(String pSyteId)
            {
                this.syteId=pSyteId;
            }
        public String getSyteId()
            {
                return  this.syteId;
            }
        public void setcMessage(String pCMessage)
            {
                this.cMessage=pCMessage;
            }
        public String getcMessage()
            {
                return this.cMessage;
            }
        public void setcSenderRegisteredNum(String pSenderRegisteredNum)
            {
                this.cSenderRegisteredNum=pSenderRegisteredNum;
            }
        public String getcSenderRegisteredNum()
            {
                return this.cSenderRegisteredNum;
            }
        public void setcSenderName(String pSenderName)
            {
                this.cSenderName=pSenderName;
            }
        public String getcSenderName()
            {
                return this.cSenderName;
            }
        public void setcSenderType(int cSenderType1)
            {
                this.cSenderType=cSenderType1;
            }
        public int getcSenderType()
            {
                return this.cSenderType;
            }
        public void setcDateTime(Object pCDateTime)
            {
                this.cDateTime=pCDateTime;
            }
        public Object getcDateTime()
            {
                return this.cDateTime;
            }
        public void setImgUrl(String imgUrl1)
            {
                this.imgUrl=imgUrl1;
            }
        public String getImgUrl()
            {
                return this.imgUrl;
            }

    }
