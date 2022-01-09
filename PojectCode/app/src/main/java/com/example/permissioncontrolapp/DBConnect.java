package com.example.permissioncontrolapp;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import java.sql.Date;
public class DBConnect extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "permission.db";

    public DBConnect(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table register(deviceid text primary key,permission_type text,permission_date text)");
        db.execSQL("create table statistics(permission text,permission_count text)");
        db.execSQL("create table details(deviceid text,permission_type text,from_date text,permission_date text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS register");
        db.execSQL("DROP TABLE IF EXISTS statistics");
        db.execSQL("DROP TABLE IF EXISTS details");
        onCreate(db);
    }

    public boolean getDate(long sec){
        boolean flag = false;
        java.sql.Date date = new java.sql.Date(sec);
        java.util.Date current_date = new java.util.Date();
        current_date = new java.sql.Date(current_date.getTime());
        if(current_date.before(date))
            flag = true;
        return flag;
    }


    public String getDetails() {
        int maybe = 0;
        int denied = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("Device ID       : "+MainActivity.android_id+"\n\n");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select deviceid,permission_type,from_date,permission_date from details",null);
        while(res.moveToNext()) {
            String device = res.getString(res.getColumnIndex("deviceid"));
            String permission = res.getString(res.getColumnIndex("permission_type"));
            String from = res.getString(res.getColumnIndex("from_date"));
            String to = res.getString(res.getColumnIndex("permission_date"));
            sb.append("Resource Type   : "+device+"\n");
            sb.append("Permission Type : "+permission+"\n");
            java.sql.Date dd = new java.sql.Date(Long.parseLong(to));
            sb.append("Time Period     : "+from+" to "+dd.toString()+"\n\n");
            if(permission.equals("May Be"))
                maybe = maybe + 1;
            if(permission.equals("Denied"))
                denied = denied + 1;
        }
        sb.append("\n\nOverall Statistics Details\n\n");
        sb.append("Deny Statistics : "+denied+"\n");
        sb.append("May Be          : "+(maybe)+"\n");
        return sb.toString();
    }

    public int isPermissionExists(String type){
        int value = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select permission,permission_count from statistics",null);
        while(res.moveToNext()) {
            String permission = res.getString(res.getColumnIndex("permission"));
            String count = res.getString(res.getColumnIndex("permission_count"));
            if(permission .equals(type)){
                value = Integer.parseInt(count);
                break;
            }
        }
        return value;
    }

    public int getDenyStatistics() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select permission_count from statistics where permission='Deny'",null);
        if(res.moveToNext()) {
            count = Integer.parseInt(res.getString(res.getColumnIndex("permission_count")));
        }
        return count;
    }

    public int getMaybe() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select permission_count from statistics where permission='May Be'",null);
        if(res.moveToNext()) {
            count = Integer.parseInt(res.getString(res.getColumnIndex("permission_count")));
        }
        return count;
    }

    public void updateStatistics(String type) {
        int count = isPermissionExists(type);
        System.out.println(type+"===="+count+"=====================");
        if(count == 0) {
            count = count + 1;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("permission", type);
            contentValues.put("permission_count", Integer.toString(count));
            db.insert("statistics", null, contentValues);
        } else {
            count = count + 1;
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE statistics SET permission_count = '"+count+"' WHERE permission = '"+type+"'";
            db.execSQL(query);
        }
    }

    public String getStatus(String device) {
        String msg = "none";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select permission_type,permission_date from register where deviceid='"+device+"'",null);
        if(res.moveToNext()) {
            String permission_type = res.getString(res.getColumnIndex("permission_type"));
            String permission_date = res.getString(res.getColumnIndex("permission_date"));
            long date = (Long.parseLong(permission_date));
            if (permission_type.equals("May Be") && !getDate(date))
                msg = "Expired";
            else if (permission_type.equals("May Be") && getDate(date))
                msg = "Not Expired";
            else if (permission_type.equals("Denied"))
                msg = "Denied";
            else if (permission_type.equals("Allow"))
                msg = "Allow";
            System.out.println(permission_type+" "+permission_date+" "+device+" "+msg);
        }
        return msg;
    }

    public void addDetails(String device,String permission,String date) {
        java.util.Date dd = new java.util.Date();
        java.sql.Date current = new java.sql.Date(dd.getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deviceid", device);
        contentValues.put("permission_type", permission);
        contentValues.put("from_date", current.toString());
        contentValues.put("permission_date", date);
        db.insert("details", null, contentValues);
        MainActivity.saveStaistics();
    }

    public void register(String device,String permission,String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deviceid", device);
        contentValues.put("permission_type", permission);
        contentValues.put("permission_date", date);
        db.insert("register", null, contentValues);
        addDetails(device,permission,date);
    }

    public void updatePermission(String device,String date,String permission) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE register SET permission_type = '"+permission+"', permission_date = '"+date+"' WHERE deviceid = '"+device+"'";
        db.execSQL(query);
        addDetails(device,permission,date);
    }
}
