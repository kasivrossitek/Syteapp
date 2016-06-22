package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 30-12-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDb {
    private String registeredNum, countryCode, deviceId, deviceToken,verificationStatus, recievedOtp;

    public AuthDb() {
    }

    public void setRegisteredNum(String mMobile_No) {
        this.registeredNum = mMobile_No;
    }

    public String getRegisteredNum() {
        return registeredNum;
    }

    public void setDeviceId(String paramDeviceId) {
        this.deviceId = paramDeviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String paramDeviceToken) {
        this.deviceToken = paramDeviceToken;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setCountryCode(String paramCountryCode) {
        this.countryCode = paramCountryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
