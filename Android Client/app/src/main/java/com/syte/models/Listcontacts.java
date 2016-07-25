package com.syte.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Developer on 6/6/2016.
 */
public class Listcontacts implements Serializable {
    String contact_id, name;
    Bitmap Photo_id;


    public Listcontacts(String name, String contact_id, Bitmap photo_id) {
        this.contact_id = contact_id;
        this.name = name;
        this.Photo_id = photo_id;

    }

    public Listcontacts() {

    }


    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto_id() {
        return Photo_id;
    }

    public void setPhoto_id(Bitmap photo_id) {
        Photo_id = photo_id;
    }
}
