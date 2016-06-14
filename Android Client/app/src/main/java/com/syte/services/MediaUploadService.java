package com.syte.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.IBinder;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.syte.database.SyteDataBase;
import com.syte.database.SyteDataBaseConstant;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MediaUploadService extends Service
    {
        private Cloudinary mCloudinary;
        public static String DUMMY_URL="1985-03-21-KM-1983-07-26";
        public MediaUploadService()
            {
            }
        @Override
        public IBinder onBind(Intent intent)
            {
                // TODO: Return the communication channel to the service.
                throw new UnsupportedOperationException("Not yet implemented");
            }
        /*@Override
        public int onStartCommand(Intent intent, int flags, int startId) {}*/
        @Override
        public void onCreate ()
            {
                mCloudinary = new Cloudinary(StaticUtils.GET_CLOUDINARY_CONFIG());
                System.out.println("MediaUploadService -- STARTED");
                upload();
            }
        @Override
        public void onDestroy ()
            {
                System.out.println("MediaUploadService -- STOPPED");
            }
        private void upload()
            {
                final SyteDataBase syteDataBase = SyteDataBase.GET_DB_INSTANCE(getApplicationContext());
                Cursor cursor = syteDataBase.sGetAllMedia();
                if(cursor.getCount()>0)
                    {
                        cursor.moveToFirst();
                        final int _id = cursor.getInt(cursor.getColumnIndex(SyteDataBaseConstant.C_ID));
                        System.out.println("_id --- : " + _id);
                        int _media_type = cursor.getInt(cursor.getColumnIndex(SyteDataBaseConstant.C_MEDIA_TYPE));
                        final int _media_tar = cursor.getInt(cursor.getColumnIndex(SyteDataBaseConstant.C_MEDIA_TAR));
                        final String _firebase_id = cursor.getString(cursor.getColumnIndex(SyteDataBaseConstant.C_FIREBASE_ID));
                        final String _syte_id = cursor.getString(cursor.getColumnIndex(SyteDataBaseConstant.C_SYTE_ID));
                        final String _cloudinary_id = cursor.getString(cursor.getColumnIndex(SyteDataBaseConstant.C_CLOUDINARY_ID));
                        final String _media_location = cursor.getString(cursor.getColumnIndex(SyteDataBaseConstant.C_MEDIA_LOCATION));
                        final String _old_cloudinary_id = cursor.getString(cursor.getColumnIndex(SyteDataBaseConstant.C_OLD_CLOUDINARY_ID));
                        cursor.close();
                        new AsyncTask<String,Void,String>()
                            {
                                @Override
                                protected String doInBackground(String... params)
                                    {
                                        if(!_cloudinary_id.trim().trim().equalsIgnoreCase(MediaUploadService.DUMMY_URL))
                                            {
                                                // media is uploaded to cloudinary but it is not updated to firebase
                                                if(_media_tar==SyteDataBaseConstant.C_MEDIA_TAR_BULLETIN)
                                                    {
                                                        mUpdateBulletin(_syte_id,_firebase_id,_cloudinary_id,_id,syteDataBase);
                                                    }
                                            }
                                        else
                                            {
                                                // media is not uploaded to cloudinary
                                                if(_old_cloudinary_id.trim().length()>0)
                                                    {
                                                        Map result = new HashMap();
                                                        try
                                                            {
                                                                mCloudinary.uploader().destroy(_old_cloudinary_id, result);
                                                            }
                                                        catch (IOException e)
                                                            {

                                                            }
                                                    }
                                                String c_id =mUploadeImage(_media_location);
                                                System.out.println("NEW CLOUDINARY ID : "+c_id);
                                                if(c_id.trim().length()>0)
                                                    {
                                                        if(_media_tar==SyteDataBaseConstant.C_MEDIA_TAR_BULLETIN)
                                                            {
                                                                mUpdateBulletin(_syte_id,_firebase_id,c_id,_id,syteDataBase);
                                                            }
                                                        if(_media_tar==SyteDataBaseConstant.C_MEDIA_TAR_SYTE_BANNER)
                                                            {
                                                                mUpdateSyte(_syte_id, _firebase_id, c_id, _id, syteDataBase);
                                                            }
                                                        if(_media_tar==SyteDataBaseConstant.C_MEDIA_TAR_TEAM_MEM_PROFILE_PIC)
                                                            {
                                                                mUpdateTeamMember(_syte_id, _firebase_id, c_id, _id, syteDataBase);
                                                            }
                                                        if(_media_tar==SyteDataBaseConstant.C_MEDIA_TAR_USER_PROFILE_PIC)
                                                            {
                                                                mUpdateUserImage(_syte_id, _firebase_id, c_id, _id, syteDataBase);
                                                            }
                                                    }
                                            }
                                        return null;
                                    }
                                @Override
                                protected void onPreExecute()
                                    {

                                    }
                                @Override
                                protected void onPostExecute(String result)
                                    {

                                    }
                            }.execute();
                    }
                else
                    {
                        cursor.close();
                        stopSelf();
                    }
            }


        /* SK start function */


        public File saveBitmapToFile1(String filePath) {
            File file = new File(filePath);

            try {

                // BitmapFactory options to downsize the image
                               BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                o.inSampleSize = 2;
                // factor of downsizing the image

                FileInputStream inputStream = new FileInputStream(file);
                //Bitmap selectedBitmap = null;
                BitmapFactory.decodeStream(inputStream, null, o);
                inputStream.close();

                // The new size we want to scale to
                final int REQUIRED_SIZE=75;

                // Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                        o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                    scale *= 2;
                }

                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                inputStream = new FileInputStream(file);

                Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
                inputStream.close();

                File tmpFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", getApplicationContext().getCacheDir()); // Saving the file
                FileOutputStream outputStream = new FileOutputStream(tmpFile);

                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

                return tmpFile;
            } catch (Exception e) {
                return null;
            }
        }


        public File saveBitmapToFile2(String filePath) {
            File imageFile = new File(filePath);

            try {

                InputStream inFileStream = new FileInputStream(imageFile);

                Bitmap bitmap = BitmapFactory.decodeStream(inFileStream);

                File tmpFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", getApplicationContext().getCacheDir());


                OutputStream outFileStream = new FileOutputStream(tmpFile);
                try {
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outFileStream)) {
                        {
//                            File tmp = imageFile;
//                            imageFile = tmpFile;
//                            tmpFile = tmp;
                            return tmpFile;
                        }
                        //tmpFile.delete();
                    } else {
                        throw new Exception("Failed to save the image as a JPEG");
                    }
                } finally {
                    outFileStream.close();
                    inFileStream.close();

                }

            } catch (Throwable t) {

                //throw t;
                return null;
            }
            //}catch (FileNotFoundException ex){
            //      throw ex;
            // }
            //finally {
            //}


        }

        /* SK End function */

        private String mUploadeImage(String filePath)
            {
                System.out.println("filePath "+ filePath);
                Map result = new HashMap();
                String cloudinarId="";
      //File imageFile = new File(filePath);

                File imageFile = saveBitmapToFile1(filePath);

                try
                    {
                        result = mCloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
                        cloudinarId = result.get("public_id").toString();
                    }
                catch(RuntimeException e)
                    {
                        System.out.println("******************* RuntimeException ***************************");
                        e.printStackTrace();
                        cloudinarId="";
                    }
                catch(IOException e)
                    {
                        System.out.println("******************* IOException ***************************");
                        e.printStackTrace();
                        cloudinarId="";
                    }

                finally {
                    imageFile.delete();
                }

                System.out.println("cloudinarId "+ cloudinarId);
                return cloudinarId;
            }// END mUploadeImage()
        private void mUpdateBulletin(String _syte_id,String _firebase_id,String _cloudinary_id, final int _local_id, final SyteDataBase syteDataBase)
            {
                Firebase mFirebaseBulletinImg = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(_syte_id).child(_firebase_id).child("imageUrl");
                mFirebaseBulletinImg.setValue(_cloudinary_id, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            syteDataBase.sDeleteMedia(_local_id);
                        }
                        upload();
                    }
                });
            }// END mUpdateBulletin()
        private void mUpdateSyte(final String _syte_id, final String _firebase_id, final String _cloudinary_id, final int _local_id, final SyteDataBase syteDataBase)
            {
                Firebase mFireBsYasPasObj=new Firebase(StaticUtils.YASPAS_URL).child(_syte_id);
                HashMap<String, Object> mUpdateMap = new HashMap<String, Object>();
                mUpdateMap.put("imageUrl",_cloudinary_id);
                mUpdateMap.put("dateModified", ServerValue.TIMESTAMP);
                mFireBsYasPasObj.updateChildren(mUpdateMap, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            final Firebase mFireBsYasPaseeObj = new Firebase(StaticUtils.YASPASEE_URL).child(_firebase_id).child(StaticUtils.OWNED_YASPAS).child(_syte_id);
                            HashMap<String, Object> mUpdateMap1 = new HashMap<String, Object>();
                            mUpdateMap1.put("imageUrl", _cloudinary_id);
                            mFireBsYasPaseeObj.updateChildren(mUpdateMap1, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError == null) {
                                        syteDataBase.sDeleteMedia(_local_id);
                                        upload();
                                    } else {
                                        upload();
                                    }
                                }
                            });
                        } else {
                            upload();
                        }
                    }
                });
            }// END mUpdateSyte()
        private void mUpdateTeamMember(final String _syte_id, final String _firebase_id, final String _cloudinary_id, final int _local_id, final SyteDataBase syteDataBase)
            {
                Firebase mFireBsYasTeamMember = new Firebase(StaticUtils.YASPAS_URL).child(_syte_id).child(StaticUtils.YASPAS_TEAM).child(_firebase_id).child("profilePic");
                mFireBsYasTeamMember.setValue(_cloudinary_id, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            syteDataBase.sDeleteMedia(_local_id);
                        }
                        upload();
                    }
                });
            }// END mUpdateTeamMember()
        private void mUpdateUserImage(final String _syte_id, final String _firebase_id, final String _cloudinary_id, final int _local_id, final SyteDataBase syteDataBase)
            {
                Firebase mFireBsYasPasee = new Firebase(StaticUtils.YASPASEE_URL).child(_firebase_id).child("userProfilePic");
                mFireBsYasPasee.setValue(_cloudinary_id, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase)
                            {
                                if (firebaseError == null)
                                    {
                                        YasPasPreferences yasPasPreferences = YasPasPreferences.GET_INSTANCE(getApplicationContext());
                                        yasPasPreferences.sSetUserProfilePic(_cloudinary_id);
                                        syteDataBase.sDeleteMedia(_local_id);
                                    }
                                upload();
                            }
                    });
            }// END mUpdateTeamMember()

    }
