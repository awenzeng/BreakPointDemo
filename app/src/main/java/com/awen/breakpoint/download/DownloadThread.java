package com.awen.breakpoint.download;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.awen.breakpoint.database.DatabaseOperation;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件下载线程
 * Created by AwenZeng on 2017/9/6.
 */

public class DownloadThread extends Thread {
    private DownloadInfo downloadInfo;
    private FileInfo mFileInfo;
    private long progress = 0;
    private boolean isPause;
    private DatabaseOperation mDatabaseOperation;
    private Context mContext;
    private static final String TAG = "DownloadThread";

    public DownloadThread(Context context, DatabaseOperation databaseOperation, DownloadInfo threadInfo, FileInfo fileInfo) {
        this.downloadInfo = threadInfo;
        mContext = context;
        mDatabaseOperation = databaseOperation;
        mFileInfo = fileInfo;
    }


    public void setPause(boolean pause) {
        isPause = pause;
    }

    @Override
    public void run() {
        if (!mDatabaseOperation.isExists(downloadInfo.getUrl(), downloadInfo.getId())) {
            mDatabaseOperation.insert(downloadInfo);
        }
        HttpURLConnection connection;
        RandomAccessFile raf;
        InputStream is;
        try {
            URL url = new URL(downloadInfo.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            //设置下载位置
            long start = downloadInfo.getStart() + downloadInfo.getProgress();
            connection.setRequestProperty("Range", "bytes=" + start + "-" + downloadInfo.getEnd());

            //设置文件写入位置
            File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(start);

            progress += downloadInfo.getProgress();
            Log.e(TAG,"下载文件进度(size)："+ downloadInfo.getProgress() + "");
            Log.e(TAG,"HttpResponseCode ==="+connection.getResponseCode() + "");
            //开始下载
            if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                Log.e(TAG,"剩余文件(size):"+connection.getContentLength() + "");
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);//广播intent
                is = connection.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                int len = -1;
                long time = System.currentTimeMillis();
                while ((len = is.read(buffer)) != -1) {
                    //下载暂停时，保存进度
                    if (isPause) {
                        Log.e(TAG,"保存进度文件(size):"+progress + "");
                        mDatabaseOperation.update(mFileInfo.getUrl(), mFileInfo.getId(), progress);
                        return;
                    }
                    //写入文件
                    raf.write(buffer, 0, len);
                    //把下载进度发送广播给Activity
                    progress += len;
                    if (System.currentTimeMillis() - time > 1000) {//超过一秒，就刷新UI
                        time = System.currentTimeMillis();
                        sendBroadcast(intent,(int)(progress * 100 / mFileInfo.getLength()));
                        Log.e(TAG,"进度：" + progress * 100 / mFileInfo.getLength() + "%");
                    }
                }
                sendBroadcast(intent,100);
                /**
                 *  删除下载信息（重新下载）
                 */
                mDatabaseOperation.delete(mFileInfo.getUrl(), mFileInfo.getId());
                is.close();
            }
            raf.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcast(Intent intent,int progress){
        intent.putExtra("progress",progress);
        mContext.sendBroadcast(intent);
    }
}
