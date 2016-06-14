package com.syte.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 09-04-2016.
 */
    public class SyteDataBase extends SQLiteOpenHelper
        {
            private static String DB_NAME = "SYTE.sqlite";
            private static int DB_VERSION = 1;
            private static SyteDataBase SYTE_DATA_BASE;
            private SyteDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
                {
                    super(context, name, factory, version);
                }
            @Override
            public void onCreate(SQLiteDatabase db)
                {
                    /*Version 1*/
                    /*TABLE - upload_media
                    * _media_type : Type of media - 1 = image & 2 = video
                    * _media_tar : Where particular media is to be shown - 1 = Syte Banner , 2 = Bulletin, 3 = User , 4 = Team Member
                    * _firebase_id : Fire base id of specific tar e.g.; it will be User/Yaspasee id in case _media_tar = 1/3 & it will be Bulletin/Team Member id in case _media_tar = 2/4
                    * _syte_id : Syte id
                    * _cloudinary_id : Cloudinary id once media is been uploaded,
                    * _old_cloudinary_id : while updating, the old media to be deleted, this is old media id*/
                    db.execSQL("CREATE TABLE " + SyteDataBaseConstant.TABLE_UPLOAD_MEDIA +
                            "(" + SyteDataBaseConstant.C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            SyteDataBaseConstant.C_MEDIA_TYPE + " INTEGER, " +
                            SyteDataBaseConstant.C_MEDIA_TAR + " INTEGER, " +
                            SyteDataBaseConstant.C_FIREBASE_ID + " VARCHAR, " +
                            SyteDataBaseConstant.C_SYTE_ID + " VARCHAR, " +
                            SyteDataBaseConstant.C_CLOUDINARY_ID + " VARCHAR, " +
                            SyteDataBaseConstant.C_MEDIA_LOCATION + " VARCHAR, " +
                            SyteDataBaseConstant.C_OLD_CLOUDINARY_ID + " VARCHAR" +
                            ")");
                }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
                {

                }
            public static SyteDataBase GET_DB_INSTANCE(Context context)
                {
                    if(SYTE_DATA_BASE==null)
                        {
                            SYTE_DATA_BASE = new SyteDataBase(context,DB_NAME,null,DB_VERSION);
                        }
                    return SYTE_DATA_BASE;
                }
            public void sInsertMedia(int mediaType, int mediaTar,String firebaseId,String syteId,String cloudinaryId, String mediaLoc,String oldCloudinaryId)
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SyteDataBaseConstant.C_MEDIA_TYPE,mediaType);
                    contentValues.put(SyteDataBaseConstant.C_MEDIA_TAR,mediaTar);
                    contentValues.put(SyteDataBaseConstant.C_FIREBASE_ID,firebaseId);
                    contentValues.put(SyteDataBaseConstant.C_SYTE_ID,syteId);
                    contentValues.put(SyteDataBaseConstant.C_CLOUDINARY_ID, cloudinaryId);
                    contentValues.put(SyteDataBaseConstant.C_MEDIA_LOCATION, mediaLoc);
                    contentValues.put(SyteDataBaseConstant.C_OLD_CLOUDINARY_ID, oldCloudinaryId);
                    SQLiteDatabase db = this.getWritableDatabase();
                    Log.e("DB OPERATION", "" + db.insert(SyteDataBaseConstant.TABLE_UPLOAD_MEDIA, null, contentValues));
                }// END sInsertMedia()
            public Cursor sGetAllMedia()
                {
                    SQLiteDatabase db = this.getWritableDatabase();
                    String Query = "SELECT * FROM "+SyteDataBaseConstant.TABLE_UPLOAD_MEDIA;
                    return db.rawQuery(Query, null);
                }// END mGetAllMedia()
            public void sDeleteMedia(int id)
                {

                    String l_id = Integer.toString(id);
                    SQLiteDatabase db = this.getWritableDatabase();
                    String deleteQuery = "DELETE FROM "
                            +SyteDataBaseConstant.TABLE_UPLOAD_MEDIA+
                            " WHERE " +
                            SyteDataBaseConstant.C_ID +
                            "='" + l_id + "'";
                    db.execSQL(deleteQuery);
                }

        }
