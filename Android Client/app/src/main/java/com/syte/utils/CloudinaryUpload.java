package com.syte.utils;

import android.os.AsyncTask;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.syte.listeners.OnCloudinaryUploadListener;
import com.syte.models.CloudinaryContent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid.p on 25-01-2016.
 */
public class CloudinaryUpload
    {
        private ArrayList<String>cloudinaryIds;
        private ArrayList<CloudinaryContent> mCloudinaryData;
        private Cloudinary mCloudinary;
        private OnCloudinaryUploadListener mObjCldUpldLst;
        private String mCallingMethod;
        public CloudinaryUpload(ArrayList<CloudinaryContent> paramContent, OnCloudinaryUploadListener paramCldUpldLstnr, Cloudinary paramCloudinary,String paramCallingMethod)
            {
                this.mCloudinaryData=paramContent;

                this.mObjCldUpldLst=paramCldUpldLstnr;
                this.mCloudinary = paramCloudinary;
                this.cloudinaryIds = new ArrayList<>();
                cloudinaryIds.removeAll(cloudinaryIds); // Just to be at safer side, though not required.
                this.mCallingMethod=paramCallingMethod;
            }
        public void mDeleteImage(String paramId)
            {
                Map result = new HashMap();
                try {
                        mCloudinary.uploader().destroy(paramId, result);
                    }
                catch (IOException e){}
            }
        public String mUploadeImage(String filePath, String oldId)
            {
                Map result = new HashMap();
                String cloudinarId="";
                File imageFile = new File(filePath);
                try
                    {
                        /*if(oldId!=null && oldId.toString().trim().length()>0)
                            {
                                result = mCloudinary.uploader().upload(imageFile,  ObjectUtils.asMap("public_id", oldId));
                            }
                        else
                            {*/
                                result = mCloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
                           /* }*/
                        cloudinarId = result.get("public_id").toString();
                    }
                catch(RuntimeException e)
                    {
                        cloudinarId="";
                    }
                catch(IOException e)
                    {
                        cloudinarId="";
                    }
                return cloudinarId;
            }
        public ArrayList<String> sManipulateData()
            {
                new AsyncTask<String,Void,String>()
                    {
                        @Override
                        protected String doInBackground(String... params)
                            {
                                for(int i=0;i<mCloudinaryData.size();i++)
                                    {
                                        CloudinaryContent data = mCloudinaryData.get(i);
                                        if(data.sDeleteFlag)
                                            {
                                                mDeleteImage(data.sImageToBeDeleted);
                                                cloudinaryIds.add(i,"");
                                            }

                                        if(data.sUploadFlag)
                                            {
                                                cloudinaryIds.add(i,mUploadeImage(data.sImageToBeUploaded,data.sImageToBeDeleted));
                                            }
                                       else
                                            {
                                                cloudinaryIds.add(i,data.sImageToBeUploaded);
                                            }
                                    }
                                return null;
                            }
                        @Override
                        protected void onPreExecute()
                            {
                                mObjCldUpldLst.onUploadStarted();
                            }
                        @Override
                        protected void onPostExecute(String result)
                            {
                                mObjCldUpldLst.onUploadCompleted(cloudinaryIds,mCallingMethod);
                            }
                    }.execute();

                return cloudinaryIds;
            }
    }
