package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by khalid.p on 09-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomMapMarker
    {
        public Marker marker;
        public String title,bulletin,imageUrl,category,city;
        public boolean isOwnedYasPas,visible,isFollowingYasPas; // It is true if a YasPas is owned or managed by the logged in user
        public CustomMapMarker(Marker paramMarker,String paramTitle,String paramBullentin,String paramImageUrl,boolean paramIsOwnedYasPas,String paramCategory,
                               boolean paramIsFollowingYasPas,String paramCity)
            {
                this.marker=paramMarker;
                this.title=paramTitle;
                this.isOwnedYasPas=paramIsOwnedYasPas;
                this.bulletin=paramBullentin;
                this.imageUrl=paramImageUrl;
                this.category=paramCategory;
                this.isFollowingYasPas=paramIsFollowingYasPas;
                this.city=paramCity;
            }
    public boolean getVisible(){
            return this.visible;
        }

        public void setVisible(boolean state){
            this.visible = state;
        }

        public Marker getMarker(){
            return this.marker;
        }

        public String getCategories(){
            return this.category;
        }

        public String getTitle(){
            return this.title;
        }
    }
