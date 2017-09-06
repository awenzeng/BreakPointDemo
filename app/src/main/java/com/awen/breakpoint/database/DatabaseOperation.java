package com.awen.breakpoint.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.awen.breakpoint.download.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类
 */
public class DatabaseOperation implements DatabaseImpl {

    private static final String TAG = "DatabaseOperation";

    private DatabaseHelper DBHelper;

    public DatabaseOperation(Context context) {
        this.DBHelper = new DatabaseHelper(context);
    }

    @Override
    public void insert(DownloadInfo downloadInfo) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.execSQL("insert into download_info(download_id,url,start,end,progress) values(?,?,?,?,?)",
                new Object[]{downloadInfo.getId(), downloadInfo.getUrl(),
                        downloadInfo.getStart(), downloadInfo.getEnd(), downloadInfo.getProgress()});
        db.close();
    }

    @Override
    public void delete(String url, int download_id) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.execSQL("delete from  download_info where url = ? and download_id=?",
                new Object[]{url, download_id});
        db.close();
    }

    @Override
    public void update(String url, int download_id, long progress) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        db.execSQL("update download_info set progress = ?  where url = ? and download_id=?",
                new Object[]{progress, url, download_id});
        db.close();
    }

    @Override
    public List<DownloadInfo> query(String url) {
        List<DownloadInfo> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from download_info where url=?", new String[]{url});
        while (cursor.moveToNext()) {
            DownloadInfo download = new DownloadInfo();
            download.setId(cursor.getInt(cursor.getColumnIndex("download_id")));
            download.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            download.setStart(cursor.getLong(cursor.getColumnIndex("start")));
            download.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
            download.setProgress(cursor.getLong(cursor.getColumnIndex("progress")));
            list.add(download);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int download_id) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from download_info where url=? and download_id = ?", new String[]{url, String.valueOf(download_id)});
        boolean isExist = cursor.moveToNext();
        cursor.close();
        db.close();
        return isExist;
    }
}
