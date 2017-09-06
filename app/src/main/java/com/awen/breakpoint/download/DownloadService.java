package com.awen.breakpoint.download;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.awen.breakpoint.database.DatabaseOperation;

import java.util.List;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";

    private FileInfoThread mFileInfoThread;
    private DownloadThread mDownloadThread;
    private DatabaseOperation mDatabaseManager = null;

    public static final int MSG_FILEINFO = 0;
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_FINISHED = "ACTION_FINISHED";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";

    public static final String TAG_FILEINFO = "fileInfo";

    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FILEINFO:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.e(TAG,"下载文件信息:"+fileInfo.toString());
                    List<DownloadInfo> downloadInfos = mDatabaseManager.query(fileInfo.getUrl());
                    DownloadInfo info;
                    if (downloadInfos.size() == 0) {
                        info = new DownloadInfo(0, fileInfo.getUrl(), 0, fileInfo.getLength(), 0);
                    } else {
                        info = downloadInfos.get(0);
                    }
                    mDownloadThread = new DownloadThread(DownloadService.this,mDatabaseManager,info,fileInfo);
                    mDownloadThread.start();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabaseManager = new DatabaseOperation(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_START)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra(TAG_FILEINFO);
            mFileInfoThread = new FileInfoThread(fileInfo,mHandler);
            mFileInfoThread.start();

        } else if (intent.getAction().equals(ACTION_PAUSE)) {
            if (mDownloadThread != null) {
                mDownloadThread.setPause(true);
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFileInfoThread!=null){
            mFileInfoThread.stop();
        }
    }
}
