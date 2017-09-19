package com.infi.myparkingapp_client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by Infi on 4/17/2016.
 */
public class TimerAdapter {

    public static final String KEY_ID="_id";
    public static final String KEY_START_HR="StartHr";
    public static final String KEY_STOP_HR="StopHrr";
    public static final String KEY_START_MIN="StartMin";
    public static final String KEY_STOP_MIN="StopMin";
    public static final String KEY_DAYS="Days";
    public static final String KEY_STATUS="Status";
    public static final String KEY_DAILY="Daily";
    public static final String KEY_TITLE="Title";
    //public static final String KEYISAM="isAm";
    public static final String KEY_LAT="Latitude";
    public static final String KEY_LON="Longitude";
    public static final String KEY_ID_GPS="_gps_id";
    public static final String KEY_ADDRESS="Address";
    public static final String KEY_STATUS_GPS="Status";
    public static final String KEY_GPS_TITLE="GTitle";
    public static final String KEY_RADIUS="Radius";
    public static final String KEY_MODE_GPS="Mode_gps";
    public static final String KEY_MODE="Mode";
    public static final String KEY_TRUSTED="IP";
    public static final String KEY_TRUSTED_TABLE="Trusted IP";
    public static final String KEY_TRUSTED_NAME="MAC_addr";
    public static final String KEY_TRUSTED_MAC_ID="id";
    private static final String GPS_SQL_TABLE="GPSEvents";

    private static final String TRUSTED_NAME="Trust";
    private static final String TAG ="TimerAdapter";
    private static final String DATABASE_NAME="Timer";
    private static final String SQL_TABLE="TimerEvents";
    private static final int DATABASE_VERSION=8;
    Context c;
    public final Context Ctx;
    public  DataBaseHelper dbHelper;
    public  SQLiteDatabase mdbr,mdb;

    private static final String DATABASE_CREATE="CREATE TABLE "+SQL_TABLE
            +" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_START_HR
            +" TEXT NOT NULL,"+KEY_START_MIN+" TEXT NOT NULL,"+KEY_STOP_HR+" TEXT NOT NULL,"+KEY_STOP_MIN+" TEXT NOT NULL,"
            +KEY_DAYS+" TEXT NOT NULL,"+KEY_TITLE+" TEXT NOT NULL,"+KEY_DAILY+" TEXT NOT NULL,"+KEY_MODE+" TEXT NOT NULL,"+KEY_STATUS+" INTEGER NOT NULL"+")";

    private static final String GPS_DB_CREATE="CREATE TABLE "+GPS_SQL_TABLE
            +" ("+KEY_ID_GPS+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_LAT
            +" TEXT NOT NULL,"+KEY_LON+" TEXT NOT NULL," +KEY_RADIUS+" TEXT NOT NULL," +KEY_GPS_TITLE+" TEXT NOT NULL,"+KEY_ADDRESS+" TEXT NOT NULL,"
            +KEY_MODE_GPS+" TEXT NOT NULL," +KEY_STATUS_GPS+" INTEGER NOT NULL"+")";

    private static final String TRUSTED_DB_CREATE="CREATE TABLE "+TRUSTED_NAME+" ("+KEY_TRUSTED_MAC_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_TRUSTED_NAME
    +" TEXT NOT NULL,"+KEY_TRUSTED
            +" TEXT NOT NULL "+")";

   /* private static final String DATABASE_CREATE="CREATE TABLE if not exists TimerEvents(_id INTEGER AUTOINCREMENT ,start_hr INTEGER NOT NULL,start_min INTEGER NOT NULL," +
            "stop_hr INTEGER NOT NULL,stop_min INTEGER NOT NULL,days TEXT NOT NULL,status INTEGER ,PRIMARY KEY(_id));";*/
    public static   class DataBaseHelper extends SQLiteOpenHelper {

        DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, DATABASE_CREATE);

            try{
                db.execSQL(GPS_DB_CREATE);
            }catch (android.database.SQLException e){
                e.printStackTrace();
            }
            try{
                db.execSQL(DATABASE_CREATE);
            }catch(android.database.SQLException e){
                e.printStackTrace();
            }try{
                db.execSQL(TRUSTED_DB_CREATE);
            }catch (android.database.SQLException e){

            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "UpGrading From" + oldVersion + "To" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS"+GPS_SQL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + SQL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + TRUSTED_NAME);
            onCreate(db);
        }
    }
    public TimerAdapter(Context cnt){
        this.Ctx=cnt;
        dbHelper=new DataBaseHelper(cnt);
        c=cnt;
    }
    public TimerAdapter open() throws SQLException{
        dbHelper=new DataBaseHelper(Ctx);
        mdb=dbHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        if(dbHelper!=null){
            dbHelper.close();
        }
    }
    public long createTrusted(String name,String ip_addr){
        mdb.beginTransaction();
        long _id=0;
        try{
            ContentValues values=new ContentValues();
            values.put(KEY_TRUSTED_NAME,name);
            values.put(KEY_TRUSTED,ip_addr);
            _id=mdb.insert(TRUSTED_NAME,null,values);

        }catch(android.database.SQLException e){}
        finally {
            mdb.endTransaction();
            return _id;
        }
    }
    public Cursor getTrusted(){
        String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + TRUSTED_NAME;
        mdbr=dbHelper.getReadableDatabase();
        Cursor mCursor=mdbr.rawQuery(USER_DETAIL_SELECT_QUERY,null);
        if(mCursor!=null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public long updateTrusted(String mac,String ip){
        long _id=-1;
        mdb.beginTransaction();
        try{
            ContentValues values=new ContentValues();
            values.put(KEY_TRUSTED,ip);
            values.put(KEY_TRUSTED_NAME,mac);
            _id=mdb.update(TRUSTED_NAME,values,KEY_TRUSTED_NAME+"="+mac,null);

        }catch (Exception e){

        }finally {
            mdb.endTransaction();
            return _id;
        }
    }
        public long createEvent(String title,String start_hr,String start_min,String stop_hr,String stop_min,String days,String isDaily,String mode,int status){
            long _id=0;
            mdb.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_TITLE,title);
                values.put(KEY_START_HR, start_hr);
                values.put(KEY_START_MIN, start_min);
                values.put(KEY_STOP_HR, stop_hr);
                values.put(KEY_STOP_MIN, stop_min);
                values.put(KEY_DAYS, days);
                values.put(KEY_STATUS, status);
                values.put(KEY_MODE,mode);
               values.put(KEY_DAILY,isDaily);
                _id = mdb.insert(SQL_TABLE, null, values);
                mdb.setTransactionSuccessful();
                //Toast.makeText(c, "Inserting" + start_hr + stop_hr + days + _id.toString() + "  "+_id, Toast.LENGTH_SHORT).show();
                if(_id==-1){
                    Toast.makeText(c,"Error",Toast.LENGTH_SHORT).show();
                }

            }catch(Exception e){
                e.printStackTrace();
            }finally {
                mdb.endTransaction();
                return _id;
            }

        }

        public boolean deleteAllEvents(){
            int doneDelete = 0;
            doneDelete = mdb.delete(SQL_TABLE, null , null);
            Log.w(TAG, Integer.toString(doneDelete));
            return doneDelete > 0;
        }
        public Cursor get_all_events(){
            String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + SQL_TABLE;
            mdbr=dbHelper.getReadableDatabase();
            Cursor mCursor=mdbr.rawQuery(USER_DETAIL_SELECT_QUERY,null);
            //Cursor mCursor=mdb.query(SQL_TABLE,new String[]{KEY_ID,KEY_START_HR,KEY_START_MIN,KEY_STOP_HR,KEY_STOP_MIN,KEY_DAYS,KEY_STATUS},null,null,null,null,null);
            if(mCursor!=null){
                mCursor.moveToFirst();
            }
            return mCursor;
        }
    /*public Cursor getByStatus(String status){

        Cursor cursor;
        Log.w("TAG","Sorting based on status");
        if(status!="1"||status!="0"){
            Cursor mCursor=mdb.query(SQL_TABLE,new String[]{KEY_ID,KEY_START_HR,KEY_START_MIN,KEY_STOP_HR,KEY_STOP_MIN,KEY_DAYS,KEY_STATUS},null,null,null,null,null);
        }
        else{
            Cursor mCursor=mdb.query(true,SQL_TABLE,new String[]{KEY_ID,KEY_START_HR,KEY_START_MIN,KEY_STOP_HR,KEY_STOP_MIN,KEY_DAYS,KEY_STATUS},KEY_STATUS,
                    "like '%"+status+"%'",null,null,null,null,null);

        }
    }*/

    void deleteRow(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        try {
            db.beginTransaction();
            db.delete(SQL_TABLE,KEY_ID+"='"+id+"'",null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete");
            Toast.makeText(Ctx,"Error while trying to delete",Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }


    }
    void delete(long id,String start,String stop){
        mdb.beginTransaction();
        try{
            mdb.delete(SQL_TABLE,KEY_ID+"=? and "+KEY_START_HR+"=? and "+KEY_STOP_HR+"=?",new String[]{id+"",start,stop});
            mdb.setTransactionSuccessful();
        }catch(Exception e){
            Toast.makeText(Ctx,"Error while trying to delete",Toast.LENGTH_SHORT).show();
        }finally {
            mdb.endTransaction();
        }
    }
    void update(long id,String title,String start_hr,String start_min,String stop_hr,String stop_min,String days,String isDaily,String mode,int status){

        mdb.beginTransaction();
        try{

        ContentValues values=new ContentValues();
            values.put(KEY_TITLE,title);
            values.put(KEY_START_HR, start_hr);
            values.put(KEY_START_MIN, start_min);
            values.put(KEY_STOP_HR, stop_hr);
            values.put(KEY_STOP_MIN, stop_min);
            values.put(KEY_DAYS, days);
            values.put(KEY_MODE,mode);
            values.put(KEY_DAILY,isDaily);
            values.put(KEY_STATUS, status);
            long _id=mdb.update(SQL_TABLE,values,KEY_ID+"='"+id+"'",null);
            //long _id=mdb.insertWithOnConflict(SQL_TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);

            mdb.setTransactionSuccessful();
        }catch (Exception e){
            Toast.makeText(c,"Error While Updating"+id,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
            mdb.endTransaction();
        }

    }



    //GPS CRUD starts here

    public void CreateGpsEvent(String title,String latitude,String longitude,String address,String radius,String mode,int status){
        mdb.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_GPS_TITLE,title);
            values.put(KEY_LAT, latitude);
            values.put(KEY_LON, longitude);
            values.put(KEY_ADDRESS, address);
            values.put(KEY_RADIUS,radius);
            values.put(KEY_MODE_GPS,mode);
            values.put(KEY_STATUS_GPS, status);
            long _id=mdb.insert(GPS_SQL_TABLE, null, values);
            mdb.setTransactionSuccessful();
            //Toast.makeText(Ctx,"Inserting"+""+_id,Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mdb.endTransaction();
        }

    }

    public Cursor getAllGpsEvents(){
        String USER_DETAIL_SELECT_QUERY = "SELECT * FROM " + GPS_SQL_TABLE;
        mdbr=dbHelper.getReadableDatabase();
        Cursor mCursor=mdbr.rawQuery(USER_DETAIL_SELECT_QUERY,null);
        //Cursor mCursor=mdb.query(SQL_TABLE,new String[]{KEY_ID,KEY_START_HR,KEY_START_MIN,KEY_STOP_HR,KEY_STOP_MIN,KEY_DAYS,KEY_STATUS},null,null,null,null,null);
        if(mCursor!=null){
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public void update_gps(long _id,String title,String latitude,String longitude,String address,String radius,String mode,int status){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();
            ContentValues values=new ContentValues();
            values.put(KEY_GPS_TITLE,title);
            values.put(KEY_LAT, latitude);
            values.put(KEY_LON, longitude);
            values.put(KEY_ADDRESS, address);
            values.put(KEY_RADIUS,radius);
            values.put(KEY_STATUS_GPS, status);
            values.put(KEY_MODE_GPS,mode);
            long _idd=db.update(GPS_SQL_TABLE,values,KEY_ID_GPS+"='"+_id+"'",null);
            db.setTransactionSuccessful();
            //Toast.makeText(Ctx,"Updating\n"+ _idd+"\n"+address,Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(Ctx,"Error while Updating",Toast.LENGTH_SHORT).show();
        }finally {
            db.endTransaction();
        }
    }

    void deleteGps(long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try{
            db.beginTransaction();
            db.delete(GPS_SQL_TABLE,KEY_ID_GPS+"='"+id+"'",null);
            db.setTransactionSuccessful();
        }catch(Exception e){
            Toast.makeText(Ctx,"Error while trying to delete",Toast.LENGTH_SHORT).show();
        }finally {
            db.endTransaction();
        }
    }
    }
