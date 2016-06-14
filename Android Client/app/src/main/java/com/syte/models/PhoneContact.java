package com.syte.models;

import android.graphics.Bitmap;

/**
 * Created by Developer on 5/31/2016.
 */
public class PhoneContact {

    String phone_Mobile, phone_ID, phone_Name;
    Bitmap photo_id;

    public PhoneContact(String phone_name, String phone_id, Bitmap photo_id, String phone_mobile) {
        this.phone_Name = phone_name;
        this.phone_ID = phone_id;
        this.photo_id = photo_id;
        this.phone_Mobile = phone_mobile;
    }

    public PhoneContact() {
    }

    public Bitmap getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(Bitmap photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhone_Name() {
        return phone_Name;
    }

    public void setPhone_Name(String phone_Name) {
        this.phone_Name = phone_Name;
    }

    public String getPhone_Mobile() {
        return phone_Mobile;
    }

    public void setPhone_Mobile(String phone_Mobile) {
        this.phone_Mobile = phone_Mobile;
    }

    public String getPhone_ID() {
        return phone_ID;
    }

    public void setPhone_ID(String phone_ID) {
        this.phone_ID = phone_ID;
    }


}
