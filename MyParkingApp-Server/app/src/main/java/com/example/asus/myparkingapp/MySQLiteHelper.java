package com.example.asus.myparkingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Asus on 18-09-2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_VECHILES = "vechile";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NUMBER = "number";
    private static final String DATABASE_NAME = "vehicles.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_VECHILES + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TYPE
            + " text not null"+ COLUMN_NUMBER + "text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VECHILES);
        onCreate(db);
    }

}
