package com.aaksoft.toar.firebase;

public abstract class Notification {

    boolean handled = false;
    boolean type;
    static int numberOfNotifications;
    String fromID;                                        // The user ID of the individual who prompted the notification
    String fromName;
    String fromUserName;


}
