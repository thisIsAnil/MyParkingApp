package com.example.asus.myparkingapp;

import android.os.Environment;

import java.io.File;

/**
 * Created by Asus on 18-09-2016.
 */
public class TempFolderMaker {
    private static final File ROOT= Environment.getExternalStorageDirectory();
    private static final String AUTHENTICATED_USER_LIST="Authenticated Users";
    private static final String ACCESS_REQUEST="Access Requests";


    private static final File parent=new File(ROOT,"MyParkingApp");
    private static final File db=new File(parent,AUTHENTICATED_USER_LIST);
    private static final File request=new File(parent,ACCESS_REQUEST);

    public static File getDb() {
        return db;
    }

    public static File getRequest() {
        return request;
    }
}
