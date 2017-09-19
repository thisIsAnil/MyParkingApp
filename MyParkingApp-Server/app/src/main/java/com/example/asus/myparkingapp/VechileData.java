package com.example.asus.myparkingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 18-09-2016.
 */
public class VechileData {
        // Database fields
        private SQLiteDatabase database;
        private MySQLiteHelper dbHelper;
        private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
                MySQLiteHelper.COLUMN_TYPE,MySQLiteHelper.COLUMN_NUMBER };

        public VechileData(Context context) {
            dbHelper = new MySQLiteHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public Vehicles createVehicleEntry(String type,String number) {
            database.beginTransaction();
            Cursor cursor=null;
            Vehicles vehicles=new Vehicles();
            try {
                ContentValues values = new ContentValues();
                values.put(MySQLiteHelper.COLUMN_TYPE, type);
                values.put(MySQLiteHelper.COLUMN_NUMBER, number);
                long insertId = database.insert(MySQLiteHelper.TABLE_VECHILES, null,
                        values);
                cursor = database.query(MySQLiteHelper.TABLE_VECHILES,
                        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                        null, null, null);
                cursor.moveToFirst();
                vehicles = cursorToComment(cursor);

            }catch (SQLException e){

            }finally {
                cursor.close();
                database.endTransaction();
            }
            return vehicles;
        }

        public void deleteVehicle(Vehicles vehicles) {
            String id = vehicles.getNAME();
            System.out.println("Comment deleted with id: " + id);
            database.delete(MySQLiteHelper.TABLE_VECHILES, MySQLiteHelper.COLUMN_NUMBER
                    + " = " + id, null);
        }

        public List<Vehicles> getAllEntry() {
            List<Vehicles> comments = new ArrayList<Vehicles>();

            String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + MySQLiteHelper.TABLE_VECHILES;
            SQLiteDatabase mdbr=dbHelper.getReadableDatabase();
            Cursor cursor=mdbr.rawQuery(USER_DETAIL_SELECT_QUERY,null);
            if(cursor!=null){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Vehicles comment = cursorToComment(cursor);
                    comments.add(comment);
                    cursor.moveToNext();
                }
            }

            // make sure to close the cursor
            try{cursor.close();}catch (NullPointerException np){}
            return comments;
        }

        private Vehicles cursorToComment(Cursor cursor) {
            Vehicles vehicles = new Vehicles();
            vehicles.setNAME(cursor.getString(2));
            vehicles.setTYPE(cursor.getString(1));
            return vehicles;
        }

}
