package com.syte.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

/**
 * Created by khalid.p on 22-01-2016.
 */
public class GalleryHelper {
    public static String KEY_IMAGE_PATH = "keyImagePath";
    public static String KEY_IMAGE_URI = "keyImageURI";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    //End of Getting the Path
    // Added - Khalid - To get the image path in all the cases.
    /*public static HashMap GET_IMAGE_FILE_PATH(int paramActionPick, Intent data, Context paramCon) {
        Uri selectedImageUri = null;
        String selectedImagePath = null;
        try {
            selectedImageUri = data.getData();
            if (Build.VERSION.SDK_INT < 19) {
                Cursor cursor = null;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                cursor = paramCon.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedImagePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                if (selectedImageUri != null) {
                    selectedImagePath = GalleryHelper.getPath(paramCon, selectedImageUri);
                } else {
                    if (paramActionPick == StaticUtils.ACTION_CAMERA_PICK)//in some devices(kitkat, lollipop) i am getting the Uri is null. so i am getting the last image what i took from camera.
                    {
                        Cursor cursor = paramCon.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
                        if (cursor != null && cursor.moveToLast()) {
                            selectedImageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                            selectedImagePath = selectedImageUri.toString();
                            cursor.close();
                            Log.d("image ", selectedImagePath);
                            if (selectedImageUri.toString().startsWith("/storage"))//path of image is coming as Uri, so i am converting into Uri from path.
                            {
                                File file = new File(selectedImageUri.toString());
                                selectedImageUri = Uri.fromFile(file);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        HashMap<String, Object> imageMap = new HashMap<String, Object>();
        imageMap.put(KEY_IMAGE_PATH, selectedImagePath);
        imageMap.put(KEY_IMAGE_URI, selectedImageUri);
        return imageMap;
    }*/
//kasi 4-7-16
    public static HashMap GET_IMAGE_FILE_PATH(int paramActionPick, Intent data, Context paramCon) {
        Uri selectedImageUri = null;
        String selectedImagePath = null;
        try {
            selectedImageUri = data.getData();
            if (Build.VERSION.SDK_INT < 19) {
                Cursor cursor = null;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                cursor = paramCon.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedImagePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                if (selectedImageUri != null) {
                    selectedImagePath = GalleryHelper.getPath(paramCon, selectedImageUri);
                } else {
                    if (paramActionPick == StaticUtils.ACTION_CAMERA_PICK)//in some devices(kitkat, lollipop) i am getting the Uri is null. so i am getting the last image what i took from camera.
                    {

                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        selectedImageUri = getImageUri(paramCon, photo);
                        selectedImagePath = getimagePath(paramCon, selectedImageUri);
                        if (selectedImagePath.startsWith("/storage"))//path of image is coming as Uri, so i am converting into Uri from path.
                        {
                            File file = new File(selectedImagePath);
                            selectedImageUri = Uri.fromFile(file);
                        }
                        Log.d("picturePath : ", selectedImagePath + "uri" + selectedImageUri);


                    }
                }
            }
        } catch (Exception e) {
        }
        HashMap<String, Object> imageMap = new HashMap<String, Object>();
        imageMap.put(KEY_IMAGE_PATH, selectedImagePath);
        imageMap.put(KEY_IMAGE_URI, selectedImageUri);
        return imageMap;
    }

    public static String getimagePath(Context paramCon, Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = paramCon.getContentResolver().query(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, MediaStore.Images.Media.DATE_ADDED, null);
        return Uri.parse(path);
    }
}
