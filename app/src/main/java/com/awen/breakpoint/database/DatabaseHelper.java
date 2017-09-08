package com.awen.breakpoint.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";
    private static final String SQL_CREATE = "create table download_info(_id integer primary key autoincrement,"
            + "download_id integer,url text,start long,end long,progress long)";
    private static final String SQL_DROP = "drop table if exists download_info";
    private static final int VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        Log.i("good","数据库初始化");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        Log.i("good","CreatDataBase");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
