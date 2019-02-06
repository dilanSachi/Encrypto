package com.example.dilansachintha.encrypto;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "encrypto.db";
    public static final String AUTHORIZATOR_TABLE_NAME = "authorizator";
    public static final String AUTHORIZATOR_COLUMN_ID = "id";
    public static final String AUTHORIZATOR_COLUMN_NAME = "pin";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table authorizator " +
                        "(id integer primary key, pin text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS authorizator");
        onCreate(db);
    }

    public boolean insertPin (String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin", pin);
        db.insert("authorizator", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from authorizator where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, AUTHORIZATOR_TABLE_NAME);
        return numRows;
    }

    public boolean updatePin (Integer id, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pin", pin);
        db.update("authorizator", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletePin (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("authorizator",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllPins() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from authorizator", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(AUTHORIZATOR_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}