package com.syte.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 25-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CloudinaryContent
    {
        public int sIndex;
        public String sImageToBeUploaded;
        public String sImageToBeDeleted;
        public int sContentType;
        public boolean sDeleteFlag;
        public boolean sUploadFlag;
        public static int CLOUDINARY_IMAGE_CONTENT=1;
        public static int CLOUDINARY_VIDEO_CONTENT=2; // Future use
        public void setsIndex(int paramIndex)
            {
                this.sIndex =paramIndex;
            }
        public void setsImageToBeUploaded(String paramUploadImage)
            {
                this.sImageToBeUploaded=paramUploadImage;
            }
        public void setsImageToBeDeleted(String paramDeleteImage)
            {
                this.sImageToBeDeleted=paramDeleteImage;
            }
        public void setsContentType(int paramContentType)
            {
                this.sContentType=paramContentType;
            }
        public void setsUploadFlag(boolean paramUploadFlag)
            {
                this.sUploadFlag=paramUploadFlag;
            }
        public void setsDeleteFlag(boolean paramDeleteFlag)
            {
                this.sDeleteFlag=paramDeleteFlag;
            }
    }
