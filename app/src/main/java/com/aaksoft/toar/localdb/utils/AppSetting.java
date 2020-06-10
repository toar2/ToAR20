package com.aaksoft.toar.localdb.utils;

/*
 *   Created By Aasharib on 05,April,2019
 */

import com.aaksoft.toar.localdb.LocalDatabaseHelper;

/*
    This class handles the individual setting variable for the application
*/

public class AppSetting {

    private String id;
    private String name;
    private String value;

    public AppSetting() {

    }

    public AppSetting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //for inserting the setting in local db
    public boolean saveNewSettingToLocalDb(LocalDatabaseHelper ldh){
        boolean isSettingSaved = ldh.insertSettingData(name,value);
        return isSettingSaved;
    }

    //for updating the setting in local db
    public void insertOrUpdateSettingToLocalDb(LocalDatabaseHelper ldh){
        //ldh.updateSettingsData(name,value);
        ldh.insertOrUpdateSettingData(name,value);
    }

}
