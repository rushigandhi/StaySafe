package com.example.rg185.staysafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rg185 on 2017-07-09.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "contacts_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "contact";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(String contact){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, contact);

        Log.d(TAG, "addData: Adding " + contact + " to " + TABLE_NAME);

        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public void deleteData(int id, String contact) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id + "'" +
                " AND " + COL2 + " = " + contact + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + contact + " from database.");
        sqLiteDatabase.execSQL(query);
    }

    public Cursor getData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        return data;
    }

    public Cursor getId(String contact){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT " + COL1 +  " FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + contact + "'";
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        return data;
    }

    public void deleteContact(int id, String contact){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + id + "' AND " + COL2 + " = '" + contact + "'";
        Log.d(TAG, "deleteContact: query: " + query);
        Log.d(TAG, "deleteContact: Deleting " + contact + " from database.");
        sqLiteDatabase.execSQL(query);
    }
}
