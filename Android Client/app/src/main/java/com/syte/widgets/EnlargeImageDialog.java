package com.syte.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.syte.R;
import com.syte.utils.StaticUtils;

/**
 * Created by khalid.p on 23-03-2016.
 */
public class EnlargeImageDialog
    {
        private static EnlargeImageDialog ENLARGE_IMAGE_DIALOG=null;
        private static Dialog DIALOG = null;
        private DisplayImageOptions displayImageOptions;
        private Cloudinary mCloudinary;
        private String imageUrl;
        private EnlargeImageDialog(Context paramCon, String paramImageURL)
            {
                this.DIALOG=new Dialog(paramCon,android.R.style.Theme_Black)
                    {
                        @Override
                        public boolean onTouchEvent(MotionEvent event)
                            {
                                // Tap anywhere to close dialog.
                                this.dismiss();
                                return true;
                            }
                    };
                this.DIALOG.requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.DIALOG.setCancelable(false);
                this.DIALOG.setCanceledOnTouchOutside(true);

                this.displayImageOptions = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true).build();
                this.mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                this.imageUrl = mCloudinary.url().generate(paramImageURL);
            }
        public static EnlargeImageDialog CREATE_ENLARGE_IMAGE_DIALOG(Context paramCon,String paramImageURL)
            {
                if(DIALOG!=null)
                    DIALOG=null;
                if(ENLARGE_IMAGE_DIALOG!=null)
                {
                    ENLARGE_IMAGE_DIALOG=null;
                }
                ENLARGE_IMAGE_DIALOG = new EnlargeImageDialog(paramCon,paramImageURL);
                return ENLARGE_IMAGE_DIALOG;
            }
        public void sShowEnlargeImageDialog()
            {
                DIALOG.setContentView(R.layout.layout_image_enlarge_dialog);
                DIALOG.show();
                Window window = DIALOG.getWindow();
                ImageView image=(ImageView)window.findViewById(R.id.image);
                ProgressBar loading=(ProgressBar)window.findViewById(R.id.loading);
                mDisplayImage(image,loading);
            }
        private void mDisplayImage(ImageView image, final ProgressBar loading)
            {
                ImageLoader.getInstance().displayImage(imageUrl, image, displayImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        loading.setVisibility(View.GONE);
                    }
                });
            } // END mDisplayImage()
    }
