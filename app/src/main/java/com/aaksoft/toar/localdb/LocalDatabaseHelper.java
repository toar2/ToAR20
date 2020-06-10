package com.aaksoft.toar.localdb;

/*
    Created By Aasharib
    on
    23 February, 2019
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.aaksoft.toar.localdb.utils.AppSetting;

/*
    This class handles the local sqllite database for image information storage
*/

public class LocalDatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "data.db";

    public static final String TABLE_IMAGES_DATA = "images";
    public static final String TABLE_SETTING_DATA = "settings";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "LAT";
    public static final String COL_4 = "LNG";
    public static final String COL_5 = "DESCRIPTION";
    public static final String COL_6 = "CREATED_ON";
    public static final String COL_7 = "OWNER_NAME";
    public static final String COL_8 = "IMAGE_STATUS";
    public static final String COL_9 = "UNIQUE_FILENAME";
    public static final String COL_10= "OWNER_ID";

    public static final String COL1_1 = "ID";
    public static final String COL1_2 = "SETTING_NAME";
    public static final String COL1_3 = "SETTING_VALUE";


    // database creation
    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //Creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_IMAGES_DATA + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, LAT DOUBLE, LNG DOUBLE, DESCRIPTION TEXT, CREATED_ON DATETIME, OWNER_NAME TEXT, IMAGE_STATUS INTEGER, UNIQUE_FILENAME TEXT, OWNER_ID TEXT)");
        db.execSQL("create table " + TABLE_SETTING_DATA + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, SETTING_NAME TEXT, SETTING_VALUE TEXT )");
    }

    //update status data information
    public boolean updateStatusData(int id,int imageStatus){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, imageStatus);
        int result=db.update(TABLE_IMAGES_DATA,contentValues,"ID="+id,null);
        if (result>=1){
            return true;
        }
        else
            return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES_DATA);
        onCreate(db);
    }

    //inserting images data in TABLE_IMAGES_DATA
    public boolean insertImagesData(String name, double lat, double lng, String description, String createdOn, String ownerName, int imageStatus, String uniqueFileName, String owner_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, lat);
        contentValues.put(COL_4, lng);
        contentValues.put(COL_5, description);
        contentValues.put(COL_6, createdOn);
        contentValues.put(COL_7, ownerName);
        contentValues.put(COL_8, imageStatus);
        contentValues.put(COL_9, uniqueFileName);
        contentValues.put(COL_10,owner_id);
        long result = db.insert(TABLE_IMAGES_DATA, null, contentValues);
        if(result == -1){
            return false;
        }
        else {
            return true;
        }
    }

    //inserting images data in TABLE_SETTING_DATA
    public boolean insertSettingData(String settingName, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1_2, settingName);
        contentValues.put(COL1_3, value);
        long result = db.insert(TABLE_SETTING_DATA, null, contentValues);
        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    //updating images data in TABLE_SETTING_DATA
    public boolean updateSettingsData(String settingName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String id = getSettingID(settingName);
        if(id.equals("")){
            return false;
        }
        contentValues.put(COL1_3, value);
        int result=db.update(TABLE_SETTING_DATA,contentValues,"ID='"+id+"'" ,null);
        if (result>=1){
            return true;
        }
        else
            return false;
    }

    //Get setting ID from setting name
    private String getSettingID(String settingName){
        String settingID="";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id FROM " + TABLE_SETTING_DATA + " WHERE " + COL1_2 + " = '" + settingName + "'";
        Cursor res = db.rawQuery(query, null);
        if (res != null) {
            if (res.moveToFirst()) {
                    settingID =  res.getString(res.getColumnIndex("id"));
            }
        } else {
            Log.e("Sqllite Error","Error During SQLLITE Setting query");
        }
        return settingID;
    }

    // insert if setting not available else update the value of setting
    public void insertOrUpdateSettingData(String settingName, String value){
        String query = "insert or replace into "+ TABLE_SETTING_DATA + " ("+ COL1_2 + ", "+COL1_3 + ") values ('" + settingName + "','" + value + "');";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }


    //retrieve All images data
    public Cursor retrieveAllImagesData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_IMAGES_DATA  , null);
        return result;
    }

    //Return all Image Information from db table TABLE_IMAGES_DATA
    public ArrayList<ImageInformation> getImageInformation(){
        Cursor result = retrieveAllImagesData();
        int resultSize = result.getCount();
        ArrayList<ImageInformation> imageInformationArrayList = new ArrayList<>(resultSize);
        if(resultSize != 0){
            while(result.moveToNext()){
                ImageInformation imageInformation = new ImageInformation();
                imageInformation.setId(result.getInt(0));
                imageInformation.setName(result.getString(1));
                imageInformation.setLat(result.getDouble(2));
                imageInformation.setLng(result.getDouble(3));
                imageInformation.setDescription(result.getString(4));
                imageInformation.setCreatedOn(result.getString(5));
                imageInformation.setOwnerName(result.getString(6));
                imageInformation.setImageStatus(result.getInt(7));
                imageInformation.setUniqueFileName(result.getString(8));
                imageInformation.setOwner_id(result.getString(9));
                imageInformationArrayList.add(imageInformation);
            }
        }
        return imageInformationArrayList;
    }

    //Function to get unique filenames for images in local database
    public ArrayList<String> getAllUniqueFileNames(){
        Cursor result = retrieveAllImagesData();
        int resultSize = result.getCount();
        ArrayList<String> uniqueFileNameArrayList = new ArrayList<>(resultSize);
        if(resultSize != 0){
            while(result.moveToNext()){
                uniqueFileNameArrayList.add(result.getString(8));
            }
        }
        return uniqueFileNameArrayList;
    }

    //clear the TABLE_IMAGES_DATA
    public boolean clearImagesDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_IMAGES_DATA);
        return true;
    }

    //clear the record from particular user from TABLE_IMAGES_DATA
    public boolean clearAccountImagesDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_IMAGES_DATA + " where " + COL_10 + " != '" + 1 + "'");
        return true;
    }

    //clear the TABLE_SETTING_DATA
    public boolean clearSettingsDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SETTING_DATA);
        return true;
    }

    //get current datatime
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //get only those data field which are not yet synced with cloud
    private Cursor retrieveUnSyncData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_IMAGES_DATA + " where " + COL_8 + "=" + 1, null);
        return result;
    }

    //get only image field having ownerID from particular owner
    public ArrayList<ImageInformation> getUnSyncImageInformation(String ownerID){
        Cursor result = retrieveUnSyncData();
        int resultSize = result.getCount();
        ArrayList<ImageInformation> imageInformationArrayList = new ArrayList<>(resultSize);
        if(resultSize != 0){
            while(result.moveToNext()){
                if(result.getString(9).equals(ownerID)) {
                    ImageInformation imageInformation = new ImageInformation();
                    imageInformation.setId(result.getInt(0));
                    imageInformation.setName(result.getString(1));
                    imageInformation.setLat(result.getDouble(2));
                    imageInformation.setLng(result.getDouble(3));
                    imageInformation.setDescription(result.getString(4));
                    imageInformation.setCreatedOn(result.getString(5));
                    imageInformation.setOwnerName(result.getString(6));
                    imageInformation.setImageStatus(result.getInt(7));
                    imageInformation.setUniqueFileName(result.getString(8));
                    imageInformation.setOwner_id(result.getString(9));
                    imageInformationArrayList.add(imageInformation);
                }
            }
        }
        return imageInformationArrayList;
    }

    // Retreive all setting information
    private Cursor retrieveSettingsData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_SETTING_DATA , null);
        return result;
    }

    //Return AppSetting
    public ArrayList<AppSetting> getAllSettings(){
        Cursor settings = retrieveSettingsData();
        int resultSize = settings.getCount();
        ArrayList<AppSetting> appSettingsArrayList = new ArrayList<>(resultSize);
        if(resultSize != 0) {
            while (settings.moveToNext()) {
                AppSetting appSetting = new AppSetting();
                appSetting.setId(settings.getString(0));
                appSetting.setName(settings.getString(1));
                appSetting.setValue(settings.getString(2));
                appSettingsArrayList.add(appSetting);
            }
        }
        return appSettingsArrayList;
    }
}
