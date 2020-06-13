package com.aaksoft.toar.firebase;

public class contact {



    String fullName;
    String userName;
    String Id;

    public contact(String fullName, String userName, String id) {
        this.fullName = fullName;
        this.userName = userName;
        Id = id;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
