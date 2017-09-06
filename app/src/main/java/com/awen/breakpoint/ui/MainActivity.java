package com.awen.breakpoint.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awen.breakpoint.R;
import com.awen.breakpoint.download.DownloadService;
import com.awen.breakpoint.download.FileInfo;
import com.awen.breakpoint.model.NotificationModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.pro_text)
    TextView proText;

    private String url;
    private FileInfo fileInfo;
    private NotificationModel notificationModel;

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                int progress = intent.getIntExtra("progress", 0);
                progressBar.setProgress(progress);
                proText.setText(new StringBuffer().append(progress).append("%"));
                notificationModel.updateNotification(progress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        url = "http://dldir1.qq.com/weixin/android/weixin6316android780.apk";
        fileInfo = new FileInfo(0, url, "WeChat", 0, 0);
        name.setText(fileInfo.getFileName());
        proText.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        notificationModel = new NotificationModel(this);
        registiReceiver();
    }


    @OnClick({R.id.start, R.id.pause})
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        switch (view.getId()) {
            case R.id.start:
                notificationModel.showNotificationProgress();
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra(DownloadService.TAG_FILEINFO, fileInfo);
                startService(intent);
                break;
            case R.id.pause:
                intent.setAction(DownloadService.ACTION_PAUSE);
                intent.putExtra(DownloadService.TAG_FILEINFO, fileInfo);
                startService(intent);
                break;
        }
    }

    /**
     * 动态注册广播接收器
     */
    private void registiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
