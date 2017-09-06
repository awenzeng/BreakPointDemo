package com.awen.breakpoint.download;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件信息线程
 * des:网络获取文件的大小
 * Created by AwenZeng on 2017/9/5.
 */

public class FileInfoThread extends Thread {

    private static final String TAG = "FileInfoThread";

    private FileInfo mFileInfo;

    private Handler mHandler;

    public FileInfoThread(FileInfo fileInfo,Handler handler) {
        mFileInfo = fileInfo;
        mHandler = handler;
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        try {
            URL url = new URL(mFileInfo.getUrl());
            conn = (HttpURLConnection) url.openConnection();//连接网络文件
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            int length = -1;
            Log.e(TAG,"HttpResponseCode=="+ conn.getResponseCode() + "");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //获取文件长度
                length = conn.getContentLength();
            }
            if (length < 0) {
                return;
            }
            File dir = new File(DownloadService.DOWNLOAD_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            //在本地创建文件
            File file = new File(dir, mFileInfo.getFileName());
            raf = new RandomAccessFile(file, "rwd");
            //设置本地文件长度
            raf.setLength(length);
            mFileInfo.setLength(length);
            Log.e(TAG,"下载文件大小(size)"+ mFileInfo.getLength() + "");
            mHandler.obtainMessage(DownloadService.MSG_FILEINFO, mFileInfo).sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && raf != null) {
                    raf.close();
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
